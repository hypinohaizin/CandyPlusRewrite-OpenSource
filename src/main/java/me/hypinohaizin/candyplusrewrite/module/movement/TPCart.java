package me.hypinohaizin.candyplusrewrite.module.movement;

import net.minecraft.util.EnumHand;
import java.util.Random;
import net.minecraft.entity.Entity;

import java.util.stream.Collectors;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import net.minecraft.entity.item.EntityMinecart;
import java.util.List;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class TPCart extends Module
{
    public Setting<Float> range;
    public Setting<Float> delay;
    public Timer timer;
    
    public TPCart() {
        super("TPCart", Categories.MOVEMENT, false, false);
        range = register(new Setting<>("Range", 10.0f, 20.0f, 2.0f));
        delay = register(new Setting<>("Delay", 10.0f, 30.0f, 1.0f));
        timer = new Timer();
    }
    
    @Override
    public void onTick() {
        try {
            if (nullCheck()) {
                return;
            }
            if (timer == null) {
                timer = new Timer();
            }
            if (timer.passedX(delay.getValue())) {
                final List<Entity> carts = TPCart.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityMinecart).filter(e -> !e.equals(TPCart.mc.player.getRidingEntity())).filter(e -> PlayerUtil.getDistance(e) <= range.getValue()).collect(Collectors.toList());
                final Entity minecart = carts.get(new Random().nextInt(carts.size()));
                if (minecart == null) {
                    return;
                }
                TPCart.mc.playerController.interactWithEntity(TPCart.mc.player, minecart, EnumHand.MAIN_HAND);
            }
        }
        catch (final Exception ex) {}
    }
}
