package me.hypinohaizin.candyplusrewrite.module.movement;

import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import me.hypinohaizin.candyplusrewrite.utils.MovementUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class PhaseWalk extends Module {

    public Setting<Float> multiplier;
    public Setting<Float> downSpeed;
    public Setting<Boolean> shiftLimit;
    public Setting<Boolean> voidSafe;
    public Setting<Integer> voidY;

    private boolean sneakedFlag;
    private boolean phaseSuffix;
    private long startTime = 0L;

    public PhaseWalk() {
        super("PhaseWalk", Categories.MOVEMENT, false, false);

        multiplier = register(new Setting<>("SpeedMultiplier", 1.0f, 5.0f, 0.1f));
        downSpeed = register(new Setting<>("DownSpeed", 0.1f, 2.0f, 0.05f));
        shiftLimit = register(new Setting<>("ShiftLimit", true));
        voidSafe = register(new Setting<>("VoidSafe", true));
        voidY = register(new Setting<>("VoidY", 60, 256, 0, v -> voidSafe.getValue()));
    }

    @Override
    public void onEnable() {
        sneakedFlag = false;
        phaseSuffix = false;
        startTime = 0L;
    }

    @Override
    public void onDisable() {
        phaseSuffix = false;
    }

    @Override
    public void onTick() {
        EntityPlayerSP player = mc.player;
        if (player == null || mc.world == null) return;

        boolean isInside = PlayerUtil.isInsideBlock();
        if (isInside) {
            if (startTime == 0L) startTime = System.currentTimeMillis();
            phaseSuffix = true;
            player.noClip = true;
            player.fallDistance = 0.0f;

            Vec3d input = MovementUtil.getMoveInputs(player);
            double mul = multiplier.getValue();
            Vec2f moveVec = MovementUtil.toWorld(new Vec2f((float)input.x, (float)input.z), player.rotationYaw);
            player.motionX = moveVec.x * mul;
            player.motionZ = moveVec.y * mul;
            player.motionY = 0.0;

            if (player.movementInput.sneak) {
                if (shiftLimit.getValue()) {
                    if (!sneakedFlag) {
                        if (voidSafe.getValue() && player.posY - downSpeed.getValue() <= voidY.getValue()) {
                            player.setPosition(player.posX, voidY.getValue() + 1.0, player.posZ);
                        } else {
                            player.setPosition(player.posX, player.posY - downSpeed.getValue(), player.posZ);
                        }
                    }
                } else {
                    player.setPosition(player.posX, player.posY - downSpeed.getValue(), player.posZ);
                }
                sneakedFlag = true;
            } else if (sneakedFlag) {
                sneakedFlag = false;
            }
        } else {
            phaseSuffix = false;
            player.noClip = false;
            if (startTime != 0L) {
                startTime = 0L;
            }
        }

        if (voidSafe.getValue() && player.posY <= voidY.getValue()) {
            player.setPosition(player.posX, voidY.getValue() + 1.0, player.posZ);
            phaseSuffix = true;
        }
    }
}
