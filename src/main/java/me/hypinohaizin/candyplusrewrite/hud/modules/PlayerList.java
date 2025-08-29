package me.hypinohaizin.candyplusrewrite.hud.modules;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.entity.player.EntityPlayer;

import me.hypinohaizin.candyplusrewrite.hud.Hud;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil;
import me.hypinohaizin.candyplusrewrite.utils.ColorUtil;

public class PlayerList extends Hud {
    public Setting<Integer> maxPlayers;
    public Setting<Boolean> health;
    public Setting<Boolean> distance;
    public Setting<Boolean> shadow;
    public Setting<Color> color;

    public PlayerList() {
        super("PlayerList", 50.0f, 50.0f);
        maxPlayers = register(new Setting<>("MaxPlayers",5,10,3));
        health = register(new Setting<>("Health",true));
        distance = register(new Setting<>("Distance",true));
        shadow = register(new Setting<>("Shadow",false));
        color = register(new Setting<>("Color", new Color(255,255,255,255)));
    }

    @Override
    public void onRender() {
        try {
            List<EntityPlayer> players = getPlayerList();
            float w = 0.0f;
            float h = 0.0f;
            float textSize = 1.0f;
            float lineHeight = RenderUtil.getStringHeight(textSize) + 4.0f;

            for (EntityPlayer p : players) {
                int hp = PlayerUtil.getHealth(p);
                double dist = PlayerUtil.getDistance(p);
                String str = p.getName();
                if (health.getValue()) str += " " + getHealthColor(hp) + hp;
                if (distance.getValue()) str += " " + getDistanceColor(dist) + (int)dist;
                float textW = RenderUtil.getStringWidth(str, textSize);
                if (textW > w) w = textW;
                RenderUtil.drawString(str, x.getValue(), y.getValue() + h,
                        ColorUtil.toRGBA(color.getValue()),
                        shadow.getValue(), textSize);
                h += lineHeight;
            }

            this.width  = w;
            this.height = h;
        } catch (Exception ignored) {}
    }

    private ChatFormatting getDistanceColor(double d) {
        if (d > 20.0)
            return ChatFormatting.GREEN;
        if (d > 6.0)
            return ChatFormatting.YELLOW;
        return ChatFormatting.RED;
    }

    private ChatFormatting getHealthColor(int hp) {
        if (hp > 23)
            return ChatFormatting.GREEN;
        if (hp > 7)
            return ChatFormatting.YELLOW;
        return ChatFormatting.RED;
    }

    private List<EntityPlayer> getPlayerList() {
        List<EntityPlayer> list = new ArrayList<>(mc.world.playerEntities);
        list.removeIf(p -> p == mc.player);
        list.sort(Comparator.comparingDouble(PlayerUtil::getDistance));
        if (list.size() > maxPlayers.getValue()) {
            return new ArrayList<>(list.subList(0, maxPlayers.getValue()));
        }
        return list;
    }
}
