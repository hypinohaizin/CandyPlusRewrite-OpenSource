package me.hypinohaizin.candyplusrewrite.module.combat;

import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.utils.BlockUtil;
import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoSelfFill extends Module {

    public Setting<Float> offset;
    public Setting<Boolean> silentSwitch;
    public Setting<Boolean> packetPlace;

    public Setting<Float> triggerRange;
    public Setting<Boolean> autoDisable;

    private EnumHand oldhand;
    private int oldslot;

    public AutoSelfFill() {
        super("AutoSelfFill", Categories.EXPLOIT, false, false);
        offset = register(new Setting<>("Offset", 0.6f, 1.0f, 0.0f));
        silentSwitch = register(new Setting<>("SilentSwitch", true));
        packetPlace = register(new Setting<>("PacketPlace", true));

        triggerRange = register(new Setting<>("Range", 4.0f, 10.0f, 1.0f));
        autoDisable = register(new Setting<>("AutoDisable", true));

        oldhand = null;
        oldslot = -1;
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;

        EntityPlayer target = getNearestEnemy(triggerRange.getValue());
        if (target == null) {
            return;
        }

        int slot = InventoryUtil.findHotbarBlockWithClass(BlockTrapDoor.class);
        if (slot == -1) {
            sendMessage("TrapDoor not found");
            if (autoDisable.getValue()) disable();
            return;
        }

        BlockPos playerPos = PlayerUtil.getPlayerPos();
        BlockPos trapPos = null;
        BlockPos[] offsets = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
        for (BlockPos off : offsets) {
            BlockPos pos = playerPos.add(off);
            if (entityCheck(pos)) {
                if (!BlockUtil.getBlock(pos).equals(Blocks.AIR) || !BlockUtil.canRightClickForPlace(pos)) {
                    trapPos = pos;
                }
            }
        }

        if (trapPos == null) {
            if (autoDisable.getValue()) disable();
            return;
        }

        setItem(slot);

        double x = mc.player.posX;
        double y = mc.player.posY;
        double z = mc.player.posZ;

        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y + offset.getValue(), z, mc.player.onGround));

        EnumFacing facing = null;
        for (EnumFacing f : EnumFacing.values()) {
            if (trapPos.add(f.getDirectionVec()).equals(playerPos)) {
                facing = f;
                break;
            }
        }
        if (facing == null) facing = EnumFacing.UP;

        BlockUtil.rightClickBlock(trapPos, facing, new Vec3d(0.5, 0.8, 0.5), packetPlace.getValue());

        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, mc.player.onGround));

        restoreItem();

        if (autoDisable.getValue()) disable();
    }

    private EntityPlayer getNearestEnemy(float range) {
        EntityPlayer best = null;
        double bestDist = Double.MAX_VALUE;
        for (EntityPlayer p : mc.world.playerEntities) {
            if (p == null || p == mc.player || p.isDead) continue;
            if (FriendManager.isFriend(p.getName())) continue;
            double d = mc.player.getDistance(p);
            if (d <= range && d < bestDist) {
                bestDist = d;
                best = p;
            }
        }
        return best;
    }

    public boolean entityCheck(final BlockPos pos) {
        AxisAlignedBB box = new AxisAlignedBB(pos);
        return mc.world.getEntitiesWithinAABB(Entity.class, box, e -> e instanceof EntityEnderCrystal || e instanceof EntityPlayer).isEmpty();
    }

    public void setItem(final int slot) {
        if (slot < 0 || slot > 8) return;
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
        if (!silentSwitch.getValue()) return;
        if (oldslot != -1) {
            if (oldhand != null) mc.player.setActiveHand(oldhand);
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            oldslot = -1;
            oldhand = null;
        }
    }
}
