package me.hypinohaizin.candyplusrewrite.module.misc;

import me.hypinohaizin.candyplusrewrite.module.Module;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;

public class HoleBreakAlert extends Module {

    public HoleBreakAlert() {
        super("HoleBreakAlert", Categories.MISC, false, false);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketBlockBreakAnim) {
            SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim) event.getPacket();

            if (isHoleBlock(packet.getPosition())) {
                int breakerId = packet.getBreakerId();
                String breakerName = "Unknown";
                if (mc.world.getEntityByID(breakerId) != null) {
                    breakerName = mc.world.getEntityByID(breakerId).getName();
                }

                sendMessage("The hole block to your " + getBlockDirectionFromPlayer(packet.getPosition()) + " is being broken by " + breakerName);
            }
        }
    }

    private String getBlockDirectionFromPlayer(BlockPos pos) {
        double posX = Math.floor(mc.player.posX);
        double posZ = Math.floor(mc.player.posZ);

        double x = posX - pos.getX();
        double z = posZ - pos.getZ();

        switch (mc.player.getHorizontalFacing()) {
            case SOUTH:
                if (x == 1) return "right";
                if (x == -1) return "left";
                if (z == 1) return "back";
                if (z == -1) return "front";
                break;
            case WEST:
                if (x == 1) return "front";
                if (x == -1) return "back";
                if (z == 1) return "right";
                if (z == -1) return "left";
                break;
            case NORTH:
                if (x == 1) return "left";
                if (x == -1) return "right";
                if (z == 1) return "front";
                if (z == -1) return "back";
                break;
            case EAST:
                if (x == 1) return "back";
                if (x == -1) return "front";
                if (z == 1) return "left";
                if (z == -1) return "right";
                break;
            default:
                return "undetermined";
        }
        return "undetermined";
    }

    private boolean isHoleBlock(BlockPos pos) {
        double posX = Math.floor(mc.player.posX);
        double posZ = Math.floor(mc.player.posZ);

        Block block = mc.world.getBlockState(pos).getBlock();

        if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN) {
            if (pos.getX() == (posX + 1) && pos.getY() == mc.player.getPosition().getY()) return true;
            if (pos.getX() == (posX - 1) && pos.getY() == mc.player.getPosition().getY()) return true;
            if (pos.getZ() == (posZ + 1) && pos.getY() == mc.player.getPosition().getY()) return true;
            if (pos.getZ() == (posZ - 1) && pos.getY() == mc.player.getPosition().getY()) return true;
        }
        return false;
    }
}
