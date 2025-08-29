package me.hypinohaizin.candyplusrewrite.module.misc;

import me.hypinohaizin.candyplusrewrite.mixin.mixins.IMixinMapData;
import me.hypinohaizin.candyplusrewrite.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;

import java.util.Map;

/**
 * @author h_ypi
 * @since 2025/08/06 1:14
 */
public class AntiMapBan extends Module {

    public AntiMapBan() {
        super("AntiMapBan", Categories.MISC, false, false);
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;

        ItemStack currentItem = mc.player.inventory.getCurrentItem();
        if (!currentItem.isEmpty() && currentItem.getItem() instanceof ItemMap) {
            MapData mapData = ((ItemMap) currentItem.getItem()).getMapData(currentItem, mc.world);
            if (mapData != null) {
                getMapDecorations(mapData).clear();
            }
        }

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityItemFrame) {
                EntityItemFrame frame = (EntityItemFrame) entity;
                ItemStack frameItem = frame.getDisplayedItem();
                if (!frameItem.isEmpty() && frameItem.getItem() instanceof ItemMap) {
                    MapData mapData = ((ItemMap) frameItem.getItem()).getMapData(frameItem, frame.world);
                    if (mapData != null) {
                        getMapDecorations(mapData).clear();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketMaps) {
            event.cancel();
        }
    }

    public Map<String, MapDecoration> getMapDecorations(MapData mapData) {
        if (mapData instanceof IMixinMapData) {
            return ((IMixinMapData) mapData).getMapDecorations();
        }
        try {
            return mapData.mapDecorations;
        } catch (Throwable t) {
            return null;
        }
    }
}
