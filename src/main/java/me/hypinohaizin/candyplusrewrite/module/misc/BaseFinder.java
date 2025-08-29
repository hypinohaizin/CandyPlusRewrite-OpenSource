package me.hypinohaizin.candyplusrewrite.module.misc;

import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.utils.ChatUtil;
import me.hypinohaizin.candyplusrewrite.utils.StringUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityShulkerBox;
import java.util.ArrayList;
import java.util.List;

/**
 * @author h_ypi
 * @since 2025/08/06 3:03
 */
public class BaseFinder extends Module {
    private final Setting<Boolean> chest;
    private final Setting<Boolean> hopper;
    private final Setting<Boolean> furnace;
    private final Setting<Boolean> shulker;

    private List<TileEntity> notified;

    public BaseFinder() {
        super("BaseFinder", Categories.MISC, false, false);
        chest = register(new Setting<>("Chest", true));
        hopper = register(new Setting<>("Hopper", true));
        furnace = register(new Setting<>("Furnace", true));
        shulker = register(new Setting<>("Shulker", true));
        notified = new ArrayList<>();
    }

    @Override
    public void onDisable() {
        notified = new ArrayList<>();
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) return;
        List<TileEntity> tiles = new ArrayList<>(mc.world.loadedTileEntityList);
        tiles.removeIf(te -> !(
                (te instanceof TileEntityChest   && chest.getValue())
                        || (te instanceof TileEntityHopper  && hopper.getValue())
                        || (te instanceof TileEntityFurnace && furnace.getValue())
                        || (te instanceof TileEntityShulkerBox && shulker.getValue())));
        for (TileEntity te : tiles) {
            if (notified.contains(te)) continue;
            notified.add(te);
            String name;
            if (te instanceof TileEntityChest) name = "Chest";
            else if (te instanceof TileEntityHopper) name = "Hopper";
            else if (te instanceof TileEntityFurnace) name = "Furnace";
            else name = "ShulkerBox";
                sendMessage(name + ": " + StringUtil.getPositionString(te.getPos()));
                ChatUtil.sendMessage(name + ": " + StringUtil.getPositionString(te.getPos()));
            }
        }
    }
