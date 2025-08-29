package me.hypinohaizin.candyplusrewrite.event.events.render;

import me.hypinohaizin.candyplusrewrite.event.CandyEvent;
import net.minecraft.entity.EntityLivingBase;

/**
 * @author h_ypi
 * @since 2025/08/03 23:12
 */
public class SwingAnimationEvent extends CandyEvent {
    private final EntityLivingBase entity;
    private int speed;

    public SwingAnimationEvent(EntityLivingBase entity, Integer speed) {
        this.entity = entity;
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }
}
