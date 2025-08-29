package me.hypinohaizin.candyplusrewrite.hud.modules;

import net.minecraft.item.ItemBlock;
import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import net.minecraft.init.Items;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.hud.Hud;

public class PvPResources extends Hud
{
    public Setting<Boolean> crystal;
    public Setting<Boolean> xp;
    public Setting<Boolean> gap;
    public Setting<Boolean> totem;
    public Setting<Boolean> obby;
    public Setting<Boolean> piston;
    public Setting<Boolean> redstone;
    public Setting<Boolean> torch;
    public Setting<Boolean> block;
    public Setting<Boolean> shadow;
    public Setting<Color> color;
    public Setting<Boolean> background;
    public Setting<Color> backcolor;
    
    public PvPResources() {
        super("PvPResources", 300.0f, 100.0f);
        crystal = register(new Setting<>("Crystal", true));
        xp = register(new Setting<>("Xp", true));
        gap = register(new Setting<>("Gap", true));
        totem = register(new Setting<>("Totem", true));
        obby = register(new Setting<>("Obsidian", true));
        piston = register(new Setting<>("Piston", true));
        redstone = register(new Setting<>("RedStone", true));
        torch = register(new Setting<>("Torch", true, v -> redstone.getValue()));
        block = register(new Setting<>("Block", true, v -> redstone.getValue()));
        shadow = register(new Setting<>("Shadow", false));
        color = register(new Setting<>("Color", new Color(255, 255, 255, 255)));
        background = register(new Setting<>("Background", false));
        backcolor = register(new Setting<>("BGColor", new Color(40, 40, 40, 60), v -> background.getValue()));
    }
    
    @Override
    public void onRender() {
        final float x = this.x.getValue();
        float y = this.y.getValue();
        if (crystal.getValue()) {
            renderItem(Items.END_CRYSTAL, getItemCount(Items.END_CRYSTAL), x, y);
            y += RenderUtil.getStringHeight(1.0f) + 13.0f;
        }
        if (xp.getValue()) {
            renderItem(Items.EXPERIENCE_BOTTLE, getItemCount(Items.EXPERIENCE_BOTTLE), x, y);
            y += RenderUtil.getStringHeight(1.0f) + 13.0f;
        }
        if (gap.getValue()) {
            renderItem(Items.GOLDEN_APPLE, getItemCount(Items.GOLDEN_APPLE), x, y);
            y += RenderUtil.getStringHeight(1.0f) + 13.0f;
        }
        if (totem.getValue()) {
            renderItem(Items.TOTEM_OF_UNDYING, getItemCount(Items.TOTEM_OF_UNDYING), x, y);
            y += RenderUtil.getStringHeight(1.0f) + 13.0f;
        }
        if (obby.getValue()) {
            renderBlock(Blocks.OBSIDIAN, getBlockCount(Blocks.OBSIDIAN), x, y);
            y += RenderUtil.getStringHeight(1.0f) + 13.0f;
        }
        if (piston.getValue()) {
            renderBlock(Blocks.PISTON, getBlockCount(Blocks.PISTON) + getBlockCount(Blocks.STICKY_PISTON), x, y);
            y += RenderUtil.getStringHeight(1.0f) + 13.0f;
        }
        if (redstone.getValue()) {
            if (block.getValue()) {
                renderBlock(Blocks.REDSTONE_BLOCK, getBlockCount(Blocks.REDSTONE_BLOCK), x, y);
                y += RenderUtil.getStringHeight(1.0f) + 13.0f;
            }
            if (torch.getValue()) {
                renderBlock(Blocks.REDSTONE_TORCH, getBlockCount(Blocks.REDSTONE_TORCH), x, y);
                y += RenderUtil.getStringHeight(1.0f) + 13.0f;
            }
        }
        y -= RenderUtil.getStringHeight(1.0f) + 13.0f;
        width = x + 20.0f + RenderUtil.getStringWidth(" : 64", 1.0f) - this.x.getValue();
        height = y - this.y.getValue();
    }
    
    public void renderItem(final Item item, final int count, final float x, final float y) {
        RenderUtil.renderItem(new ItemStack(item), x, y - 8.0f, false);
        RenderUtil.drawString(" : " + count, x + 20.0f, y, ColorUtil.toRGBA(color.getValue()), shadow.getValue(), 1.0f);
    }
    
    public void renderBlock(final Block block, final int count, final float x, final float y) {
        RenderUtil.renderItem(new ItemStack(block), x, y - 10.0f, false);
        RenderUtil.drawString(" : " + count, x + 20.0f, y, ColorUtil.toRGBA(color.getValue()), shadow.getValue(), 1.0f);
    }
    
    public int getItemCount(final Item item) {
        int count = 0;
        for (int i = 0; i < PvPResources.mc.player.inventory.getSizeInventory(); ++i) {
            final ItemStack itemStack = PvPResources.mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == item) {
                count += itemStack.getCount();
            }
        }
        return count;
    }
    
    public int getBlockCount(final Block block) {
        int count = 0;
        for (int i = 0; i < PvPResources.mc.player.inventory.getSizeInventory(); ++i) {
            final ItemStack itemStack = PvPResources.mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() instanceof ItemBlock && ((ItemBlock)itemStack.getItem()).getBlock() == block) {
                count += itemStack.getCount();
            }
        }
        return count;
    }
}
