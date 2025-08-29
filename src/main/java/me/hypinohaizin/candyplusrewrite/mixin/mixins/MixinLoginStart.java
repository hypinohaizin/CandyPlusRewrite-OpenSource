package me.hypinohaizin.candyplusrewrite.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.client.CPacketLoginStart;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ CPacketLoginStart.class })
public class MixinLoginStart
{
    @Inject(method = { "writePacketData" }, cancellable = true, at = { @At("HEAD") })
    public void writePacketData(final PacketBuffer buf, final CallbackInfo ci) {
    }
}
