package me.hypinohaizin.candyplusrewrite.utils;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Random;

public class MathUtil implements Util
{
    public static Random rnd;
    
    public static int getRandom(final int min, final int max) {
        return MathUtil.rnd.nextInt(max - min + 1) + min;
    }
    
    public static ArrayList moveItemToFirst(final ArrayList list, final int index) {
        final ArrayList newlist = new ArrayList();
        newlist.add(list.get(index));
        for (int i = 0; i < list.size(); ++i) {
            if (i != index) {
                newlist.add(list.get(index));
            }
        }
        return new ArrayList(list);
    }
    
    public static float[] calcAngle(final Vec3d from, final Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0;
        final double difZ = to.z - from.z;
        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))) };
    }
    
    public static float square(final float v1) {
        return v1 * v1;
    }
    
    static {
        MathUtil.rnd = new Random();
    }

    public static double[] directionSpeed(double speed) {
        float forward = MathUtil.mc.player.movementInput.moveForward;
        float side = MathUtil.mc.player.movementInput.moveStrafe;
        float yaw = MathUtil.mc.player.prevRotationYaw + (MathUtil.mc.player.rotationYaw - MathUtil.mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += (float)(forward > 0.0f ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += (float)(forward > 0.0f ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double posX = (double)forward * speed * cos + (double)side * speed * sin;
        double posZ = (double)forward * speed * sin - (double)side * speed * cos;
        return new double[]{posX, posZ};
    }
}
