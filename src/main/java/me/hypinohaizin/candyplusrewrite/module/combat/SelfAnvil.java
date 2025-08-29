package me.hypinohaizin.candyplusrewrite.module.combat;

import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;
import net.minecraft.util.EnumFacing;
import me.hypinohaizin.candyplusrewrite.utils.BlockUtil;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class SelfAnvil extends Module
{
    public Setting<Boolean> packetPlace;
    public Setting<Boolean> silentSwitch;
    public Setting<Boolean> entityCheck;
    public Setting<Boolean> crystalOnly;
    public BlockPos basePos;
    public int stage;
    private EnumHand oldhand;
    private int oldslot;
    
    public SelfAnvil() {
        super("SelfAnvil", Categories.COMBAT, false, false);
        packetPlace = register(new Setting<>("PacketPlace", true));
        silentSwitch = register(new Setting<>("SilentSwitch", true));
        entityCheck = register(new Setting<>("EntityCheck", true));
        crystalOnly = register(new Setting<>("CrystalOnly", false, v -> entityCheck.getValue()));
        oldhand = null;
        oldslot = -1;
    }
    
    @Override
    public void onEnable() {
        basePos = null;
        stage = 0;
    }
    
    @Override
    public void onTick() {
        if (nullCheck()) {
            return;
        }
        final int obby = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        final int anvil = InventoryUtil.findHotbarBlock(Blocks.ANVIL);
        if (obby == -1 || anvil == -1) {
            sendMessage("Cannot find materials! disabling");
            disable();
            return;
        }
        final BlockPos anvilPos = PlayerUtil.getPlayerPos().add(0, 2, 0);
        if (!BlockUtil.hasNeighbour(anvilPos)) {
            if ((basePos = findPos()) == null) {
                sendMessage("Cannot find space! disabling");
                disable();
                return;
            }
            setItem(obby);
            final BlockPos pos0 = basePos.add(0, 1, 0);
            final BlockPos pos2 = basePos.add(0, 2, 0);
            BlockUtil.placeBlock(pos0, packetPlace.getValue());
            BlockUtil.rightClickBlock(pos0, EnumFacing.UP, packetPlace.getValue());
            setItem(anvil);
            EnumFacing facing = null;
            for (final EnumFacing f : EnumFacing.values()) {
                if (pos2.add(f.getDirectionVec()).equals(anvilPos)) {
                    facing = f;
                }
            }
            BlockUtil.rightClickBlock(anvilPos, facing, packetPlace.getValue());
            restoreItem();
            disable();
        }
        else {
            setItem(anvil);
            BlockUtil.placeBlock(anvilPos, packetPlace.getValue());
            restoreItem();
            disable();
        }
    }
    
    public BlockPos findPos() {
        final BlockPos playerPos = PlayerUtil.getPlayerPos();
        final BlockPos lookingPos = playerPos.add(BlockUtil.getBackwardFacing(PlayerUtil.getLookingFacing()).getDirectionVec());
        final List<BlockPos> possiblePlacePositions = new ArrayList<>();
        final BlockPos[] array;
        final BlockPos[] offsets = array = new BlockPos[] { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
        for (final BlockPos offset : array) {
            final BlockPos pos = playerPos.add(offset);
            if (BlockUtil.getBlock(pos) != Blocks.AIR) {
                if (BlockUtil.canRightClickForPlace(pos)) {
                    final BlockPos pos2 = pos.add(0, 1, 0);
                    if (!entityCheck(pos2)) {
                        final BlockPos pos3 = pos2.add(0, 1, 0);
                        if (!entityCheck(pos3)) {
                            final BlockPos anvil = playerPos.add(0, 2, 0);
                            if (!entityCheck(anvil)) {
                                possiblePlacePositions.add(pos);
                            }
                        }
                    }
                }
            }
        }
        return possiblePlacePositions.stream().min(Comparator.comparing(b -> lookingPos.getDistance(b.getX(), b.getY(), b.getZ()))).orElse(null);
    }
    
    public boolean entityCheck(final BlockPos pos) {
        if (!entityCheck.getValue()) {
            return false;
        }
        for (final Entity e : SelfAnvil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (!(e instanceof EntityEnderCrystal) && crystalOnly.getValue()) {
                continue;
            }
            return true;
        }
        return false;
    }
    
    public void setItem(final int slot) {
        if (silentSwitch.getValue()) {
            oldhand = null;
            if (SelfAnvil.mc.player.isHandActive()) {
                oldhand = SelfAnvil.mc.player.getActiveHand();
            }
            oldslot = SelfAnvil.mc.player.inventory.currentItem;
            SelfAnvil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        }
        else {
            SelfAnvil.mc.player.inventory.currentItem = slot;
            SelfAnvil.mc.playerController.updateController();
        }
    }
    
    public void restoreItem() {
        if (oldslot != -1 && silentSwitch.getValue()) {
            if (oldhand != null) {
                SelfAnvil.mc.player.setActiveHand(oldhand);
            }
            SelfAnvil.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            oldslot = -1;
            oldhand = null;
        }
    }
}
