package me.hypinohaizin.candyplusrewrite.module.misc;

import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import net.minecraft.item.ItemStack;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class Refill extends Module
{
    public Setting<Float> delay;
    public Timer timer;
    
    public Refill() {
        super("Refill", Categories.MISC, false, false);
        delay = register(new Setting<>("Delay", 1.0f, 10.0f, 0.0f));
        timer = new Timer();
    }
    
    @Override
    public void onUpdate() {
        if (nullCheck()) {
            return;
        }
        if (timer == null) {
            timer = new Timer();
        }
        if (timer.passedDms(delay.getValue())) {
            timer.reset();
            for (int i = 0; i < 9; ++i) {
                final ItemStack itemstack = Refill.mc.player.inventory.mainInventory.get(i);
                if (!itemstack.isEmpty()) {
                    if (itemstack.isStackable()) {
                        if (itemstack.getCount() < itemstack.getMaxStackSize()) {
                            if (doRefill(itemstack)) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean doRefill(final ItemStack stack) {
        for (int i = 9; i < 36; ++i) {
            final ItemStack item = Refill.mc.player.inventory.getStackInSlot(i);
            if (CanItemBeMergedWith(item, stack)) {
                InventoryUtil.moveItem(i);
                return true;
            }
        }
        return false;
    }
    
    private boolean CanItemBeMergedWith(final ItemStack p_Source, final ItemStack p_Target) {
        return p_Source.getItem() == p_Target.getItem() && p_Source.getDisplayName().equals(p_Target.getDisplayName());
    }
}
