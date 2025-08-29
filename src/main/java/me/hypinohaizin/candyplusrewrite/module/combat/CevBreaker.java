package me.hypinohaizin.candyplusrewrite.module.combat;

import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import net.minecraft.init.Items;
import java.util.List;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import me.hypinohaizin.candyplusrewrite.utils.CrystalUtil;
import java.util.ArrayList;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil3D;
import net.minecraft.network.play.client.CPacketUseEntity;
import java.util.Comparator;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.Entity;
import me.hypinohaizin.candyplusrewrite.module.exploit.InstantMine;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.init.Blocks;
import me.hypinohaizin.candyplusrewrite.utils.BlockUtil;
import me.hypinohaizin.candyplusrewrite.module.exploit.SilentPickel;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class CevBreaker extends Module
{
    public Setting<Float> preDelay;
    public Setting<Float> crystalDelay;
    public Setting<Float> breakDelay;
    public Setting<Float> attackDelay;
    public Setting<Float> endDelay;
    public Setting<Float> range;
    public Setting<Boolean> tick;
    public Setting<Boolean> toggle;
    public Setting<Boolean> noSwingBlock;
    public Setting<Boolean> packetPlace;
    public Setting<Boolean> packetCrystal;
    public Setting<Boolean> instantBreak;
    public Setting<Boolean> toggleSilentPickel;
    public Setting<Boolean> packetBreak;
    public Setting<Boolean> offHandBreak;
    public Setting<Boolean> skip;
    public Setting<Integer> breakAttempts;
    public Setting<Float> targetRange;
    public Setting<Target> targetType;
    public Setting<Color> blockColor;
    public Setting<Boolean> outline;
    public Setting<Float> width;
    public BlockPos base;
    public BlockPos old;
    public boolean builtTrap;
    public boolean placedCrystal;
    public boolean brokeBlock;
    public boolean attackedCrystal;
    public boolean done;
    public boolean headMode;
    public boolean based;
    public int crystalSlot;
    public int obbySlot;
    public int pickelSlot;
    public int attempts;
    public static EntityPlayer target;
    public Timer blockTimer;
    public Timer crystalTimer;
    public Timer breakTimer;
    public Timer attackTimer;
    public Timer endTimer;
    public static boolean breaking;
    private Integer lockedTargetId = null;
    private BlockPos lockedHead = null;
    private int lockTicks = 0;
    private static final int LOCK_TICKS_DEFAULT = 20;

    public CevBreaker() {
        super("CevBreaker", Categories.COMBAT, false, false);
        preDelay = register(new Setting<>("BlockDelay", 0.0f, 20.0f, 0.0f));
        crystalDelay = register(new Setting<>("CrystalDelay", 0.0f, 20.0f, 0.0f));
        breakDelay = register(new Setting<>("BreakDelay", 0.0f, 20.0f, 0.0f));
        attackDelay = register(new Setting<>("AttackDelay", 3.0f, 20.0f, 0.0f));
        endDelay = register(new Setting<>("EndDelay", 0.0f, 20.0f, 0.0f));
        range = register(new Setting<>("Range", 10.0f, 20.0f, 1.0f));
        tick = register(new Setting<>("Tick", true));
        toggle = register(new Setting<>("Toggle", true));
        noSwingBlock = register(new Setting<>("NoSwingBlock", true));
        packetPlace = register(new Setting<>("PacketPlace", false));
        packetCrystal = register(new Setting<>("PacketCrystal", true));
        instantBreak = register(new Setting<>("InstantBreak", false));
        toggleSilentPickel = register(new Setting<>("ToggleSilentPickel", false));
        packetBreak = register(new Setting<>("PacketBreak", true));
        offHandBreak = register(new Setting<>("OffhandBreak", true));
        skip = register(new Setting<>("Skip", false));
        breakAttempts = register(new Setting<>("BreakAttempts", 7, 20, 1));
        targetRange = register(new Setting<>("Target Range", 10.0f, 20.0f, 0.0f));
        targetType = register(new Setting<>("Target", Target.Nearest));
        blockColor = register(new Setting<>("Color", new Color(250, 0, 200, 50)));
        outline = register(new Setting<>("Outline", false));
        width = register(new Setting<>("Width", 2.0f, 5.0f, 0.1f, v -> outline.getValue()));
        old = null;
        done = false;
        based = false;
        pickelSlot = -1;
        attempts = 0;
        endTimer = null;
    }

    @Override
    public void onEnable() {
        reset();
        if (toggleSilentPickel.getValue()) setToggleSilentPickel(true);
    }

    @Override
    public void onDisable() {
        if (toggleSilentPickel.getValue()) setToggleSilentPickel(false);
    }

    public void setToggleSilentPickel(final boolean toggle) {
        final Module silent = CandyPlusRewrite.m_module.getModuleWithClass(SilentPickel.class);
        if (toggle) silent.enable(); else silent.disable();
    }

    @Override
    public void onTick() {
        if (tick.getValue()) doCB();
    }

    @Override
    public void onUpdate() {
        if (!tick.getValue()) doCB();
    }

    public void doCB() {
        if (nullCheck()) return;
        try {
            if (!findMaterials()) {
                if (toggle.getValue()) {
                    sendMessage("Cannot find materials! disabling...");
                    disable();
                }
                return;
            }
            if (lockedTargetId == null) {
                CevBreaker.target = findTarget();
                if (isNull(CevBreaker.target)) {
                    if (toggle.getValue()) {
                        sendMessage("Cannot find target! disabling...");
                        disable();
                    }
                    return;
                }
                if (FriendManager.isFriend(CevBreaker.target.getName())) {
                    reset();
                    return;
                }
                lockedTargetId = CevBreaker.target.getEntityId();
                lockTicks = LOCK_TICKS_DEFAULT;
            } else {
                Entity e = mc.world.getEntityByID(lockedTargetId);
                CevBreaker.target = (e instanceof EntityPlayer) ? (EntityPlayer) e : null;
                if (isNull(CevBreaker.target) || FriendManager.isFriend(CevBreaker.target.getName())) {
                    reset();
                    return;
                }
            }
            if (isNull(base) && !findSpace(CevBreaker.target)) {
                if (toggle.getValue()) {
                    sendMessage("Cannot find space! disabling...");
                    disable();
                }
                return;
            }
            final BlockPos targetPos = new BlockPos(CevBreaker.target.posX, CevBreaker.target.posY, CevBreaker.target.posZ);
            final BlockPos headPos = targetPos.add(0, 2, 0);
            if (lockedHead == null) {
                lockedHead = headPos;
                lockTicks = LOCK_TICKS_DEFAULT;
            } else {
                if (lockTicks > 0) lockTicks--;
            }
            if (headMode) {
                builtTrap = true;
                tryPlaceCrystalImmediate(lockedHead);
            }
            if (blockTimer == null) blockTimer = new Timer();
            if (!builtTrap) {
                if (BlockUtil.getBlock(base) == Blocks.AIR && !based && blockTimer.passedX(preDelay.getValue())) {
                    setItem(obbySlot);
                    placeBlock(base, false);
                    blockTimer.reset();
                }
                if (BlockUtil.getBlock(base.add(0, 1, 0)) == Blocks.AIR && !based && blockTimer.passedX(preDelay.getValue())) {
                    setItem(obbySlot);
                    placeBlock(base.add(0, 1, 0), false);
                    blockTimer.reset();
                }
                if (BlockUtil.getBlock(lockedHead) == Blocks.AIR && blockTimer.passedX(preDelay.getValue())) {
                    setItem(obbySlot);
                    placeBlock(lockedHead, packetPlace.getValue());
                    blockTimer = null;
                    builtTrap = true;
                    tryPlaceCrystalImmediate(lockedHead);
                }
            }
            if (builtTrap && (!base.equals(old) && skip.getValue())) placedCrystal = true;
            if (crystalTimer == null && builtTrap) crystalTimer = new Timer();
            if (builtTrap && !placedCrystal && crystalTimer.passedX(crystalDelay.getValue())) {
                if (CrystalUtil.canPlaceCrystal(lockedHead)) {
                    if (crystalSlot != 999) setItem(crystalSlot);
                    EnumHand hand = crystalSlot != 999 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                    mc.playerController.updateController();
                    try { mc.rightClickDelayTimer = 0; } catch (Throwable ignored) {}
                    if (packetCrystal.getValue()) {
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(lockedHead, EnumFacing.UP, hand, 0.5f, 1.0f, 0.5f));
                    } else {
                        mc.playerController.processRightClickBlock(mc.player, mc.world, lockedHead, EnumFacing.UP, new Vec3d(0.5, 1.0, 0.5), hand);
                    }
                    if (!noSwingBlock.getValue()) mc.player.swingArm(hand);
                    placedCrystal = true;
                    crystalTimer.reset();
                } else {
                    crystalTimer.reset();
                }
            }
            if (breakTimer == null && placedCrystal) breakTimer = new Timer();
            if (placedCrystal && !brokeBlock && breakTimer.passedX(breakDelay.getValue())) {
                setItem(pickelSlot);
                if (BlockUtil.getBlock(lockedHead) == Blocks.AIR) {
                    brokeBlock = true;
                }
                if (!CevBreaker.breaking) {
                    if (!instantBreak.getValue()) {
                        if (!noSwingBlock.getValue()) CevBreaker.mc.player.swingArm(EnumHand.MAIN_HAND);
                        CevBreaker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, lockedHead, EnumFacing.DOWN));
                        CevBreaker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lockedHead, EnumFacing.DOWN));
                    } else {
                        if (!noSwingBlock.getValue()) CevBreaker.mc.player.swingArm(EnumHand.MAIN_HAND);
                        CandyPlusRewrite.m_module.getModuleWithClass(InstantMine.class).enable();
                        InstantMine.startBreak(lockedHead, EnumFacing.DOWN);
                    }
                    CevBreaker.breaking = true;
                }
            }
            if (brokeBlock && (!base.equals(old) && skip.getValue())) attackedCrystal = true;
            if (attackTimer == null && brokeBlock) attackTimer = new Timer();
            if (brokeBlock && !attackedCrystal) {
                CevBreaker.breaking = false;
                if (attackTimer.passedX(attackDelay.getValue())) {
                    final BlockPos plannedCrystalPos = lockedHead.add(0, 1, 0);
                    final Entity crystal = mc.world.loadedEntityList.stream()
                            .filter(e -> e instanceof EntityEnderCrystal)
                            .filter(e -> e.getDistance(plannedCrystalPos.getX() + 0.5, plannedCrystalPos.getY(), plannedCrystalPos.getZ() + 0.5) < 2.0)
                            .min(Comparator.comparing(c -> c.getDistance(target)))
                            .orElse(null);
                    if (crystal == null) {
                        if (attempts >= breakAttempts.getValue()) {
                            attackedCrystal = true;
                            attempts = 0;
                        } else {
                            attempts++;
                            attackTimer.reset();
                        }
                        return;
                    }
                    EnumHand hand2 = offHandBreak.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                    if (packetBreak.getValue()) {
                        mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                    } else {
                        mc.playerController.attackEntity(mc.player, crystal);
                        mc.player.swingArm(hand2);
                    }
                    attackedCrystal = true;
                    attempts = 0;
                }
            }
            if (endTimer == null && attackedCrystal) endTimer = new Timer();
            if (attackedCrystal && !done && endTimer.passedX(endDelay.getValue())) {
                done = true;
                old = new BlockPos(base.getX(), base.getY(), base.getZ());
                reset();
            }
            restoreItem();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private void tryPlaceCrystalImmediate(BlockPos headPos) {
        if (placedCrystal) return;
        if (!CrystalUtil.canPlaceCrystal(headPos)) return;
        if (crystalSlot != 999) setItem(crystalSlot);
        EnumHand hand = crystalSlot != 999 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        mc.playerController.updateController();
        try { mc.rightClickDelayTimer = 0; } catch (Throwable ignored) {}
        if (packetCrystal.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(headPos, EnumFacing.UP, hand, 0.5f, 1.0f, 0.5f));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, headPos, EnumFacing.UP, new Vec3d(0.5, 1.0, 0.5), hand);
        }
        if (!noSwingBlock.getValue()) mc.player.swingArm(hand);
        placedCrystal = true;
        if (crystalTimer == null) crystalTimer = new Timer();
        crystalTimer.reset();
    }

    public void reset() {
        base = null;
        builtTrap = false;
        placedCrystal = false;
        brokeBlock = false;
        attackedCrystal = false;
        done = false;
        headMode = false;
        based = false;
        crystalSlot = -1;
        obbySlot = -1;
        pickelSlot = -1;
        attempts = 0;
        CevBreaker.target = null;
        blockTimer = null;
        crystalTimer = null;
        breakTimer = null;
        attackTimer = null;
        endTimer = null;
        CevBreaker.breaking = false;
        lockedTargetId = null;
        lockedHead = null;
        lockTicks = 0;
    }

    @Override
    public void onRender3D() {
        try {
            if (isNull(CevBreaker.target)) return;
            final BlockPos targetPos = new BlockPos(CevBreaker.target.posX, CevBreaker.target.posY, CevBreaker.target.posZ);
            final BlockPos headPos = targetPos.add(0, 2, 0);
            RenderUtil3D.drawBox(headPos, 1.0, blockColor.getValue(), 63);
            if (outline.getValue()) RenderUtil3D.drawBoundingBox(headPos, 1.0, width.getValue(), blockColor.getValue());
        } catch (final Exception ex) {}
    }

    public void setItem(final int slot) {
        CevBreaker.mc.player.inventory.currentItem = slot;
        CevBreaker.mc.playerController.updateController();
    }

    public void restoreItem() {}

    public boolean findSpace(final EntityPlayer player) {
        final BlockPos targetPos = new BlockPos(player.posX, player.posY, player.posZ);
        final BlockPos headPos = targetPos.add(0, 2, 0);
        final BlockPos[] offsets = { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
        final List<BlockPos> posess = new ArrayList<>();
        if (BlockUtil.getBlock(headPos) == Blocks.OBSIDIAN) {
            headMode = true;
            base = targetPos;
            return true;
        }
        if (BlockUtil.canPlaceBlock(headPos)) {
            based = true;
            base = targetPos;
            return true;
        }
        for (final BlockPos offset : offsets) {
            final BlockPos basePos = targetPos.add(offset);
            if ((BlockUtil.getBlock(basePos) == Blocks.OBSIDIAN || BlockUtil.getBlock(basePos) == Blocks.BEDROCK) &&
                    BlockUtil.canPlaceBlockFuture(basePos.add(0, 1, 0)) &&
                    BlockUtil.canPlaceBlockFuture(basePos.add(0, 2, 0))) {
                if (BlockUtil.getBlock(headPos) == Blocks.AIR) {
                    posess.add(basePos.add(0, 1, 0));
                }
            }
        }
        base = posess.stream()
                .filter(p -> CevBreaker.mc.player.getDistance(p.getX(), p.getY(), p.getZ()) <= range.getValue())
                .max(Comparator.comparing(PlayerUtil::getDistanceI))
                .orElse(null);
        if (base == null) return false;
        headMode = false;
        return true;
    }

    public EntityPlayer findTarget() {
        List<EntityPlayer> candidates = new ArrayList<>(mc.world.playerEntities);
        candidates.removeIf(p ->
                p == null
                        || p == mc.player
                        || p.isDead
                        || FriendManager.isFriend(p.getName())
                        || mc.player.getDistance(p) > targetRange.getValue()
        );
        if (candidates.isEmpty()) return null;

        switch (targetType.getValue()) {
            case Nearest:
                return candidates.stream()
                        .min(Comparator.comparingDouble(p -> mc.player.getDistance(p)))
                        .orElse(null);
            case Looking: {
                EntityPlayer looking = PlayerUtil.getLookingPlayer(targetRange.getValue());
                if (looking != null
                        && !FriendManager.isFriend(looking.getName())
                        && candidates.contains(looking)) {
                    return looking;
                }
                return null;
            }
            case Best:
                return candidates.stream()
                        .filter(this::findSpace)
                        .min(Comparator.comparingDouble(PlayerUtil::getDistance))
                        .orElse(null);
            default:
                return null;
        }
    }

    public boolean findMaterials() {
        crystalSlot = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
        obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        pickelSlot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
        if (itemCheck(crystalSlot) && CevBreaker.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) crystalSlot = 999;
        return !itemCheck(crystalSlot) && !itemCheck(obbySlot) && !itemCheck(pickelSlot);
    }

    public boolean itemCheck(final int slot) {
        return slot == -1;
    }

    public boolean isNull(final Object o) {
        return o == null;
    }

    public void placeBlock(final BlockPos pos, final Boolean packet) {
        BlockUtil.placeBlock(pos, packet);
        if (!noSwingBlock.getValue()) CevBreaker.mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    static {
        CevBreaker.target = null;
        CevBreaker.breaking = false;
    }

    public enum Target
    {
        Nearest,
        Looking,
        Best
    }
}