package me.hypinohaizin.candyplusrewrite.managers;

import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import me.hypinohaizin.candyplusrewrite.utils.BlockUtil;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;

public class HoleManager extends Manager
{
    private static final BlockPos[] surroundOffset;
    private final List<BlockPos> midSafety;
    private List<BlockPos> holes;
    
    public HoleManager() {
        midSafety = new ArrayList<BlockPos>();
        holes = new ArrayList<BlockPos>();
    }
    
    public void update() {
        if (!nullCheck()) {
            holes = calcHoles();
        }
    }
    
    public List<BlockPos> getHoles() {
        return holes;
    }
    
    public List<BlockPos> getMidSafety() {
        return midSafety;
    }
    
    public List<BlockPos> getSortedHoles() {
        holes.sort(Comparator.comparingDouble(hole -> HoleManager.mc.player.getDistanceSq(hole)));
        return getHoles();
    }
    
    public List<BlockPos> calcHoles() {
        final ArrayList<BlockPos> safeSpots = new ArrayList<BlockPos>();
        midSafety.clear();
        final List<BlockPos> positions = BlockUtil.getSphere(new BlockPos(HoleManager.mc.player.posX, HoleManager.mc.player.posY, HoleManager.mc.player.posZ), 6.0f, 6, false, true, 0);
        for (final BlockPos pos : positions) {
            if (HoleManager.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && HoleManager.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                if (!HoleManager.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                    continue;
                }
                boolean isSafe = true;
                boolean midSafe = true;
                for (final BlockPos offset : HoleManager.surroundOffset) {
                    final Block block = HoleManager.mc.world.getBlockState(pos.add(offset)).getBlock();
                    if (BlockUtil.isBlockUnSolid(block)) {
                        midSafe = false;
                    }
                    if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
                        isSafe = false;
                    }
                }
                if (isSafe) {
                    safeSpots.add(pos);
                }
                if (!midSafe) {
                    continue;
                }
                midSafety.add(pos);
            }
        }
        return safeSpots;
    }
    
    public List<BlockPos> calcHoles(final float range) {
        final ArrayList<BlockPos> safeSpots = new ArrayList<BlockPos>();
        midSafety.clear();
        final List<BlockPos> positions = BlockUtil.getSphere(new BlockPos(HoleManager.mc.player.posX, HoleManager.mc.player.posY, HoleManager.mc.player.posZ), range, 6, false, true, 0);
        for (final BlockPos pos : positions) {
            if (HoleManager.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && HoleManager.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                if (!HoleManager.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                    continue;
                }
                boolean isSafe = true;
                boolean midSafe = true;
                for (final BlockPos offset : HoleManager.surroundOffset) {
                    final Block block = HoleManager.mc.world.getBlockState(pos.add(offset)).getBlock();
                    if (BlockUtil.isBlockUnSolid(block)) {
                        midSafe = false;
                    }
                    if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
                        isSafe = false;
                    }
                }
                if (isSafe) {
                    safeSpots.add(pos);
                }
                if (!midSafe) {
                    continue;
                }
                midSafety.add(pos);
            }
        }
        return safeSpots;
    }
    
    public boolean isSafe(final BlockPos pos) {
        boolean isSafe = true;
        for (final BlockPos offset : HoleManager.surroundOffset) {
            final Block block = HoleManager.mc.world.getBlockState(pos.add(offset)).getBlock();
            if (BlockUtil.isBlockUnSafe(block)) {
                isSafe = false;
                break;
            }
        }
        return isSafe;
    }
    
    public boolean inHole() {
        return HoleManager.mc.player != null && isSafe(new BlockPos(HoleManager.mc.player.posX, HoleManager.mc.player.posY, HoleManager.mc.player.posZ));
    }
    
    public boolean inHole(final EntityPlayer player) {
        return player != null && isSafe(new BlockPos(player.posX, player.posY, player.posZ));
    }
    
    static {
        surroundOffset = BlockUtil.toBlockPos(PlayerUtil.getOffsets(0, true));
    }
}
