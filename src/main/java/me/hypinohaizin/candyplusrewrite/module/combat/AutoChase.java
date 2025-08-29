package me.hypinohaizin.candyplusrewrite.module.combat;

import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.utils.EntityUtil;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import me.hypinohaizin.candyplusrewrite.utils.RotationUtil;
import me.hypinohaizin.candyplusrewrite.managers.HoleManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.*;

public class AutoChase extends Module {

    public Setting<Integer> targetRange;
    public Setting<Integer> fixedRange;
    public Setting<Integer> cancelRange;
    public Setting<Integer> downRange;
    public Setting<Integer> upRange;
    public Setting<Float> hRange;
    public Setting<Float> timer;
    public Setting<Float> speed;
    public Setting<Boolean> step;
    public Setting<ModeType> mode;
    public Setting<Float> height;
    public Setting<Float> vHeight;
    public Setting<Boolean> abnormal;
    public Setting<Integer> centerSpeed;
    public Setting<Boolean> only;
    public Setting<Boolean> single;
    public Setting<Boolean> twoBlocks;
    public Setting<Boolean> custom;
    public Setting<Boolean> four;
    public Setting<Boolean> near;
    public Setting<Boolean> disable;

    private int stuckTicks;
    private BlockPos originPos;
    private BlockPos startPos;
    private boolean isActive;
    private boolean wasInHole;
    private boolean slowDown;
    private double playerSpeed;
    private EntityPlayer target;
    private Timer gameTimer;
    private HoleManager holeManager;

    private final double[] one = {0.42, 0.753};
    private final double[] oneFive = {0.42, 0.75, 1.0, 1.16, 1.23, 1.2};
    private final double[] two = {0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
    private final double[] twoFive = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};

    private long lastStepMs = 0L;

    public AutoChase() {
        super("AutoChase", Categories.COMBAT, false, false);

        targetRange = register(new Setting<>("Target Range", 16, 256, 0));
        fixedRange = register(new Setting<>("Fixed Target Range", 16, 256, 0));
        cancelRange = register(new Setting<>("Cancel Range", 6, 16, 0));
        downRange = register(new Setting<>("Down Range", 5, 8, 0));
        upRange = register(new Setting<>("Up Range", 1, 8, 0));
        hRange = register(new Setting<>("H Range", 4.0f, 8.0f, 1.0f));
        timer = register(new Setting<>("Timer", 2.0f, 50.0f, 1.0f));
        speed = register(new Setting<>("Speed", 2.0f, 10.0f, 0.0f));
        step = register(new Setting<>("Step", true));
        mode = register(new Setting<>("Mode", ModeType.NCP));
        height = register(new Setting<>("NCP Height", 2.5f, 4.0f, 1.0f, v -> mode.getValue() == ModeType.NCP && step.getValue()));
        vHeight = register(new Setting<>("Vanilla Height", 2.5f, 4.0f, 1.0f, v -> mode.getValue() == ModeType.VANILLA && step.getValue()));
        abnormal = register(new Setting<>("Abnormal", false, v -> mode.getValue() != ModeType.VANILLA && step.getValue()));
        centerSpeed = register(new Setting<>("Center Speed", 2, 10, 1));
        only = register(new Setting<>("Only 1x1", true));
        single = register(new Setting<>("Single Hole", true, v -> !only.getValue()));
        twoBlocks = register(new Setting<>("Double Hole", true, v -> !only.getValue()));
        custom = register(new Setting<>("Custom Hole", true, v -> !only.getValue()));
        four = register(new Setting<>("Four Blocks", true, v -> !only.getValue()));
        near = register(new Setting<>("Near Target", true));
        disable = register(new Setting<>("Disable", true));

        gameTimer = new Timer();
        holeManager = new HoleManager();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        wasInHole = false;
        BlockPos playerPos = PlayerUtil.getPlayerPos();
        originPos = playerPos;
        startPos = playerPos;
        stuckTicks = 0;
        isActive = false;
        gameTimer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        isActive = false;
        stuckTicks = 0;
        resetTimer();
        if (mc.player != null) {
            if (mc.player.getRidingEntity() != null) mc.player.getRidingEntity().stepHeight = 1.0f;
            mc.player.stepHeight = 0.6f;
        }
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        if (isActive && event.getMovementInput() != null) {
            event.getMovementInput().jump = false;
            event.getMovementInput().sneak = false;
            event.getMovementInput().forwardKeyDown = false;
            event.getMovementInput().backKeyDown = false;
            event.getMovementInput().leftKeyDown = false;
            event.getMovementInput().rightKeyDown = false;
            event.getMovementInput().moveForward = 0.0f;
            event.getMovementInput().moveStrafe = 0.0f;
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (nullCheck()) return;
        if (event.phase != TickEvent.Phase.START || mc.player == null || mc.world == null) return;

        isActive = false;
        resetTimer();

        if (!mc.player.isEntityAlive() || mc.player.isElytraFlying() || mc.player.capabilities.isFlying) return;

        final double currentSpeed = Math.hypot(mc.player.motionX, mc.player.motionZ);
        if (currentSpeed <= 0.05) originPos = PlayerUtil.getPlayerPos();

        target = getNearestPlayer(target);
        if (target == null) return;

        final double range = mc.player.getDistance(target);
        final boolean inRange = range <= cancelRange.getValue();

        if (shouldDisable(currentSpeed, inRange)) {
            if (disable.getValue()) disable();
            return;
        }

        final BlockPos hole = findHoles(target, inRange);
        if (hole != null) {
            final double x = hole.getX() + 0.5;
            final double y = hole.getY();
            final double z = hole.getZ() + 0.5;

            if (checkYRange((int) mc.player.posY, hole.getY())) {
                final Vec3d playerPos = mc.player.getPositionVector();
                final double yawRad = Math.toRadians(RotationUtil.getRotationTo(playerPos, new Vec3d(x, y, z)).x);
                final double dist = Math.hypot(x - playerPos.x, z - playerPos.z);

                if (mc.player.onGround) {
                    playerSpeed = PlayerUtil.getBaseMoveSpeed() * ((EntityUtil.isInLiquid()) ? 0.91 : speed.getValue());
                    slowDown = true;
                }

                final double moveSpeed = Math.min(dist, playerSpeed);
                mc.player.motionX = -Math.sin(yawRad) * moveSpeed;
                mc.player.motionZ = Math.cos(yawRad) * moveSpeed;

                if (moveSpeed != 0.0 && (-Math.sin(yawRad) != 0.0 || Math.cos(yawRad) != 0.0)) {
                    setTimer((float) (50.0 / timer.getValue()));
                    isActive = true;
                }
            }
        }

        if (mc.player.collidedHorizontally && hole == null) stuckTicks++;
        else stuckTicks = 0;

        if (step.getValue()) {
            if (mode.getValue() == ModeType.VANILLA) {
                if (canStep()) mc.player.stepHeight = vHeight.getValue();
                else mc.player.stepHeight = 0.6f;
            } else {
                mc.player.stepHeight = 0.6f;
                if (canStep()) tryNcpPacketStep();
            }
        } else {
            if (mc.player.getRidingEntity() != null) mc.player.getRidingEntity().stepHeight = 1.0f;
            mc.player.stepHeight = 0.6f;
        }

        if (target == null) isActive = false;
    }

    private void tryNcpPacketStep() {
        long now = System.currentTimeMillis();
        if (now - lastStepMs < 150L) return;
        if (!mc.player.onGround || mc.gameSettings.keyBindJump.isKeyDown() || mc.player.fallDistance > 0.1f) return;
        if (mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || mc.player.isInWeb) return;
        if (mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f) return;
        if (!mc.player.collidedHorizontally) return;

        double[] dir = forward(0.1);
        double dx = dir[0], dz = dir[1];
        AxisAlignedBB bb = mc.player.getEntityBoundingBox();

        boolean can1 = emptyAt(bb, dx, 1.00, dz) && !emptyAt(bb, dx, 0.60, dz) && height.getValue() >= 1.0f;
        boolean can15 = emptyAt(bb, dx, 1.60, dz) && !emptyAt(bb, dx, 1.40, dz) && height.getValue() >= 1.5f;
        boolean can2 = emptyAt(bb, dx, 2.10, dz) && !emptyAt(bb, dx, 1.90, dz) && height.getValue() >= 2.0f;
        boolean can25 = emptyAt(bb, dx, 2.60, dz) && !emptyAt(bb, dx, 2.40, dz) && height.getValue() >= 2.5f;

        if (can25) doStep(2.5, twoFive);
        else if (can2) doStep(2.0, two);
        else if (can15) doStep(1.5, oneFive);
        else if (can1) doStep(1.0, one);
    }

    private void doStep(double h, double[] offsets) {
        for (double off : offsets) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + off, mc.player.posZ, mc.player.onGround));
        }
        mc.player.setPosition(mc.player.posX, mc.player.posY + h, mc.player.posZ);
        lastStepMs = System.currentTimeMillis();
    }

    private boolean emptyAt(AxisAlignedBB bb, double ox, double oy, double oz) {
        return mc.world.getCollisionBoxes(mc.player, bb.offset(ox, oy, oz)).isEmpty();
    }

    private double[] forward(double speed) {
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

    private EntityPlayer getNearestPlayer(EntityPlayer currentTarget) {
        if (currentTarget != null
                && mc.player.getDistance(currentTarget) <= fixedRange.getValue()
                && !isInvalidTarget(currentTarget)) return currentTarget;
        return mc.world.playerEntities.stream()
                .filter(p -> mc.player.getDistance(p) <= targetRange.getValue())
                .filter(p -> mc.player.getEntityId() != p.getEntityId())
                .filter(p -> !isInvalidTarget(p))
                .min(Comparator.comparing(p -> mc.player.getDistance(p)))
                .orElse(null);
    }

    private boolean isInvalidTarget(EntityPlayer player) {
        return player.isDead || player.getHealth() <= 0 || FriendManager.isFriend(player.getName());
    }

    private BlockPos findHoles(EntityPlayer target, boolean inRange) {
        if (inRange && wasInHole) return null;
        wasInHole = false;

        List<BlockPos> holes = new ArrayList<>();
        List<BlockPos> blockPosList = getSphere(getPlayerPos(target), hRange.getValue(), 8.0, false, true, 0);

        for (BlockPos pos : blockPosList) {
            if (!checkYRange((int) mc.player.posY, pos.getY())) continue;
            if (!mc.world.isAirBlock(PlayerUtil.getPlayerPos().up(2)) && (int) mc.player.posY < pos.getY()) continue;

            if (holeManager.isSafe(pos)) {
                if (mc.world.isAirBlock(pos) && mc.world.isAirBlock(pos.up()) && mc.world.isAirBlock(pos.up(2))) {
                    boolean valid = true;
                    for (int high = 0; high < mc.player.posY - pos.getY(); high++) {
                        if (high != 0) {
                            if (mc.player.posY > pos.getY()) {
                                if (!mc.world.isAirBlock(new BlockPos(pos.getX(), pos.getY() + high, pos.getZ()))) { valid = false; break; }
                            }
                            if (mc.player.posY < pos.getY()) {
                                BlockPos np = new BlockPos(pos.getX(), pos.getY() + high, pos.getZ());
                                if (mc.world.isAirBlock(np) && (mc.world.isAirBlock(np.down()) || mc.world.isAirBlock(np.up()))) { valid = false; break; }
                            }
                        }
                    }
                    if (valid) holes.add(pos);
                }
            }
        }

        return holes.stream()
                .min(Comparator.comparing(p -> near.getValue()
                        ? target.getDistance(p.getX() + 0.5, p.getY(), p.getZ() + 0.5)
                        : mc.player.getDistance(p.getX() + 0.5, p.getY(), p.getZ() + 0.5)))
                .orElse(null);
    }

    private boolean shouldDisable(Double currentSpeed, boolean inRange) {
        if (isActive) return false;
        if (!mc.player.onGround) return false;
        if (stuckTicks > 5 && currentSpeed < 0.05) return true;

        if (holeManager.isSafe(new BlockPos(PlayerUtil.getPlayerPos().getX(), PlayerUtil.getPlayerPos().getY() + 0.5, PlayerUtil.getPlayerPos().getZ())) && inRange) {
            BlockPos holePos = new BlockPos(PlayerUtil.getPlayerPos().getX(), PlayerUtil.getPlayerPos().getY(), PlayerUtil.getPlayerPos().getZ());
            Vec3d center = new Vec3d(holePos.getX() + 0.5, holePos.getY(), holePos.getZ() + 0.5);

            double xDiff = Math.abs(center.x - mc.player.posX);
            double zDiff = Math.abs(center.z - mc.player.posZ);

            if ((xDiff > 0.3 || zDiff > 0.3) && !wasInHole) {
                double motionX = center.x - mc.player.posX;
                double motionZ = center.z - mc.player.posZ;
                mc.player.motionX = motionX / centerSpeed.getValue();
                mc.player.motionZ = motionZ / centerSpeed.getValue();
            }

            return wasInHole = true;
        }

        return false;
    }

    private boolean checkYRange(int playerY, int holeY) {
        if (playerY >= holeY) return playerY - holeY <= downRange.getValue();
        return holeY - playerY <= upRange.getValue();
    }

    public float getHeight() {
        return mode.getValue() == ModeType.VANILLA ? vHeight.getValue() : height.getValue();
    }

    private boolean canStep() {
        return !mc.player.isInWater() && mc.player.onGround && !mc.player.isOnLadder() &&
                !mc.player.movementInput.jump && mc.player.collidedVertically &&
                mc.player.fallDistance < 0.1 && step.getValue() && isActive;
    }

    private void setTimer(float tickLength) { }

    private void resetTimer() { setTimer(50.0f); }

    private List<BlockPos> getSphere(BlockPos center, double radius, double height, boolean hollow, boolean sphere, int plusY) {
        List<BlockPos> result = new ArrayList<>();
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        for (int x = cx - (int) radius; x <= cx + radius; x++) {
            for (int z = cz - (int) radius; z <= cz + radius; z++) {
                int y = sphere ? (cy - (int) radius) : cy;
                while (y <= (sphere ? (cy + radius) : (cy + height))) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < radius * radius && (!hollow || dist >= (radius - 1.0) * (radius - 1.0))) result.add(new BlockPos(x, y + plusY, z));
                    y++;
                }
            }
        }
        return result;
    }

    private BlockPos getPlayerPos(EntityPlayer player) {
        return new BlockPos(player.posX, player.posY, player.posZ);
    }

    public enum ModeType {
        NCP,
        VANILLA
    }
}
