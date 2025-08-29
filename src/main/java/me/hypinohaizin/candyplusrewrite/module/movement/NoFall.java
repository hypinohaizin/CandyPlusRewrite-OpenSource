package me.hypinohaizin.candyplusrewrite.module.movement;

import me.hypinohaizin.candyplusrewrite.module.Module;

/**
 * @author h_ypi
 * @since 2025/08/03 23:11
 */
public class NoFall extends Module {
    public NoFall() {
        super("NoFall", Categories.MOVEMENT, false, false);
    }
//wow easy nofall
    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        mc.player.onGround = true;
    }
}
