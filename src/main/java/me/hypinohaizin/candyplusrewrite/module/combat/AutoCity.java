package me.hypinohaizin.candyplusrewrite.module.combat;

import java.util.function.Function;
import java.util.List;
import java.util.Comparator;

import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import net.minecraft.init.Blocks;
import me.hypinohaizin.candyplusrewrite.utils.BlockUtil;

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

public class AutoCity extends Module
{
    public Setting<Target> targetType;
    public Setting<Float> targetRange;
    public Setting<Float> range;
    public Setting<Boolean> instantBreak;
    public Setting<Boolean> noSwing;
    public Setting<Boolean> switcH;
    public Setting<Boolean> noSuicide;
    public EntityPlayer target;
    public BlockPos breakPos;
    
    public AutoCity() {
        super("AutoCity", Categories.COMBAT, false, false);
        targetType = register(new Setting<>("Target", Target.Nearest));
        targetRange = register(new Setting<>("Target Range", 10.0f, 20.0f, 0.0f));
        range = register(new Setting<>("Range", 10.0f, 20.0f, 0.0f));
        instantBreak = register(new Setting<>("InstantBreak", true));
        noSwing = register(new Setting<>("NoSwing", false));
        switcH = register(new Setting<>("Switch", false));
        noSuicide = register(new Setting<>("NoSuicide", true));
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
            sendMessage("Cannot find target! disabling...");
            disable();
            return;
        }
        if (findSpace(target) == -1) {
            sendMessage("Cannot find space! disabling...");
            disable();
            return;
        }
        sendMessage("Breaking...");
        if (!instantBreak.getValue()) {
            if (!noSwing.getValue()) {
                AutoCity.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            AutoCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakPos, EnumFacing.DOWN));
            AutoCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, EnumFacing.DOWN));
        }
        else {
            if (!noSwing.getValue()) {
                AutoCity.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            CandyPlusRewrite.m_module.getModuleWithClass(InstantMine.class).enable();
            InstantMine.startBreak(breakPos, EnumFacing.DOWN);
        }
        if (switcH.getValue()) {
            final int pickel = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
            if (pickel == -1) {
                return;
            }
            AutoCity.mc.player.inventory.currentItem = pickel;
            AutoCity.mc.playerController.updateController();
        }
        disable();
    }
    
    public int findSpace(final EntityPlayer target) {
        final BlockPos mypos = new BlockPos(AutoCity.mc.player.posX, AutoCity.mc.player.posY, AutoCity.mc.player.posZ);
        final BlockPos base = new BlockPos(target.posX, target.posY, target.posZ);
        final BlockPos[] offsets = { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
        final List<CitySpace> spaces = new ArrayList<>();
        BlockPos s = null;
        for (final BlockPos offset : offsets) {
            final CitySpace pos = new CitySpace();
            final BlockPos breakPos = base.add(offset);
            Label_0349: {
                if (BlockUtil.getBlock(breakPos) == Blocks.OBSIDIAN || BlockUtil.getBlock(breakPos) == Blocks.ENDER_CHEST) {
                    if (noSuicide.getValue()) {
                        boolean shouldSkip = false;
                        final BlockPos[] array2 = offsets;
                        for (int length2 = array2.length, j = 0; j < length2; ++j) {
                            s = array2[j];
                            final BlockPos spos = mypos.add(s);
                            if (spos.equals(breakPos)) {
                                shouldSkip = true;
                            }
                        }
                        if (shouldSkip) {
                            break Label_0349;
                        }
                    }
                    pos.setPos(breakPos);
                    final BlockPos levelPos = breakPos.add(offset);
                    if (BlockUtil.getBlock(levelPos) != Blocks.AIR) {
                        pos.setLevel(0);
                    }
                    else if (BlockUtil.getBlock(levelPos.add(0, 1, 0)) != Blocks.AIR) {
                        pos.setLevel(1);
                    }
                    else {
                        pos.setLevel(2);
                    }
                    spaces.add(pos);
                }
            }
        }
        final CitySpace space = spaces.stream().filter(s2 -> PlayerUtil.getDistance(s2.pos) <= range.getValue()).max(Comparator.comparing(s2 -> s2.level + (range.getValue() - PlayerUtil.getDistance(s2.pos)))).orElse(null);
        if (space == null) {
            return -1;
        }
        breakPos = space.pos;
        return space.level;
    }

    public EntityPlayer findTarget() {
        EntityPlayer target = null;

        final List<EntityPlayer> players = new ArrayList<>(AutoCity.mc.world.playerEntities);
        players.removeIf(p -> p == mc.player || FriendManager.isFriend(p.getName()));

        if (targetType.getValue() == Target.Nearest) {
            target = PlayerUtil.getNearestPlayer(targetRange.getValue());
        }
        if (targetType.getValue() == Target.Looking) {
            target = PlayerUtil.getLookingPlayer(targetRange.getValue());
        }
        if (targetType.getValue() == Target.Best) {
            target = (EntityPlayer) players.stream().max(Comparator.comparing((Function<? super EntityPlayer, ? extends Comparable>)this::findSpace)).orElse(null);
        }
        return target;
    }
    
    public enum Target
    {
        Nearest, 
        Looking, 
        Best
    }
    
    public class CitySpace
    {
        public BlockPos pos;
        public int level;
        
        public CitySpace() {
            level = -1;
        }
        
        public void setPos(final BlockPos pos) {
            this.pos = pos;
        }
        
        public void setLevel(final int level) {
            this.level = level;
        }
    }
}
