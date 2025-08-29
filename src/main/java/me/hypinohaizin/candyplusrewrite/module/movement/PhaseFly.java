package me.hypinohaizin.candyplusrewrite.module.movement;

import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import net.minecraft.client.entity.EntityPlayerSP;
import me.hypinohaizin.candyplusrewrite.utils.MathUtil;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.math.BlockPos;

public class PhaseFly extends Module {

    public Setting<Float> speed;
    public Setting<Float> vSpeed;
    public Setting<Boolean> onlyInBlock;
    public Setting<Boolean> autoPhase;

    private boolean active;
    private long lastInsideAt;
    private static final long EXIT_GRACE_MS = 150L;
    private int stage = 0;

    public PhaseFly() {
        super("PhaseFly", Categories.MOVEMENT, false, false);
        speed = register(new Setting<>("Speed", 1.0f, 5.0f, 0.1f));
        vSpeed = register(new Setting<>("VerticalSpeed", 1.0f, 5.0f, 0.1f));
        onlyInBlock = register(new Setting<>("OnlyInBlock", false));
        autoPhase = register(new Setting<>("AutoPhase", true));
    }

    @Override
    public void onEnable() {
        active = false;
        lastInsideAt = 0L;
        stage = 0;
        if (mc.player != null) mc.player.setSneaking(false);
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.noClip = false;
            mc.player.setSneaking(false);
        }
        active = false;
        stage = 0;
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) return;
        EntityPlayerSP p = mc.player;
        boolean inside = isInsideBlock(p);
        long now = System.currentTimeMillis();
        if (inside) {
            lastInsideAt = now;
            active = true;
        } else if (active && now - lastInsideAt <= EXIT_GRACE_MS) {
            active = true;
        } else {
            active = false;
        }
        if (onlyInBlock.getValue() && !active) {
            p.noClip = false;
            return;
        }
        p.noClip = true;
        p.onGround = false;
        p.fallDistance = 0.0f;

        double[] mv = MathUtil.directionSpeed(speed.getValue());
        p.motionX = mv[0];
        p.motionZ = mv[1];

        double vy = 0.0;
        double vs = vSpeed.getValue();
        if (p.movementInput.jump) vy += vs;
        if (p.movementInput.sneak) vy -= vs;
        p.motionY = vy;
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        if (!autoPhase.getValue()) return;
        EntityPlayerSP player = mc.player;
        if (player == null) return;
        boolean inside = isInsideBlock(player);
        boolean roof = isHittingRoof();
        if (stage == 0) {
            if (inside || roof) {
                stage = 1;
            } else {
                return;
            }
        }
        if (stage == 1 || stage == 2) {
            event.getMovementInput().sneak = true;
            player.setSneaking(true);
            stage++;
        } else if (stage == 3) {
            event.getMovementInput().sneak = true;
            player.setSneaking(true);
            player.setPosition(player.posX, player.posY + 0.1, player.posZ);
            stage++;
        } else if (stage >= 4) {
            player.setSneaking(false);
            stage = 0;
        }
    }

    private boolean isHittingRoof() {
        if (mc.player == null || mc.world == null) return false;
        BlockPos pos = mc.player.getPosition();
        return !mc.world.isAirBlock(pos.up(2)) || !mc.world.isAirBlock(pos.up(3));
    }

    private boolean isInsideBlock(EntityPlayerSP p) {
        return !p.world.getCollisionBoxes(p, p.getEntityBoundingBox()).isEmpty();
    }
}
