package me.hypinohaizin.candyplusrewrite.module.movement;

import me.hypinohaizin.candyplusrewrite.module.Module;

/**
 * @author h_ypi
 * @since 2025/08/03 23:11
 */
public class FastStop extends Module {
    public FastStop() {
        super("FastStop", Categories.MOVEMENT, false, false);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        if (FastStop.mc.player.movementInput.moveForward == 0.0f && FastStop.mc.player.movementInput.moveStrafe == 0.0f) {
            FastStop.mc.player.motionX = 0.0;
            FastStop.mc.player.motionZ = 0.0;
        }
    }
}
