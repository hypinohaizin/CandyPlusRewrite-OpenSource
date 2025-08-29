package me.hypinohaizin.candyplusrewrite.module.render;

import me.hypinohaizin.candyplusrewrite.event.events.render.FlagGetEvent;
import me.hypinohaizin.candyplusrewrite.event.events.render.SwingAnimationEvent;
import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author h_ypi
 * @since 2025/08/03 23:19
 */
public class Animation extends Module {
    public static Animation INSTANCE;

    public Setting<Boolean> swingSpeed;
    public Setting<Integer> speed;
    public Setting<Boolean> offhand;
    public Setting<Boolean> sneak;

    public Animation() {
        super("Animation", Categories.RENDER, false, false);

        swingSpeed = register(new Setting<>("SwingSpeed", false));
        speed = register(new Setting<>("Speed", 12, 32, 6, v -> swingSpeed.getValue()));
        offhand = register(new Setting<>("Offhand", false));
        sneak = register(new Setting<>("Sneak", false));

        INSTANCE = this;
    }

    @SubscribeEvent
    public void onArmSwingAnim(SwingAnimationEvent event) {
        if (swingSpeed.getValue() && event.getEntity() == mc.player) {
            event.setSpeed(speed.getValue());
        }
    }

    @Override
    public void onTick() {
        if (offhand.getValue() && mc.player != null && mc.player.isSwingInProgress) {
            mc.player.swingingHand = EnumHand.OFF_HAND;
        }

        if (swingSpeed.getValue()) {
            speed.getValue().toString();
        }
    }

    @SubscribeEvent
    public void onFlagGet(FlagGetEvent event) {
        if (event.getEntity() instanceof EntityPlayer &&
                event.getEntity() != mc.player &&
                event.getFlag() == 1 &&
                sneak.getValue()) {
            event.setReturnValue(true);
        }
    }
}
