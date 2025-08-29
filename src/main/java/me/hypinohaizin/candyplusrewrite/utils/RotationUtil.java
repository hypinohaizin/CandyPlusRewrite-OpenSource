package me.hypinohaizin.candyplusrewrite.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;

// Sponsored by KAMI Blue
// https://github.com/kami-blue/client/blob/master/src/main/kotlin/org/kamiblue/client/util/math/RotationUtils.kt
public class RotationUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Get rotation from player position to the closest hit vector in a `AxisAlignedBB`
     *
     * @param box Calculate rotation to this AABB
     */
    public static Vec2f getRotationTo(AxisAlignedBB box) {
        EntityPlayerSP player = mc.player;
        if (player == null) {
            return Vec2f.ZERO;
        }

        Vec3d eyePos = player.getPositionEyes(1.0f);

        if (player.getEntityBoundingBox().intersects(box)) {
            return getRotationTo(eyePos, box.getCenter());
        }

        double x = MathHelper.clamp(eyePos.x, box.minX, box.maxX);
        double y = MathHelper.clamp(eyePos.y, box.minY, box.maxY);
        double z = MathHelper.clamp(eyePos.z, box.minZ, box.maxZ);

        return getRotationTo(eyePos, new Vec3d(x, y, z));
    }

    /**
     * Get rotation from player position to another position vector
     *
     * @param posTo Calculate rotation to this position vector
     */
    public static Vec2f getRotationTo(Vec3d posTo) {
        EntityPlayerSP player = mc.player;
        return player != null ? getRotationTo(player.getPositionEyes(1.0f), posTo) : Vec2f.ZERO;
    }

    /**
     * Get rotation from a position vector to another position vector
     *
     * @param posFrom Calculate rotation from this position vector
     * @param posTo   Calculate rotation to this position vector
     */
    public static Vec2f getRotationTo(Vec3d posFrom, Vec3d posTo) {
        return getRotationFromVec(posTo.subtract(posFrom));
    }

    /**
     * Get rotation from a vector
     *
     * @param vec Calculate rotation from this vector
     */
    public static Vec2f getRotationFromVec(Vec3d vec) {
        double lengthXZ = Math.hypot(vec.x, vec.z);
        double yaw = normalizeAngle(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
        double pitch = normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, lengthXZ)));

        return new Vec2f((float) yaw, (float) pitch);
    }

    public static double normalizeAngle(double angle) {
        angle %= 360.0;

        if (angle >= 180.0) {
            angle -= 360.0;
        }

        if (angle < -180.0) {
            angle += 360.0;
        }

        return angle;
    }

    public static float normalizeAngle(float angle) {
        angle %= 360.0f;

        if (angle >= 180.0f) {
            angle -= 360.0f;
        }

        if (angle < -180.0f) {
            angle += 360.0f;
        }

        return angle;
    }

    public static boolean isInFov(BlockPos pos) {
        return pos != null && (mc.player.getDistanceSq(pos) < 4.0 || yawDist(pos) < (double)(getHalvedfov() + 2.0f));
    }

    public static boolean isInFov(Entity entity) {
        return entity != null && (mc.player.getDistanceSq(entity) < 4.0 || yawDist(entity) < (double)(getHalvedfov() + 2.0f));
    }

    public static double yawDist(BlockPos pos) {
        if (pos != null) {
            Vec3d difference = new Vec3d((Vec3i)pos).subtract(mc.player.getPositionEyes(mc.getRenderPartialTicks()));
            double d = Math.abs((double)mc.player.rotationYaw - (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0)) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static double yawDist(Entity e) {
        if (e != null) {
            Vec3d difference = e.getPositionVector().add(0.0, (double)(e.getEyeHeight() / 2.0f), 0.0).subtract(mc.player.getPositionEyes(mc.getRenderPartialTicks()));
            double d = Math.abs((double)mc.player.rotationYaw - (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0)) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static float transformYaw() {
        float yaw = mc.player.rotationYaw % 360.0f;
        if (mc.player.rotationYaw > 0.0f) {
            if (yaw > 180.0f) {
                yaw = -180.0f + (yaw - 180.0f);
            }
        } else if (yaw < -180.0f) {
            yaw = 180.0f + (yaw + 180.0f);
        }
        if (yaw < 0.0f) {
            return 180.0f + yaw;
        }
        return -180.0f + yaw;
    }

    public static boolean isInFov(Vec3d vec3d, Vec3d other) {
        if (mc.player.rotationPitch > 30.0f ? other.y > mc.player.posY : mc.player.rotationPitch < -30.0f && other.y < mc.player.posY) {
            return true;
        }
        float angle = BlockUtil.calcAngleNoY(vec3d, other)[0] - transformYaw();
        if (angle < -270.0f) {
            return true;
        }
        float fov = mc.gameSettings.fovSetting / 2.0f;
        return angle < fov + 10.0f && angle > -fov - 10.0f;
    }

    public static float getFov() {
        return mc.gameSettings.fovSetting;
    }

    public static float getHalvedfov() {
        return getFov() / 2.0f;
    }

}