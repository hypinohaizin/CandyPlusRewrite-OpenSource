package me.hypinohaizin.candyplusrewrite.module.render;

import me.hypinohaizin.candyplusrewrite.utils.RenderUtil3D;
import net.minecraft.init.Blocks;
import me.hypinohaizin.candyplusrewrite.utils.BlockUtil;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;

import java.util.stream.Collectors;
import java.util.List;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class CityESP extends Module
{
    public Setting<Float> range;
    public Setting<Color> color;
    public Setting<Boolean> outline;
    public Setting<Float> width;
    
    public CityESP() {
        super("CityESP", Categories.RENDER, false, false);
        range = register(new Setting<>("Range", 10.0f, 12.0f, 1.0f));
        color = register(new Setting<>("Color", new Color(230, 50, 50, 100)));
        outline = register(new Setting<>("Outline", false));
        width = register(new Setting<>("Width", 3.0f, 6.0f, 0.1f, v -> outline.getValue()));
    }
    
    @Override
    public void onRender3D() {
        final List<EntityPlayer> players = CityESP.mc.world.playerEntities.stream().filter(e -> e.getEntityId() != CityESP.mc.player.getEntityId()).collect(Collectors.toList());
        for (final EntityPlayer player : players) {
            final BlockPos[] array;
            final BlockPos[] surroundOffset = array = new BlockPos[] { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
            for (final BlockPos offset : array) {
                final BlockPos position = new BlockPos(player.posX, player.posY, player.posZ).add(offset);
                if (PlayerUtil.getDistance(position) <= range.getValue()) {
                    if (BlockUtil.getBlock(position) == Blocks.OBSIDIAN) {
                        RenderUtil3D.drawBox(position, 1.0, color.getValue(), 63);
                        if (outline.getValue()) {
                            RenderUtil3D.drawBoundingBox(position, 1.0, width.getValue(), color.getValue());
                        }
                    }
                }
            }
        }
    }
}
