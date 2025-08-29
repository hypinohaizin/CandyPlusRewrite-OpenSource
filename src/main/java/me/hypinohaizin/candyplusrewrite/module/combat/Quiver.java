package me.hypinohaizin.candyplusrewrite.module.combat;

import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.potion.PotionUtils;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;

import java.util.List;
import java.util.ArrayList;

public class Quiver extends Module {
    private final Setting<Integer> tickDelay;

    public Quiver() {
        super("Quiver", Categories.COMBAT, false, false);
        tickDelay = register(new Setting<>("TickDelay", 3, 0, 8));
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) return;

        if (mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow
                && mc.player.isHandActive()
                && mc.player.getItemInUseMaxCount() >= tickDelay.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(
                    mc.player.rotationYaw,
                    -90.0f,
                    mc.player.onGround
            ));
            mc.playerController.onStoppedUsingItem(mc.player);
        }

        List<Integer> arrowSlots = getItemInventory(Items.TIPPED_ARROW);
        if (arrowSlots.size() == 1 && arrowSlots.get(0) == -1) return;

        int speedSlot = -1, strengthSlot = -1;
        for (int slot : arrowSlots) {
            ResourceLocation loc = PotionUtils.getPotionFromItem(mc.player.inventory.getStackInSlot(slot))
                    .getRegistryName();
            if (loc == null) continue;
            String path = loc.getPath();
            if (path.contains("swiftness")) {
                speedSlot = slot;
            } else if (path.contains("strength")) {
                strengthSlot = slot;
            }
        }

        if (speedSlot != -1) {
            silentSwitchTo(speedSlot);
            mc.playerController.onStoppedUsingItem(mc.player);
            restoreSwitch();
        }
    }

    private List<Integer> getItemInventory(Item item) {
        List<Integer> slots = new ArrayList<>();
        for (int i = 9; i < 36; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                slots.add(i);
            }
        }
        if (slots.isEmpty()) {
            slots.add(-1);
        }
        return slots;
    }

    private int lastSlot = -1;
    private void silentSwitchTo(int slot) {
        if (slot < 0) return;
        lastSlot = mc.player.inventory.currentItem;
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
    }

    private void restoreSwitch() {
        if (lastSlot != -1) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(lastSlot));
            lastSlot = -1;
        }
    }
}
