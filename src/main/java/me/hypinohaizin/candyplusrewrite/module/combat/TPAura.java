package me.hypinohaizin.candyplusrewrite.module.combat;

import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;

public class TPAura extends Module {

    private Setting<Float> maxRange;
    private Setting<Boolean> onlyPlayer;
    private Setting<Boolean> strafe;
    private Setting<Boolean> look;
    private Setting<Boolean> motionTp;
    private Setting<Float> attackDelay;
    private Setting<Boolean> swing;
    private Setting<Boolean> onGround;
    private Setting<Integer> strafeKey;

    private Setting<Boolean> lockOrigin;
    private Setting<Integer> forceReturnKey;

    private final Timer timer = new Timer();

    private boolean hasOrigin = false;
    private double originX = 0.0;
    private double originY = 0.0;
    private double originZ = 0.0;

    public TPAura() {
        super("TPAura", Categories.COMBAT, false, false);
        maxRange = register(new Setting<>("MaxRange", 10.0f, 50.0f, 2.0f));
        onlyPlayer = register(new Setting<>("OnlyPlayer", true));
        strafe = register(new Setting<>("Strafe", false));
        look = register(new Setting<>("LookRotate", true));
        motionTp = register(new Setting<>("MotionTp", false));
        attackDelay = register(new Setting<>("Delay", 500f, 2000f, 0f));
        swing = register(new Setting<>("Swing", true));
        onGround = register(new Setting<>("OnGround", false));
        strafeKey = register(new Setting<>("StrafeKey", Keyboard.KEY_NONE, 256, 0));
        lockOrigin = register(new Setting<>("LockOrigin", false));
        forceReturnKey = register(new Setting<>("ForceReturnKey", Keyboard.KEY_NONE, 256, 0));
    }

    @Override
    public void onEnable() {
        if (mc.player != null && lockOrigin.getValue()) {
            originX = mc.player.posX;
            originY = mc.player.posY;
            originZ = mc.player.posZ;
            hasOrigin = true;
        }
    }

    @Override
    public void onDisable() {
        hasOrigin = false;
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        if (!hasOrigin && !lockOrigin.getValue()) {
            originX = mc.player.posX;
            originY = mc.player.posY;
            originZ = mc.player.posZ;
            hasOrigin = true;
        }

        if (safeKeyDown(forceReturnKey.getValue())) {
            if (hasOrigin) tpTo(originX, originY, originZ);
            return;
        }

        Entity target = pickTarget();
        if (target == null) return;
        if (!timer.passedMs(attackDelay.getValue().longValue())) return;

        if (!lockOrigin.getValue()) {
            originX = mc.player.posX;
            originY = mc.player.posY;
            originZ = mc.player.posZ;
            hasOrigin = true;
        }

        if (look.getValue()) rotateTo(target);

        double px = mc.player.posX, py = mc.player.posY, pz = mc.player.posZ;

        tpTo(target.posX, target.posY, target.posZ);

        mc.playerController.attackEntity(mc.player, target);
        if (swing.getValue()) mc.player.swingArm(EnumHand.MAIN_HAND);

        tpTo(px, py, pz);

        if (strafe.getValue() && safeKeyDown(strafeKey.getValue())) {
            strafeAround(target);
        }

        timer.reset();
    }

    private Entity pickTarget() {
        Entity best = null;
        double bestDistSq = Double.MAX_VALUE;
        double rangeSq = maxRange.getValue() * maxRange.getValue();

        if (onlyPlayer.getValue()) {
            for (EntityPlayer p : mc.world.playerEntities) {
                if (p == mc.player || !p.isEntityAlive()) continue;
                if (FriendManager.isFriend(p)) continue;
                double d2 = mc.player.getDistanceSq(p);
                if (d2 > rangeSq) continue;
                if (d2 < bestDistSq) {
                    bestDistSq = d2;
                    best = p;
                }
            }
        } else {
            for (Entity e : mc.world.loadedEntityList) {
                if (e == mc.player || !e.isEntityAlive()) continue;
                if (e instanceof EntityPlayer && FriendManager.isFriend((EntityPlayer) e)) continue;
                double d2 = mc.player.getDistanceSq(e);
                if (d2 > rangeSq) continue;
                if (d2 < bestDistSq) {
                    bestDistSq = d2;
                    best = e;
                }
            }
        }
        return best;
    }

    private void tpTo(double x, double y, double z) {
        EntityPlayerSP p = mc.player;
        if (p == null) return;

        double dx = x - p.posX;
        double dy = y - p.posY;
        double dz = z - p.posZ;

        double distSq = dx * dx + dy * dy + dz * dz;
        if (distSq < 1.0e-8) return;

        double dist = Math.sqrt(distSq);
        double stepLen = motionTp.getValue() ? 0.45 : 1.35;
        int steps = Math.max(1, Math.min(40, (int) Math.ceil(dist / stepLen)));

        double sx = dx / steps;
        double sy = dy / steps;
        double sz = dz / steps;

        for (int i = 0; i < steps; i++) {
            double nx = mc.player.posX + sx;
            double ny = mc.player.posY + sy;
            double nz = mc.player.posZ + sz;
            mc.player.setPosition(nx, ny, nz);
            mc.player.connection.sendPacket(new CPacketPlayer.Position(nx, ny, nz, onGround.getValue()));
        }
    }

    private void rotateTo(Entity t) {
        double dx = t.posX - mc.player.posX;
        double dz = t.posZ - mc.player.posZ;
        double dy = (t.posY + t.getEyeHeight() * 0.5) - (mc.player.posY + mc.player.getEyeHeight());
        double distxz = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) -(Math.atan2(dy, distxz) * 180.0 / Math.PI);
        mc.player.rotationYaw = yaw;
        mc.player.rotationPitch = pitch;
    }

    private void strafeAround(Entity t) {
        double angle = (mc.player.ticksExisted % 360) * Math.PI / 18.0;
        double radius = 8.0;
        double tx = t.posX + Math.cos(angle) * radius;
        double tz = t.posZ + Math.sin(angle) * radius;
        double dx = tx - mc.player.posX;
        double dz = tz - mc.player.posZ;
        double dy = t.posY - mc.player.posY;
        double max = 1.0;
        mc.player.motionX = clamp(dx, -max, max);
        mc.player.motionZ = clamp(dz, -max, max);
        mc.player.motionY = clamp(dy, -1.0, 1.0);
    }

    private double clamp(double v, double min, double max) {
        return v < min ? min : (v > max ? max : v);
    }

    private boolean safeKeyDown(int key) {
        if (!Keyboard.isCreated()) return false;
        if (key < 0 || key >= 256) return false;
        try { return Keyboard.isKeyDown(key); } catch (Throwable t) { return false; }
    }
}
