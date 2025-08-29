package me.hypinohaizin.candyplusrewrite.module.misc;

import net.minecraft.item.Item;
import me.hypinohaizin.candyplusrewrite.module.combat.AutoMend;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemBow;
import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import net.minecraft.item.ItemSword;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class AutoDrop extends Module
{
    public Setting<Boolean> sword;
    public Setting<Boolean> bow;
    public Setting<Boolean> pickel;
    public Setting<Boolean> armor;
    public Setting<Boolean> autoMend;
    public Setting<Float> damege;
    public Setting<Float> delay;
    public Timer timer;
    
    public AutoDrop() {
        super("AutoDrop", Categories.MISC, false, false);
        sword = register(new Setting<>("Sword", true));
        bow = register(new Setting<>("Bow", true));
        pickel = register(new Setting<>("Pickel", true));
        armor = register(new Setting<>("DamageArmor", true));
        autoMend = register(new Setting<>("PauseWhenAutoMend", true, v -> armor.getValue()));
        damege = register(new Setting<>("MinDamege", 50.0f, 100.0f, 0.0f, v -> armor.getValue()));
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
            for (int i = 9; i < 36; ++i) {
                final ItemStack itemStack = AutoDrop.mc.player.inventoryContainer.getInventory().get(i);
                final Item item = itemStack.getItem();
                if (!itemStack.isEmpty()) {
                    if (itemStack.getItem() != Items.AIR) {
                        if (item instanceof ItemSword && sword.getValue()) {
                            InventoryUtil.dropItem(i);
                            break;
                        }
                        if (item instanceof ItemBow && bow.getValue()) {
                            InventoryUtil.dropItem(i);
                            break;
                        }
                        if (item instanceof ItemPickaxe && pickel.getValue()) {
                            InventoryUtil.dropItem(i);
                            break;
                        }
                        if (item instanceof ItemArmor && armor.getValue()) {
                            final Module automend = CandyPlusRewrite.m_module.getModuleWithClass(AutoMend.class);
                            if (!automend.isEnable || !autoMend.getValue()) {
                                if (getDamage(itemStack) <= damege.getValue()) {
                                    InventoryUtil.dropItem(i);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public float getDamage(final ItemStack itemStack) {
        return (itemStack.getMaxDamage() - itemStack.getItemDamage()) / (itemStack.getMaxDamage() * 1.0f) * 100.0f;
    }
}
