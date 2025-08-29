package me.hypinohaizin.candyplusrewrite.module.combat;

import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class AutoTotem extends Module
{
    public Setting<Boolean> packet;
    public Setting<Boolean> doublE;
    
    public AutoTotem() {
        super("AutoTotem", Categories.COMBAT, false, false);
        packet = register(new Setting<>("Packet", true));
        doublE = register(new Setting<>("Double", true));
    }
    
    @Override
    public void onTotemPop(final EntityPlayer player) {
        if (player.getEntityId() != AutoTotem.mc.player.getEntityId()) {
            return;
        }
        if (packet.getValue()) {
            doTotem();
        }
    }
    
    @Override
    public void onTick() {
        if (nullCheck()) {
            return;
        }
        if (shouldTotem()) {
            doTotem();
        }
    }
    
    @Override
    public void onUpdate() {
        if (nullCheck()) {
            return;
        }
        if (shouldTotem() && doublE.getValue()) {
            doTotem();
        }
    }
    
    public void doTotem() {
        final int totem = findTotemSlot();
        if (totem == -1) {
            return;
        }
        InventoryUtil.moveItemTo(totem, InventoryUtil.offhandSlot);
    }
    
    public boolean shouldTotem() {
        return AutoTotem.mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING;
    }
    
    public int findTotemSlot() {
        for (int i = 0; i < AutoTotem.mc.player.inventoryContainer.getInventory().size(); ++i) {
            if (i != InventoryUtil.offhandSlot) {
                final ItemStack stack = AutoTotem.mc.player.inventoryContainer.getInventory().get(i);
                if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                    return i;
                }
            }
        }
        return -1;
    }
}
