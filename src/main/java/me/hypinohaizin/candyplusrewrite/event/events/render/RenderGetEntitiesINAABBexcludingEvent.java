package me.hypinohaizin.candyplusrewrite.event.events.render;

import com.google.common.base.Predicate;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import net.minecraft.client.multiplayer.WorldClient;
import me.hypinohaizin.candyplusrewrite.event.CandyEvent;

public class RenderGetEntitiesINAABBexcludingEvent extends CandyEvent
{
    public RenderGetEntitiesINAABBexcludingEvent(final WorldClient worldClient, final Entity entityIn, final AxisAlignedBB boundingBox, final Predicate predicate) {
    }
}
