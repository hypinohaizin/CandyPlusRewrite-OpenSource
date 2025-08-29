package me.hypinohaizin.candyplusrewrite.managers;

import net.minecraft.entity.Entity;
import me.hypinohaizin.candyplusrewrite.utils.MathUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;

public class RotateManager extends Manager
{
    private float yaw;
    private float pitch;
    
    public void updateRotations() {
        yaw = RotateManager.mc.player.rotationYaw;
        pitch = RotateManager.mc.player.rotationPitch;
    }
    
    public void restoreRotations() {
        RotateManager.mc.player.rotationYaw = yaw;
        RotateManager.mc.player.rotationYawHead = yaw;
        RotateManager.mc.player.rotationPitch = pitch;
    }
    
    public void setPlayerRotations(final float yaw, final float pitch) {
        RotateManager.mc.player.rotationYaw = yaw;
        RotateManager.mc.player.rotationYawHead = yaw;
        RotateManager.mc.player.rotationPitch = pitch;
    }
    
    public void setPlayerYaw(final float yaw) {
        RotateManager.mc.player.rotationYaw = yaw;
        RotateManager.mc.player.rotationYawHead = yaw;
    }
    
    public void lookAtPos(final BlockPos pos) {
        final float[] angle = MathUtil.calcAngle(RotateManager.mc.player.getPositionEyes(RotateManager.mc.getRenderPartialTicks()), new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));
        setPlayerRotations(angle[0], angle[1]);
    }
    
    public void lookAtVec3d(final Vec3d vec3d) {
        final float[] angle = MathUtil.calcAngle(RotateManager.mc.player.getPositionEyes(RotateManager.mc.getRenderPartialTicks()), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
        setPlayerRotations(angle[0], angle[1]);
    }
    
    public void lookAtVec3d(final double x, final double y, final double z) {
        final Vec3d vec3d = new Vec3d(x, y, z);
        lookAtVec3d(vec3d);
    }
    
    public void lookAtEntity(final Entity entity) {
        final float[] angle = MathUtil.calcAngle(RotateManager.mc.player.getPositionEyes(RotateManager.mc.getRenderPartialTicks()), entity.getPositionEyes(RotateManager.mc.getRenderPartialTicks()));
        setPlayerRotations(angle[0], angle[1]);
    }
    
    public void setPlayerPitch(final float pitch) {
        RotateManager.mc.player.rotationPitch = pitch;
    }
    
    public float getYaw() {
        return yaw;
    }
    
    public void setYaw(final float yaw) {
        this.yaw = yaw;
    }
    
    public float getPitch() {
        return pitch;
    }
    
    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }
}
