package me.hypinohaizin.candyplusrewrite.utils;

import net.minecraft.entity.player.EntityPlayer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ConcurrentModificationException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.HashMap;

public class ResistanceDetector implements Util
{
    public static HashMap<String, Integer> resistanceList;

    public static void init() {
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                final HashMap a = new HashMap<String, Integer>();
                final ArrayList i = new ArrayList();
                ResistanceDetector.resistanceList.forEach((k, v) -> {
                    if (v > 0) {
                        a.put(k, v - 1);
                    }
                    else {
                        i.add(k);
                    }
                });
                a.forEach((k, v) -> {
                    if (ResistanceDetector.resistanceList.containsKey(k)) {
                        ResistanceDetector.resistanceList.replace((String) k, (Integer) v);
                    }
                });
                a.clear();
                i.forEach(w -> {
                    ResistanceDetector.resistanceList.remove(i);
                });
            }
            catch (final ConcurrentModificationException ex) {}
        }, 0L, 1L, TimeUnit.SECONDS);
    }

    public static void onUpdate() {
        if (ResistanceDetector.mc.world != null && ResistanceDetector.mc.player != null) {
            for (final EntityPlayer uwu : ResistanceDetector.mc.world.playerEntities) {
                if (uwu.getAbsorptionAmount() < 9.0f) {
                    continue;
                }
                ResistanceDetector.resistanceList.remove(uwu.getName());
                ResistanceDetector.resistanceList.put(uwu.getName(), 180);
            }
        }
    }

    static {
        ResistanceDetector.resistanceList = new HashMap<>();
    }
}
