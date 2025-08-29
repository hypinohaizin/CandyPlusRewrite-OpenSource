package me.hypinohaizin.candyplusrewrite.hud.modules;

import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.module.combat.HoleFill;
import me.hypinohaizin.candyplusrewrite.module.combat.CivBreaker;
import me.hypinohaizin.candyplusrewrite.module.combat.CevBreaker;
import me.hypinohaizin.candyplusrewrite.module.combat.Blocker;
import me.hypinohaizin.candyplusrewrite.module.combat.AutoMend;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.hud.Hud;

public class CombatInfo extends Hud
{
    public Setting<Boolean> shadow;
    public Setting<Color> color;
    public Setting<Boolean> mend;
    public Setting<Boolean> blocker;
    public Setting<Boolean> cev;
    public Setting<Boolean> civ;
    public Setting<Boolean> holefill;

    public CombatInfo() {
        super("CombatInfo", 50.0f, 10.0f);
        shadow = register(new Setting<>("Shadow", true));
        color = register(new Setting<>("Color", new Color(255, 255, 255, 255)));
        mend = register(new Setting<>("AutoMend", true));
        blocker = register(new Setting<>("Blocker", true));
        cev = register(new Setting<>("CevBreaker", true));
        civ = register(new Setting<>("CivBreaker", true));
        holefill = register(new Setting<>("HoleFill", true));
    }
    
    @Override
    public void onRender() {
        try {
            final Module mend = getModule(AutoMend.class);
            final Module blocker = getModule(Blocker.class);
            final Module cev = getModule(CevBreaker.class);
            final Module civ = getModule(CivBreaker.class);
            final Module holefill = getModule(HoleFill.class);
            float width = 0.0f;
            float height = 0.0f;
            if (this.mend.getValue()) {
                final float _width = drawModuleInfo(mend, height);
                if (width < _width) {
                    width = _width;
                }
                height += RenderUtil.getStringHeight(1.0f) + 5.0f;
            }
            if (this.blocker.getValue()) {
                final float _width = drawModuleInfo(blocker, height);
                if (width < _width) {
                    width = _width;
                }
                height += RenderUtil.getStringHeight(1.0f) + 5.0f;
            }
            if (this.cev.getValue()) {
                final float _width = drawModuleInfo(cev, height);
                if (width < _width) {
                    width = _width;
                }
                height += RenderUtil.getStringHeight(1.0f) + 5.0f;
            }
            if (this.civ.getValue()) {
                final float _width = drawModuleInfo(civ, height);
                if (width < _width) {
                    width = _width;
                }
                height += RenderUtil.getStringHeight(1.0f) + 5.0f;
            }
            if (this.holefill.getValue()) {
                final float _width = drawModuleInfo(holefill, height);
                if (width < _width) {
                    width = _width;
                }
                height += RenderUtil.getStringHeight(1.0f) + 5.0f;
            }
            this.width = width - x.getValue();
            this.height = height;
        }
        catch (final Exception ex) {}
    }
    
    public float drawModuleInfo(final Module module, final float offset) {
        return RenderUtil.drawString(module.name + " : " + (module.isEnable ? (ChatFormatting.GREEN + "ON") : (ChatFormatting.RED + "OFF")), x.getValue(), y.getValue() + offset, ColorUtil.toRGBA(color.getValue()), shadow.getValue(), 1.0f);
    }
    
    public Module getModule(final Class clazz) {
        return CandyPlusRewrite.m_module.getModuleWithClass(clazz);
    }
}
