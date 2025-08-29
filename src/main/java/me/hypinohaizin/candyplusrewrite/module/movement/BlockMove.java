package me.hypinohaizin.candyplusrewrite.module.movement;

import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import me.hypinohaizin.candyplusrewrite.utils.BlockUtil;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockMove extends Module {

    public Setting<Boolean> middle;
    public Setting<Integer> delay;
    public Setting<Boolean> only;
    public Setting<Boolean> avoid;
    public Setting<Float> verticalSpeed;
    public Setting<Boolean> voidSafe;
    public Setting<Float> voidY;

    private final Timer timer;
    private final Vec3d[] sides;

    public BlockMove() {
        super("BlockMove", Categories.MOVEMENT, false, false);
        middle = register(new Setting<>("Middle", true));
        delay = register(new Setting<>("Delay", 250, 2000, 0));
        only = register(new Setting<>("Only In Block", true));
        avoid = register(new Setting<>("Avoid Out", true, v -> !only.getValue()));
        verticalSpeed = register(new Setting<>("VerticalSpeed", 0.3f, 1.0f, 0.05f));
        voidSafe = register(new Setting<>("VoidSafe", true));
        voidY = register(new Setting<>("VoidY", 60f, 256f, 0f, v -> voidSafe.getValue()));
        timer = new Timer();
        sides = new Vec3d[]{
                new Vec3d(0.24, 0.0, 0.24),
                new Vec3d(-0.24, 0.0, 0.24),
                new Vec3d(0.24, 0.0, -0.24),
                new Vec3d(-0.24, 0.0, -0.24)
        };
    }

    @Override
    public void onEnable() {
        super.onEnable();
        timer.reset();
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        double vy = voidY.getValue();
        if (voidSafe.getValue() && mc.player.posY <= vy) {
            mc.player.setPosition(mc.player.posX, vy + 1.0, mc.player.posZ);
            cancelInput(event);
            timer.reset();
            return;
        }

        Vec3d posVec = mc.player.getPositionVector();
        boolean air = true;
        AxisAlignedBB playerBox = mc.player.getEntityBoundingBox();
        for (Vec3d side : sides) {
            if (!air) break;
            for (int i = 0; i < 2; i++) {
                BlockPos pos = new BlockPos(posVec.add(side).add(0.0, i, 0.0));
                if (!BlockUtil.isAir(pos)) {
                    AxisAlignedBB box = BlockUtil.getBoundingBox(pos);
                    if (box != null && playerBox.intersects(box)) {
                        air = false;
                        break;
                    }
                }
            }
        }

        if (only.getValue() && air) return;

        MovementInput input = event.getMovementInput();

        if (timer.passedMs(delay.getValue())) {
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                double nextY = mc.player.posY + verticalSpeed.getValue();
                mc.player.setPosition(mc.player.posX, nextY, mc.player.posZ);
                timer.reset();
                cancelInput(event);
                return;
            } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                double nextY = mc.player.posY - verticalSpeed.getValue();
                if (voidSafe.getValue() && nextY <= vy) nextY = vy + 1.0;
                mc.player.setPosition(mc.player.posX, nextY, mc.player.posZ);
                timer.reset();
                cancelInput(event);
                return;
            }

            BlockPos playerPos = middle.getValue()
                    ? PlayerUtil.getPlayerPos()
                    : new BlockPos((double) Math.round(posVec.x), posVec.y, (double) Math.round(posVec.z));
            EnumFacing facing = mc.player.getHorizontalFacing();
            int dx = playerPos.offset(facing).getX() - playerPos.getX();
            int dz = playerPos.offset(facing).getZ() - playerPos.getZ();
            boolean addX = dx != 0;
            Vec3d target = posVec;
            if (input.forwardKeyDown) {
                target = moveTo(playerPos, addX, addX ? (dx < 0) : (dz < 0));
            } else if (input.backKeyDown) {
                target = moveTo(playerPos, addX, addX ? (dx > 0) : (dz > 0));
            } else if (input.leftKeyDown) {
                target = moveTo(playerPos, !addX, addX ? (dx > 0) : (dz < 0));
            } else if (input.rightKeyDown) {
                target = moveTo(playerPos, !addX, addX ? (dx < 0) : (dz > 0));
            }
            if (target != null) {
                mc.player.setPosition(target.x, target.y, target.z);
                timer.reset();
                cancelInput(event);
            }
        }
    }

    private Vec3d moveTo(BlockPos base, boolean xDir, boolean negative) {
        return negative
                ? toVec3d(base.add(xDir ? -1 : 0, 0, xDir ? 0 : -1))
                : toVec3d(base.add(xDir ? 1 : 0, 0, xDir ? 0 : 1));
    }

    private Vec3d toVec3d(BlockPos pos) {
        if (middle.getValue()) {
            return new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        }
        Vec3d last = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
        boolean any = !mc.world.isAirBlock(pos) || !mc.world.isAirBlock(pos.up());
        Vec3d v = new Vec3d(pos.getX() - 1.0E-8, pos.getY(), pos.getZ());
        if (mc.world.isAirBlock(new BlockPos(v)) && mc.world.isAirBlock(new BlockPos(v).up())) {
            last = v;
        } else {
            any = true;
        }
        v = new Vec3d(pos.getX(), pos.getY(), pos.getZ() - 1.0E-8);
        if (mc.world.isAirBlock(new BlockPos(v)) && mc.world.isAirBlock(new BlockPos(v).up())) {
            last = v;
        } else {
            any = true;
        }
        v = new Vec3d(pos.getX() - 1.0E-8, pos.getY(), pos.getZ() - 1.0E-8);
        if (mc.world.isAirBlock(new BlockPos(v)) && mc.world.isAirBlock(new BlockPos(v).up())) {
            last = v;
        } else {
            any = true;
        }
        if (!only.getValue() && !any && avoid.getValue()) {
            return null;
        }
        return last;
    }

    private void cancelInput(InputUpdateEvent event) {
        event.getMovementInput().forwardKeyDown = false;
        event.getMovementInput().backKeyDown = false;
        event.getMovementInput().leftKeyDown = false;
        event.getMovementInput().rightKeyDown = false;
        event.getMovementInput().moveForward = 0.0f;
        event.getMovementInput().moveStrafe = 0.0f;
    }
}