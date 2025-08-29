package me.hypinohaizin.candyplusrewrite.hud.modules;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.InventoryPlayer;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import me.hypinohaizin.candyplusrewrite.hud.Hud;

public class InventoryViewer extends Hud
{
    public InventoryViewer() {
        super("InventoryViewer", 150.0f, 100.0f);
    }
    
    @Override
    public void onRender() {
        if (nullCheck()) {
            return;
        }
        RenderUtil.drawRect(x.getValue() - 6.0f, y.getValue() - 6.0f, 180.0f, 72.0f, ColorUtil.toRGBA(0, 0, 0));
        RenderUtil.drawRect(x.getValue() - 5.0f, y.getValue() - 5.0f, 180.0f, 69.0f, ColorUtil.toRGBA(40, 40, 40));
        float _x = 0.0f;
        float _y = 0.0f;
        int c = 0;
        final int scale = 19;
        final InventoryPlayer inv = InventoryViewer.mc.player.inventory;
        for (int i = 9; i < 36; ++i) {
            final ItemStack item = inv.getStackInSlot(i);
            RenderUtil.renderItem(item, x.getValue() + _x + 3.0f, y.getValue() + _y + 3.0f);
            _x += scale;
            if (++c == 9) {
                _x = 0.0f;
                _y += scale;
                c = 0;
            }
        }
        width = 168.0f;
        height = 60.0f;
    }
}
