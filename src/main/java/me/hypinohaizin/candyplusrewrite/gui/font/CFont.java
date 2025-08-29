package me.hypinohaizin.candyplusrewrite.gui.font;

import org.lwjgl.opengl.GL11;
import java.awt.geom.Rectangle2D;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.FontFormatException;
import java.io.IOException;
import net.minecraft.client.renderer.texture.DynamicTexture;
import java.awt.Font;

public class CFont
{
    private final float imgSize = 512.0f;
    protected CharData[] charData;
    protected Font font;
    protected boolean antiAlias;
    protected boolean fractionalMetrics;
    protected int fontHeight;
    protected int charOffset;
    protected DynamicTexture tex;
    
    public CFont(final Font font, final boolean antiAlias, final boolean fractionalMetrics) {
        charData = new CharData[256];
        fontHeight = -1;
        charOffset = 0;
        this.font = font;
        this.antiAlias = antiAlias;
        this.fractionalMetrics = fractionalMetrics;
        tex = setupTexture(font, antiAlias, fractionalMetrics, charData);
    }
    
    public CFont(final CustomFont font, final boolean antiAlias, final boolean fractionalMetrics) {
        charData = new CharData[256];
        fontHeight = -1;
        charOffset = 0;
        try {
            final Font inputFont = Font.createFont(0, CFont.class.getResourceAsStream(font.getFile())).deriveFont(font.getSize()).deriveFont(font.getType());
            this.font = inputFont;
            this.antiAlias = antiAlias;
            this.fractionalMetrics = fractionalMetrics;
            tex = setupTexture(inputFont, antiAlias, fractionalMetrics, charData);
        }
        catch (final IOException | FontFormatException ex) {}
    }
    
    protected DynamicTexture setupTexture(final Font font, final boolean antiAlias, final boolean fractionalMetrics, final CharData[] chars) {
        final BufferedImage img = generateFontImage(font, antiAlias, fractionalMetrics, chars);
        try {
            return new DynamicTexture(img);
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    protected BufferedImage generateFontImage(final Font font, final boolean antiAlias, final boolean fractionalMetrics, final CharData[] chars) {
        getClass();
        final int imgSize = 512;
        final BufferedImage bufferedImage = new BufferedImage(imgSize, imgSize, 2);
        final Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
        g.setFont(font);
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, imgSize, imgSize);
        g.setColor(Color.WHITE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        final FontMetrics fontMetrics = g.getFontMetrics();
        int charHeight = 0;
        int positionX = 0;
        int positionY = 1;
        for (int i = 0; i < chars.length; ++i) {
            final char ch = (char)i;
            final CharData charData = new CharData();
            final Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(ch), g);
            charData.width = dimensions.getBounds().width + 8;
            charData.height = dimensions.getBounds().height;
            if (positionX + charData.width >= imgSize) {
                positionX = 0;
                positionY += charHeight;
                charHeight = 0;
            }
            if (charData.height > charHeight) {
                charHeight = charData.height;
            }
            charData.storedX = positionX;
            charData.storedY = positionY;
            if (charData.height > fontHeight) {
                fontHeight = charData.height;
            }
            chars[i] = charData;
            g.drawString(String.valueOf(ch), positionX + 2, positionY + fontMetrics.getAscent());
            positionX += charData.width;
        }
        return bufferedImage;
    }
    
    public void drawChar(final CharData[] chars, final char c, final float x, final float y) throws ArrayIndexOutOfBoundsException {
        try {
            drawQuad(x, y, (float)chars[c].width, (float)chars[c].height, (float)chars[c].storedX, (float)chars[c].storedY, (float)chars[c].width, (float)chars[c].height);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void drawQuad(final float x, final float y, final float width, final float height, final float srcX, final float srcY, final float srcWidth, final float srcHeight) {
        final float renderSRCX = srcX / 512.0f;
        final float renderSRCY = srcY / 512.0f;
        final float renderSRCWidth = srcWidth / 512.0f;
        final float renderSRCHeight = srcHeight / 512.0f;
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2d(x + width, y);
        GL11.glTexCoord2f(renderSRCX, renderSRCY);
        GL11.glVertex2d(x, y);
        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
        GL11.glVertex2d(x + width, y + height);
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2d(x + width, y);
    }
    
    public int getHeight() {
        return (fontHeight - 8) / 2;
    }
    
    public int getStringWidth(final String text) {
        int width = 0;
        for (final char c : text.toCharArray()) {
            if (c < charData.length && c >= '\0') {
                width += charData[c].width - 8 + charOffset;
            }
        }
        return width / 2;
    }
    
    public boolean isAntiAlias() {
        return antiAlias;
    }
    
    public void setAntiAlias(final boolean antiAlias) {
        if (antiAlias != antiAlias) {
            this.antiAlias = antiAlias;
            tex = setupTexture(font, antiAlias, fractionalMetrics, charData);
        }
    }
    
    public boolean isFractionalMetrics() {
        return fractionalMetrics;
    }
    
    public void setFractionalMetrics(final boolean fractionalMetrics) {
        if (fractionalMetrics != fractionalMetrics) {
            this.fractionalMetrics = fractionalMetrics;
            tex = setupTexture(font, antiAlias, fractionalMetrics, charData);
        }
    }
    
    public Font getFont() {
        return font;
    }
    
    public void setFont(final Font font) {
        this.font = font;
        tex = setupTexture(font, antiAlias, fractionalMetrics, charData);
    }
    
    public static class CustomFont
    {
        float size;
        String file;
        int style;
        
        public CustomFont(final String file, final float size, final int style) {
            this.file = file;
            this.size = size;
        }
        
        public float getSize() {
            return size;
        }
        
        public String getFile() {
            return file;
        }
        
        public int getType() {
            if (style > 3) {
                return 3;
            }
            return Math.max(style, 0);
        }
    }
    
    protected static class CharData
    {
        public int width;
        public int height;
        public int storedX;
        public int storedY;
    }
}
