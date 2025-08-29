package me.hypinohaizin.candyplusrewrite.module.combat;

import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.module.exploit.InstantMine;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.utils.BlockUtil;
import me.hypinohaizin.candyplusrewrite.utils.InventoryUtil;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import net.minecraft.init.Blocks;
import net.minecraft.block.BlockAnvil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.init.Items;

public class AnvilAura extends Module {
    public Setting<Float> targetRange;
    public Setting<Float> placeRange;
    public Setting<Boolean> rotate;
    public Setting<Boolean> packet;
    public Setting<Integer> placeDelay;
    public Setting<Integer> breakDelay;
    public Setting<Integer> stackHeight;
    public Setting<Float> maxTargetSpeed;
    public Setting<Boolean> instantBreak;
    public Setting<Boolean> packetBreak;
    public Setting<Boolean> autoPlace;
    public Setting<Boolean> continuousBreak;

    public Timer placeTimer = new Timer();
    public Timer breakTimer = new Timer();
    public BlockPos currentTarget = null;
    public BlockPos breakingPos = null;
    public EntityPlayer targetPlayer = null;
    private int oldSlot = -1;
    private boolean isBreaking = false;

    public AnvilAura() {
        super("AnvilAura", Categories.COMBAT, false, false);

        targetRange = register(new Setting<>("Target Range", 5.0f, 10.0f, 0.0f));
        placeRange = register(new Setting<>("Place Range", 5.0f, 10.0f, 0.0f));
        rotate = register(new Setting<>("Rotate", true));
        packet = register(new Setting<>("Packet", true));
        placeDelay = register(new Setting<>("Place Delay", 100, 1000, 0));
        breakDelay = register(new Setting<>("Break Delay", 0, 500, 0));
        stackHeight = register(new Setting<>("Stack Height", 5, 15, 1));
        maxTargetSpeed = register(new Setting<>("Max Target Speed", 10.0f, 30f, 0.0f));
        instantBreak = register(new Setting<>("InstantBreak", true));
        packetBreak = register(new Setting<>("PacketBreak", true));
        autoPlace = register(new Setting<>("Auto Place", true));
        continuousBreak = register(new Setting<>("Continuous Break", true));
    }

    public void onTick() {
        if (nullCheck()) {
            return;
        }

        targetPlayer = findTarget();
        if (targetPlayer == null) {
            reset();
            return;
        }

        BlockPos playerPos = new BlockPos(targetPlayer.posX, targetPlayer.posY, targetPlayer.posZ);
        BlockPos feetPos = playerPos;
        BlockPos headPos = playerPos.up(2);

        if (autoPlace.getValue() && placeTimer.passedMs(placeDelay.getValue())) {
            placeAnvilStack(headPos);
        }

        if (continuousBreak.getValue() && breakTimer.passedMs(breakDelay.getValue())) {
            breakFeetAnvil(feetPos);
        }
    }

    private EntityPlayer findTarget() {
        EntityPlayer bestTarget = null;
        double bestDistance = Double.MAX_VALUE;

        for (EntityPlayer player : mc.world.playerEntities) {
            if (player == mc.player) continue;
            if (player.isDead || player.getHealth() <= 0) continue;
            if (FriendManager.isFriend(player)) continue;

            double distance = PlayerUtil.getDistance(player);
            if (distance > targetRange.getValue()) continue;
            if (getSpeed(player) > maxTargetSpeed.getValue()) continue;

            if (distance < bestDistance) {
                bestDistance = distance;
                bestTarget = player;
            }
        }

        return bestTarget;
    }

    private void placeAnvilStack(BlockPos headPos) {
        int anvilSlot = InventoryUtil.findHotbarBlockWithClass(BlockAnvil.class);
        if (anvilSlot == -1) return;

        int obsidianSlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        if (obsidianSlot != -1) {
            placeSupportBlocks(headPos, obsidianSlot);
        }

        boolean needsPlacement = false;
        for (int i = 0; i < stackHeight.getValue(); i++) {
            BlockPos checkPos = headPos.up(i);
            if (mc.world.isAirBlock(checkPos) && canPlace(checkPos)) {
                needsPlacement = true;
                break;
            }
        }

        if (!needsPlacement) return;

        oldSlot = mc.player.inventory.currentItem;
        InventoryUtil.switchToHotbarSlot(anvilSlot, false);

        for (int i = 0; i < stackHeight.getValue(); i++) {
            BlockPos placePos = headPos.up(i);

            if (mc.world.isAirBlock(placePos) && canPlace(placePos)) {
                BlockUtil.placeBlock(placePos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue());
                placeTimer.reset();
                break;
            }
        }

        if (oldSlot != -1) {
            InventoryUtil.switchToHotbarSlot(oldSlot, false);
        }
    }

    private void placeSupportBlocks(BlockPos basePos, int slot) {
        oldSlot = mc.player.inventory.currentItem;
        InventoryUtil.switchToHotbarSlot(slot, false);

        if (!mc.world.isAirBlock(basePos) || canPlace(basePos)) {
            if (oldSlot != -1) {
                InventoryUtil.switchToHotbarSlot(oldSlot, false);
            }
            return;
        }

        final BlockPos feetPos = new BlockPos(targetPlayer.posX, targetPlayer.posY, targetPlayer.posZ);
        final int maxHeight = feetPos.getY() + 2;

        final BlockPos[] horizontalOffsets = {
                new BlockPos(1, 0, 0),
                new BlockPos(-1, 0, 0),
                new BlockPos(0, 0, 1),
                new BlockPos(0, 0, -1)
        };

        BlockPos farthestOffset = null;
        double farthestDistance = -1;

        for (BlockPos offset : horizontalOffsets) {
            BlockPos posToCheck = feetPos.add(offset);
            double distance = mc.player.getDistance(posToCheck.getX() + 0.5, posToCheck.getY(), posToCheck.getZ() + 0.5);
            if (distance > farthestDistance) {
                farthestDistance = distance;
                farthestOffset = offset;
            }
        }

        if (farthestOffset == null) {
            if (oldSlot != -1) {
                InventoryUtil.switchToHotbarSlot(oldSlot, false);
            }
            return;
        }

        BlockPos supportPos = basePos.add(farthestOffset).down(1);

        for (int i = 0; i < stackHeight.getValue(); i++) {
            BlockPos placePos = supportPos.up(i);

            if (placePos.getY() > maxHeight) break;

            if (mc.world.isAirBlock(placePos) && canPlace(placePos)) {
                BlockUtil.placeBlock(placePos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue());
                placeTimer.reset();
                break;
            }
        }

        if (oldSlot != -1) {
            InventoryUtil.switchToHotbarSlot(oldSlot, false);
        }
    }

    private void breakFeetAnvil(BlockPos feetPos) {
        if (!(BlockUtil.getBlock(feetPos) instanceof BlockAnvil)) {
            return;
        }

        boolean hasAnvilsAbove = false;
        for (int i = 1; i <= stackHeight.getValue(); i++) {
            if (BlockUtil.getBlock(feetPos.up(i)) instanceof BlockAnvil) {
                hasAnvilsAbove = true;
                break;
            }
        }

        if (!hasAnvilsAbove) {
            return;
        }

        breakingPos = feetPos;

        int pickaxeSlot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
        if (pickaxeSlot == -1) return;

        int currentSlot = mc.player.inventory.currentItem;
        InventoryUtil.switchToHotbarSlot(pickaxeSlot, false);

        try {
            Module instantMineModule = CandyPlusRewrite.m_module.getModuleWithClass(InstantMine.class);

            if (instantBreak.getValue() && instantMineModule != null) {

                if (!isBreaking) {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    instantMineModule.enable();
                    InstantMine.startBreak(feetPos, EnumFacing.DOWN);
                    isBreaking = true;
                }

                if (mc.world.isAirBlock(feetPos)) {
                    isBreaking = false;
                    InstantMine.resetMining();
                }
            } else {
                mc.player.swingArm(EnumHand.MAIN_HAND);

                if (packetBreak.getValue()) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(
                            CPacketPlayerDigging.Action.START_DESTROY_BLOCK, feetPos, EnumFacing.DOWN
                    ));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(
                            CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, feetPos, EnumFacing.DOWN
                    ));
                } else {
                    mc.playerController.clickBlock(feetPos, EnumFacing.DOWN);
                }
            }

            breakTimer.reset();

        } catch (Exception e) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket(new CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.START_DESTROY_BLOCK, feetPos, EnumFacing.DOWN
            ));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, feetPos, EnumFacing.DOWN
            ));
        }

        InventoryUtil.switchToHotbarSlot(currentSlot, false);
    }

    private boolean canPlace(BlockPos pos) {
        if (!BlockUtil.canPlaceBlock(pos)) return false;
        return mc.player.getDistance(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) <= placeRange.getValue();
    }

    private double getSpeed(EntityPlayer player) {
        double dx = player.posX - player.prevPosX;
        double dz = player.posZ - player.prevPosZ;
        return Math.sqrt(dx * dx + dz * dz) * 20.0;
    }

    private void reset() {
        currentTarget = null;
        breakingPos = null;
        targetPlayer = null;
        isBreaking = false;

        try {
            InstantMine.resetMining();
        } catch (Exception e) {
        }
    }

    public void onDisable() {
        reset();
        if (oldSlot != -1) {
            InventoryUtil.switchToHotbarSlot(oldSlot, false);
            oldSlot = -1;
        }
    }
}
