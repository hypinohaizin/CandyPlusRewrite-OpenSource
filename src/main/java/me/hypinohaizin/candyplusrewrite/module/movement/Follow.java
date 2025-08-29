package me.hypinohaizin.candyplusrewrite.module.movement;

import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import me.hypinohaizin.candyplusrewrite.utils.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;

public class Follow extends Module {

    public Setting<ModeType> mode;

    public Follow() {
        super("Follow", Categories.MOVEMENT, false, false);
        mode  = register(new Setting<>("Mode", ModeType.MOTION));
    }

    private EntityPlayer getTarget() {
        return mc.world.playerEntities.stream()
                .filter(p -> p.getEntityId() != mc.player.getEntityId())
                .filter(p -> !p.isDead && p.getHealth() > 0)
                .filter(p -> !FriendManager.isFriend(p.getName()))
                .min(Comparator.comparingDouble(p -> mc.player.getDistance(p)))
                .orElse(null);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.player == null || mc.world == null) return;
        EntityPlayer target = getTarget();
        if (target == null) return;
        switch (mode.getValue()) {
            case MOTION:
                EntityUtil.motion(target.posX, target.posY, target.posZ);
                break;
            case SET_POSITION:
                mc.player.setPosition(target.posX, target.posY, target.posZ);
                break;
            case SET_POS_UPDATE:
                mc.player.setPositionAndUpdate(target.posX, target.posY, target.posZ);
                break;
        }
    }
    public enum ModeType {
        MOTION,
        SET_POSITION,
        SET_POS_UPDATE
    }

}
