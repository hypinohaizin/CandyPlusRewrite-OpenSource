package me.hypinohaizin.candyplusrewrite.module.misc;

import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.utils.BlockUtil;
import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InstantWither extends Module {

    public Setting<Integer> delay;
    private int tickDelay;

    public InstantWither() {
        super("InstantWither", Categories.MISC, false, false);
        delay = register(new Setting<>("Delay", 2, 10, 0));
        tickDelay = 0;
    }

    @Override
    public void onDisable() {
        tickDelay = 0;
    }

    @Override
    public void onTick() {
        if (tickDelay > 0) tickDelay--;
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (nullCheck() || tickDelay > 0) return;
        if (event.getEntityPlayer() != mc.player) return;
        if (mc.player.getHeldItem(event.getHand()).getItem() != Item.getItemFromBlock(Blocks.SOUL_SAND)) return;

        BlockPos base = event.getPos().offset(event.getFace());
        EnumFacing front = mc.player.getHorizontalFacing();
        EnumFacing left = front.rotateYCCW();

        int sandSlot = InventoryUtil.findHotbarBlock(Blocks.SOUL_SAND);
        int skullSlot = InventoryUtil.findHotbarItem(Item.getItemById(397));
        if (sandSlot == -1 || skullSlot == -1) return;

        int originalSlot = mc.player.inventory.currentItem;
        boolean placedAnything = false;

        BlockPos s0 = base;
        BlockPos s1 = base.up(1);
        BlockPos s2 = base.up(1).offset(left, 1);
        BlockPos s3 = base.up(1).offset(left, -1);

        BlockPos k0 = base.up(2).offset(left, 1);
        BlockPos k1 = base.up(2);
        BlockPos k2 = base.up(2).offset(left, -1);

        if (canReplace(s0)) {
            InventoryUtil.switchToHotbarSlot(sandSlot, true);
            if (BlockUtil.placeBlock(s0, false)) placedAnything = true;
        }
        if (canReplace(s1)) {
            InventoryUtil.switchToHotbarSlot(sandSlot, true);
            if (BlockUtil.placeBlock(s1, false)) placedAnything = true;
        }
        if (canReplace(s2)) {
            InventoryUtil.switchToHotbarSlot(sandSlot, true);
            if (BlockUtil.placeBlock(s2, false)) placedAnything = true;
        }
        if (canReplace(s3)) {
            InventoryUtil.switchToHotbarSlot(sandSlot, true);
            if (BlockUtil.placeBlock(s3, false)) placedAnything = true;
        }

        if (canReplace(k0)) {
            InventoryUtil.switchToHotbarSlot(skullSlot, true);
            if (BlockUtil.placeBlock(k0, false)) placedAnything = true;
        }
        if (canReplace(k1)) {
            InventoryUtil.switchToHotbarSlot(skullSlot, true);
            if (BlockUtil.placeBlock(k1, false)) placedAnything = true;
        }
        if (canReplace(k2)) {
            InventoryUtil.switchToHotbarSlot(skullSlot, true);
            if (BlockUtil.placeBlock(k2, false)) placedAnything = true;
        }

        if (mc.player.inventory.currentItem != originalSlot) {
            InventoryUtil.switchToHotbarSlot(originalSlot, true);
        }

        if (placedAnything) {
            tickDelay = delay.getValue();
        }
    }

    private boolean canReplace(BlockPos pos) {
        return mc.world.getBlockState(pos).getMaterial().isReplaceable();
    }
}
