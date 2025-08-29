package me.hypinohaizin.candyplusrewrite.module.movement;

import java.util.Comparator;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import net.minecraft.util.math.BlockPos;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class HoleTP extends Module
{
    public Setting<Float> range;
    public Setting<Boolean> stopMotion;

    public HoleTP() {
        super("HoleTP", Categories.MOVEMENT, false, false);
        range = register(new Setting("Range", 1.0f, 3.0f, 0.1f));
        stopMotion = register(new Setting("StopMotion", false));
    }

    @Override
    public void onEnable() {
        final BlockPos hole = CandyPlusRewrite.m_hole.calcHoles().stream().min(Comparator.comparing(p -> HoleTP.mc.player.getDistance(p.getX(), p.getY(), p.getZ()))).orElse(null);
        if (hole != null) {
            if (HoleTP.mc.player.getDistance(hole.getX(), hole.getY(), hole.getZ()) < range.getValue() + 1.5) {
                HoleTP.mc.player.setPosition(hole.getX() + 0.5, HoleTP.mc.player.posY, hole.getZ() + 0.5);
                if (stopMotion.getValue()) {
                    HoleTP.mc.player.motionX = 0.0;
                    HoleTP.mc.player.motionZ = 0.0;
                }
                HoleTP.mc.player.motionY = -3.0;
                sendMessage("Accepting teleport...");
            }
            else {
                sendMessage("Out of range! disabling...");
            }
        }
        else {
            sendMessage("Not found hole! disabling...");
        }
        disable();
    }
}