package me.hypinohaizin.candyplusrewrite.utils;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class HoleUtil implements Util
{
    private static BlockPos[] surroundOffsets;
    
    public static boolean isObbyHole(final BlockPos pos) {
        for (final BlockPos offset : HoleUtil.surroundOffsets) {
            if (BlockUtil.getBlock(pos.add(offset)) != Blocks.OBSIDIAN) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isBedrockHole(final BlockPos pos) {
        for (final BlockPos offset : HoleUtil.surroundOffsets) {
            if (BlockUtil.getBlock(pos.add(offset)) != Blocks.BEDROCK) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSafeHole(final BlockPos pos) {
        for (final BlockPos offset : HoleUtil.surroundOffsets) {
            final Block block = BlockUtil.getBlock(pos.add(offset));
            if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) {
                return false;
            }
        }
        return true;
    }

    static {
        HoleUtil.surroundOffsets = BlockUtil.toBlockPos(PlayerUtil.getOffsets(0, true));
    }
}
