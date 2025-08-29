package me.hypinohaizin.candyplusrewrite.module.misc;

import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.utils.BlockUtil;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

/**
 * @author h_ypi
 * @since 2025/08/06 1:09
 */
public class BuildRandom extends Module {
    public Setting<Integer> range;
    public Setting<Integer> delay;

    private final Random random = new Random();
    private final Timer timer = new Timer();

    public BuildRandom() {
        super("BuildRandom", Categories.MISC, false, false);

       range = register(new Setting<>("Range", 4, 10, 1));
       delay = register(new Setting<>("Delay", 80, 500, 10));
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;
        if (!checkHeldItem()) return;
        if (!timer.passedMs(delay.getValue())) return;

        int bound = range.getValue() * 2 + 1;
        int attempts = 0;
        BlockPos pos;

        try {
            do {
                pos = new BlockPos(mc.player.getPosition()).add(random.nextInt(bound) - range.getValue(), random.nextInt(bound) - range.getValue(), random.nextInt(bound) - range.getValue());
            } while (++attempts < 128 && !tryToPlaceBlock(pos));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean tryToPlaceBlock(BlockPos pos) {
        if (pos == null || !mc.world.getBlockState(pos).getMaterial().isReplaceable())
            return false;

        double maxReach = range.getValue() + 1.5;
        if (mc.player.getDistanceSq(pos) > maxReach * maxReach)
            return false;

        boolean result = BlockUtil.placeBlock(pos, false);
        if (result) timer.reset();
        return result;
    }

    private boolean checkHeldItem() {
        ItemStack stack = mc.player.inventory.getCurrentItem();
        return !stack.isEmpty() && (stack.getItem() instanceof ItemBlock || stack.getItem() instanceof ItemSkull);
    }
}
