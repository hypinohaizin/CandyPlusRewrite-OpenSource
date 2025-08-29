package me.hypinohaizin.candyplusrewrite.managers;

import java.awt.Color;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Map;
import java.awt.Font;
import java.util.Objects;

import me.hypinohaizin.candyplusrewrite.gui.font.CFontRenderer;

public class FontManager extends Manager
{
    public CFontRenderer iconFont;
    public Font fontRenderer_;
    public Font iconFont_;
    public CFontRenderer fontRenderer;

    private static Font getFont(final Map<String, Font> locationMap, final String location, final int size) {
        Font font;
        try {
            if (locationMap.containsKey(location)) {
                font = locationMap.get(location).deriveFont(Font.PLAIN, (float) size);
            } else {
                InputStream is = FontManager.class.getResourceAsStream("/assets/candyplusrewrite/fonts/" + location);
                font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(is));
                locationMap.put(location, font);
                font = font.deriveFont(Font.PLAIN, (float) size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading font: " + location);
            font = new Font("default", Font.PLAIN, 18);
        }
        return font;
    }


    @Override
    public void load() {
        final Map<String, Font> locationmap = new HashMap<>();
        fontRenderer_ = getFont(locationmap, "LD.ttf", 15);
        iconFont_ = getFont(locationmap, "Comfortaa-Bold.ttf", 25);
        fontRenderer = new CFontRenderer(fontRenderer_, true, true);
        iconFont = new CFontRenderer(iconFont_, true, true);
    }
    
    public int getWidth(final String str) {
        return fontRenderer.getStringWidth(str);
    }
    
    public int getHeight() {
        return fontRenderer.getHeight() + 2;
    }
    
    public void draw(final String str, final int x, final int y, final int color, final float scale) {
        fontRenderer.drawString(str, (float)x, (float)y, color, scale);
    }
    
    public void draw(final String str, final int x, final int y, final Color color, final float scale) {
        fontRenderer.drawString(str, (float)x, (float)y, color.getRGB(), scale);
    }
    
    public int getIconWidth() {
        return iconFont.getStringWidth("q");
    }
    
    public int getIconHeight() {
        return iconFont.getHeight();
    }
    
    public void drawIcon(final int x, final int y, final int color, final float scale) {
        iconFont.drawString("q", (float)x, (float)y, color, scale);
    }
    
    public void drawIcon(final int x, final int y, final Color color, final float scale) {
        iconFont.drawString("+", (float)x, (float)y, color.getRGB(), scale);
    }
}
