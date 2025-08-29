package me.hypinohaizin.candyplusrewrite.module.render;

import java.util.Iterator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.hypinohaizin.candyplusrewrite.event.events.player.UpdateWalkingPlayerEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class BreadCrumbs extends Module
{
    public Setting<Color> color;
    public Setting<Float> fadeSpeed;
    public Setting<Float> thickness;
    public Setting<Float> offset;
    public Setting<Boolean> other;
    public List<Trace> traces;

    public BreadCrumbs() {
        super("BreadCrumbs", Categories.RENDER, false, false);
        color = register(new Setting<>("Color", new Color(130, 10, 220, 200)));
        fadeSpeed = register(new Setting<>("Fadeout Speed", 10.0f, 20.0f, 1.0f));
        thickness = register(new Setting<>("Thickness", 3.0f, 10.0f, 1.0f));
        offset = register(new Setting<>("OffsetY", 5.0f, 10.0f, 0.0f));
        other = register(new Setting<>("Other", false));
        traces = new ArrayList<>();
    }

    @Override
    public void onRender3D(final float ticks) {
        try {
            doRender(ticks);
        }
        catch (final Exception ex) {
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingEvent(final UpdateWalkingPlayerEvent event) {
        traces.add(new Trace(BreadCrumbs.mc.player.posX, BreadCrumbs.mc.player.posY + (offset.getValue() - 5.0), BreadCrumbs.mc.player.posZ, color.getValue()));
    }

    public void doRender(final float ticks) {
        if (traces.isEmpty()) return;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableDepth();

        final float posx = (float)(BreadCrumbs.mc.player.lastTickPosX + (BreadCrumbs.mc.player.posX - BreadCrumbs.mc.player.lastTickPosX) * ticks);
        final float posy = (float)(BreadCrumbs.mc.player.lastTickPosY + (BreadCrumbs.mc.player.posY - BreadCrumbs.mc.player.lastTickPosY) * ticks);
        final float posz = (float)(BreadCrumbs.mc.player.lastTickPosZ + (BreadCrumbs.mc.player.posZ - BreadCrumbs.mc.player.lastTickPosZ) * ticks);
        GlStateManager.translate(posx * -1.0f, posy * -1.0f, posz * -1.0f);

        final Tessellator tessellator = new Tessellator(2097152);
        final BufferBuilder worldRenderer = tessellator.getBuffer();
        final float thickness = this.thickness.getValue();

        GL11.glEnable(2848);
        GL11.glLineWidth(thickness);

        try {
            worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

            final Iterator<Trace> iterator = traces.iterator();
            while (iterator.hasNext()) {
                final Trace trace = iterator.next();
                final int r = trace.r;
                final int g = trace.g;
                final int b = trace.b;
                final int a = trace.a;
                worldRenderer.pos(trace.x, trace.y, trace.z).color(r, g, b, a).endVertex();
                trace.includeAlpha(fadeSpeed.getValue());
            }

            tessellator.draw();
        } catch (IllegalStateException e) {
            try {
                worldRenderer.finishDrawing();
            } catch (Exception ignored) {}
        }

        GL11.glLineWidth(1.0f);
        GL11.glDisable(2848);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();

        traces.removeIf(t -> t.a <= 0);
    }

    public class Trace
    {
        public double x;
        public double y;
        public double z;
        public int r;
        public int g;
        public int b;
        public int a;

        public Trace(final double x, final double y, final double z, final Color color) {
            this.x = x;
            this.y = y;
            this.z = z;
            r = color.getRed();
            g = color.getGreen();
            b = color.getBlue();
            a = color.getAlpha();
        }

        public void includeAlpha(final Float speed) {
            a -= speed.intValue(); // Fixed the casting issue
            if (a < 0) {
                a = 0;
            }
        }
    }
}