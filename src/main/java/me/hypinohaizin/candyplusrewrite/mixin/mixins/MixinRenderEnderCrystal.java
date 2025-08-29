package me.hypinohaizin.candyplusrewrite.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import org.lwjgl.opengl.GL11;
import net.minecraftforge.common.MinecraftForge;
import me.hypinohaizin.candyplusrewrite.event.events.render.RenderEntityModelEvent;
import net.minecraft.client.renderer.GlStateManager;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.module.render.CandyCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ RenderEnderCrystal.class })
public class MixinRenderEnderCrystal
{
    @Redirect(method = { "doRender" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModelBaseHook(final ModelBase model, final Entity entity, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
        final CandyCrystal candycrystal = (CandyCrystal) CandyPlusRewrite.m_module.getModuleWithClass(CandyCrystal.class);
        if (candycrystal == null) {
            return;
        }
        GlStateManager.scale(candycrystal.scale.getValue(), candycrystal.scale.getValue(), candycrystal.scale.getValue());
        if (candycrystal.isEnable && candycrystal.wireframe.getValue()) {
            final RenderEntityModelEvent event = new RenderEntityModelEvent(model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            MinecraftForge.EVENT_BUS.post(event);
        }
        if (candycrystal.isEnable && candycrystal.chams.getValue()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            if (candycrystal.rainbow.getValue()) {
                final Color rainbowColor1 = candycrystal.colorSync.getValue() ? candycrystal.getCurrentColor() : new Color(RenderUtil.getRainbow(candycrystal.speed.getValue() * 100, 0, candycrystal.saturation.getValue() / 100.0f, candycrystal.brightness.getValue() / 100.0f));
                final Color rainbowColor2 = new Color(rainbowColor1.getRed(), rainbowColor1.getGreen(), rainbowColor1.getBlue());
                if (candycrystal.throughWalls.getValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f(rainbowColor2.getRed() / 255.0f, rainbowColor2.getGreen() / 255.0f, rainbowColor2.getBlue() / 255.0f, candycrystal.alpha.getValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (candycrystal.throughWalls.getValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
            }
            else if (candycrystal.xqz.getValue() && candycrystal.throughWalls.getValue()) {
                final Color hiddenColor = candycrystal.hiddenColor.getValue();
                final Color color = candycrystal.color.getValue();
                final Color visibleColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                if (candycrystal.throughWalls.getValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f(hiddenColor.getRed() / 255.0f, hiddenColor.getGreen() / 255.0f, hiddenColor.getBlue() / 255.0f, candycrystal.alpha.getValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (candycrystal.throughWalls.getValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
                GL11.glColor4f(visibleColor.getRed() / 255.0f, visibleColor.getGreen() / 255.0f, visibleColor.getBlue() / 255.0f, candycrystal.alpha.getValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }
            else {
                final Color color;
                final Color visibleColor2 = color = (candycrystal.colorSync.getValue() ? candycrystal.getCurrentColor() : candycrystal.color.getValue());
                if (candycrystal.throughWalls.getValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f(visibleColor2.getRed() / 255.0f, visibleColor2.getGreen() / 255.0f, visibleColor2.getBlue() / 255.0f, candycrystal.alpha.getValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (candycrystal.throughWalls.getValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
            }
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
            if (candycrystal.glint.getValue()) {
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GlStateManager.enableAlpha();
                GlStateManager.color(1.0f, 0.0f, 0.0f, 0.13f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                GlStateManager.disableAlpha();
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
            }
        }
        else {
            model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        if (candycrystal.isEnable()) {
            GlStateManager.scale(1.0f / candycrystal.scale.getValue(), 1.0f / candycrystal.scale.getValue(), 1.0f / candycrystal.scale.getValue());
        }
    }
}
