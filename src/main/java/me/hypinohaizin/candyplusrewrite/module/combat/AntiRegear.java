package me.hypinohaizin.candyplusrewrite.module.combat;

import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil3D;
import me.hypinohaizin.candyplusrewrite.utils.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class AntiRegear extends Module {
    public Setting<Boolean> outline;
    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Integer> alpha;
    public Setting<Integer> outlineAlpha;
    public Setting<Boolean> render;
    public Setting<Boolean> pickOnly;
    public Setting<Boolean> enderChest;
    public Setting<Float> range;
    public Setting<Float> delay;
    public Setting<Float> wallRange;
    public Setting<Float> targetRange;
    public Setting<RotationMode> rotationMode;
    public Setting<Boolean> strict;


    private final Timer timer = new Timer();
    private boolean isBreakingBlock = false;
    private BlockPos breakingPos = null;

    public AntiRegear() {
        super("AntiRegear", Categories.COMBAT, false, false);

        outline = register(new Setting<>("Outline", true));
        red = register(new Setting<>("Red", 176, 255, 0));
        green = register(new Setting<>("Green", 118, 255, 0));
        blue = register(new Setting<>("Blue", 255, 255, 0));
        alpha = register(new Setting<>("Alpha", 60, 255, 0));
        outlineAlpha = register(new Setting<>("O-Alpha", 255, 255, 0));
        render = register(new Setting<>("Render", true));
        pickOnly = register(new Setting<>("Pickaxe Only", false));
        enderChest = register(new Setting<>("E-Chest Support", false));
        range = register(new Setting<>("Range", 5.0f, 6.0f, 1.0f));
        delay = register(new Setting<>("Delay", 1.0f, 5.0f, 0.0f));
        wallRange = register(new Setting<>("Wall-Range", 3.0f, 6.0f, 0.0f));
        targetRange = register(new Setting<>("Enemy-Range", 5.0f, 12.0f, 0.0f));
        rotationMode = register(new Setting<>("Rotation Mode", RotationMode.Vanilla));
        strict = register(new Setting<>("Strict", true));

    }

    @Override
    public void onRender3D() {
        if (nullCheck()) {
            return;
        }
        if (!render.getValue() || !isBreakingBlock || breakingPos == null) return;
        Block block = mc.world.getBlockState(breakingPos).getBlock();
        if (block instanceof BlockShulkerBox || (enderChest.getValue() && block instanceof BlockEnderChest)) {
            Color boxColor = new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
            RenderUtil3D.drawBox(breakingPos, 1.0, boxColor, alpha.getValue());
            if (outline.getValue()) {
                Color outlineColor = new Color(red.getValue(), green.getValue(), blue.getValue(), outlineAlpha.getValue());
                RenderUtil3D.drawBoundingBox(breakingPos, 1.0, 1.0f, outlineColor);
            }
        }
    }

    @Override
    public void onTick() {
        if (nullCheck()) {
            return;
        }
        if (!timer.passedMs((long)(delay.getValue()*1000))) return;
        if (strict.getValue() && !mc.player.onGround) return;

        isBreakingBlock = false;
        breakingPos = null;
        BlockPos playerPos = mc.player.getPosition();

        for (BlockPos pos : BlockPos.getAllInBox(
                playerPos.add(-range.getValue(),-range.getValue(),-range.getValue()),
                playerPos.add(range.getValue(),range.getValue(),range.getValue())
        )) {
            Block block = mc.world.getBlockState(pos).getBlock();
            if (!(block instanceof BlockShulkerBox) && !(enderChest.getValue() && block instanceof BlockEnderChest)) continue;
            if (pickOnly.getValue() && !(mc.player.inventory.getCurrentItem().getItem() instanceof ItemPickaxe)) return;
            if ((isBlockBehindWall(pos) && pos.distanceSq(playerPos) > wallRange.getValue()*wallRange.getValue())
                    || !isEnemyNearby(pos)) continue;
            faceBlock(pos);
            mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            timer.reset();
            isBreakingBlock = true;
            breakingPos = pos;
            break;
        }
    }

    private boolean isBlockBehindWall(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        Vec3d posVec = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        RayTraceResult result = mc.world.rayTraceBlocks(eyesPos, posVec, false, true, false);
        return result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && !result.getBlockPos().equals(pos);
    }


    private boolean isEnemyNearby(BlockPos pos) {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player == mc.player) continue;
            if (FriendManager.isFriend(player.getName())) continue;
            if (player.getDistanceSq(pos) <= targetRange.getValue()*targetRange.getValue()) return true;
        }
        return false;
    }

    private void faceBlock(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        Vec3d posVec = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        double diffX = posVec.x - eyesPos.x;
        double diffY = posVec.y - eyesPos.y;
        double diffZ = posVec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180 / Math.PI) - 90f;
        float pitch = (float) (-(Math.atan2(diffY, diffXZ) * 180 / Math.PI));
        if (rotationMode.getValue() == RotationMode.Vanilla) {
            mc.player.rotationYaw = yaw;
            mc.player.rotationPitch = pitch;
        }
    }

    public enum RotationMode {
        Vanilla,
    }
}
