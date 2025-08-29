package me.hypinohaizin.candyplusrewrite.gui.clickguis.clickgui;

import java.util.ArrayList;
import org.lwjgl.input.Mouse;
import me.hypinohaizin.candyplusrewrite.hud.Hud;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.module.render.HUDEditor;
import me.hypinohaizin.candyplusrewrite.module.Module;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.Minecraft;
import java.util.List;
import me.hypinohaizin.candyplusrewrite.gui.clickguis.CGui;

public class CandyGUI extends CGui
{
    public static List<Panel> panelList;
    public static Panel hubPanel;

    public void initGui() {
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null) {
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        }
        Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        if (CandyGUI.panelList.size() == 0) {
            int x = 50;
            for (final Module.Categories category : Module.Categories.values()) {
                if (category == Module.Categories.HUB) {
                    CandyGUI.hubPanel = new Panel(200.0f, 20.0f, category);
                }
                else {
                    CandyGUI.panelList.add(new Panel((float)x, 20.0f, category));
                    x += 120;
                }
            }
        }
    }

    public void onGuiClosed() {
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null) {
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        }
    }

    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        scroll();
        if (HUDEditor.instance.isEnable) {
            CandyGUI.hubPanel.onRender(mouseX, mouseY);
        }
        else {
            CandyGUI.panelList.forEach(p -> p.onRender(mouseX, mouseY));
        }
    }

    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (HUDEditor.instance.isEnable) {
            CandyGUI.hubPanel.onMouseClicked(mouseX, mouseY, mouseButton);
            CandyPlusRewrite.m_module.modules.stream().filter(m -> m instanceof Hud).forEach(m -> ((Hud)m).mouseClicked(mouseX, mouseY, mouseButton));
        }
        else {
            CandyGUI.panelList.forEach(p -> p.onMouseClicked(mouseX, mouseY, mouseButton));
        }
    }

    public void mouseReleased(final int mouseX, final int mouseY, final int state) {
        if (HUDEditor.instance.isEnable) {
            CandyGUI.hubPanel.onMouseReleased(mouseX, mouseY, state);
            CandyPlusRewrite.m_module.modules.stream().filter(m -> m instanceof Hud).forEach(m -> ((Hud)m).mouseReleased(mouseX, mouseY, state));
        }
        else {
            CandyGUI.panelList.forEach(p -> p.onMouseReleased(mouseX, mouseY, state));
        }
    }

    public void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick) {
        if (HUDEditor.instance.isEnable) {
            CandyGUI.hubPanel.onMouseClickMove(mouseX, mouseY, clickedMouseButton);
            CandyPlusRewrite.m_module.modules.stream().filter(m -> m instanceof Hud).forEach(m -> ((Hud)m).mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick));
        }
        else {
            CandyGUI.panelList.forEach(p -> p.onMouseClickMove(mouseX, mouseY, clickedMouseButton));
        }
    }

    public void keyTyped(final char typedChar, final int keyCode) {
        if (1 != keyCode) {
            if (HUDEditor.instance.isEnable) {
                CandyGUI.hubPanel.keyTyped(typedChar, keyCode);
            }
            else {
                CandyGUI.panelList.forEach(p -> p.keyTyped(typedChar, keyCode));
            }
            return;
        }
        if (HUDEditor.instance.isEnable) {
            HUDEditor.instance.disable();
            return;
        }
        mc.displayGuiScreen(null);
    }

    public void scroll() {
        final int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            if (HUDEditor.instance.isEnable) {
                final Panel hubPanel = CandyGUI.hubPanel;
                hubPanel.y -= 15.0f;
            }
            else {
                CandyGUI.panelList.forEach(p -> p.y -= 15.0f);
            }
        }
        else if (dWheel > 0) {
            if (HUDEditor.instance.isEnable) {
                final Panel hubPanel2 = CandyGUI.hubPanel;
                hubPanel2.y += 15.0f;
            }
            else {
                CandyGUI.panelList.forEach(p -> p.y += 15.0f);
            }
        }
    }

    static {
        CandyGUI.panelList = new ArrayList<Panel>();
    }
}