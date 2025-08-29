package me.hypinohaizin.candyplusrewrite.event.events.player;

import net.minecraft.entity.MoverType;
import me.hypinohaizin.candyplusrewrite.event.CandyEvent;

public class MoveEvent extends CandyEvent
{
    private MoverType type;
    private double x;
    private double y;
    private double z;
    
    public MoveEvent(final MoverType type, final double x, final double y, final double z) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public MoverType getType() {
        return type;
    }
    
    public void setType(final MoverType type) {
        this.type = type;
    }
    
    public double getX() {
        return x;
    }
    
    public void setX(final double x) {
        this.x = x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setY(final double y) {
        this.y = y;
    }
    
    public double getZ() {
        return z;
    }
    
    public void setZ(final double z) {
        this.z = z;
    }
}
