package me.hypinohaizin.candyplusrewrite.module.render;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import me.hypinohaizin.candyplusrewrite.event.events.player.UpdateWalkingPlayerEvent;
import java.util.ArrayList;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import java.util.List;
import net.minecraft.client.renderer.entity.RenderPlayer;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class Afterglow extends Module
{
    public Setting<Float> delay;
    public Setting<Color> color;
    public Setting<Float> fadeSpeed;
    public Setting<Float> thickness;
    public static RenderPlayer render;
    public List<AfterGlow> glows;
    public Timer timer;
    
    public Afterglow() {
        super("Afterglow", Categories.RENDER, false, false);
        delay = register(new Setting<>("Delay", 10.0f, 20.0f, 1.0f));
        color = register(new Setting<>("Color", new Color(255, 255, 255, 200)));
        fadeSpeed = register(new Setting<>("Fadeout Speed", 10.0f, 20.0f, 1.0f));
        thickness = register(new Setting<>("Thickness", 3.0f, 10.0f, 1.0f));
        glows = new ArrayList<>();
        timer = new Timer();
    }
    
    @SubscribeEvent
    public void onUpdateWalkingEvent(final UpdateWalkingPlayerEvent event) {
        if (timer == null) {
            timer = new Timer();
        }
        if (timer.passedDms(delay.getValue())) {
            if (Afterglow.mc.player.motionX == 0.0 && Afterglow.mc.player.motionZ == 0.0) {
                return;
            }
            final double[] forward = PlayerUtil.forward(-0.5);
            glows.add(new AfterGlow(forward[0] + Afterglow.mc.player.posX, Afterglow.mc.player.posY, forward[1] + Afterglow.mc.player.posZ, Afterglow.mc.player.rotationYaw, new Color(color.getValue().getRed(), color.getValue().getGreen(), color.getValue().getBlue(), 150)));
        }
    }
    
    @Override
    public void onRender3D(final float ticks) {
        if (Afterglow.render == null) {
            Afterglow.render = new RenderPlayer(Afterglow.mc.getRenderManager());
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        final float posx = (float)(Afterglow.mc.player.lastTickPosX + (Afterglow.mc.player.posX - Afterglow.mc.player.lastTickPosX) * ticks);
        final float posy = (float)(Afterglow.mc.player.lastTickPosY + (Afterglow.mc.player.posY - Afterglow.mc.player.lastTickPosY) * ticks);
        final float posz = (float)(Afterglow.mc.player.lastTickPosZ + (Afterglow.mc.player.posZ - Afterglow.mc.player.lastTickPosZ) * ticks);
        GlStateManager.translate(posx * -1.0f, posy * -1.0f, posz * -1.0f);
        for (final AfterGlow glow : glows) {
            GL11.glPushMatrix();
            GL11.glDepthRange(0.0, 0.01);
            GL11.glDisable(2896);
            GL11.glDisable(3553);
            GL11.glPolygonMode(1032, 6913);
            GL11.glEnable(3008);
            GL11.glEnable(3042);
            GL11.glLineWidth(thickness.getValue());
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glColor4f(glow.r / 255.0f, glow.g / 255.0f, glow.b / 255.0f, glow.a / 255.0f);
            Afterglow.mc.getRenderManager().renderEntityStatic(Afterglow.mc.player, 0.0f, false);
            GL11.glHint(3154, 4352);
            GL11.glPolygonMode(1032, 6914);
            GL11.glEnable(2896);
            GL11.glDepthRange(0.0, 1.0);
            GL11.glEnable(3553);
            GL11.glPopMatrix();
            glow.includeAlpha(fadeSpeed.getValue());
        }
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        glows.removeIf(g -> g.a >= 255);
    }
    
    static {
        Afterglow.render = null;
    }
    
    public class AfterGlow
    {
        public double x;
        public double y;
        public double z;
        public float yaw;
        public int r;
        public int g;
        public int b;
        public int a;
        
        public AfterGlow(final double x, final double y, final double z, final float yaw, final Color color) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            r = color.getRed();
            g = color.getGreen();
            b = color.getBlue();
            a = color.getAlpha();
        }

        public void includeAlpha(final Float speed) {
            a += speed.intValue();
            if (a > 255) {
                a = 255;
            }
        }
    }
}
