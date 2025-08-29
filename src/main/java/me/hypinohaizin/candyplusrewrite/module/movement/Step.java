package me.hypinohaizin.candyplusrewrite.module.movement;

import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.play.client.CPacketPlayer;

public class Step extends Module {
    public Setting<Integer> height;
    public Setting<Mode> mode;
    public Setting<Boolean> pauseBurrow;
    public Setting<Boolean> pauseSneak;
    public Setting<Boolean> pauseWeb;
    public Setting<Boolean> onlyMoving;
    public Setting<Boolean> spoof;
    public Setting<Integer> delay;
    public Setting<Boolean> turnOff;

    private final double[] oneBlockOffset = {0.42, 0.753};
    private final double[] oneFiveOffset = {0.42, 0.75, 1.0, 1.16, 1.23, 1.2};
    private final double[] twoOffset = {0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
    private final double[] twoFiveOffset = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
    public Step() {
        super("Step", Categories.MOVEMENT, false, false);

        height = register(new Setting<>("Height", 2, 10, 1));
        mode = register(new Setting<>("Mode", Mode.Vanilla));
        pauseBurrow = register(new Setting<>("PauseBurrow", true));
        pauseSneak = register(new Setting<>("PauseSneak", true));
        pauseWeb = register(new Setting<>("PauseWeb", true));
        onlyMoving = register(new Setting<>("OnlyMoving", true));
        spoof = register(new Setting<>("Spoof", true));
        delay = register(new Setting<>("Delay", 3, 20, 0));
        turnOff = register(new Setting<>("Disable", false));
    }

    public static double[] forward(double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) yaw += (forward > 0.0f ? -45 : 45);
            else if (side < 0.0f) yaw += (forward > 0.0f ? 45 : -45);
            side = 0.0f;
            forward = forward > 0.0f ? 1.0f : -1.0f;
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double posX = forward * speed * cos + side * speed * sin;
        double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) return;

        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if ((mc.world.getBlockState(playerPos).getBlock() == Blocks.PISTON_HEAD || mc.world.getBlockState(playerPos).getBlock() == Blocks.OBSIDIAN ||
                mc.world.getBlockState(playerPos).getBlock() == Blocks.ENDER_CHEST || mc.world.getBlockState(playerPos).getBlock() == Blocks.BEDROCK) && pauseBurrow.getValue()) {
            mc.player.stepHeight = 0.1f;
            return;
        }
        if ((mc.world.getBlockState(playerPos.up()).getBlock() == Blocks.PISTON_HEAD || mc.world.getBlockState(playerPos.up()).getBlock() == Blocks.OBSIDIAN ||
                mc.world.getBlockState(playerPos.up()).getBlock() == Blocks.ENDER_CHEST || mc.world.getBlockState(playerPos.up()).getBlock() == Blocks.BEDROCK) && pauseBurrow.getValue()) {
            mc.player.stepHeight = 0.1f;
            return;
        }
        if (pauseWeb.getValue() && mc.player.isInWeb) {
            mc.player.stepHeight = 0.1f;
            return;
        }
        if (pauseSneak.getValue() && mc.player.isSneaking()) {
            mc.player.stepHeight = 0.1f;
            return;
        }
        if (onlyMoving.getValue() && (mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f)) {
            mc.player.stepHeight = 0.1f;
            return;
        }
        if (mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.stepHeight = 0.1f;
            return;
        }

        if (mode.getValue() == Mode.Normal) {
            mc.player.stepHeight = 0.6f;
            double[] dir = forward(0.1);
            boolean one = false, onefive = false, two = false, twofive = false;
            AxisAlignedBB bb = mc.player.getEntityBoundingBox();

            if (mc.world.getCollisionBoxes(mc.player, bb.offset(dir[0], 1.0, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, bb.offset(dir[0], 0.6, dir[1])).isEmpty())
                one = true;
            if (mc.world.getCollisionBoxes(mc.player, bb.offset(dir[0], 1.6, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, bb.offset(dir[0], 1.4, dir[1])).isEmpty())
                onefive = true;
            if (mc.world.getCollisionBoxes(mc.player, bb.offset(dir[0], 2.1, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, bb.offset(dir[0], 1.9, dir[1])).isEmpty())
                two = true;
            if (mc.world.getCollisionBoxes(mc.player, bb.offset(dir[0], 2.6, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, bb.offset(dir[0], 2.4, dir[1])).isEmpty())
                twofive = true;

            if (mc.player.collidedHorizontally && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) && mc.player.onGround) {
                if (one && height.getValue() >= 1.0f) {
                    for (double off : oneBlockOffset)
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + off, mc.player.posZ, mc.player.onGround));
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0f, mc.player.posZ);
                }
                if (onefive && height.getValue() >= 1.5f) {
                    for (double off : oneFiveOffset)
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + off, mc.player.posZ, mc.player.onGround));
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.5f, mc.player.posZ);
                }
                if (two && height.getValue() >= 2.0f) {
                    for (double off : twoOffset)
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + off, mc.player.posZ, mc.player.onGround));
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0f, mc.player.posZ);
                }
                if (twofive && height.getValue() >= 2.5f) {
                    for (double off : twoFiveOffset)
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + off, mc.player.posZ, mc.player.onGround));
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.5f, mc.player.posZ);
                }
            }
        }
        if (mode.getValue() == Mode.Vanilla) {
            mc.player.stepHeight = height.getValue();
        }
    }

    @Override
    public void onDisable() {
        mc.player.stepHeight = 0.6f;
    }

    public enum Mode {
        Vanilla,
        Normal
    }
}
