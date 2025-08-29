package me.hypinohaizin.candyplusrewrite.utils;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

/**
 * @author h_ypi
 * @since 2025/08/03 23:33
 */
public class MovementUtil {
    public static Vec3d getMoveInputs(EntityPlayerSP player) {
        float moveUpward = 0.0f;
        if (player.movementInput.jump) {
            moveUpward += 1.0f;
        }
        if (player.movementInput.sneak) {
            moveUpward -= 1.0f;
        }
        float moveForward = 0.0f;
        float moveStrafe = 0.0f;
        moveForward += player.movementInput.forwardKeyDown ? 1.0f : 0.0f;
        moveStrafe += player.movementInput.leftKeyDown ? 1.0f : 0.0f;
        return new Vec3d(moveForward -= player.movementInput.backKeyDown ? 1.0f : 0.0f, moveUpward, moveStrafe -= player.movementInput.rightKeyDown ? 1.0f : 0.0f);
    }

    public static Vec2f toWorld(Vec2f vec, float yaw) {
        if (vec.x == 0.0f && vec.y == 0.0f) {
            return new Vec2f(0.0f, 0.0f);
        }
        float r = MathHelper.sqrt(vec.x * vec.x + vec.y * vec.y);
        float yawCos = MathHelper.cos((float)((double)yaw * Math.PI / 180.0));
        float yawSin = MathHelper.sin((float)((double)yaw * Math.PI / 180.0));
        float vecCos = vec.x / r;
        float vecSin = vec.y / r;
        float cos = yawCos * vecCos + yawSin * vecSin;
        float sin = yawSin * vecCos - vecSin * yawCos;
        return new Vec2f(-r * sin, r * cos);
    }
}
