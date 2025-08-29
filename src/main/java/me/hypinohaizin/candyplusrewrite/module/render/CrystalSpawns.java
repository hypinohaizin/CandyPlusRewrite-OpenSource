package me.hypinohaizin.candyplusrewrite.module.render;

import me.hypinohaizin.candyplusrewrite.module.Module;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CrystalSpawns extends Module {
    private final Setting<Boolean> rainbow = register(new Setting<>("Rainbow", false));
    private final Setting<Integer> red = register(new Setting<>("Red", 0, 255, 0));
    private final Setting<Integer> green = register(new Setting<>("Green", 0, 255, 0));
    private final Setting<Integer> blue = register(new Setting<>("Blue", 0, 255, 0));

    private static CrystalSpawns INSTANCE;
    public static HashMap<UUID, Thingering> thingers = new HashMap<>();

    public CrystalSpawns() {
        super("CrystalSpawns", Categories.RENDER, false, false);
        setInstance();
    }

    public static CrystalSpawns getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrystalSpawns();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onTick() {
        if (mc.world == null) return;
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal) || thingers.containsKey(entity.getUniqueID())) continue;
            thingers.put(entity.getUniqueID(), new Thingering(entity));
            thingers.get(entity.getUniqueID()).starTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onRender3D() {
        if (mc.player == null || mc.world == null) return;
        for (Map.Entry<UUID, Thingering> entry : thingers.entrySet()) {
            long time = System.currentTimeMillis();
            long duration = time - entry.getValue().starTime;
            if (duration >= 1500L) continue;

            float cycle = (duration % 1500L) / 1500.0f; // 0.0～1.0
            float moveY = (float)Math.sin(Math.PI * cycle) * 1.5f; // 0→+1.5→0
            float scale = 0.6f + 0.2f * ((float)Math.sin(Math.PI * cycle)); // 0.6～0.8
            float opacity = 1.0f - (duration / 1500.0f);

            drawCircle(entry.getValue().entity, mc.getRenderPartialTicks(), scale, moveY, opacity);
        }
    }

    public void drawCircle(Entity entity, float partialTicks, double rad, float moveY, float alpha) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        startSmooth();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(2.5f);

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;

        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 90; ++i) {
            Color color = rainbow.getValue() ? rainbowColor(i * 4) : new Color(red.getValue(), green.getValue(), blue.getValue());
            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha);
            double theta = i * 2 * Math.PI / 90;
            GL11.glVertex3d(x + rad * Math.cos(theta), y + moveY, z + rad * Math.sin(theta));
        }
        GL11.glEnd();

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        endSmooth();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    private Color rainbowColor(int delay) {
        float hue = (System.currentTimeMillis() % 11520L + delay) / 11520.0f;
        return Color.getHSBColor(hue, 0.8f, 1.0f);
    }

    public static void startSmooth() {
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    }

    public static void endSmooth() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static class Thingering {
        public final Entity entity;
        public long starTime;

        public Thingering(Entity entity) {
            this.entity = entity;
            this.starTime = 0L;
        }
    }
}