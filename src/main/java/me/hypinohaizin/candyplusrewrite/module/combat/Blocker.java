package me.hypinohaizin.candyplusrewrite.module.combat;

import net.minecraft.network.play.client.CPacketHeldItemChange;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.play.client.CPacketPlayer;
import me.hypinohaizin.candyplusrewrite.utils.CrystalUtil;
import net.minecraft.network.play.client.CPacketUseEntity;
import me.hypinohaizin.candyplusrewrite.utils.BlockUtil;
import java.util.stream.Collectors;
import net.minecraft.entity.item.EntityEnderCrystal;
import java.util.Iterator;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil3D;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import net.minecraft.init.Blocks;
import java.util.ArrayList;
import net.minecraft.util.EnumHand;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import net.minecraft.entity.Entity;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class Blocker extends Module {

    public Setting<Boolean> piston;
    public Setting<Boolean> crystalSync;
    public Setting<Boolean> breakCrystalPA;
    public Setting<Boolean> teleportPA;
    public Setting<Integer> limitPA;
    public Setting<Float> crystalDelayPA;
    public Setting<Float> range;
    public Setting<Integer> maxY;
    public Setting<Boolean> cev;
    public Setting<Float> crystalDelayCEV;
    public Setting<Boolean> teleportCEV;
    public Setting<Integer> limitCEV;
    public Setting<Boolean> civ;
    public Setting<Float> crystalDelayCIV;
    public Setting<Float> placeDelay;
    public Setting<Boolean> packetPlace;
    public Setting<Boolean> packetBreak;
    public Setting<Arm> swingArm;
    public Setting<Boolean> silentSwitch;
    public Setting<Boolean> tick;
    public Setting<Boolean> render;
    public Setting<Color> pistonColor;
    public Setting<Boolean> onlySelfTarget;

    public Entity PAcrystal;
    public List<BlockPos> pistonPos;
    public Timer crystalTimerPA;
    public int oldCrystal;
    public int limitCounterPA;
    public boolean needBlockCEV;
    public Timer crystalTimerCEV;
    public int limitCounterCEV;
    public int stage;
    public Timer timerCiv;
    public List<BlockPos> detectedPosCiv;
    public Timer placeTimer;
    private int oldslot;
    private EnumHand oldhand;

    public Blocker() {
        super("Blocker", Categories.COMBAT, false, false);
        piston = register(new Setting<>("Piston", true));
        crystalSync = register(new Setting<>("CrystalSyncPA", false, v -> piston.getValue()));
        breakCrystalPA = register(new Setting<>("BreakCrystalPA", false, v -> piston.getValue()));
        teleportPA = register(new Setting<>("FlightBreakPA", true, v -> piston.getValue() && breakCrystalPA.getValue()));
        limitPA = register(new Setting<>("LimitPA", 3, 10, 1, v -> piston.getValue() && breakCrystalPA.getValue() && teleportPA.getValue()));
        crystalDelayPA = register(new Setting<>("CrystalDelayPA", 3.0f, 25.0f, 0.0f, v -> piston.getValue() && breakCrystalPA.getValue()));
        range = register(new Setting<>("Range", 7.0f, 13.0f, 0.0f, v -> piston.getValue()));
        maxY = register(new Setting<>("MaxY", 4, 6, 2, v -> piston.getValue()));
        cev = register(new Setting<>("CevBreaker", true));
        crystalDelayCEV = register(new Setting<>("CrystalDelayCEV", 3.0f, 25.0f, 0.0f, v -> cev.getValue()));
        teleportCEV = register(new Setting<>("FlightBreakCEV", true, v -> cev.getValue()));
        limitCEV = register(new Setting<>("LimitCEV", 3, 10, 1, v -> piston.getValue() && teleportCEV.getValue()));
        civ = register(new Setting<>("CivBreaker", true));
        crystalDelayCIV = register(new Setting<>("CrystalDelayCIV", 3.0f, 25.0f, 0.0f, v -> cev.getValue()));
        placeDelay = register(new Setting<>("PlaceDelay", 3.0f, 25.0f, 0.0f));
        packetPlace = register(new Setting<>("PacketPlace", false));
        packetBreak = register(new Setting<>("PacketBreak", true));
        swingArm = register(new Setting<>("SwingArm", Arm.None));
        silentSwitch = register(new Setting<>("SilentSwitch", false));
        tick = register(new Setting<>("Tick", true));
        render = register(new Setting<>("Render", true));
        pistonColor = register(new Setting<>("PistonColor", new Color(230, 10, 10, 50), v -> piston.getValue()));
        onlySelfTarget = register(new Setting<>("OnlySelfTarget", true));
        PAcrystal = null;
        pistonPos = new ArrayList<>();
        crystalTimerPA = new Timer();
        oldCrystal = -1;
        limitCounterPA = 0;
        needBlockCEV = false;
        crystalTimerCEV = new Timer();
        limitCounterCEV = 0;
        stage = 0;
        timerCiv = new Timer();
        detectedPosCiv = new ArrayList<>();
        placeTimer = new Timer();
        oldslot = -1;
        oldhand = null;
    }

    @Override
    public void onEnable() {
        PAcrystal = null;
        pistonPos = new ArrayList<>();
        crystalTimerPA = new Timer();
        needBlockCEV = false;
        crystalTimerCEV = new Timer();
        limitCounterCEV = 0;
        stage = 0;
        timerCiv = new Timer();
        detectedPosCiv = new ArrayList<>();
        placeTimer = new Timer();
    }

    @Override
    public void onTick() {
        if (tick.getValue()) doBlock();
    }

    @Override
    public void onUpdate() {
        if (!tick.getValue()) doBlock();
    }

    public void doBlock() {
        if (nullCheck()) return;
        int obby = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        if (obby == -1) return;
        if (cev.getValue()) execute(() -> blockCEV(obby));
        Module civ = CandyPlusRewrite.m_module.getModuleWithClass(CivBreaker.class);
        if (this.civ.getValue() && !civ.isEnable) execute(() -> blockCIV(obby));
        restoreItem();
    }

    public void execute(final Runnable r) {
        try { r.run(); } catch (Exception ignored) {}
    }

    @Override
    public void onRender3D() {
        try {
            if (pistonPos != null && piston.getValue() && render.getValue()) {
                for (BlockPos p : pistonPos) RenderUtil3D.drawBox(p, 1.0, pistonColor.getValue(), 63);
            }
        } catch (Exception ignored) {}
    }

    public void blockCIV(final int obby) {
        BlockPos my = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        BlockPos[] offsets = new BlockPos[]{new BlockPos(1,1,0),new BlockPos(-1,1,0),new BlockPos(0,1,1),new BlockPos(0,1,-1),new BlockPos(1,1,1),new BlockPos(1,1,-1),new BlockPos(-1,1,1),new BlockPos(-1,1,-1)};
        for (BlockPos off : offsets) {
            BlockPos base = my.add(off);
            if (onlySelfTarget.getValue() && !isAdjacentToSelf(base.add(0,-1,0))) continue;
            if (BlockUtil.getBlock(base) == Blocks.OBSIDIAN) {
                for (Entity e : mc.world.loadedEntityList.stream().filter(x -> x instanceof EntityEnderCrystal).collect(Collectors.toList())) {
                    BlockPos cpos = new BlockPos(e.posX, e.posY, e.posZ);
                    if (base.equals(cpos.add(0,-1,0))) {
                        mc.player.connection.sendPacket(new CPacketUseEntity(e));
                        detectedPosCiv.add(cpos);
                    }
                }
            }
        }
        if (timerCiv.passedX(crystalDelayCIV.getValue())) {
            timerCiv.reset();
            Iterator<BlockPos> it = detectedPosCiv.iterator();
            while (it.hasNext()) {
                BlockPos pos = it.next();
                if (BlockUtil.getBlock(pos) == Blocks.AIR) {
                    setItem(obby);
                    if (BlockUtil.getBlock(pos.add(0,-1,0)) == Blocks.AIR) BlockUtil.placeBlock(pos.add(0,-1,0), packetPlace.getValue());
                    BlockUtil.placeBlock(pos, packetPlace.getValue());
                    it.remove();
                }
            }
        }
    }

    public void blockCEV(final int obby) {
        BlockPos my = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        BlockPos ceil = my.add(0,2,0);
        if (placeTimer.passedX(crystalDelayCEV.getValue())) {
            if (stage == 1) {
                crystalTimerCEV.reset();
                setItem(obby);
                BlockUtil.placeBlock(ceil.add(0,1,0), packetPlace.getValue());
                stage = 0;
            }
            boolean selfTarget = !onlySelfTarget.getValue() || isSameColumnAsSelf(ceil);
            if (selfTarget && BlockUtil.getBlock(ceil) == Blocks.OBSIDIAN && CrystalUtil.hasCrystal(ceil) && teleportCEV.getValue() && limitCounterCEV < limitCEV.getValue()) {
                crystalTimerCEV.reset();
                limitCounterCEV++;
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, my.getY() + 0.2, mc.player.posZ, false));
                breakCrystal(CrystalUtil.getCrystal(ceil));
                swingArm();
                stage = 1;
            } else {
                limitCounterCEV = 0;
            }
        }
    }

    public void blockPA(final int obby) {
        if (crystalTimerPA.passedX(crystalDelayPA.getValue()) && piston.getValue() && breakCrystalPA.getValue() && PAcrystal != null) {
            crystalTimerPA.reset();
            if (PAcrystal.getEntityId() == oldCrystal) limitCounterPA++; else limitCounterPA = 0;
            BlockPos my = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            BlockPos cpos = new BlockPos(PAcrystal.posX, PAcrystal.posY, PAcrystal.posZ);
            boolean selfTarget = isCrystalThreatToSelf(cpos, my);
            if (selfTarget && BlockUtil.getBlock(my.add(0,2,0)) == Blocks.OBSIDIAN && teleportPA.getValue() && limitCounterPA <= limitPA.getValue()) {
                double ox = (my.getX() - cpos.getX()) * 0.4;
                double oz = (my.getZ() - cpos.getZ()) * 0.4;
                mc.player.connection.sendPacket(new CPacketPlayer.Position(my.getX() + 0.5 + ox, my.getY() + 0.2, my.getZ() + 0.5 + oz, false));
            }
            if (selfTarget) {
                breakCrystal(PAcrystal);
                swingArm();
            }
            oldCrystal = PAcrystal.getEntityId();
            PAcrystal = null;
        }
        if (placeTimer.passedX(placeDelay.getValue()) && piston.getValue()) {
            placeTimer.reset();
            Iterator<BlockPos> it = pistonPos.iterator();
            while (it.hasNext()) {
                BlockPos pos = it.next();
                if (BlockUtil.getBlock(pos) == Blocks.AIR) {
                    if (onlySelfTarget.getValue() && !isAdjacentToSelf(pos)) { it.remove(); continue; }
                    setItem(InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN));
                    if (BlockUtil.hasNeighbour(pos)) {
                        BlockUtil.placeBlock(pos, packetPlace.getValue());
                    } else {
                        BlockUtil.placeBlock(pos.add(0,-1,0), packetPlace.getValue());
                        BlockUtil.rightClickBlock(pos.add(0,-1,0), EnumFacing.UP, packetPlace.getValue());
                    }
                    it.remove();
                }
            }
        }
    }

    public void detectPA() {
        List<BlockPos> tmp = new ArrayList<>();
        Iterator<BlockPos> it = pistonPos.iterator();
        while (it.hasNext()) {
            BlockPos p = it.next();
            if (tmp.contains(p) || PlayerUtil.getDistance(p) > range.getValue()) it.remove();
            tmp.add(p);
        }
        BlockPos my = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        BlockPos[] horiz = new BlockPos[]{new BlockPos(1,0,0),new BlockPos(-1,0,0),new BlockPos(0,0,1),new BlockPos(0,0,-1)};
        for (int y = 0; y <= maxY.getValue(); y++) {
            for (BlockPos o : horiz) {
                BlockPos crystalPos = my.add(o.getX(), y, o.getZ());
                boolean selfTarget = !onlySelfTarget.getValue() || isCrystalThreatToSelf(crystalPos, my);
                if (!selfTarget) continue;
                if (CrystalUtil.hasCrystal(crystalPos) || !crystalSync.getValue()) {
                    List<BlockPos> ps = new ArrayList<>();
                    BlockPos a = crystalPos.add(o);
                    BlockPos s0 = crystalPos.add(o.getZ(),0,o.getX());
                    BlockPos s2 = crystalPos.add(-o.getZ(),0,-o.getX());
                    BlockPos b0 = a.add(o);
                    BlockPos b2 = a.add(o.getZ(),0,o.getX());
                    BlockPos b3 = a.add(-o.getZ(),0,-o.getX());
                    BlockPos b4 = b2.add(o);
                    BlockPos b5 = b3.add(o);
                    add(ps,a); add(ps,s0); add(ps,s2); add(ps,b0); add(ps,b2); add(ps,b3); add(ps,b4); add(ps,b5);
                    List<BlockPos> up = new ArrayList<>(); ps.forEach(p -> up.add(p.add(0,1,0))); up.forEach(ps::add);
                    for (BlockPos p : ps) {
                        if (isPiston(p)) {
                            pistonPos.add(p);
                            if (CrystalUtil.hasCrystal(crystalPos)) PAcrystal = CrystalUtil.getCrystal(crystalPos);
                        }
                    }
                }
            }
        }
    }

    public void add(final List<BlockPos> target, final BlockPos base) {
        target.add(base.add(0,1,0));
    }

    public boolean isPiston(final BlockPos pos) {
        return BlockUtil.getBlock(pos) == Blocks.PISTON || BlockUtil.getBlock(pos) == Blocks.STICKY_PISTON;
    }

    public void swingArm() {
        EnumHand arm = (swingArm.getValue() == Arm.Offhand) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        if (swingArm.getValue() != Arm.None) mc.player.swingArm(arm);
    }

    public void breakCrystal(final Entity crystal) {
        if (!(crystal instanceof EntityEnderCrystal)) return;
        if (packetBreak.getValue()) mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        else mc.playerController.attackEntity(mc.player, crystal);
    }

    public void setItem(final int slot) {
        if (silentSwitch.getValue()) {
            oldhand = null;
            if (mc.player.isHandActive()) oldhand = mc.player.getActiveHand();
            oldslot = mc.player.inventory.currentItem;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        } else {
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    public void restoreItem() {
        if (oldslot != -1 && silentSwitch.getValue()) {
            if (oldhand != null) mc.player.setActiveHand(oldhand);
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            oldslot = -1;
            oldhand = null;
        }
    }

    private boolean isAdjacentToSelf(BlockPos pos) {
        BlockPos me = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        return Math.abs(pos.getX() - me.getX()) <= 1 && Math.abs(pos.getZ() - me.getZ()) <= 1 && Math.abs(pos.getY() - me.getY()) <= maxY.getValue();
    }

    private boolean isSameColumnAsSelf(BlockPos pos) {
        BlockPos me = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        return pos.getX() == me.getX() && pos.getZ() == me.getZ();
    }

    private boolean isCrystalThreatToSelf(BlockPos crystal, BlockPos me) {
        if (Math.abs(crystal.getX() - me.getX()) > 1 || Math.abs(crystal.getZ() - me.getZ()) > 1) return false;
        if (crystal.getY() < me.getY() || crystal.getY() > me.getY() + maxY.getValue()) return false;
        return true;
    }

    public enum Arm {
        Mainhand, Offhand, None
    }
}
