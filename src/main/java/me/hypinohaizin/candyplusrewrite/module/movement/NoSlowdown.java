package me.hypinohaizin.candyplusrewrite.module.movement;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayer;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;
import net.minecraftforge.client.event.InputUpdateEvent;

public class NoSlowdown extends Module {

    public final Setting<Float> webHorizontalFactor;
    public final Setting<Float> webVerticalFactor;
    public Setting<Boolean> noSlow;
    public Setting<Boolean> strict;
    public Setting<Boolean> sneakPacket;
    public Setting<Boolean> webs;

    private boolean sneaking;
    private boolean sending;

    public NoSlowdown() {
        super("NoSlowdown", Categories.MOVEMENT, false, false);
        webHorizontalFactor = register(new Setting<>("WebHSpeed", 2.0f, 50.0f, 0.0f));
        webVerticalFactor = register(new Setting<>("WebVSpeed", 2.0f, 50.0f, 0.0f));
        noSlow = register(new Setting<>("NoSlow", true));
        strict = register(new Setting<>("Strict", false));
        sneakPacket = register(new Setting<>("SneakPacket", false));
        webs = register(new Setting<>("Webs", false));
        sneaking = false;
        sending = false;
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) return;
        if (!sneakPacket.getValue() && sneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            sneaking = false;
        }
        if (sneaking && !mc.player.isHandActive() && sneakPacket.getValue()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            sneaking = false;
        }
        if (webs.getValue() && mc.player.isInWeb) {
            mc.player.isInWeb = false;
            mc.player.motionX *= webHorizontalFactor.getValue();
            mc.player.motionZ *= webHorizontalFactor.getValue();
            mc.player.motionY *= webVerticalFactor.getValue();
        }
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent e) {
        if (nullCheck()) return;
        if (noSlow.getValue() && mc.player.isHandActive() && !mc.player.isRiding()) {
            e.getMovementInput().moveForward *= 5.0f;
            e.getMovementInput().moveStrafe *= 5.0f;
        }
    }

    @SubscribeEvent
    public void onUseItem(PlayerInteractEvent.RightClickItem event) {
        if (nullCheck()) return;
        if (!sneakPacket.getValue()) return;
        Item item = mc.player.getHeldItem(event.getHand()).getItem();
        if ((item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion) && !sneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            sneaking = true;
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if (nullCheck()) return;
        if (!(event.getPacket() instanceof CPacketPlayer)) return;
        if (!strict.getValue() || !noSlow.getValue()) return;
        if (!mc.player.isHandActive() || mc.player.isRiding()) return;
        if (sending) return;
        sending = true;
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        sending = false;
    }
}