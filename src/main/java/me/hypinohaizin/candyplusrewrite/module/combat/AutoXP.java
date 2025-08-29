package me.hypinohaizin.candyplusrewrite.module.combat;

import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayer;
import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import net.minecraft.init.Items;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import net.minecraft.util.EnumHand;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class AutoXP extends Module
{
    public Setting<Boolean> packetThrow;
    public Setting<Boolean> silentSwitch;
    public Setting<Float> delay;
    public int oldSlot;
    public EnumHand oldHand;
    public Timer timer;
    
    public AutoXP() {
        super("AutoXP", Categories.COMBAT, false, false);
        packetThrow = register(new Setting<>("PacketThrow", true));
        silentSwitch = register(new Setting<>("SilentSwitch", true));
        delay = register(new Setting<>("Delay", 0.0f, 25.0f, 0.0f));
        oldSlot = -1;
        oldHand = null;
        timer = new Timer();
    }
    
    @Override
    public void onEnable() {
        timer = new Timer();
    }
    
    @Override
    public void onTick() {
        if (nullCheck()) {
            return;
        }
        final int xp = InventoryUtil.getItemHotbar(Items.EXPERIENCE_BOTTLE);
        if (xp == -1) {
            restoreItem();
            return;
        }
        if (timer.passedX(delay.getValue())) {
            timer.reset();
            setItem(xp);
            AutoXP.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0.0f, 90.0f, true));
            if (packetThrow.getValue()) {
                AutoXP.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            }
            else {
                AutoXP.mc.playerController.processRightClick(AutoXP.mc.player, AutoXP.mc.world, EnumHand.MAIN_HAND);
            }
        }
        restoreItem();
    }
    
    public void setItem(final int slot) {
        if (silentSwitch.getValue()) {
            oldHand = null;
            if (AutoXP.mc.player.isHandActive()) {
                oldHand = AutoXP.mc.player.getActiveHand();
            }
            oldSlot = AutoXP.mc.player.inventory.currentItem;
            AutoXP.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        }
        else {
            AutoXP.mc.player.inventory.currentItem = slot;
            AutoXP.mc.playerController.updateController();
        }
    }
    
    public void restoreItem() {
        if (oldSlot != -1 && silentSwitch.getValue()) {
            if (oldHand != null) {
                AutoXP.mc.player.setActiveHand(oldHand);
            }
            AutoXP.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            oldSlot = -1;
            oldHand = null;
        }
    }
}
