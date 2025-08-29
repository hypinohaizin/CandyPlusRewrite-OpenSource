package me.hypinohaizin.candyplusrewrite.module.combat;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import me.hypinohaizin.candyplusrewrite.utils.BlockUtil;
import java.util.List;
import java.util.function.Function;
import java.util.Comparator;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;

import java.util.ArrayList;
import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import net.minecraft.init.Items;
import me.hypinohaizin.candyplusrewrite.module.exploit.InstantMine;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class AntiBurrow extends Module
{
    public Setting<Target> targetType;
    public Setting<Float> targetRange;
    public Setting<Float> range;
    public Setting<Boolean> instantBreak;
    public Setting<Boolean> noSwing;
    public Setting<Boolean> switcH;
    public Setting<Boolean> obby;
    public Setting<Boolean> echest;
    public EntityPlayer target;
    public BlockPos breakPos;
    
    public AntiBurrow() {
        super("AntiBurrow", Categories.COMBAT, false, false);
        targetType = register(new Setting<>("Target", Target.Nearest));
        targetRange = register(new Setting<>("Target Range", 10.0f, 20.0f, 0.0f));
        range = register(new Setting<>("Range", 10.0f, 20.0f, 0.0f));
        instantBreak = register(new Setting<>("InstantBreak", true));
        noSwing = register(new Setting<>("NoSwing", false));
        switcH = register(new Setting<>("Switch", false));
        obby = register(new Setting<>("Obby", false));
        echest = register(new Setting<>("EChest", false));
        target = null;
        breakPos = null;
    }
    
    @Override
    public void onEnable() {
        if (nullCheck()) {
            return;
        }
        target = findTarget();
        if (target == null) {
            sendMessage("Cannot find target! disabling");
            disable();
            return;
        }
        if (!canBreak(getPos(target))) {
            sendMessage("Target is not in block! disabling");
            return;
        }
        breakPos = getPos(target);
        sendMessage("Breaking...");
        if (!instantBreak.getValue()) {
            if (!noSwing.getValue()) {
                AntiBurrow.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            AntiBurrow.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakPos, EnumFacing.DOWN));
            AntiBurrow.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, EnumFacing.DOWN));
        }
        else {
            if (!noSwing.getValue()) {
                AntiBurrow.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            CandyPlusRewrite.m_module.getModuleWithClass(InstantMine.class).enable();
            InstantMine.startBreak(breakPos, EnumFacing.DOWN);
        }
        if (switcH.getValue()) {
            final int pickel = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
            if (pickel == -1) {
                return;
            }
            AntiBurrow.mc.player.inventory.currentItem = pickel;
            AntiBurrow.mc.playerController.updateController();
        }
        disable();
    }
    
    public EntityPlayer findTarget() {
        EntityPlayer target = null;
        final List<EntityPlayer> players = new ArrayList<>(AntiBurrow.mc.world.playerEntities);
        if (targetType.getValue() == Target.Nearest) {
            target = PlayerUtil.getNearestPlayer(targetRange.getValue());
        }
        if (targetType.getValue() == Target.Looking) {
            target = PlayerUtil.getLookingPlayer(targetRange.getValue());
        }
        if (targetType.getValue() == Target.Best) {
            target = (EntityPlayer) players.stream().filter(e -> canBreak(getPos(e))).min(Comparator.comparing((Function<? super EntityPlayer, ? extends Comparable>)PlayerUtil::getDistance)).orElse(null);
        }
        return target;
    }
    
    public BlockPos getPos(final EntityPlayer player) {
        return new BlockPos(player.posX, player.posY, player.posZ);
    }
    
    public boolean canBreak(final BlockPos pos) {
        final Block block = BlockUtil.getBlock(pos);
        boolean can = block == Blocks.ANVIL;
        if (block == Blocks.ENDER_CHEST && echest.getValue()) {
            can = true;
        }
        if (block == Blocks.OBSIDIAN && obby.getValue()) {
            can = true;
        }
        return can;
    }
    
    public enum Target
    {
        Nearest, 
        Looking, 
        Best
    }
}
