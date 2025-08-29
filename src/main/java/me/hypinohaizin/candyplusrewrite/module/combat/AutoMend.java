package me.hypinohaizin.candyplusrewrite.module.combat;

import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.item.ItemStack;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;
import java.util.concurrent.atomic.AtomicInteger;
import org.lwjgl.input.Keyboard;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayer;
import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import net.minecraft.init.Items;
import java.util.HashMap;
import net.minecraft.util.EnumHand;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import java.util.Map;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class AutoMend extends Module
{
    public Setting<Float> delay;
    public Setting<Float> armor;
    public Setting<Float> pct;
    public Setting<Boolean> press;
    public Setting<Boolean> silentSwitch;
    public boolean toggleoff;
    public Map<Integer, Integer> armors;
    public Timer xpTimer;
    public Timer armorTimer;
    private EnumHand oldhand;
    private int oldslot;
    
    public AutoMend() {
        super("AutoMend", Categories.COMBAT, false, false);
        delay = register(new Setting<>("Delay", 3.0f, 10.0f, 0.0f));
        armor = register(new Setting<>("ArmorDelay", 3.0f, 20.0f, 0.0f));
        pct = register(new Setting<>("Pct", 80.0f, 100.0f, 10.0f));
        press = register(new Setting<>("Press", true));
        silentSwitch = register(new Setting<>("SilentSwitch", true));
        toggleoff = false;
        armors = new HashMap<Integer, Integer>();
        armorTimer = new Timer();
        oldhand = null;
        oldslot = -1;
        disable();
    }
    
    @Override
    public void onEnable() {
        xpTimer = new Timer();
        armorTimer = new Timer();
    }
    
    @Override
    public void onDisable() {
        if (!toggleoff) {
            toggleoff = true;
            armors = new HashMap<Integer, Integer>();
            enable();
            return;
        }
        toggleoff = false;
    }
    
    @Override
    public void onTick() {
        if (nullCheck()) {
            return;
        }
        if (!toggleoff) {
            if (armorTimer == null) {
                armorTimer = new Timer();
            }
            armorTimer.reset();
            final int xp = InventoryUtil.getItemHotbar(Items.EXPERIENCE_BOTTLE);
            if (xp == -1) {
                sendMessage("Cannot find XP! disabling");
                setDisable();
                return;
            }
            if (isDone()) {
                setDisable();
                return;
            }
            if (xpTimer == null) {
                xpTimer = new Timer();
            }
            if (xpTimer.passedX(delay.getValue())) {
                xpTimer.reset();
                setItem(xp);
                AutoMend.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0.0f, 90.0f, false));
                AutoMend.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                restoreItem();
            }
            if (key.key != -1 && press.getValue()) {
                if (!Keyboard.isKeyDown(key.key)) {
                    setDisable();
                }
            }
        }
        else {
            if (armorTimer == null) {
                armorTimer = new Timer();
            }
            if (armorTimer.passedDms(armor.getValue())) {
                armorTimer.reset();
                final AtomicInteger c = new AtomicInteger();
                final AtomicInteger key = new AtomicInteger();
                armors.forEach((k, v) -> {
                    if (c.get() == 0) {
                        InventoryUtil.moveItemTo(k, v);
                        key.set(k);
                    }
                    c.getAndIncrement();
                });
                if (c.get() == 0) {
                    disable();
                }
                else {
                    armors.remove(key.get());
                }
            }
        }
    }
    
    @Override
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            final CPacketPlayer cPacketPlayer = (CPacketPlayer)event.getPacket();
        }
    }
    
    public void setDisable() {
        toggleoff = true;
    }
    
    public void moveArmorToSlot(final int armor, final int empty) {
        InventoryUtil.moveItemTo(armor, empty);
        armors.put(empty, armor);
    }
    
    public boolean isDone() {
        boolean done = true;
        for (int i = 0; i < AutoMend.mc.player.inventoryContainer.getInventory().size(); ++i) {
            final ItemStack itemStack = AutoMend.mc.player.inventoryContainer.getInventory().get(i);
            if (i > 4) {
                if (i < 9) {
                    if (!itemStack.isEmpty()) {
                        if (getRate(itemStack) < pct.getValue()) {
                            done = false;
                        }
                        else {
                            final int slot = getFreeSlot();
                            if (slot != -1) {
                                moveArmorToSlot(i, slot);
                            }
                        }
                    }
                }
            }
        }
        return done;
    }
    
    public int getFreeSlot() {
        for (int i = 0; i < AutoMend.mc.player.inventoryContainer.getInventory().size(); ++i) {
            if (i != 0 && i != 1 && i != 2 && i != 3 && i != 4 && i != 5 && i != 6 && i != 7) {
                if (i != 8) {
                    final ItemStack stack = AutoMend.mc.player.inventoryContainer.getInventory().get(i);
                    if (stack.isEmpty() || stack.getItem() == Items.AIR) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    
    public float getRate(final ItemStack itemStack) {
        return (itemStack.getMaxDamage() - itemStack.getItemDamage()) / (itemStack.getMaxDamage() * 1.0f) * 100.0f;
    }
    
    public void setItem(final int slot) {
        if (silentSwitch.getValue()) {
            oldhand = null;
            if (AutoMend.mc.player.isHandActive()) {
                oldhand = AutoMend.mc.player.getActiveHand();
            }
            oldslot = AutoMend.mc.player.inventory.currentItem;
            AutoMend.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        }
        else {
            AutoMend.mc.player.inventory.currentItem = slot;
            AutoMend.mc.playerController.updateController();
        }
    }
    
    public void restoreItem() {
        if (oldslot != -1 && silentSwitch.getValue()) {
            if (oldhand != null) {
                AutoMend.mc.player.setActiveHand(oldhand);
            }
            AutoMend.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            oldslot = -1;
            oldhand = null;
        }
    }
}
