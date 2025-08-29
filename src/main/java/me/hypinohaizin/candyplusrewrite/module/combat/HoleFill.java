package me.hypinohaizin.candyplusrewrite.module.combat;

import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import me.hypinohaizin.candyplusrewrite.utils.BlockUtil;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumHand;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import me.hypinohaizin.candyplusrewrite.module.Module;

import java.util.List;
import java.util.stream.Collectors;

public class HoleFill extends Module {
    public Setting<Float> range;
    public Setting<Integer> place;
    public Setting<Boolean> toggle;
    public Setting<Boolean> packetPlace;
    public Setting<Boolean> silentSwitch;
    public Setting<Boolean> autoMode;
    public Setting<Float> detectRange;
    public Setting<Integer> placeIntervalMs;

    private EnumHand oldhand;
    private int oldslot;
    private final Timer placeTimer = new Timer();

    public HoleFill() {
        super("HoleFill", Categories.COMBAT, false, false);
        range = register(new Setting<>("Range", 6.0f, 12.0f, 1.0f));
        place = register(new Setting<>("Place", 2, 10, 1));
        toggle = register(new Setting<>("Toggle", false));
        packetPlace = register(new Setting<>("PacketPlace", false));
        silentSwitch = register(new Setting<>("SilentSwitch", false));
        autoMode = register(new Setting<>("AutoMode", false));
        detectRange = register(new Setting<>("DetectRange", 4.0f, 10.0f, 1.0f));
        placeIntervalMs = register(new Setting<>("PlaceInterval", 75, 300, 0));
        oldhand = null;
        oldslot = -1;
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;

        int slot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        if (slot == -1) {
            restoreItem();
            if (toggle.getValue()) disable();
            return;
        }

        List<BlockPos> holes;
        if (autoMode.getValue()) {
            holes = CandyPlusRewrite.m_hole.getHoles().stream()
                    .filter(h -> PlayerUtil.getDistance(h) < range.getValue())
                    .filter(h -> !isAnyEntityInBlock(h))
                    .filter(this::isPlaceable)
                    .filter(h -> !isFriendNear(h, detectRange.getValue()))
                    .filter(h -> {
                        for (EntityPlayer p : mc.world.playerEntities) {
                            if (p == mc.player) continue;
                            if (FriendManager.isFriend(p)) continue;
                            double dx = h.getX() + 0.5;
                            double dy = h.getY() + 0.5;
                            double dz = h.getZ() + 0.5;
                            if (p.getDistance(dx, dy, dz) < detectRange.getValue()) return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        } else {
            holes = CandyPlusRewrite.m_hole.getHoles().stream()
                    .filter(h -> PlayerUtil.getDistance(h) < range.getValue())
                    .filter(h -> !isAnyEntityInBlock(h))
                    .filter(this::isPlaceable)
                    .filter(h -> !isFriendNear(h, detectRange.getValue()))
                    .collect(Collectors.toList());
        }

        int counter = 0;
        if (!holes.isEmpty()) setItem(slot);

        for (BlockPos hole : holes) {
            if (counter >= place.getValue()) break;
            if (!placeTimer.passedMs(placeIntervalMs.getValue())) continue;
            if (!isPlaceable(hole)) continue;
            if (isFriendNear(hole, detectRange.getValue())) continue;
            BlockUtil.placeBlock(hole, packetPlace.getValue());
            placeTimer.reset();
            counter++;
        }

        if (!autoMode.getValue() && holes.isEmpty() && toggle.getValue()) {
            restoreItem();
            disable();
            return;
        }

        restoreItem();
    }

    public boolean isPlaceable(BlockPos pos) {
        if (!mc.world.isAirBlock(pos) && !mc.world.getBlockState(pos).getMaterial().isReplaceable()) return false;
        if (isAnyEntityInBlock(pos)) return false;
        return true;
    }

    public static boolean isAnyEntityInBlock(BlockPos pos) {
        AxisAlignedBB box = new AxisAlignedBB(pos);
        List<Entity> list = mc.world.getEntitiesWithinAABBExcludingEntity(null, box);
        for (Entity e : list) {
            if (e instanceof EntityPlayer && e == mc.player) continue;
            if (!e.isDead && e.getEntityBoundingBox().intersects(box)) return true;
        }
        return false;
    }

    private boolean isFriendNear(BlockPos pos, double range) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;
        for (EntityPlayer p : mc.world.playerEntities) {
            if (p == mc.player) continue;
            if (!FriendManager.isFriend(p)) continue;
            if (p.getDistance(cx, cy, cz) <= range) return true;
        }
        return false;
    }

    public void setItem(int slot) {
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
}
