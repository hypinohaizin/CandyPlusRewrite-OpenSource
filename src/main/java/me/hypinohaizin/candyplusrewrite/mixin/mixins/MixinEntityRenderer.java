package me.hypinohaizin.candyplusrewrite.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.ArrayList;

import net.minecraftforge.common.MinecraftForge;
import me.hypinohaizin.candyplusrewrite.event.events.render.RenderGetEntitiesINAABBexcludingEvent;
import java.util.List;
import com.google.common.base.Predicate;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ EntityRenderer.class })
public class MixinEntityRenderer
{
    @Redirect(method = { "getMouseOver" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(final WorldClient worldClient, final Entity entityIn, final AxisAlignedBB boundingBox, final Predicate predicate) {
        final RenderGetEntitiesINAABBexcludingEvent event = new RenderGetEntitiesINAABBexcludingEvent(worldClient, entityIn, boundingBox, predicate);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            return new ArrayList<>();
        }
        return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }
}
