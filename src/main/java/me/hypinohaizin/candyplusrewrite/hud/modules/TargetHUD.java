package me.hypinohaizin.candyplusrewrite.hud.modules;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import net.minecraft.entity.player.EntityPlayer;
import me.hypinohaizin.candyplusrewrite.hud.Hud;

public class TargetHUD extends Hud {
    public static EntityPlayer target;
    public double health;

    public Setting<Boolean> shadow;
    public Setting<Color> color;
    public Setting<Float> size;

    private final float baseWidth = 200.0f;
    private final float baseHeight = 70.0f;

    public TargetHUD() {
        super("TargetHud", 100.0f, 50.0f);
        health = 36.0;
        shadow = register(new Setting<>("Shadow", true));
        color = register(new Setting<>("Color", new Color(255, 255, 255, 255)));
        size = register(new Setting<>("Size", 1.0f, 2.0f, 0.5f));
    }

    @Override
    public void onRender() {
        try {
            if (nullCheck()) return;

            float scale = size.getValue();
            float width  = baseWidth  * scale;
            float height = baseHeight * scale;
            this.width = width;
            this.height = height;

            target = PlayerUtil.getNearestPlayer(30.0);
            if (target == null) return;

            float startX = x.getValue();
            float startY = y.getValue();

            RenderUtil.drawRect(startX, startY, width, height, ColorUtil.toRGBA(40, 40, 40));

            RenderUtil.renderEntity(target, startX + 30.0f * scale, startY + height - 7.0f * scale, 30.0f * scale);

            float targetHealth = target.getHealth() + target.getAbsorptionAmount();
            this.health += (targetHealth - this.health) * 0.4;

            float ratio = Math.max(0f, Math.min(1f, targetHealth / 36.0f));
            float barLeft   = startX;
            float barTop    = startY + height - 1.0f * scale;
            float barRight  = startX + width * ratio;
            float barBottom = startY + height;
            RenderUtil.drawGradientRect(barLeft, barTop, barRight, barBottom,
                    ColorUtil.toRGBA(255, 0, 0),
                    ColorUtil.toRGBA(getHealthColor((int) targetHealth)));

            int white = ColorUtil.toRGBA(255, 255, 255);
            float fontSpacing = 12.0f * scale;

            RenderUtil.drawString(target.getName(), startX + 60.0f * scale, startY + 10.0f * scale, white, shadow.getValue(), scale);

            float itemY = startY + 20.0f * scale;
            renderItem(startX, startY, getArmorInvSafe(3),  60.0f * scale, itemY);
            renderItem(startX, startY, getArmorInvSafe(2),  80.0f * scale, itemY);
            renderItem(startX, startY, getArmorInvSafe(1), 100.0f * scale, itemY);
            renderItem(startX, startY, getArmorInvSafe(0), 120.0f * scale, itemY);
            renderItem(startX, startY, target.getHeldItemMainhand(), 140.0f * scale, itemY);
            renderItem(startX, startY, target.getHeldItemOffhand(),  160.0f * scale, itemY);

            RenderUtil.drawString("Health : " + (int) targetHealth, startX + 60.0f * scale, startY + 42.0f * scale, white, shadow.getValue(), scale);
            RenderUtil.drawString("Distance : " + (int) PlayerUtil.getDistance(target), startX + 60.0f * scale, startY + 42.0f * scale + fontSpacing, white, shadow.getValue(), scale);

        } catch (Exception ignored) { }
    }

    private void renderItem(final float startX, final float startY, final ItemStack item, final float offsetX, final float offsetY) {
        if (item == null || item.isEmpty()) return;
        RenderUtil.renderItem(item, startX + offsetX, startY + offsetY - 4.0f * size.getValue());
    }

    private ItemStack getArmorInvSafe(final int slot) {
        if (target == null) return ItemStack.EMPTY;
        InventoryPlayer inv = target.inventory;
        ItemStack is = inv.armorItemInSlot(slot);
        return is == null ? ItemStack.EMPTY : is;
    }

    public float getItemDmg(final ItemStack is) {
        if (is == null || is.isEmpty() || !is.isItemStackDamageable()) return 100.0f;
        return (is.getMaxDamage() - is.getItemDamage()) / (float) is.getMaxDamage() * 100.0f;
    }

    public int getItemDmgColor(final ItemStack is) {
        if (is == null || is.isEmpty() || !is.isItemStackDamageable()) return ColorUtil.toRGBA(0, 255, 0, 255);
        float maxDmg = is.getMaxDamage();
        float dmg = maxDmg - is.getItemDamage();
        double offset = 255.0f / (maxDmg / 2.0f);
        int red, green;
        if (dmg > maxDmg / 2.0f) {
            red = (int) ((maxDmg - dmg) * offset);
            green = 255;
        } else {
            red = 255;
            green = (int) (255.0 - (maxDmg / 2.0 - dmg) * offset);
        }
        return ColorUtil.toRGBA(red, green, 0, 255);
    }

    private static Color getHealthColor(int health) {
        health = Math.max(0, Math.min(health, 36));
        int red, green;
        if (health > 18) {
            red = (int)((36 - health) * 14.1666667);
            green = 255;
        } else {
            red = 255;
            green = (int)(255 - (18 - health) * 14.1666667);
        }
        return new Color(red, green, 0, 255);
    }
}
