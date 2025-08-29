package me.hypinohaizin.candyplusrewrite.utils;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.network.play.client.CPacketHeldItemChange;

import java.util.function.Predicate;

public class InventoryUtil implements Util
{
    public static int offhandSlot = 45;
    public static final int HOTBAR_SIZE = 9;
    public static final int INVENTORY_SIZE = 36;
    public static final int ARMOR_SIZE = 4;

    public static void switchToHotbarSlot(int slot, boolean silent) {
        if (InventoryUtil.mc.player.inventory.currentItem == slot || slot < 0) {
            return;
        }
        if (silent) {
            InventoryUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            InventoryUtil.mc.playerController.updateController();
        } else {
            InventoryUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            InventoryUtil.mc.player.inventory.currentItem = slot;
            InventoryUtil.mc.playerController.updateController();
        }
    }

    public static int findHotbarBlock(final Block blockIn) {
        for (int i = 0; i < HOTBAR_SIZE; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block == blockIn) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int findHotbarItem(Item item) {
        for (int i = 0; i < HOTBAR_SIZE; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static int findHotbarBlockWithClass(final Class<?> clazz) {
        for (int i = 0; i < HOTBAR_SIZE; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block.getClass().equals(clazz)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int getItemHotbar(final Item input) {
        for (int i = 0; i < HOTBAR_SIZE; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == input) {
                return i;
            }
        }
        return -1;
    }

    public static int findItem(final Item item) {
        for (int i = HOTBAR_SIZE; i < INVENTORY_SIZE; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static int findBlock(final Block block) {
        for (int i = HOTBAR_SIZE; i < INVENTORY_SIZE; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
                final Block foundBlock = ((ItemBlock) stack.getItem()).getBlock();
                if (foundBlock == block) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int findItemAnywhere(final Item item) {
        int slot = getItemHotbar(item);
        if (slot != -1) {
            return slot;
        }
        return findItem(item);
    }

    public static int findBlockAnywhere(final Block block) {
        int slot = findHotbarBlock(block);
        if (slot != -1) {
            return slot;
        }
        return findBlock(block);
    }

    public static int getItemCount(final Item item) {
        int count = 0;
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static int getBlockCount(final Block block) {
        int count = 0;
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
                final Block foundBlock = ((ItemBlock) stack.getItem()).getBlock();
                if (foundBlock == block) {
                    count += stack.getCount();
                }
            }
        }
        return count;
    }

    public static int findEmptyHotbar() {
        for (int i = 0; i < HOTBAR_SIZE; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public static int findEmptySlot() {
        for (int i = HOTBAR_SIZE; i < INVENTORY_SIZE; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public static boolean hasItemInOffhand(final Item item) {
        final ItemStack offhandStack = mc.player.getHeldItemOffhand();
        return !offhandStack.isEmpty() && offhandStack.getItem() == item;
    }

    public static boolean hasBlockInOffhand(final Block block) {
        final ItemStack offhandStack = mc.player.getHeldItemOffhand();
        if (!offhandStack.isEmpty() && offhandStack.getItem() instanceof ItemBlock) {
            final Block offhandBlock = ((ItemBlock) offhandStack.getItem()).getBlock();
            return offhandBlock == block;
        }
        return false;
    }

    public static boolean moveToHotbar(final int sourceSlot) {
        int emptySlot = findEmptyHotbar();
        if (emptySlot != -1) {
            moveItemTo(sourceSlot, emptySlot);
            return true;
        }
        return false;
    }

    public static int getWindowSlot(final int slot) {
        if (slot < HOTBAR_SIZE) {
            return slot + 36;
        } else if (slot < INVENTORY_SIZE) {
            return slot;
        } else if (slot == offhandSlot) {
            return 45;
        }
        return slot;
    }

    public static boolean safeItemMove(final int sourceSlot, final int targetSlot) {
        try {
            if (sourceSlot < 0 || targetSlot < 0 || sourceSlot >= INVENTORY_SIZE + 1 || targetSlot >= INVENTORY_SIZE + 1) {
                return false;
            }
            moveItemTo(sourceSlot, targetSlot);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void moveItemTo(final int item, final int empty) {
        int windowItem = getWindowSlot(item);
        int windowEmpty = getWindowSlot(empty);

        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, windowItem, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, windowEmpty, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, windowItem, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
    }

    public static void moveItem(final int item) {
        int windowSlot = getWindowSlot(item);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, windowSlot, 0, ClickType.QUICK_MOVE, mc.player);
        mc.playerController.updateController();
    }

    public static void dropItem(final int item) {
        int windowSlot = getWindowSlot(item);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, windowSlot, 0, ClickType.THROW, mc.player);
        mc.playerController.updateController();
    }

    public static void dropStack(final int item) {
        int windowSlot = getWindowSlot(item);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, windowSlot, 1, ClickType.THROW, mc.player);
        mc.playerController.updateController();
    }

    public static boolean isItemDamaged(final int slot, final double threshold) {
        final ItemStack stack = mc.player.inventory.getStackInSlot(slot);
        if (stack.isEmpty() || !stack.isItemStackDamageable()) {
            return false;
        }
        double durabilityPercentage = (double) (stack.getMaxDamage() - stack.getItemDamage()) / stack.getMaxDamage();
        return durabilityPercentage < threshold;
    }


    public static boolean isHolding(Item item) {
        return isHolding((EntityLivingBase)mc.player, item);
    }

    public static boolean isHolding(EntityLivingBase entity, Item item) {
        ItemStack mainHand = entity.getHeldItemMainhand();
        ItemStack offHand = entity.getHeldItemOffhand();
        return areSame(mainHand, item) || areSame(offHand, item);
    }

    public static void click(int slot) {
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
    }

    public static ItemStack get(int slot) {
        if (slot == -2) {
            return mc.player.inventory.getItemStack();
        }
        return mc.player.inventoryContainer.getInventory().get(slot);
    }

    public static void put(int slot, ItemStack stack) {
        if (slot == -2) {
            mc.player.inventory.setItemStack(stack);
        }
        mc.player.inventoryContainer.putStackInSlot(slot, stack);
    }

    public static boolean validScreen() {
        return !(mc.currentScreen instanceof GuiContainer) || mc.currentScreen instanceof GuiInventory;
    }

    public static boolean equals(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null) return stack2 == null;
        if (stack2 == null) return false;
        boolean empty1 = stack1.isEmpty();
        boolean empty2 = stack2.isEmpty();
        return empty1 == empty2 && stack1.getDisplayName().equals(stack2.getDisplayName())
                && stack1.getItem() == stack2.getItem()
                && stack1.getHasSubtypes() == stack2.getHasSubtypes()
                && stack1.getMetadata() == stack2.getMetadata()
                && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    public static void clickLocked(int slot, int to, Item inSlot, Item inTo) {
        if ((slot == -1 || get(slot).getItem() == inSlot) && get(to).getItem() == inTo) {
            click(to);
        }
    }

    public static void switchTo(int slot) {
        if (mc.player.inventory.currentItem != slot && slot > -1 && slot < 9) {
            mc.player.inventory.currentItem = slot;
            syncItem();
        }
    }

    public static void switchToAlt(int slot) {
        int altSlot = hotbarToInventory(slot);
        if (mc.player.inventory.currentItem != altSlot && altSlot > 35 && altSlot < 45) {
            mc.playerController.windowClick(0, altSlot, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
        }
    }

    public static boolean areSame(ItemStack stack, Item item) {
        return stack != null && Item.getIdFromItem(stack.getItem()) == Item.getIdFromItem(item);
    }

    public static int findInHotbar(Predicate<ItemStack> condition) {
        for (int i = 8; i >= 0; --i) {
            if (condition.test(mc.player.inventory.getStackInSlot(i))) {
                if (mc.player.inventory.currentItem == i) return i;
            }
        }
        return -1;
    }

    public static void syncItem() {
        mc.playerController.updateController();
    }

    public static int hotbarToInventory(int slot) {
        if (slot == -2) return 45;
        if (slot > -1 && slot < 9) return 36 + slot;
        return slot;
    }

    public static boolean isFood(ItemStack stack) {
        return stack.getItem() instanceof ItemFood;
    }

    public static double getDamageInPercent(ItemStack stack) {
        double percent = (double)stack.getItemDamage() / (double)stack.getMaxDamage();
        if (percent == 0.0) return 100.0;
        if (percent == 1.0) return 0.0;
        return 100.0 - percent * 100.0;
    }

    public enum SwapEnum {
        SILENT,
        ALTERNATIVE,
        NONE
    }
}