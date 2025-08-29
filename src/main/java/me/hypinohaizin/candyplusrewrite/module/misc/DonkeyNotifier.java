package me.hypinohaizin.candyplusrewrite.module.misc;

import java.util.Iterator;
import me.hypinohaizin.candyplusrewrite.utils.StringUtil;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityDonkey;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import java.util.List;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class DonkeyNotifier extends Module
{
    private final Setting<Boolean> donkey;
    private final Setting<Boolean> llama;
    private List<Entity> entities;
    
    public DonkeyNotifier() {
        super("DonkeyNotifer", Categories.MISC, false, false);
        donkey = register(new Setting<>("Donkey", true));
        llama = register(new Setting<>("Llama", true));
        entities = new ArrayList<>();
    }
    
    @Override
    public void onDisable() {
        entities = new ArrayList<>();
    }
    
    @Override
    public void onUpdate() {
        if (nullCheck()) {
            return;
        }
        final List<Entity> donkeys = new ArrayList<>(DonkeyNotifier.mc.world.loadedEntityList);
        Entity e;
        donkeys.removeIf(e2 -> (!donkey.getValue() || !(e2 instanceof EntityDonkey)) && (!llama.value || !(e2 instanceof EntityLlama)));
        final Iterator<Entity> iterator = donkeys.iterator();
        while (iterator.hasNext()) {
            e = iterator.next();
            if (!entities.contains(e)) {
                entities.add(e);
                sendMessage("Found a " + ((e instanceof EntityDonkey) ? "Donkey" : "Llama") + " at " + StringUtil.getPositionString(e.getPosition()));
            }
        }
    }
}
