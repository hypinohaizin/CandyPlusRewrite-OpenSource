package me.hypinohaizin.candyplusrewrite.module.render;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import me.hypinohaizin.candyplusrewrite.event.events.render.RenderEntityModelEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;
import net.minecraft.entity.Entity;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.item.EntityEnderCrystal;
import java.util.Map;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class CandyCrystal extends Module
{
    public Setting<Boolean> chams;
    public Setting<Boolean> throughWalls;
    public Setting<Boolean> wireframeThroughWalls;
    public Setting<Boolean> glint;
    public Setting<Boolean> wireframe;
    public Setting<Float> scale;
    public Setting<Float> lineWidth;
    public Setting<Boolean> colorSync;
    public Setting<Boolean> rainbow;
    public Setting<Integer> saturation;
    public Setting<Integer> brightness;
    public Setting<Integer> speed;
    public Setting<Boolean> xqz;
    public Setting<Color> color;
    public Setting<Color> hiddenColor;
    public Setting<Integer> alpha;
    public Map<EntityEnderCrystal, Float> scaleMap;
    public float hue;
    public Map<Integer, Integer> colorHeightMap;
    
    public CandyCrystal() {
        super("CandyCrystal", Categories.RENDER, false, false);
        chams = register(new Setting<>("Chams", false));
        throughWalls = register(new Setting<>("ThroughWalls", true));
        wireframeThroughWalls = register(new Setting<>("WireThroughWalls", true));
        glint = register(new Setting<>("Glint", false, v -> chams.getValue()));
        wireframe = register(new Setting<>("Wireframe", false));
        scale = register(new Setting<>("Scale", 1.0f, 10.0f, 0.1f));
        lineWidth = register(new Setting<>("LineWidth", 1.0f, 3.0f, 0.1f));
        colorSync = register(new Setting<>("Sync", false));
        rainbow = register(new Setting<>("Rainbow", false));
        saturation = register(new Setting<>("Saturation", 50, 100, 0, v -> rainbow.getValue()));
        brightness = register(new Setting<>("Brightness", 100, 100, 0, v -> rainbow.getValue()));
        speed = register(new Setting<>("Speed", 40, 100, 1, v -> rainbow.getValue()));
        xqz = register(new Setting<>("XQZ", false, v -> !rainbow.getValue() && throughWalls.getValue()));
        color = register(new Setting<>("Color", new Color(255, 255, 255, 100), v -> !rainbow.getValue()));
        hiddenColor = register(new Setting<>("Hidden Color", new Color(255, 255, 255, 100), v -> xqz.getValue()));
        alpha = register(new Setting<>("Alpha", 50, 255, 0, v -> !rainbow.getValue()));
        scaleMap = new ConcurrentHashMap<>();
        colorHeightMap = new HashMap<>();
    }
    
    @Override
    public void onUpdate() {
        try {
            for (final Entity crystal : CandyCrystal.mc.world.loadedEntityList) {
                if (!(crystal instanceof EntityEnderCrystal)) {
                    continue;
                }
                if (!scaleMap.containsKey(crystal)) {
                    scaleMap.put((EntityEnderCrystal)crystal, 3.125E-4f);
                }
                else {
                    scaleMap.put((EntityEnderCrystal)crystal, scaleMap.get(crystal) + 3.125E-4f);
                }
                if (scaleMap.get(crystal) < 0.0625f * scale.getValue()) {
                    continue;
                }
                scaleMap.remove(crystal);
            }
        }
        catch (final Exception ex) {}
    }
    
    @SubscribeEvent
    public void onReceivePacket(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketDestroyEntities) {
            final SPacketDestroyEntities packet = (SPacketDestroyEntities)event.getPacket();
            for (final int id : packet.getEntityIDs()) {
                final Entity entity = CandyCrystal.mc.world.getEntityByID(id);
                if (entity instanceof EntityEnderCrystal) {
                    scaleMap.remove(entity);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onRenderModel(final RenderEntityModelEvent event) {
        if (!(event.entity instanceof EntityEnderCrystal) || !wireframe.getValue()) {
            return;
        }
        final Color color = colorSync.getValue() ? getCurrentColor() : this.color.getValue();
        final boolean fancyGraphics = CandyCrystal.mc.gameSettings.fancyGraphics;
        CandyCrystal.mc.gameSettings.fancyGraphics = false;
        final float gamma = CandyCrystal.mc.gameSettings.gammaSetting;
        CandyCrystal.mc.gameSettings.gammaSetting = 10000.0f;
        GL11.glPushMatrix();
        GL11.glPushAttrib(1048575);
        GL11.glPolygonMode(1032, 6913);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        if (wireframeThroughWalls.getValue()) {
            GL11.glDisable(2929);
        }
        GL11.glEnable(2848);
        GL11.glEnable(3042);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GlStateManager.glLineWidth(lineWidth.getValue());
        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
    
    @Override
    public void onTick() {
        final int colorSpeed = 101 - speed.getValue();
        final float hue = System.currentTimeMillis() % (360 * colorSpeed) / (360.0f * colorSpeed);
        this.hue = hue;
        float tempHue = hue;
        for (int i = 0; i <= 510; ++i) {
            colorHeightMap.put(i, Color.HSBtoRGB(tempHue, saturation.getValue() / 255.0f, brightness.getValue() / 255.0f));
            tempHue += 0.0013071896f;
        }
    }
    
    public Color getCurrentColor() {
        return Color.getHSBColor(hue, saturation.getValue() / 255.0f, brightness.getValue() / 255.0f);
    }
}
