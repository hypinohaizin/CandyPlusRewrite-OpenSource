package me.hypinohaizin.candyplusrewrite.module.render;

import java.util.List;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil3D;
import net.minecraft.init.Blocks;
import me.hypinohaizin.candyplusrewrite.utils.BlockUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import java.util.ArrayList;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class BurrowESP extends Module
{
    public Setting<Boolean> obby;
    public Setting<Color> color;
    
    public BurrowESP() {
        super("BurrowESP", Categories.RENDER, false, false);
        obby = register(new Setting<>("Only Obby", false));
        color = register(new Setting<>("Color", new Color(255, 64, 207, 210)));
    }
    
    @Override
    public void onRender3D() {
        final List<EntityPlayer> players = new ArrayList<EntityPlayer>(BurrowESP.mc.world.playerEntities);
        for (final EntityPlayer player : players) {
            final BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
            if (BlockUtil.getBlock(pos) != Blocks.AIR && (!obby.getValue() || BlockUtil.getBlock(pos) == Blocks.OBSIDIAN)) {
                RenderUtil3D.drawBox(pos, 1.0, color.getValue(), 63);
            }
        }
    }
}
