package me.hypinohaizin.candyplusrewrite.module.movement;

import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Mod;

public class Speed extends Module {

    public Setting<SpeedMode> mode;
    public Setting<Float> multiplier;
    public Setting<Boolean> forceSprint;
    public Setting<Boolean> enableOnJump;
    public Setting<Boolean> resetMotion;
    public Setting<Boolean> fastJump;
    public Setting<Boolean> jumpSkip;

    private boolean lastActive = false;
    public Speed() {
        super("Speed", Categories.MOVEMENT, false, false);

        mode = register(new Setting<>("Mode", SpeedMode.STRAFE));
        multiplier = register(new Setting<>("Multiplier", 2.0f, 5f, 0f));
        forceSprint = register(new Setting<>("Force Sprint", true));
        enableOnJump = register(new Setting<>("Enable On Jump", false));
        resetMotion = register(new Setting<>("Reset Motion", true));
        fastJump = register(new Setting<>("Fast Jump", true));
        jumpSkip = register(new Setting<>("Jump Skip", true));
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        resetMotion();
    }

    private void resetMotion() {
        if (!resetMotion.getValue() || !lastActive || mc.player == null) {
            return;
        }
        lastActive = false;
        mc.player.motionX = 0.0;
        mc.player.motionZ = 0.0;
    }

    @Mod.EventHandler
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        EntityPlayer player = mc.player;

        Vec3d input = getMoveInputs(player);
        if (forceSprint.getValue() && (input.x != 0.0 || input.z != 0.0)) {
            player.setSprinting(true);
        }

        if (enableOnJump.getValue() && !mc.gameSettings.keyBindJump.isKeyDown()) {
            resetMotion();
            return;
        }

        if (player.isInWater() || player.isInLava() || player.isOnLadder()) {
            return;
        }

        lastActive = true;
        Vec3d moveInputs = getMoveInputs(player);
        moveInputs = moveInputs.subtract(0.0, moveInputs.y, 0.0).normalize();

        float yaw = player.rotationYaw;
        Vec3d motion = toWorld(moveInputs.x, moveInputs.z, yaw);

        AxisAlignedBB box = player.getEntityBoundingBox().offset(motion.x / 10.0, 0.0, motion.z / 10.0);

        if (fastJump.getValue() && mc.gameSettings.keyBindJump.isKeyDown() && player.onGround) {
            player.motionY = 0.42;
        }

        if (jumpSkip.getValue() &&
                mc.world.collidesWithAnyBlock(box) &&
                !mc.world.collidesWithAnyBlock(box.offset(0.0, 0.25, 0.0)) &&
                player.motionY > 0.0) {

            double maxHeight = getMaxHeight(box.offset(0.0, 0.25, 0.0));
            player.setPosition(
                    player.posX + motion.x / 10.0,
                    maxHeight,
                    player.posZ + motion.z / 10.0
            );
            player.motionY = 0.0;
        }

        if (mode.getValue() == SpeedMode.VANILLA) {
            float value = multiplier.getValue();
            player.motionX = motion.x * value;
            player.motionZ = motion.z * value;
        } else if (mode.getValue() == SpeedMode.STRAFE) {
            Vec3d vec = new Vec3d(player.motionX, 0.0, player.motionZ);
            double mul = Math.max(vec.distanceTo(Vec3d.ZERO), player.isSneaking() ? 0.0 : 0.1);

            if (!mc.gameSettings.keyBindForward.isKeyDown() && player.onGround) {
                mul *= 1.18;
            } else if (!mc.gameSettings.keyBindForward.isKeyDown()) {
                mul *= 1.025;
            }

            player.motionX = motion.x * mul;
            player.motionZ = motion.z * mul;
        }
    }

    private Vec3d getMoveInputs(EntityPlayer player) {
        double forward = 0.0;
        double strafe = 0.0;

        if (mc.gameSettings.keyBindForward.isKeyDown()) forward += 1.0;
        if (mc.gameSettings.keyBindBack.isKeyDown()) forward -= 1.0;
        if (mc.gameSettings.keyBindLeft.isKeyDown()) strafe += 1.0;
        if (mc.gameSettings.keyBindRight.isKeyDown()) strafe -= 1.0;

        return new Vec3d(strafe, 0.0, forward);
    }

    private Vec3d toWorld(double x, double z, float yaw) {
        double radians = Math.toRadians(yaw);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        return new Vec3d(
                x * cos - z * sin,
                0.0,
                x * sin + z * cos
        );
    }

    private double getMaxHeight(AxisAlignedBB box) {
        return box.maxY;
    }

    public enum SpeedMode {
        VANILLA,
        STRAFE
    }
}