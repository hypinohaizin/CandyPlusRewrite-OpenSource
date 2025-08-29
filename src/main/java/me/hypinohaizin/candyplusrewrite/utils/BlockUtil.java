package me.hypinohaizin.candyplusrewrite.utils;

import java.util.Arrays;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.init.Blocks;
import java.util.ArrayList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockAir;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import java.util.List;

public class BlockUtil implements Util
{
    public static List<Block> emptyBlocks;
    public static final List<Block> blackList;
    public static List<Block> unSolidBlocks;
    public static final List<Block> unSafeBlocks;
    public static List<Block> rightclickableBlocks;

    public static AxisAlignedBB getBoundingBox(final BlockPos pos) {
        if (pos == null) {
            return null;
        }
        final AxisAlignedBB box = getState(pos).getCollisionBoundingBox(BlockUtil.mc.world, pos);
        return (box == null) ? null : new AxisAlignedBB(pos.getX() + box.minX, pos.getY() + box.minY, pos.getZ() + box.minZ, pos.getX() + box.maxX, pos.getY() + box.maxY, pos.getZ() + box.maxZ);
    }


    public static boolean isAir(Block block) {
        return block == Blocks.AIR || block instanceof BlockAir;
    }

    public static boolean isAir(BlockPos pos) {
        return isAir(mc.world.getBlockState(pos).getBlock());
    }

    public static boolean isAirBlock(Block block) {
        return block == Blocks.AIR || block instanceof BlockAir;
    }

    public static boolean isAirBlock(BlockPos pos) {
        return isAirBlock(mc.world.getBlockState(pos).getBlock());
    }

    public static boolean placeBlock(final BlockPos pos, final boolean packet) {
        final Block block = BlockUtil.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }
        final EnumFacing side = getPlaceableSide(pos);
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        if (!canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = BlockUtil.mc.world.getBlockState(neighbour).getBlock();
        if (packet) {
            rightClickBlock(neighbour, hitVec, EnumHand.MAIN_HAND, opposite);
        }
        else {
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
            BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        return true;
    }

    public static void faceplace(BlockPos pos, EnumHand hand, EnumFacing facing, boolean packet) {
        if (packet) {
            mc.player.connection.sendPacket(
                    new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.5f, 0.5f, 0.5f)
            );
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, facing, new Vec3d(0.5, 0.5, 0.5), hand);
        }
    }

    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet) {
        EnumFacing side = getPlaceableSide(pos);
        if (side == null) return false;
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5)
                .add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        boolean sneaking = false;
        if (rightclickableBlocks.contains(neighbourBlock) && !mc.player.isSneaking()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            sneaking = true;
        }
        if (rotate) {
            faceVector(hitVec, true);
        }
        if (packet) {
            rightClickBlock(neighbour, hitVec, hand, opposite, true);
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, hand);
            mc.player.swingArm(hand);
        }
        if (sneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        return true;
    }

    public static void rightClickBlock(final BlockPos pos, final EnumFacing facing, final boolean packet) {
        final Vec3d hitVec = new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));
        if (packet) {
            rightClickBlock(pos, hitVec, EnumHand.MAIN_HAND, facing);
        }
        else {
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos, facing, hitVec, EnumHand.MAIN_HAND);
            BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
    
    public static void rightClickBlock(final BlockPos pos, final EnumFacing facing, final Vec3d hVec, final boolean packet) {
        final Vec3d hitVec = new Vec3d(pos).add(hVec).add(new Vec3d(facing.getDirectionVec()).scale(0.5));
        if (packet) {
            rightClickBlock(pos, hitVec, EnumHand.MAIN_HAND, facing);
        }
        else {
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos, facing, hitVec, EnumHand.MAIN_HAND);
            BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
    
    public static void rightClickBlock(final BlockPos pos, final Vec3d vec, final EnumHand hand, final EnumFacing direction) {
        final float f = (float)(vec.x - pos.getX());
        final float f2 = (float)(vec.y - pos.getY());
        final float f3 = (float)(vec.z - pos.getZ());
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f2, f3));
    }
    
    public static void rightClickBlock(final BlockPos pos, final Vec3d vec, final EnumHand hand, final EnumFacing direction, final boolean packet) {
        if (packet) {
            final float f = (float)(vec.x - pos.getX());
            final float f2 = (float)(vec.y - pos.getY());
            final float f3 = (float)(vec.z - pos.getZ());
            BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f2, f3));
        }
        else {
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos, direction, vec, hand);
        }
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
    }
    
    public static boolean canRightClickForPlace(final BlockPos pos) {
        return !BlockUtil.rightclickableBlocks.contains(getBlock(pos));
    }
    
    public static boolean canBeClicked(final BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }
    
    public static Block getBlock(final BlockPos pos) {
        return getState(pos).getBlock();
    }
    
    public static Block getBlock(final double x, final double y, final double z) {
        return BlockUtil.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }
    
    private static IBlockState getState(final BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos);
    }
    
    public static boolean checkForNeighbours(final BlockPos blockPos) {
        if (!hasNeighbour(blockPos)) {
            for (final EnumFacing side : EnumFacing.values()) {
                final BlockPos neighbour = blockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public static EnumFacing getPlaceableSide(final BlockPos pos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = pos.offset(side);
            if (BlockUtil.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(neighbour), false)) {
                final IBlockState blockState = BlockUtil.mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable() && !BlockUtil.blackList.contains(getBlock(neighbour))) {
                    return side;
                }
            }
        }
        return null;
    }
    
    public static boolean hasNeighbour(final BlockPos blockPos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = blockPos.offset(side);
            if (!BlockUtil.mc.world.getBlockState(neighbour).getMaterial().isReplaceable() && !BlockUtil.blackList.contains(getBlock(neighbour))) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean canBeNeighbour(final BlockPos pos, final BlockPos a) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = pos.offset(side);
            if (a.equals(neighbour)) {
                return true;
            }
        }
        return false;
    }
    
    public static List<BlockPos> getSphere(final BlockPos pos, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = pos.getX();
        final int cy = pos.getY();
        final int cz = pos.getZ();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                int y = sphere ? (cy - (int)r) : cy;
                while (true) {
                    final float f = (float)y;
                    final float f2 = sphere ? (cy + r) : ((float)(cy + h));
                    if (f >= f2) {
                        break;
                    }
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        final BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
            }
        }
        return circleblocks;
    }
    
    public static boolean canPlaceBlock(final BlockPos pos) {
        return (getBlock(pos) == Blocks.AIR || getBlock(pos) instanceof BlockLiquid) && hasNeighbour(pos) && !BlockUtil.blackList.contains(getBlock(pos));
    }
    
    public static boolean canPlaceBlockFuture(final BlockPos pos) {
        return (getBlock(pos) == Blocks.AIR || getBlock(pos) instanceof BlockLiquid) && !BlockUtil.blackList.contains(getBlock(pos));
    }
    
    public static boolean isBlockSolid(final BlockPos pos) {
        return !isBlockUnSolid(pos);
    }
    
    public static boolean isBlockUnSolid(final BlockPos pos) {
        return isBlockUnSolid(BlockUtil.mc.world.getBlockState(pos).getBlock());
    }
    
    public static boolean isBlockUnSolid(final Block block) {
        return BlockUtil.unSolidBlocks.contains(block);
    }
    
    public static boolean isBlockUnSafe(final Block block) {
        return BlockUtil.unSafeBlocks.contains(block);
    }
    
    public static BlockPos[] toBlockPos(final Vec3d[] vec3ds) {
        final BlockPos[] list = new BlockPos[vec3ds.length];
        for (int i = 0; i < vec3ds.length; ++i) {
            list[i] = new BlockPos(vec3ds[i]);
        }
        return list;
    }
    
    public static double getDistance(final double blockposx, final double blockposy, final double blockposz, final double blockposx1, final double blockposy1, final double blockposz1) {
        final double deltaX = blockposx1 - blockposx;
        final double deltaY = blockposy1 - blockposy;
        final double deltaZ = blockposz1 - blockposz;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }
    
    public static boolean canBreak(final BlockPos pos) {
        final IBlockState blockState = BlockUtil.mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, BlockUtil.mc.world, pos) != -1.0f;
    }
    
    public static void placeCrystalOnBlock(final BlockPos pos, final EnumHand hand, final boolean swing) {
        final RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5));
        final EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
        if (swing) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketAnimation(hand));
        }
    }
    
    public static boolean placeBlock(final BlockPos pos, final int slot, final boolean rotate, final boolean rotateBack, final EnumHand hand) {
        if (isBlockEmpty(pos)) {
            int old_slot = -1;
            if (slot != BlockUtil.mc.player.inventory.currentItem) {
                old_slot = BlockUtil.mc.player.inventory.currentItem;
                BlockUtil.mc.player.inventory.currentItem = slot;
            }
            final EnumFacing[] values;
            final EnumFacing[] facings = values = EnumFacing.values();
            for (final EnumFacing f : values) {
                final Block neighborBlock = BlockUtil.mc.world.getBlockState(pos.offset(f)).getBlock();
                final Vec3d vec = new Vec3d(pos.getX() + 0.5 + f.getXOffset() * 0.5, pos.getY() + 0.5 + f.getYOffset() * 0.5, pos.getZ() + 0.5 + f.getZOffset() * 0.5);
                if (!BlockUtil.emptyBlocks.contains(neighborBlock) && BlockUtil.mc.player.getPositionEyes(BlockUtil.mc.getRenderPartialTicks()).distanceTo(vec) <= 4.25) {
                    final float[] rot = { BlockUtil.mc.player.rotationYaw, BlockUtil.mc.player.rotationPitch };
                    if (rotate) {
                        rotatePacket(vec.x, vec.y, vec.z);
                    }
                    if (BlockUtil.rightclickableBlocks.contains(neighborBlock)) {
                        BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    }
                    BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos.offset(f), f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
                    if (BlockUtil.rightclickableBlocks.contains(neighborBlock)) {
                        BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    }
                    if (rotateBack) {
                        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], BlockUtil.mc.player.onGround));
                    }
                    BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean placeBlock(final BlockPos pos, final EnumHand hand, final boolean rotate, final boolean packet, final boolean isSneaking) {
        boolean sneaking = false;
        final EnumFacing side = getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = BlockUtil.mc.world.getBlockState(neighbour).getBlock();
        if (!BlockUtil.mc.player.isSneaking()) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            BlockUtil.mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            faceVector(hitVec, true);
        }
        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        return sneaking || isSneaking;
    }
    
    public static void faceVector(final Vec3d vec, final boolean normalizeAngle) {
        final float[] rotations = getLegitRotations(vec);
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? ((float)MathHelper.normalizeAngle((int)rotations[1], 360)) : rotations[1], BlockUtil.mc.player.onGround));
    }
    
    public static float[] getLegitRotations(final Vec3d vec) {
        final Vec3d eyesPos = getEyesPos();
        final double diffX = vec.x - eyesPos.x;
        final double diffY = vec.y - eyesPos.y;
        final double diffZ = vec.z - eyesPos.z;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[] { BlockUtil.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - BlockUtil.mc.player.rotationYaw), BlockUtil.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - BlockUtil.mc.player.rotationPitch) };
    }
    
    public static EnumFacing getFirstFacing(final BlockPos pos) {
        final Iterator<EnumFacing> iterator = getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            final EnumFacing facing = iterator.next();
            return facing;
        }
        return null;
    }
    
    public static Vec3d getEyesPos() {
        return new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ);
    }
    
    public static List<EnumFacing> getPossibleSides(final BlockPos pos) {
        final List<EnumFacing> facings = new ArrayList<EnumFacing>();
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = pos.offset(side);
            if (BlockUtil.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(neighbour), false)) {
                final IBlockState blockState = BlockUtil.mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable()) {
                    facings.add(side);
                }
            }
        }
        return facings;
    }
    
    public static void rotatePacket(final double x, final double y, final double z) {
        final double diffX = x - BlockUtil.mc.player.posX;
        final double diffY = y - (BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight());
        final double diffZ = z - BlockUtil.mc.player.posZ;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, BlockUtil.mc.player.onGround));
    }
    
    public static boolean isBlockEmpty(final BlockPos pos) {
        try {
            if (BlockUtil.emptyBlocks.contains(BlockUtil.mc.world.getBlockState(pos).getBlock())) {
                final AxisAlignedBB box = new AxisAlignedBB(pos);
                for (final Entity e : BlockUtil.mc.world.loadedEntityList) {
                    if (e instanceof EntityLivingBase && box.intersects(e.getEntityBoundingBox())) {
                        return false;
                    }
                }
                return true;
            }
        }
        catch (final Exception ex) {}
        return false;
    }
    
    public static EnumFacing getBackwardFacing(final EnumFacing facing) {
        final Vec3i vec = facing.getDirectionVec();
        return EnumFacing.getFacingFromVector((float)(vec.getX() * -1), (float)(vec.getY() * -1), (float)(vec.getZ() * -1));
    }
    
    static {
        BlockUtil.emptyBlocks = Arrays.asList(Blocks.AIR, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.VINE, Blocks.SNOW_LAYER, Blocks.TALLGRASS, Blocks.FIRE);
        blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
        BlockUtil.unSolidBlocks = Arrays.asList(Blocks.FLOWING_LAVA, Blocks.FLOWER_POT, Blocks.SNOW, Blocks.CARPET, Blocks.END_ROD, Blocks.SKULL, Blocks.FLOWER_POT, Blocks.TRIPWIRE, Blocks.TRIPWIRE_HOOK, Blocks.WOODEN_BUTTON, Blocks.LEVER, Blocks.STONE_BUTTON, Blocks.LADDER, Blocks.UNPOWERED_COMPARATOR, Blocks.POWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.UNLIT_REDSTONE_TORCH, Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WIRE, Blocks.AIR, Blocks.PORTAL, Blocks.END_PORTAL, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.SAPLING, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS, Blocks.REEDS, Blocks.PUMPKIN_STEM, Blocks.MELON_STEM, Blocks.WATERLILY, Blocks.NETHER_WART, Blocks.COCOA, Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT, Blocks.TALLGRASS, Blocks.DEADBUSH, Blocks.VINE, Blocks.FIRE, Blocks.RAIL, Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.TORCH);
        unSafeBlocks = Arrays.asList(Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST, Blocks.ANVIL);
        BlockUtil.rightclickableBlocks = Arrays.asList(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.ANVIL, Blocks.WOODEN_BUTTON, Blocks.STONE_BUTTON, Blocks.UNPOWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.POWERED_COMPARATOR, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.BREWING_STAND, Blocks.DISPENSER, Blocks.DROPPER, Blocks.LEVER, Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.BEACON, Blocks.BED, Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR, Blocks.CAKE, Blocks.ENCHANTING_TABLE, Blocks.DRAGON_EGG, Blocks.HOPPER, Blocks.REPEATING_COMMAND_BLOCK, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.CRAFTING_TABLE);
    }

    public static float[] calcAngle(final Vec3d from, final Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0;
        final double difZ = to.z - from.z;
        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))) };
    }

    public static CPacketPlayer.Rotation getFaceVectorPacket(Vec3d vec, Boolean roundAngles) {
        float[] rotations = getNeededRotations2(vec);

        CPacketPlayer.Rotation e = new CPacketPlayer.Rotation(rotations[0], roundAngles ? MathHelper.normalizeAngle((int) rotations[1], 360) : rotations[1], mc.player.onGround);

        mc.player.connection.sendPacket(e);

        return e;
    }

    public static float[] calcAngleNoY(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difZ = to.z - from.z;
        return new float[]{(float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0)};
    }

    private static float[] getNeededRotations2(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{
                mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
                mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)
        };
    }
    public static double getMaxHeight(AxisAlignedBB box) {
        if (mc.player == null || mc.world == null) return Double.NaN;

        List<AxisAlignedBB> collisions = mc.world.getCollisionBoxes(mc.player, box.offset(0.0, -1.0, 0.0));
        double maxY = 0.0;
        boolean updated = false;

        for (AxisAlignedBB collision : collisions) {
            if (collision.maxY > maxY) {
                updated = true;
                maxY = collision.maxY;
            }
        }
        return updated ? maxY : Double.NaN;
    }
}
