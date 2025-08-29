package me.hypinohaizin.candyplusrewrite.mixin.mixins;

import me.hypinohaizin.candyplusrewrite.event.events.render.SwingAnimationEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author h_ypi
 * @since 2025/08/03 23:16
 */
@Mixin(value={EntityLivingBase.class})
public abstract class MixinEntityLivingBase {

    @Inject(method={"getArmSwingAnimationEnd()I"}, at={@At(value="HEAD")}, cancellable=true)
    private void getArmSwingAnimationEnd(CallbackInfoReturnable<Integer> cir) {
        SwingAnimationEvent armSwingAnimation = new SwingAnimationEvent(EntityLivingBase.class.cast(this), 6);
        MinecraftForge.EVENT_BUS.post(armSwingAnimation);
        cir.setReturnValue((armSwingAnimation.getSpeed()));
    }
}