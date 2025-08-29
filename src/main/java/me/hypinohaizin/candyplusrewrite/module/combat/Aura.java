package me.hypinohaizin.candyplusrewrite.module.combat;

import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Aura extends Module {

    public Setting<Boolean> targetPlayer;
    public Setting<Boolean> targetMob;
    public Setting<Boolean> targetPassive;
    public Setting<Boolean> targetNeutral;
    public Setting<Boolean> targetHostile;
    public Setting<Float> range;
    public Setting<Float> wallRange;
    public Setting<Boolean> preferAxe;
    public Setting<Float> enemyHp;
    public Setting<Float> minSelfHp;
    public Setting<Integer> delayMs;
    public Setting<Boolean> autoSwap;
    public Setting<Boolean> swordOnly;
    public Setting<Boolean> criticals;
    public Setting<Integer> critPackets;

    private long lastAttackTime = 0;

    public Aura() {
        super("Aura", Categories.COMBAT, false, false);

        targetPlayer = register(new Setting<>("Player", true));
        targetMob = register(new Setting<>("Mob", false));
        targetPassive = register(new Setting<>("Passive", false));
        targetNeutral = register(new Setting<>("Neutral", false));
        targetHostile = register(new Setting<>("Hostile", false));
        range = register(new Setting<>("Range", 4.5f, 10f, 1.0f));
        wallRange = register(new Setting<>("WallRange", 3.0f, 6f, 1.0f));
        preferAxe = register(new Setting<>("PreferAxe", true));
        enemyHp = register(new Setting<>("EnemyHP", 6.0f, 20f, 0.0f));
        minSelfHp = register(new Setting<>("MinSelfHP", 6.0f, 20f, 0.0f));
        delayMs = register(new Setting<>("DelayMs", 50, 1000, 0));
        autoSwap = register(new Setting<>("AutoSwap", true));
        swordOnly = register(new Setting<>("SwordOnly", true));
        criticals = register(new Setting<>("Criticals", true));
        critPackets = register(new Setting<>("CritPackets", 2, 4, 1));
    }

    @Override
    public void onEnable() {
        if (autoSwap.getValue()) {
            int swordSlot = findWeaponSlot(ItemSword.class);
            if (swordSlot != -1) {
                mc.player.inventory.currentItem = swordSlot;
                mc.playerController.updateController();
            }
        }
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.getHealth() <= minSelfHp.getValue()) return;
        if (swordOnly.getValue() && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) return;

        List<EntityLivingBase> possibleTargets = new ArrayList<>();
        EntityLivingBase lowHpTarget = null;

        List<EntityLivingBase> entities = mc.world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                new AxisAlignedBB(
                        mc.player.posX - range.getValue(),
                        mc.player.posY - range.getValue(),
                        mc.player.posZ - range.getValue(),
                        mc.player.posX + range.getValue(),
                        mc.player.posY + range.getValue(),
                        mc.player.posZ + range.getValue()
                )
        );

        for (EntityLivingBase entity : entities) {
            if (entity == mc.player) continue;
            if (!isValidTarget(entity)) continue;

            double distSq = mc.player.getDistanceSq(entity);
            if (distSq > range.getValue() * range.getValue()) continue;
            if (!canSeeEntity(entity) && distSq > wallRange.getValue() * wallRange.getValue()) continue;

            possibleTargets.add(entity);

            if (entity.getHealth() < enemyHp.getValue()) {
                lowHpTarget = entity;
                break;
            }
        }

        EntityLivingBase target;
        if (lowHpTarget != null) {
            target = lowHpTarget;
        } else if (!possibleTargets.isEmpty()) {
            possibleTargets.sort(Comparator.comparingDouble(mc.player::getDistanceSq));
            target = possibleTargets.get(0);
        } else {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime < delayMs.getValue()) return;

        // --- Criticals処理追加 ---
        if (criticals.getValue()
                && mc.player.onGround
                && !mc.player.isInWater()
                && !mc.player.isInLava()) {
            sendCriticalPackets(critPackets.getValue());
        }

        mc.playerController.attackEntity(mc.player, target);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        lastAttackTime = currentTime;
    }

    private void sendCriticalPackets(int mode) {
        switch (mode) {
            case 1:
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1f, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                break;
            case 2:
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625101, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.1E-5, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                break;
            case 3:
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625101, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0125, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                break;
            case 4:
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1625, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 4.0E-6, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0E-6, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                break;
        }
    }

    private boolean isValidTarget(EntityLivingBase entity) {
        if (entity == null || entity.isDead || !entity.isEntityAlive()) return false;

        if (entity instanceof EntityPlayer) {
            if (!targetPlayer.getValue()) return false;
            if (entity == mc.player) return false;
            return !FriendManager.isFriend(entity.getName());
        } else {
            return targetMob.getValue();
        }
    }

    private boolean canSeeEntity(EntityLivingBase entity) {
        Vec3d eyesPos = mc.player.getPositionEyes(1.0F);
        Vec3d entityPos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        RayTraceResult result = mc.world.rayTraceBlocks(eyesPos, entityPos, false, true, false);
        return result == null || result.typeOfHit == Type.MISS;
    }

    private int findWeaponSlot(Class<?> weaponClass) {
        for (int i = 0; i < 9; i++) {
            if (weaponClass.isAssignableFrom(mc.player.inventory.getStackInSlot(i).getItem().getClass())) {
                return i;
            }
        }
        return -1;
    }
}
