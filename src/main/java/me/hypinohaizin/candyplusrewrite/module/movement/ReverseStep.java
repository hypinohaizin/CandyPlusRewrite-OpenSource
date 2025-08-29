package me.hypinohaizin.candyplusrewrite.module.movement;

import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.utils.EntityUtil;
import net.minecraft.client.entity.EntityPlayerSP;

/**
 * @author h_ypi
 * @since 2025/08/04 21:08
 */
public class ReverseStep extends Module
{
    public ReverseStep() {
        super("ReverseStep", Categories.MOVEMENT,false, false);
    }

    @Override
    public void onUpdate() {
        if (ReverseStep.mc.player.onGround) {
            final EntityPlayerSP player = ReverseStep.mc.player;
            if (!EntityUtil.isInLiquid()) {
                --player.motionY;
            }
        }
    }
}
