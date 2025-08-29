package me.hypinohaizin.candyplusrewrite.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import me.hypinohaizin.candyplusrewrite.event.events.player.PlayerDeathEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.client.network.NetHandlerPlayClient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ NetHandlerPlayClient.class })
public class MixinNetHandlerPlayClient
{
    @Inject(method = { "handleEntityMetadata" }, at = { @At("RETURN") }, cancellable = true)
    private void handleEntityMetadataHook(final SPacketEntityMetadata packetIn, final CallbackInfo info) {
        final Minecraft mc = Minecraft.getMinecraft();
        final Entity entity;
        final EntityPlayer player;
        if (mc.world != null && (entity = mc.world.getEntityByID(packetIn.getEntityId())) instanceof EntityPlayer && (player = (EntityPlayer)entity).getHealth() <= 0.0f) {
            final PlayerDeathEvent event = new PlayerDeathEvent(player);
            MinecraftForge.EVENT_BUS.post(event);
        }
    }
}
