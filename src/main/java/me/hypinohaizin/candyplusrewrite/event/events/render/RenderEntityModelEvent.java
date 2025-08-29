package me.hypinohaizin.candyplusrewrite.event.events.render;

import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;
import me.hypinohaizin.candyplusrewrite.event.CandyEvent;

public class RenderEntityModelEvent extends CandyEvent
{
    public ModelBase modelBase;
    public Entity entity;
    public float limbSwing;
    public float limbSwingAmount;
    public float age;
    public float headYaw;
    public float headPitch;
    public float scale;
    
    public RenderEntityModelEvent(final ModelBase modelBase, final Entity entity, final float limbSwing, final float limbSwingAmount, final float age, final float headYaw, final float headPitch, final float scale) {
        this.modelBase = modelBase;
        this.entity = entity;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.age = age;
        this.headYaw = headYaw;
        this.headPitch = headPitch;
        this.scale = scale;
    }
}
