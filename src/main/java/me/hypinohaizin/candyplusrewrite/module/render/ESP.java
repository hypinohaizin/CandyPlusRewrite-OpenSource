package me.hypinohaizin.candyplusrewrite.module.render;

import net.minecraft.util.math.AxisAlignedBB;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil3D;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

import java.util.stream.Collectors;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class ESP extends Module
{
    public Setting<Float> width;
    public Setting<Color> color;
    
    public ESP() {
        super("ESP", Categories.RENDER, false, false);
        width = register(new Setting<>("Width", 1.5f, 5.0f, 0.5f));
        color = register(new Setting<>("Color", new Color(255, 255, 255, 255)));
    }
    
    @Override
    public void onRender3D() {
        if (nullCheck()) {
            return;
        }
        for (final Object player : ESP.mc.world.playerEntities.stream().filter(e -> e.getEntityId() != ESP.mc.player.getEntityId()).collect(Collectors.toList())) {
            drawESP((EntityPlayer) player);
        }
    }
    
    public void drawESP(final EntityPlayer player) {
        GlStateManager.pushMatrix();
        final AxisAlignedBB bb = player.getCollisionBoundingBox();
        final double z = bb.minZ + (bb.maxZ - bb.minZ) / 2.0;
        RenderUtil3D.drawLine(bb.minX, bb.maxY, z, bb.maxX, bb.maxY, z, color.getValue(), width.getValue());
        RenderUtil3D.drawLine(bb.minX, bb.minY, z, bb.maxX, bb.minY, z, color.getValue(), width.getValue());
        RenderUtil3D.drawLine(bb.minX, bb.minY, z, bb.minX, bb.maxY, z, color.getValue(), width.getValue());
        RenderUtil3D.drawLine(bb.maxX, bb.minY, z, bb.maxX, bb.maxY, z, color.getValue(), width.getValue());
        final double x = bb.minX - 0.28;
        final double y = bb.minY;
        final double width = 0.04;
        final double height = bb.maxY - bb.minY;
        RenderUtil3D.drawRect(x, y, z, width, height, new Color(0, 0, 0, 255), 255, 63);
        RenderUtil3D.drawRect(x, y, z, width, height * (player.getHealth() / 36.0), getHealthColor((int)player.getHealth()), 255, 63);
        GlStateManager.popMatrix();
    }
    
    private static Color getHealthColor(int health) {
        if (health > 36) {
            health = 36;
        }
        if (health < 0) {
            health = 0;
        }
        int red = 0;
        int green = 0;
        if (health > 18) {
            red = (int)((36 - health) * 14.1666666667);
            green = 255;
        }
        else {
            red = 255;
            green = (int)(255.0 - (18 - health) * 14.1666666667);
        }
        return new Color(red, green, 0, 255);
    }
}
