package me.hypinohaizin.candyplusrewrite.module.combat;

import java.util.HashMap;
import net.minecraft.util.math.BlockPos;
import java.util.Map;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class SelfTrap extends Module
{
    private final Setting<Integer> blocksPerTick;
    private final Setting<Integer> delay;
    private final Setting<Boolean> rotate;
    private final Setting<Integer> disableTime;
    private final Setting<Boolean> disable;
    private final Setting<Boolean> packet;
    private final Timer offTimer;
    private final Timer timer;
    private final Map<BlockPos, Integer> retries;
    private final Timer retryTimer;
    private final int blocksThisTick;
    private boolean isSneaking;
    private final boolean hasOffhand;
    
    public SelfTrap() {
        super("SelfTrap", Categories.COMBAT, false, false);
        blocksPerTick = register(new Setting<>("BlocksPerTick", 8, 1, 20));
        delay = register(new Setting<>("Delay", 50, 0, 250));
        rotate = register(new Setting<>("Rotate", true));
        disableTime = register(new Setting<>("DisableTime", 200, 50, 300));
        disable = register(new Setting<>("AutoDisable", true));
        packet = register(new Setting<>("PacketPlace", false));
        offTimer = new Timer();
        timer = new Timer();
        retries = new HashMap<>();
        retryTimer = new Timer();
        blocksThisTick = 0;
        hasOffhand = false;
    }
}
