package me.hypinohaizin.candyplusrewrite.event.events.world;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import me.hypinohaizin.candyplusrewrite.event.CandyEvent;

public class BlockEvent extends CandyEvent
{
    public BlockPos pos;
    public EnumFacing facing;
    public int stage;
    
    public BlockEvent(final int stage, final BlockPos pos, final EnumFacing facing) {
        this.stage = 0;
        this.stage = stage;
        this.pos = pos;
        this.facing = facing;
    }
}
