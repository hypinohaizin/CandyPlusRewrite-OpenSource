package me.hypinohaizin.candyplusrewrite.module.render;

import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Freecam extends Module {

    public Setting<Modes> mode;
    public Setting<Float> speed;
    public Setting<Boolean> noClip;
    public Setting<Boolean> freeze;

    private Entity riding;
    private EntityOtherPlayerMP camera;
    private Vec3d position;
    private float yaw;
    private float pitch;
    private boolean wasFlying;
    private float oldFlySpeed;

    public Freecam() {
        super("FreeCam", Categories.RENDER, false, false);
        mode = register(new Setting<>("Mode", Modes.Camera));
        speed = register(new Setting<>("Speed", 1.0f, 5.0f, 0.1f));
        noClip = register(new Setting<>("NoClip", true));
        freeze = register(new Setting<>("Freeze", false));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (nullCheck()) {
            this.disable();
            return;
        }

        position = mc.player.getPositionVector();
        yaw = mc.player.rotationYaw;
        pitch = mc.player.rotationPitch;
        wasFlying = mc.player.capabilities.isFlying;
        oldFlySpeed = mc.player.capabilities.getFlySpeed();

        if (mode.getValue() == Modes.Normal) {
            setupNormalMode();
        } else {
            setupCameraMode();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (nullCheck()) {
            return;
        }

        if (mode.getValue() == Modes.Normal) {
            cleanupNormalMode();
        } else {
            cleanupCameraMode();
        }

        if (position != null) {
            mc.player.setPosition(position.x, position.y, position.z);
            mc.player.rotationYaw = yaw;
            mc.player.rotationPitch = pitch;
            mc.player.prevRotationYaw = yaw;
            mc.player.prevRotationPitch = pitch;
        }

        mc.player.capabilities.isFlying = wasFlying;
        mc.player.capabilities.setFlySpeed(oldFlySpeed);
        mc.player.capabilities.allowFlying = mc.player.capabilities.isCreativeMode;

        position = null;
        camera = null;
        riding = null;
    }

    private void setupNormalMode() {
        riding = null;
        if (mc.player.getRidingEntity() != null) {
            riding = mc.player.getRidingEntity();
            mc.player.dismountRidingEntity();
        }

        camera = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
        camera.copyLocationAndAnglesFrom(mc.player);
        camera.prevRotationYaw = mc.player.rotationYaw;
        camera.rotationYawHead = mc.player.rotationYawHead;
        camera.inventory.copyInventory(mc.player.inventory);
        mc.world.addEntityToWorld(-69, camera);

        if (noClip.getValue()) {
            mc.player.noClip = true;
        }

        mc.player.capabilities.isFlying = true;
        mc.player.capabilities.allowFlying = true;
        mc.player.capabilities.setFlySpeed(speed.getValue() / 10.0f);
    }

    private void setupCameraMode() {
        camera = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
        camera.copyLocationAndAnglesFrom(mc.player);
        camera.prevRotationYaw = mc.player.rotationYaw;
        camera.rotationYawHead = mc.player.rotationYawHead;
        camera.inventory.copyInventory(mc.player.inventory);

        if (noClip.getValue()) {
            camera.noClip = true;
        }

        mc.world.addEntityToWorld(-69, camera);
        mc.setRenderViewEntity(camera);
    }

    private void cleanupNormalMode() {
        if (camera != null) {
            mc.world.removeEntityFromWorld(-69);
        }

        if (riding != null) {
            mc.player.startRiding(riding);
        }

        mc.player.noClip = false;
    }

    private void cleanupCameraMode() {
        if (camera != null) {
            mc.world.removeEntityFromWorld(-69);
        }

        mc.setRenderViewEntity(mc.player);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent event) {
        if (nullCheck()) {
            return;
        }

        if (mode.getValue() == Modes.Normal) {
            updateNormalMode();
        } else {
            updateCameraMode();
        }
    }

    private void updateNormalMode() {
        mc.player.capabilities.setFlySpeed(speed.getValue() / 10.0f);

        if (camera != null && position != null) {
            camera.setPosition(position.x, position.y, position.z);
            camera.rotationYaw = yaw;
            camera.rotationPitch = pitch;
            camera.prevRotationYaw = yaw;
            camera.prevRotationPitch = pitch;
        }
    }

    private void updateCameraMode() {
        if (camera == null) return;

        handleCameraMovement();

        float forward = 0;
        float strafe = 0;
        float vertical = 0;

        if (mc.gameSettings.keyBindForward.isKeyDown()) forward++;
        if (mc.gameSettings.keyBindBack.isKeyDown()) forward--;
        if (mc.gameSettings.keyBindLeft.isKeyDown()) strafe++;
        if (mc.gameSettings.keyBindRight.isKeyDown()) strafe--;
        if (mc.gameSettings.keyBindJump.isKeyDown()) vertical++;
        if (mc.gameSettings.keyBindSneak.isKeyDown()) vertical--;

        if (forward != 0 || strafe != 0 || vertical != 0) {
            double moveSpeed = speed.getValue() * 0.1;

            double yawRad = Math.toRadians(camera.rotationYaw);
            double motionX = (strafe * Math.cos(yawRad) - forward * Math.sin(yawRad)) * moveSpeed;
            double motionZ = (forward * Math.cos(yawRad) + strafe * Math.sin(yawRad)) * moveSpeed;
            double motionY = vertical * moveSpeed;

            camera.setPosition(
                    camera.posX + motionX,
                    camera.posY + motionY,
                    camera.posZ + motionZ
            );
        }
    }

    private void handleCameraMovement() {
        if (camera == null) return;

        float mouseSensitivity = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float f = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0f;

        if (!freeze.getValue()) {
            camera.rotationYaw = mc.player.rotationYaw;
            camera.rotationPitch = mc.player.rotationPitch;
            camera.prevRotationYaw = mc.player.prevRotationYaw;
            camera.prevRotationPitch = mc.player.prevRotationPitch;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent event) {
        if (nullCheck()) {
            return;
        }

        if (freeze.getValue() && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();
            if (position != null) {
                if (packet instanceof CPacketPlayer.Position || packet instanceof CPacketPlayer.PositionRotation) {
                    packet.x = position.x;
                    packet.y = position.y;
                    packet.z = position.z;
                }
                if (packet instanceof CPacketPlayer.Rotation || packet instanceof CPacketPlayer.PositionRotation) {
                    packet.yaw = yaw;
                    packet.pitch = pitch;
                }
            }
        }
    }

    public enum Modes {
        Normal,
        Camera
    }
}