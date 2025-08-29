package me.hypinohaizin.candyplusrewrite.module.combat;

import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.module.exploit.InstantMine;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil3D;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.*;

import java.awt.Color;

public class AutoCityRewrite extends Module {
    public Setting<Target> targetType;
    public Setting<Float> targetRange;
    public Setting<Float> range;
    public Setting<Boolean> instantBreak;
    public Setting<Boolean> noSwing;
    public Setting<Boolean> switcH;
    public Setting<Boolean> noSuicide;
    public Setting<Boolean> burrow;
    public Setting<Boolean> multi;
    public Setting<Integer> delay;
    public Setting<Boolean> tick;

    public Setting<Color> renderColor;
    public Setting<Boolean> outline;
    public Setting<Float> width;

    public EntityPlayer target;
    public BlockPos breakPos;
    public Timer timer = new Timer();

    public AutoCityRewrite() {
        super("AutoCityRewrite", Categories.COMBAT, false, false);

        targetType = register(new Setting<>("Target", Target.Nearest));
        targetRange = register(new Setting<>("Target Range", 10.0f, 20.0f, 0.0f));
        range = register(new Setting<>("Range", 6.0f, 12.0f, 1.0f));
        instantBreak = register(new Setting<>("InstantBreak", true));
        noSwing = register(new Setting<>("NoSwing", false));
        switcH = register(new Setting<>("Switch", false));
        noSuicide = register(new Setting<>("NoSuicide", true));
        burrow = register(new Setting<>("MineBurrow", true));
        multi = register(new Setting<>("Multi", false));
        delay = register(new Setting<>("Delay", 200, 1000, 0));
        tick = register(new Setting<>("Tick", true));

        renderColor = register(new Setting<>("Color", new Color(230, 50, 50, 100)));
        outline = register(new Setting<>("Outline", false));
        width = register(new Setting<>("Width", 3.0f, 6.0f, 0.1f, v -> outline.getValue()));
    }

    @Override
    public void onEnable() {
        reset();
    }

    @Override
    public void onDisable() {
        reset();
    }

    @Override
    public void onTick() {
        if (nullCheck()) {
            return;
        }
        if (tick.getValue()) run();
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) {
            return;
        }
        if (!tick.getValue()) run();
    }

    private void run() {
        if (nullCheck() || !timer.passedMs(delay.getValue())) return;
        target = findTarget();
        if (target == null) {
            disable();
            return;
        }
        BlockPos base = new BlockPos(target.posX, target.posY, target.posZ);

        if (burrow.getValue() && isBurrowed(target)) {
            mineBlock(base);
            breakPos = base;
            timer.reset();
            return;
        }

        List<BlockPos> cityList = findCityBlocks(target);
        if (cityList.isEmpty()) {
            sendMessage("Cannot find cityable block! disabling...");
            disable();
            return;
        }
        if (multi.getValue()) {
            for (BlockPos pos : cityList) {
                mineBlock(pos);
                breakPos = pos;
            }
        } else {
            mineBlock(cityList.get(0));
            breakPos = cityList.get(0);
        }
        timer.reset();
        if (!tick.getValue()) disable();
    }

    private boolean isBurrowed(EntityPlayer player) {
        BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
        Block block = mc.world.getBlockState(pos).getBlock();
        return block != Blocks.AIR && block != Blocks.BEDROCK && block != Blocks.WATER && block != Blocks.LAVA;
    }

    private List<BlockPos> findCityBlocks(EntityPlayer target) {
        List<BlockPos> result = new ArrayList<>();
        BlockPos base = new BlockPos(target.posX, target.posY, target.posZ);
        BlockPos[] offsets = {new BlockPos(1,0,0), new BlockPos(-1,0,0), new BlockPos(0,0,1), new BlockPos(0,0,-1)};
        for (BlockPos offset : offsets) {
            BlockPos breakPos = base.add(offset);
            Block block = mc.world.getBlockState(breakPos).getBlock();
            if (block == Blocks.OBSIDIAN || block == Blocks.ENDER_CHEST) {
                if (noSuicide.getValue()) {
                    BlockPos mypos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
                    if (mypos.equals(breakPos)) continue;
                }
                BlockPos up1 = breakPos.up();
                BlockPos up2 = up1.up();
                if (mc.world.getBlockState(up1).getBlock() == Blocks.AIR && mc.world.getBlockState(up2).getBlock() == Blocks.AIR) {
                    result.add(breakPos);
                }
            }
        }
        return result;
    }

    private void mineBlock(BlockPos pos) {
        if (switcH.getValue()) {
            int pickel = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
            if (pickel != -1) {
                mc.player.inventory.currentItem = pickel;
                mc.playerController.updateController();
            }
        }
        if (!instantBreak.getValue()) {
            if (!noSwing.getValue()) mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
        } else {
            if (!noSwing.getValue()) mc.player.swingArm(EnumHand.MAIN_HAND);
            CandyPlusRewrite.m_module.getModuleWithClass(InstantMine.class).enable();
            InstantMine.startBreak(pos, EnumFacing.DOWN);
        }
    }

    private EntityPlayer findTarget() {
        List<EntityPlayer> players = new ArrayList<>(mc.world.playerEntities);
        players.removeIf(p -> p == mc.player || FriendManager.isFriend(p.getName()) || p.isDead || p.getHealth() <= 0);
        if (players.isEmpty()) return null;
        if (targetType.getValue() == Target.Nearest) {
            return players.stream().min(Comparator.comparing(p -> mc.player.getDistance(p))).orElse(null);
        }
        if (targetType.getValue() == Target.Looking) {
            return PlayerUtil.getLookingPlayer(targetRange.getValue());
        }
        if (targetType.getValue() == Target.Best) {
            return players.stream().max(Comparator.comparing(this::scoreCity)).orElse(null);
        }
        return null;
    }

    private int scoreCity(EntityPlayer p) {
        List<BlockPos> list = findCityBlocks(p);
        return list.size();
    }

    private void reset() {
        target = null;
        breakPos = null;
        timer.reset();
    }

    @Override
    public void onRender3D() {
        if (breakPos != null) {
            RenderUtil3D.drawBox(breakPos, 1.0, renderColor.getValue(), 63);
            if (outline.getValue()) {
                RenderUtil3D.drawBoundingBox(breakPos, 1.0, width.getValue(), renderColor.getValue());
            }
        }
    }

    public enum Target {
        Nearest, Looking, Best
    }
}
