package me.hypinohaizin.candyplusrewrite.module.combat;

import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnObject;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;
import net.minecraft.entity.item.EntityEnderCrystal;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import me.hypinohaizin.candyplusrewrite.utils.CrystalUtil;
import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import net.minecraft.init.Items;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil3D;
import net.minecraft.util.EnumHand;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import net.minecraft.entity.player.EntityPlayer;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;
import java.awt.Color;

public class CrystalAura extends Module {
    public Setting<Boolean> place;
    public Setting<Float> placeDelay;
    public Setting<Float> placeRange;
    public Setting<Float> wallRangePlace;
    public Setting<Boolean> placeSwing;
    public Setting<Boolean> autoSwitch;
    public Setting<Boolean> silentSwitch;
    public Setting<Boolean> opPlace;
    public Setting<Boolean> explode;
    public Setting<Boolean> predict;
    public Setting<Float> explodeDelay;
    public Setting<Float> breakRange;
    public Setting<Float> wallRangeBreak;
    public Setting<Boolean> packetBreak;
    public Setting<swingArm> swing;
    public Setting<Boolean> packetSwing;
    public Setting<Boolean> predictHit;
    public Setting<Integer> amount;
    public Setting<Integer> amountOffset;
    public Setting<Boolean> checkOtherEntity;
    public Setting<Boolean> ignoreSelfDmg;
    public Setting<Float> maxSelf;
    public Setting<Float> minDmg;
    public Setting<Boolean> smartMode;
    public Setting<Float> dmgError;
    public Setting<Boolean> antiSuicide;
    public Setting<Float> pauseHealth;
    public Setting<Boolean> betterFps;
    public Setting<Color> color;

    public Setting<Integer> maxCalcPerTick;
    public Setting<Integer> multiPlace;
    public Setting<Integer> multiBreak;
    public Setting<Float> facePlaceHealth;
    public Setting<Boolean> onlyOffhandOrMain;
    public Setting<Boolean> strictLine;
    public Setting<Integer> cacheMs;

    public EntityPlayer target;
    public int lastEntityID;
    public Timer placeTimer;
    public Timer breakTimer;
    private EnumHand oldhand;
    private int oldslot;
    public BlockPos renderPos = null;

    private final List<CrystalPos> candBuf = new ArrayList<>(128);
    private final List<BlockPos> cachedPlacePos = new ArrayList<>(256);
    private long cacheAt = 0L;
    private double cachePx, cachePy, cachePz;

    public CrystalAura() {
        super("CrystalAura", Categories.COMBAT, false, false);
        place = register(new Setting<>("Place", true));
        placeDelay = register(new Setting<>("PlaceDelay", 6.0f, 16.0f, 0.0f, v -> place.getValue()));
        placeRange = register(new Setting<>("PlaceRange", 7.0f, 16.0f, 1.0f, v -> place.getValue()));
        wallRangePlace = register(new Setting<>("WallRangePlace", 4.0f, 16.0f, 1.0f, v -> place.getValue()));
        placeSwing = register(new Setting<>("Swing", false));
        autoSwitch = register(new Setting<>("Switch", true));
        silentSwitch = register(new Setting<>("SilentSwitch", false, v -> autoSwitch.getValue()));
        opPlace = register(new Setting<>("1.13", false, v -> place.getValue()));
        explode = register(new Setting<>("Explode", true));
        predict = register(new Setting<>("Predict", true));
        explodeDelay = register(new Setting<>("ExplodeDelay", 6.0f, 16.0f, 0.0f, v -> explode.getValue()));
        breakRange = register(new Setting<>("ExplodeRange", 6.0f, 16.0f, 1.0f, v -> explode.getValue()));
        wallRangeBreak = register(new Setting<>("WallRangeBreak", 3.0f, 16.0f, 1.0f, v -> explode.getValue()));
        packetBreak = register(new Setting<>("PacketBreak", true));
        swing = register(new Setting<>("SwingArm", swingArm.Mainhand));
        packetSwing = register(new Setting<>("PacketSwing", true, v -> swing.getValue() != swingArm.None));
        predictHit = register(new Setting<>("PredictHit", false));
        amount = register(new Setting<>("Amount", 1, 15, 1, v -> predictHit.getValue()));
        amountOffset = register(new Setting<>("Offset", 1, 10, 0, v -> predictHit.getValue()));
        checkOtherEntity = register(new Setting<>("OtherEntity", false, v -> predictHit.getValue()));
        ignoreSelfDmg = register(new Setting<>("IgnoreSelfDamage", false));
        maxSelf = register(new Setting<>("MaxSelfDamage", 5.0f, 36.0f, 0.0f, v -> !ignoreSelfDmg.getValue()));
        minDmg = register(new Setting<>("MinDamage", 3.0f, 36.0f, 0.0f));
        smartMode = register(new Setting<>("SmartMode", true));
        dmgError = register(new Setting<>("DamageError", 3.0f, 15.0f, 1.0f, v -> smartMode.getValue()));
        antiSuicide = register(new Setting<>("AntiSuicide", true));
        pauseHealth = register(new Setting<>("RequireHealth", 3.0f, 36.0f, 0.0f));
        betterFps = register(new Setting<>("BetterFps", true));
        color = register(new Setting<>("Color", new Color(230, 50, 50, 100)));

        maxCalcPerTick = register(new Setting<>("MaxCalcPerTick", 64, 512, 8));
        multiPlace = register(new Setting<>("MultiPlace", 1, 4, 1, v -> place.getValue()));
        multiBreak = register(new Setting<>("MultiBreak", 1, 4, 1, v -> explode.getValue()));
        facePlaceHealth = register(new Setting<>("FacePlaceHP", 8.0f, 20.0f, 0.0f));
        onlyOffhandOrMain = register(new Setting<>("OnlyCrystal", false));
        strictLine = register(new Setting<>("StrictLine", false));
        cacheMs = register(new Setting<>("CacheMs", 100, 1000, 0));

        lastEntityID = -1;
        placeTimer = new Timer();
        breakTimer = new Timer();
        oldhand = null;
        oldslot = -1;
    }

    @Override
    public void onTick() {
        doCrystalAura();
    }

    public void doCrystalAura() {
        try {
            if (nullCheck()) return;
            target = nearestEnemy(35.0);
            if (target == null) { renderPos = null; return; }
            if (pauseHealth.getValue() > mc.player.getHealth()) { renderPos = null; return; }
            if (place.getValue()) doPlaceFast();
            if (explode.getValue()) doBreakFast();
        } catch (Exception ignored) {}
    }

    private boolean ensureHoldingCrystal() {
        if (!autoSwitch.getValue() && onlyOffhandOrMain.getValue()) {
            if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) return false;
        }
        return true;
    }

    public void doPlaceFast() {
        if (!placeTimer.passedDms(placeDelay.getValue())) return;
        if (!ensureHoldingCrystal()) return;
        placeTimer.reset();

        EnumHand hand;
        if (autoSwitch.getValue()) {
            if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
                hand = EnumHand.OFF_HAND;
            } else {
                int crystalSlot = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
                if (crystalSlot == -1) { renderPos = null; return; }
                setItem(crystalSlot);
                hand = EnumHand.MAIN_HAND;
            }
        } else if (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            hand = EnumHand.MAIN_HAND;
        } else {
            if (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) { renderPos = null; return; }
            hand = EnumHand.OFF_HAND;
        }

        refillPlaceCacheIfNeeded();
        candBuf.clear();

        int budget = maxCalcPerTick.getValue();
        int placed = 0;
        CrystalPos best = null;

        for (int i = 0, n = cachedPlacePos.size(); i < n && budget > 0; i++) {
            BlockPos pos = cachedPlacePos.get(i);
            if (!CrystalUtil.canSeePos(pos) && PlayerUtil.getDistance(pos) > wallRangePlace.getValue()) continue;
                EntityPlayer near = closestEnemyWithin(pos, 4.0);
                if (near == null) continue;
            double sx = pos.getX() + 0.5;
            double sy = pos.getY() + 1;
            double sz = pos.getZ() + 0.5;

            double selfDamage = CrystalUtil.calculateDamage(sx, sy, sz, mc.player);
            if (!ignoreSelfDmg.getValue() && selfDamage > maxSelf.getValue()) continue;
            if (antiSuicide.getValue() && mc.player.getHealth() - selfDamage <= 0.0) continue;

            double enemyDamage = CrystalUtil.calculateDamage(sx, sy, sz, target);
            if (facePlaceHealth.getValue() <= 0.0f || target.getHealth() > facePlaceHealth.getValue()) {
                if (enemyDamage < minDmg.getValue()) { budget--; continue; }
                if (smartMode.getValue() && selfDamage > enemyDamage && Math.abs(selfDamage - enemyDamage) >= dmgError.getValue()) { budget--; continue; }
            }

            CrystalPos cp = new CrystalPos(pos, enemyDamage);
            candBuf.add(cp);
            if (best == null || cp.dmg > best.dmg) best = cp;
            budget--;
        }

        if (candBuf.isEmpty() || best == null) { renderPos = null; restoreItem(); return; }

        candBuf.sort((a,b) -> Double.compare(b.dmg, a.dmg));
        int limit = Math.min(multiPlace.getValue(), candBuf.size());
        for (int i = 0; i < limit; i++) {
            BlockPos p = candBuf.get(i).pos;
            renderPos = p;
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(p, EnumFacing.UP, hand, 0.5f, 0.5f, 0.5f));
            if (placeSwing.getValue()) mc.player.connection.sendPacket(new CPacketAnimation(hand));
            placed++;
        }

        if (predictHit.getValue() && packetBreak.getValue() && lastEntityID != -1) {
            for (int i = amountOffset.getValue(); i < amount.getValue(); i++) {
                Entity e = mc.world.getEntityByID(lastEntityID + i);
                if (e instanceof EntityEnderCrystal) {
                    mc.player.connection.sendPacket(new CPacketUseEntity(e));
                    EnumHand h = (swing.getValue() == swingArm.Mainhand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                    if (swing.getValue() != swingArm.None) {
                        if (packetSwing.getValue()) mc.player.connection.sendPacket(new CPacketAnimation(h)); else mc.player.swingArm(h);
                    }
                }
            }
        }

        restoreItem();
    }

    public void doBreakFast() {
        if (!breakTimer.passedDms(explodeDelay.getValue())) return;
        breakTimer.reset();

        AxisAlignedBB box = new AxisAlignedBB(
                mc.player.posX - breakRange.getValue(), mc.player.posY - breakRange.getValue(), mc.player.posZ - breakRange.getValue(),
                mc.player.posX + breakRange.getValue(), mc.player.posY + breakRange.getValue(), mc.player.posZ + breakRange.getValue()
        );

        List<EntityEnderCrystal> crystals = mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, box);
        if (crystals.isEmpty()) return;

        int broken = 0;
        crystals.sort(Comparator.comparingDouble(PlayerUtil::getDistance));

        for (int i = 0; i < crystals.size() && broken < multiBreak.getValue(); i++) {
            EntityEnderCrystal c = crystals.get(i);
            if (!isValidCrystal(c.posX, c.posY, c.posZ)) continue;
            if (packetBreak.getValue()) mc.player.connection.sendPacket(new CPacketUseEntity(c));
            else mc.playerController.attackEntity(mc.player, c);
            EnumHand hand = (swing.getValue() == swingArm.Mainhand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
            if (swing.getValue() != swingArm.None) {
                if (packetSwing.getValue()) mc.player.connection.sendPacket(new CPacketAnimation(hand)); else mc.player.swingArm(hand);
            }
            broken++;
        }
    }

    private void refillPlaceCacheIfNeeded() {
        long now = System.currentTimeMillis();
        double px = mc.player.posX, py = mc.player.posY, pz = mc.player.posZ;
        boolean expired = now - cacheAt >= cacheMs.getValue();
        boolean moved = sq(px - cachePx) + sq(py - cachePy) + sq(pz - cachePz) > 0.36;
        if (expired || moved || cachedPlacePos.isEmpty()) {
            cachedPlacePos.clear();
            List<BlockPos> src = CrystalUtil.possiblePlacePositions(placeRange.getValue(), true, opPlace.getValue());
            for (int i = 0, n = src.size(); i < n; i++) cachedPlacePos.add(src.get(i));
            cacheAt = now; cachePx = px; cachePy = py; cachePz = pz;
        }
    }

    private static double sq(double v) { return v * v; }

    @SubscribeEvent(priority = EventPriority.HIGH)
    @Override
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject p = (SPacketSpawnObject) event.getPacket();
            if (p.getType() == 51 && explode.getValue() && predict.getValue() && packetBreak.getValue() && target != null) {
                lastEntityID = p.getEntityID();
                if (!isValidCrystal(p.getX(), p.getY(), p.getZ())) return;
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity instanceof EntityEnderCrystal) {
                        double dx = entity.posX - p.getX();
                        double dy = entity.posY - p.getY();
                        double dz = entity.posZ - p.getZ();
                        if (dx * dx + dy * dy + dz * dz < 1.0) {
                            mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                            EnumHand hand = (swing.getValue() == swingArm.Mainhand) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                            if (swing.getValue() != swingArm.None) {
                                if (packetSwing.getValue()) mc.player.connection.sendPacket(new CPacketAnimation(hand)); else mc.player.swingArm(hand);
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (checkOtherEntity.getValue()) {
            if (event.getPacket() instanceof SPacketSpawnExperienceOrb) lastEntityID = ((SPacketSpawnExperienceOrb) event.getPacket()).getEntityID();
            if (event.getPacket() instanceof SPacketSpawnMob) lastEntityID = ((SPacketSpawnMob) event.getPacket()).getEntityID();
            if (event.getPacket() instanceof SPacketSpawnPainting) lastEntityID = ((SPacketSpawnPainting) event.getPacket()).getEntityID();
            if (event.getPacket() instanceof SPacketSpawnPlayer) lastEntityID = ((SPacketSpawnPlayer) event.getPacket()).getEntityID();
        }
    }

    public boolean isValidCrystal(final double posX, final double posY, final double posZ) {
        if (target == null) return false;
        if (FriendManager.isFriend(target.getName())) return false;
        BlockPos pos = new BlockPos(posX, posY, posZ);
        if (PlayerUtil.getDistance(pos) > breakRange.getValue()) return false;
        if (!CrystalUtil.canSeePos(pos)) {
            if (strictLine.getValue()) return false;
            if (PlayerUtil.getDistance(pos) > wallRangeBreak.getValue()) return false;
        }
        double selfDamage = CrystalUtil.calculateDamage(posX, posY, posZ, mc.player);
        if (!ignoreSelfDmg.getValue() && selfDamage > maxSelf.getValue()) return false;
        if (antiSuicide.getValue() && mc.player.getHealth() - selfDamage <= 0.0) return false;
        double enemyDamage = CrystalUtil.calculateDamage(posX, posY, posZ, target);
        if (target.getHealth() <= facePlaceHealth.getValue()) return true;
        if (enemyDamage < minDmg.getValue()) return false;
        if (smartMode.getValue() && selfDamage > enemyDamage && Math.abs(selfDamage - enemyDamage) >= dmgError.getValue()) return false;
        return true;
    }

    public void setItem(final int slot) {
        if (!autoSwitch.getValue()) return;
        if (slot < 0 || slot > 8) return;
        if (silentSwitch.getValue()) {
            if (mc.player.inventory.currentItem == slot) return;
            oldhand = null;
            if (mc.player.isHandActive()) oldhand = mc.player.getActiveHand();
            if (oldslot == -1) oldslot = mc.player.inventory.currentItem;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        } else {
            if (mc.player.inventory.currentItem == slot) return;
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    public void restoreItem() {
        if (!silentSwitch.getValue()) return;
        if (oldslot != -1) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            oldslot = -1;
        }
        if (oldhand != null) {
            mc.player.setActiveHand(oldhand);
            oldhand = null;
        }
    }

    @Override
    public void onRender3D() {
        if (renderPos != null) RenderUtil3D.drawBox(renderPos, 1.0, color.getValue(), 63);
    }

    private EntityPlayer nearestEnemy(double maxRange) {
        EntityPlayer best = null;
        double bestD = Double.MAX_VALUE;
        List<EntityPlayer> list = mc.world.playerEntities;
        for (int i = 0, n = list.size(); i < n; i++) {
            EntityPlayer p = list.get(i);
            if (p == null || p == mc.player || p.isDead) continue;
            if (FriendManager.isFriend(p.getName())) continue;
            double d = PlayerUtil.getDistance(p);
            if (d <= maxRange && d < bestD) { best = p; bestD = d; }
        }
        return best;
    }

    private EntityPlayer closestEnemyWithin(BlockPos pos, double radius) {
        double r2 = radius * radius;
        EntityPlayer closest = null;
        double best = Double.MAX_VALUE;
        List<EntityPlayer> list = mc.world.playerEntities;
        for (int i = 0, n = list.size(); i < n; i++) {
            EntityPlayer p = list.get(i);
            if (p == null || p == mc.player || p.isDead) continue;
            if (FriendManager.isFriend(p.getName())) continue;
            double d = p.getDistanceSq(pos);
            if (d <= r2 && d < best) { best = d; closest = p; }
        }
        return closest;
    }

    public enum swingArm { Mainhand, Offhand, None }

    public static class CrystalPos {
        public BlockPos pos;
        public double dmg;
        public CrystalPos(final BlockPos pos, final double dmg) { this.pos = pos; this.dmg = dmg; }
    }
}
