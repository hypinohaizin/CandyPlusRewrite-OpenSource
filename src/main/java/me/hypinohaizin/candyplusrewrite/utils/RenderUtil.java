package me.hypinohaizin.candyplusrewrite.utils;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import me.hypinohaizin.candyplusrewrite.gui.font.CFontRenderer;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import java.awt.Rectangle;
import net.minecraft.client.renderer.culling.ICamera;

public class RenderUtil implements Util
{
    private static ICamera camera;
    private static Rectangle _lasted;
    private static Rectangle lasted;
    
    public static void drawRect(final float x, final float y, final float w, final float h, final int color) {
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y + h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x + w, y + h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x + w, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    public static void drawGradientRect(final float left, final float top, final float right, final float bottom, final int startColor, final int endColor) {
        final float f = (startColor >> 24 & 0xFF) / 255.0f;
        final float f2 = (startColor >> 16 & 0xFF) / 255.0f;
        final float f3 = (startColor >> 8 & 0xFF) / 255.0f;
        final float f4 = (startColor & 0xFF) / 255.0f;
        final float f5 = (endColor >> 24 & 0xFF) / 255.0f;
        final float f6 = (endColor >> 16 & 0xFF) / 255.0f;
        final float f7 = (endColor >> 8 & 0xFF) / 255.0f;
        final float f8 = (endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(left, top, 0.0).color(f2, f3, f4, f).endVertex();
        bufferbuilder.pos(left, bottom, 0.0).color(f2, f3, f4, f).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).color(f6, f7, f8, f5).endVertex();
        bufferbuilder.pos(right, top, 0.0).color(f6, f7, f8, f5).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    
    public static void drawLine(final float x, final float y, final float x1, final float y1, final float thickness, final int hex) {
        final float red = (hex >> 16 & 0xFF) / 255.0f;
        final float green = (hex >> 8 & 0xFF) / 255.0f;
        final float blue = (hex & 0xFF) / 255.0f;
        final float alpha = (hex >> 24 & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GL11.glLineWidth(thickness);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x1, y1, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GL11.glDisable(2848);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
    
    public static void drawTexturedModalRect(final float xCoord, final float yCoord, final int minU, final int minV, final int maxU, final int maxV) {
        final float f = 0.00390625f;
        final float f2 = 0.00390625f;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(xCoord + 0.0f, yCoord + maxV, 0.0).tex((minU) * 0.00390625f, (minV + maxV) * 0.00390625f).endVertex();
        bufferbuilder.pos(xCoord + maxU, yCoord + maxV, 0.0).tex((minU + maxU) * 0.00390625f, (minV + maxV) * 0.00390625f).endVertex();
        bufferbuilder.pos(xCoord + maxU, yCoord + 0.0f, 0.0).tex((minU + maxU) * 0.00390625f, (minV) * 0.00390625f).endVertex();
        bufferbuilder.pos(xCoord + 0.0f, yCoord + 0.0f, 0.0).tex((minU) * 0.00390625f, (minV) * 0.00390625f).endVertex();
        tessellator.draw();
    }
    
    public static void drawTexture(final float x, final float y, final float textureX, final float textureY, final float width, final float height) {
        GL11.glPushAttrib(524288);
        GL11.glDisable(3089);
        GlStateManager.clear(256);
        GL11.glPopAttrib();
        final float f = 0.00390625f;
        final float f2 = 0.00390625f;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.color(0, 0, 0, 150);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, 0.0).tex(textureX * f, (textureY + height) * f2).endVertex();
        bufferbuilder.pos(x + width, y + height, 0.0).tex((textureX + width) * f, (textureY + height) * f2).endVertex();
        bufferbuilder.pos(x + width, y, 0.0).tex((textureX + width) * f, textureY * f2).endVertex();
        bufferbuilder.pos(x, y, 0.0).tex(textureX * f, textureY * f2).endVertex();
        tessellator.draw();
    }
    
    public static void drawTexture(final float x, final float y, final float width, final float height, final float u, final float v, final float t, final float s) {
        GL11.glPushAttrib(524288);
        GL11.glDisable(3089);
        GlStateManager.clear(256);
        GL11.glPopAttrib();
        GlStateManager.depthMask(false);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(4, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x + width, y, 0.0).tex(t, v).endVertex();
        bufferbuilder.pos(x, y, 0.0).tex(u, v).endVertex();
        bufferbuilder.pos(x, y + height, 0.0).tex(u, s).endVertex();
        bufferbuilder.pos(x, y + height, 0.0).tex(u, s).endVertex();
        bufferbuilder.pos(x + width, y + height, 0.0).tex(t, s).endVertex();
        bufferbuilder.pos(x + width, y, 0.0).tex(t, v).endVertex();
        tessellator.draw();
    }
    
    public static void drawTexture(final float posX, final float posY, final float width, final float height) {
        GL11.glPushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(width, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(width, 0.0f, 0.0f);
        GL11.glEnd();
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GL11.glPopMatrix();
    }
    
    public static float drawString(final String str, final float x, final float y, final int color, final boolean shadow, final float s) {
        final CFontRenderer font = CandyPlusRewrite.m_font.fontRenderer;
        if (shadow) {
            font.drawString(str, x + 0.5 * s, y + 0.5 * s, ColorUtil.toRGBA(new Color(0, 0, 0, 255)), true, 1.0f);
        }
        return font.drawString(str, x, y, color, false, s);
    }
    
    public static float getStringWidth(final String str, final float s) {
        final CFontRenderer font = CandyPlusRewrite.m_font.fontRenderer;
        return font.getStringWidth(str) * s;
    }
    
    public static float getStringHeight(final float s) {
        final CFontRenderer font = CandyPlusRewrite.m_font.fontRenderer;
        return (font.getHeight() - 1) * s;
    }
    
    public static int getRainbow(final int speed, final int offset, final float s, final float b) {
        float hue = (float)((System.currentTimeMillis() + offset) % speed);
        return Color.getHSBColor(hue /= speed, s, b).getRGB();
    }
    
    public static void renderItem(final ItemStack item, final float x, final float y) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(524288);
        GL11.glDisable(3089);
        GlStateManager.clear(256);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderItem().zLevel = -150.0f;
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item, (int)x, (int)y);
        Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRenderer, item, (int)x, (int)y);
        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().zLevel = 0.0f;
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
    }
    
    public static void renderItem(final ItemStack item, final float x, final float y, final boolean itemOverlay) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(524288);
        GL11.glDisable(3089);
        GlStateManager.clear(256);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderItem().zLevel = -150.0f;
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item, (int)x, (int)y);
        if (itemOverlay) {
            Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRenderer, item, (int)x, (int)y);
        }
        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().zLevel = 0.0f;
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
    }

    public static void renderEntity(final EntityLivingBase entity, final float x, final float y, final float scale) {
        GlStateManager.pushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            RenderHelper.enableGUIStandardItemLighting();
            drawEntityOnScreen((int)x, (int)y, (int)scale, entity);
            RenderHelper.disableStandardItemLighting();
        } finally {
            GL11.glPopAttrib();
            GlStateManager.popMatrix();
        }
    }

    private static void drawEntityOnScreen(final int posX, final int posY, final int scale, final EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0f);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        final float f = ent.renderYawOffset;
        final float f2 = ent.rotationYaw;
        final float f3 = ent.rotationPitch;
        final float f4 = ent.prevRotationYawHead;
        final float f5 = ent.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        rendermanager.getEntityRenderObject(ent).doRender(ent, 0.0, 0.0, 0.0, ent.rotationYaw, 1.0f);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f2;
        ent.rotationPitch = f3;
        ent.prevRotationYawHead = f4;
        ent.rotationYawHead = f5;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
    
    public static float guiToScreen(final float shit) {
        final ScaledResolution scaledresolution = new ScaledResolution(RenderUtil.mc);
        final double scaleFactor = scaledresolution.getScaleFactor();
        return (float)(shit * scaleFactor);
    }
    
    public static void Scissor(final float x, final float y, final float width, final float height) {
        RenderUtil._lasted = new Rectangle((int)x, (int)y, (int)width, (int)height);
        GL11.glScissor((int)guiToScreen(x), (int)(RenderUtil.mc.displayHeight - (guiToScreen(y) + guiToScreen(height))), (int)guiToScreen(width), (int)guiToScreen(height));
    }
    
    public static void pushScissor() {
        RenderUtil.lasted = RenderUtil._lasted;
    }
    
    public static void popScissor() {
        GL11.glScissor((int)guiToScreen((float)RenderUtil.lasted.x), (int)(RenderUtil.mc.displayHeight - (guiToScreen((float)RenderUtil.lasted.y) + guiToScreen((float)RenderUtil.lasted.height))), (int)guiToScreen((float)RenderUtil.lasted.width), (int)guiToScreen((float)RenderUtil.lasted.height));
    }
    
    static {
        RenderUtil.camera = new Frustum();
    }
}
