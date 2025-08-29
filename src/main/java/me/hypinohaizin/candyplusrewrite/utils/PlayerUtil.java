package me.hypinohaizin.candyplusrewrite.utils;

import net.minecraft.entity.MoverType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockAir;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import org.apache.logging.log4j.Level;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.potion.Potion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerUtil implements Util
{
    public static int getHealth(final EntityPlayer player) {
        return (int)(player.getHealth() + player.getAbsorptionAmount());
    }
    
    public static BlockPos getPlayerPos() {
        return new BlockPos(PlayerUtil.mc.player.posX, PlayerUtil.mc.player.posY, PlayerUtil.mc.player.posZ);
    }
    
    public static BlockPos getPlayerPos(final EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }
    
    public static EntityPlayer getNearestPlayer(final double range) {
        return PlayerUtil.mc.world.playerEntities.stream().filter(p -> PlayerUtil.mc.player.getDistance(p) <= range).filter(p -> PlayerUtil.mc.player.getEntityId() != p.getEntityId()).min(Comparator.comparing(p -> PlayerUtil.mc.player.getDistance(p))).orElse(null);
    }
    
    public static EntityPlayer getLookingPlayer(final double range) {
        final List<EntityPlayer> players = new ArrayList<>(PlayerUtil.mc.world.playerEntities);
        for (int i = 0; i < players.size(); ++i) {
            if (getDistance(players.get(i)) > range) {
                players.remove(i);
            }
        }
        players.remove(PlayerUtil.mc.player);
        EntityPlayer target = null;
        final Vec3d positionEyes = PlayerUtil.mc.player.getPositionEyes(PlayerUtil.mc.getRenderPartialTicks());
        final Vec3d rotationEyes = PlayerUtil.mc.player.getLook(PlayerUtil.mc.getRenderPartialTicks());
        final int precision = 2;
        for (int j = 0; j < (int)range; ++j) {
            for (int k = precision; k > 0; --k) {
                for (final EntityPlayer targetTemp : players) {
                    final AxisAlignedBB playerBox = targetTemp.getEntityBoundingBox();
                    final double xArray = positionEyes.x + rotationEyes.x * j + rotationEyes.x / k;
                    final double yArray = positionEyes.y + rotationEyes.y * j + rotationEyes.y / k;
                    final double zArray = positionEyes.z + rotationEyes.z * j + rotationEyes.z / k;
                    if (playerBox.maxY >= yArray && playerBox.minY <= yArray && playerBox.maxX >= xArray && playerBox.minX <= xArray && playerBox.maxZ >= zArray && playerBox.minZ <= zArray) {
                        target = targetTemp;
                    }
                }
            }
        }
        return target;
    }

    public static double getDistance(final Entity entity) {
        if (entity == null || mc == null || mc.player == null) return Double.MAX_VALUE;
        return mc.player.getDistance(entity);
    }

    public static double getDistance(final BlockPos pos) {
        if (pos == null || mc == null || mc.player == null) return Double.MAX_VALUE;
        return mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ());
    }

    public static double getDistanceI(final BlockPos pos) {
        if (pos == null || mc == null || mc.player == null) return Double.MAX_VALUE;
        return getPlayerPos(PlayerUtil.mc.player).getDistance(pos.getX(), pos.getY(), pos.getZ());
    }

    
    public static BlockPos min(final List<BlockPos> posList) {
        return posList.stream().min(Comparator.comparing(b -> getDistance(b))).orElse(null);
    }
    
    public static Vec3d[] getOffsets(final int y, final boolean floor) {
        final List<Vec3d> offsets = getOffsetList(y, floor);
        final Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }
    
    public static List<Vec3d> getOffsetList(final int y, final boolean floor) {
        final ArrayList<Vec3d> offsets = new ArrayList<>();
        offsets.add(new Vec3d(-1.0, y, 0.0));
        offsets.add(new Vec3d(1.0, y, 0.0));
        offsets.add(new Vec3d(0.0, y, -1.0));
        offsets.add(new Vec3d(0.0, y, 1.0));
        if (floor) {
            offsets.add(new Vec3d(0.0, y - 1, 0.0));
        }
        return offsets;
    }
    
    public static boolean isMoving(final EntityLivingBase entity) {
        return entity.moveForward != 0.0f || entity.moveStrafing != 0.0f;
    }
    
    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (PlayerUtil.mc.player != null && PlayerUtil.mc.player.isPotionActive(Potion.getPotionById(1))) {
            final int amplifier = PlayerUtil.mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }
    
    public static double[] forward(final double speed) {
        float forward = PlayerUtil.mc.player.movementInput.moveForward;
        float side = PlayerUtil.mc.player.movementInput.moveStrafe;
        float yaw = PlayerUtil.mc.player.prevRotationYaw + (PlayerUtil.mc.player.rotationYaw - PlayerUtil.mc.player.prevRotationYaw) * PlayerUtil.mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            }
            else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            }
            else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[] { posX, posZ };
    }
    
    public static double[] forward(final EntityPlayer player, final double speed) {
        float forward = player.moveForward;
        float side = player.moveStrafing;
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * PlayerUtil.mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            }
            else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            }
            else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[] { posX, posZ };
    }
    
    public static EnumFacing getLookingFacing() {
        switch (MathHelper.floor(PlayerUtil.mc.player.rotationYaw * 8.0f / 360.0f + 0.5) & 0x7) {
            case 0:
            case 1: {
                return EnumFacing.SOUTH;
            }
            case 2:
            case 3: {
                return EnumFacing.WEST;
            }
            case 4:
            case 5: {
                return EnumFacing.NORTH;
            }
            case 6:
            case 7: {
                return EnumFacing.EAST;
            }
            default: {
                CandyPlusRewrite.Log(Level.ERROR, "Invalid Rotation");
                return EnumFacing.EAST;
            }
        }
    }
    
    public static boolean isInsideBlock() {
        try {
            final AxisAlignedBB playerBoundingBox = PlayerUtil.mc.player.getEntityBoundingBox();
            for (int x = MathHelper.floor(playerBoundingBox.minX); x < MathHelper.floor(playerBoundingBox.maxX) + 1; ++x) {
                for (int y = MathHelper.floor(playerBoundingBox.minY); y < MathHelper.floor(playerBoundingBox.maxY) + 1; ++y) {
                    for (int z = MathHelper.floor(playerBoundingBox.minZ); z < MathHelper.floor(playerBoundingBox.maxZ) + 1; ++z) {
                        final Block block = PlayerUtil.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                        if (!(block instanceof BlockAir)) {
                            AxisAlignedBB boundingBox = block.getCollisionBoundingBox(PlayerUtil.mc.world.getBlockState(new BlockPos(x, y, z)), PlayerUtil.mc.world, new BlockPos(x, y, z)).offset(x, y, z);
                            if (block instanceof BlockHopper) {
                                boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                            }
                            if (playerBoundingBox.intersects(boundingBox)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            return false;
        }
        return false;
    }
    
    public static void setPosition(final double x, final double y, final double z) {
        PlayerUtil.mc.player.setPosition(x, y, z);
    }
    
    public static void setPosition(final BlockPos pos) {
        PlayerUtil.mc.player.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }
    
    public static Vec3d getMotionVector() {
        return new Vec3d(PlayerUtil.mc.player.motionX, PlayerUtil.mc.player.motionY, PlayerUtil.mc.player.motionZ);
    }
    
    public static void vClip(final double d) {
        PlayerUtil.mc.player.setPosition(PlayerUtil.mc.player.posX, PlayerUtil.mc.player.posY + d, PlayerUtil.mc.player.posZ);
    }
    
    public static void move(final double x, final double y, final double z) {
        PlayerUtil.mc.player.move(MoverType.SELF, x, y, z);
    }
    
    public static void setMotionVector(final Vec3d vec) {
        PlayerUtil.mc.player.motionX = vec.x;
        PlayerUtil.mc.player.motionY = vec.y;
        PlayerUtil.mc.player.motionZ = vec.z;
    }
}
