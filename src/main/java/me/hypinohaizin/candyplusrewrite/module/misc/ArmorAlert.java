package me.hypinohaizin.candyplusrewrite.module.misc;

import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import java.util.HashMap;
import java.util.Map;

public class ArmorAlert extends Module {

    public Setting<Float> alertPercent;
    public Setting<Float> alertDelay;

    private Map<EntityEquipmentSlot, Long> lastAlertTimes = new HashMap<>();

    public ArmorAlert() {
        super("ArmorAlert", Categories.MISC, false, false);

        alertPercent = register(new Setting<>("AlertPercent", 20f, 100f, 1f));
        alertDelay = register(new Setting<>("AlertDelay", 2000f, 1000f, 60000f));
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (mc.player == null) return;

        long currentTime = System.currentTimeMillis();

        int alertPercentInt = alertPercent.getValue().intValue();
        int alertDelayInt = alertDelay.getValue().intValue();

        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
                ItemStack armor = mc.player.getItemStackFromSlot(slot);
                if (!armor.isEmpty()) {
                    int maxDurability = armor.getMaxDamage();
                    int currentDamage = armor.getItemDamage();

                    int durabilityLeft = maxDurability - currentDamage;
                    int percent = (durabilityLeft * 100) / maxDurability;

                    long lastAlertTime = lastAlertTimes.getOrDefault(slot, 0L);

                    if (percent <= alertPercentInt && currentTime - lastAlertTime >= alertDelayInt) {
                        sendMessage(slot.name() + " armor durability is low: " + percent + "%");
                        lastAlertTimes.put(slot, currentTime);
                    }
                }
            }
        }
    }
}
