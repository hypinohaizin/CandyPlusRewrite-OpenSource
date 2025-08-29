package me.hypinohaizin.candyplusrewrite.mixin.mixins;

import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author h_ypi
 * @since 2025/08/06 1:17
 */

import java.util.Map;

@Mixin(MapData.class)
public interface IMixinMapData {
    @Accessor("mapDecorations")
    Map<String, MapDecoration> getMapDecorations();
}