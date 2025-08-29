package me.hypinohaizin.candyplusrewrite.module.movement;

import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import me.hypinohaizin.candyplusrewrite.utils.MathUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

public class Flight extends Module {

    public Setting<FlyMode> mode;
    public Setting<Float> speed;
    public Setting<Boolean> viewBob;
    public Setting<Float> bobAmount;
    public Setting<Boolean> damageBoost;
    public Setting<Integer> jpSpeed;
    public Setting<Integer> teleportTime;

    private BlockPos startPos;
    private double startY;
    private boolean onDamage = false;
    private Timer timer = new Timer();

    public Flight() {
        super("Flight", Categories.MOVEMENT, false, false);
        mode = register(new Setting<>("Mode", FlyMode.CREATIVE));
        speed = register(new Setting<>("Speed", 0.1f, 5.0f, 0.0f, v -> mode.getValue() == FlyMode.CREATIVE));
        viewBob = register(new Setting<>("ViewBob", false, v -> mode.getValue() == FlyMode.CREATIVE));
        bobAmount = register(new Setting<>("BobAmount", 0.1f, 2.0f, 0.0f, v -> viewBob.getValue() && mode.getValue() == FlyMode.CREATIVE));
        damageBoost = register(new Setting<>("DamageBoost", false, v -> mode.getValue() == FlyMode.CREATIVE));
        jpSpeed = register(new Setting<>("TeleportAmount", 5, 20, 0, v -> mode.getValue() == FlyMode._2B2TJB));
        teleportTime = register(new Setting<>("TeleportTime", 750, 5000, 0, v -> mode.getValue() == FlyMode._2B2TJB));
    }

    @Override
    public void onEnable() {
        if (mc.player == null) return;

        super.onEnable();
        startPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        startY = mc.player.posY;
        onDamage = false;
        if (mode.getValue() == FlyMode.CREATIVE && damageBoost.getValue()) {
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, 3.0001, 0)).isEmpty()) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 3.0001, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
            }
            mc.player.setPosition(mc.player.posX, mc.player.posY + 0.42, mc.player.posZ);
            onDamage = true;
        }
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        switch (mode.getValue()) {
            case CREATIVE:
                if (damageBoost.getValue() && !onDamage) break;
                mc.player.setVelocity(0,0,0);
                mc.player.jumpMovementFactor = speed.getValue();
                double[] mv = MathUtil.directionSpeed(speed.getValue());
                if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                    if (viewBob.getValue()) mc.player.cameraYaw = bobAmount.getValue() / 10;
                    mc.player.motionX = mv[0];
                    mc.player.motionZ = mv[1];
                }
                if (mc.gameSettings.keyBindJump.isKeyDown()) mc.player.motionY += speed.getValue();
                if (mc.gameSettings.keyBindSneak.isKeyDown()) mc.player.motionY -= speed.getValue();
                break;
            case JUMP:
                if (mc.player.posY < startY) mc.player.jump();
                break;
            case _2B2TJB:
                double yaw = Math.cos(Math.toRadians(mc.player.rotationYaw + 90));
                double pitch = Math.sin(Math.toRadians(mc.player.rotationYaw + 90));
                mc.player.setPosition(mc.player.posX + jpSpeed.getValue() * yaw,
                        mc.player.posY, mc.player.posZ + jpSpeed.getValue() * pitch);
                if (timer.passedMs(teleportTime.getValue())) {
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 30, mc.player.posZ);
                    timer.reset();
                }
                mc.player.motionY = 0;
                break;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.motionX = 0;
        mc.player.motionY = 0;
        mc.player.motionZ = 0;
    }


    public enum FlyMode {
        CREATIVE,
        JUMP,
        _2B2TJB
    }
}
