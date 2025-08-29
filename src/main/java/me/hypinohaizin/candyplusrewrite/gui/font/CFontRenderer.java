package me.hypinohaizin.candyplusrewrite.gui.font;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import java.awt.Font;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class CFontRenderer extends CFont
{
    protected CharData[] boldChars;
    protected CharData[] italicChars;
    protected CharData[] boldItalicChars;
    DynamicTexture texBold;
    DynamicTexture texItalic;
    DynamicTexture texItalicBold;
    private final int[] colorCode;
    
    public CFontRenderer(final Font font, final boolean antiAlias, final boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        boldChars = new CharData[256];
        italicChars = new CharData[256];
        boldItalicChars = new CharData[256];
        colorCode = new int[32];
        setupMinecraftColorcodes();
        setupBoldItalicIDs();
    }
    
    public CFontRenderer(final CustomFont font, final boolean antiAlias, final boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        boldChars = new CharData[256];
        italicChars = new CharData[256];
        boldItalicChars = new CharData[256];
        colorCode = new int[32];
        setupMinecraftColorcodes();
        setupBoldItalicIDs();
    }
    
    public float drawStringWithShadow(final String text, final double x, final double y, final int color, final float scale) {
        final float shadowWidth = drawString(text, x + 1.0, y + 1.0, color, true, scale);
        return Math.max(shadowWidth, drawString(text, x, y, color, false, scale));
    }
    
    public float drawString(final String text, final float x, final float y, final int color, final float scale) {
        return drawString(text, x, y, color, false, scale);
    }
    
    public float drawCenteredString(final String text, final float x, final float y, final int color, final float scale) {
        return drawString(text, x - getStringWidth(text) / 2, y, color, scale);
    }
    
    public float drawCenteredStringWithShadow(final String text, final float x, final float y, final int color, final float scale) {
        final float shadowWidth = drawString(text, x - getStringWidth(text) / 2 + 1.0, y + 1.0, color, true, scale);
        return drawString(text, x - getStringWidth(text) / 2, y, color, scale);
    }
    
    public float drawString(final String text, double x, double y, int color, final boolean shadow, final float scale) {
        --x;
        if (text == null) {
            return 0.0f;
        }
        if (color == 553648127) {
            color = 16777215;
        }
        if ((color & 0xFC000000) == 0x0) {
            color |= 0xFF000000;
        }
        if (shadow) {
            color = ((color & 0xFCFCFC) >> 2 | (color & 0xFF000000));
        }
        CharData[] currentData = charData;
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        boolean bold = false;
        boolean italic = false;
        boolean strikethrough = false;
        boolean underline = false;
        final boolean render = true;
        x *= 2.0;
        y = (y - 3.0) * 2.0;
        if (render) {
            GL11.glPushMatrix();
            GL11.glShadeModel(7425);
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.blendFunc(770, 771);
            GL11.glEnable(2848);
            GlStateManager.color((color >> 16 & 0xFF) / 255.0f, (color >> 8 & 0xFF) / 255.0f, (color & 0xFF) / 255.0f, alpha);
            final int size = text.length();
            GlStateManager.enableTexture2D();
            GlStateManager.bindTexture(tex.getGlTextureId());
            GL11.glBindTexture(3553, tex.getGlTextureId());
            for (int i = 0; i < size; ++i) {
                final char character = text.charAt(i);
                if (character == '§' && i < size) {
                    int colorIndex = 21;
                    try {
                        colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                    }
                    catch (final Exception e) {
                        e.printStackTrace();
                    }
                    if (colorIndex < 16) {
                        bold = false;
                        italic = false;
                        underline = false;
                        strikethrough = false;
                        GlStateManager.bindTexture(tex.getGlTextureId());
                        currentData = charData;
                        if (colorIndex < 0 || colorIndex > 15) {
                            colorIndex = 15;
                        }
                        if (shadow) {
                            colorIndex += 16;
                        }
                        final int colorcode = colorCode[colorIndex];
                        GlStateManager.color((colorcode >> 16 & 0xFF) / 255.0f, (colorcode >> 8 & 0xFF) / 255.0f, (colorcode & 0xFF) / 255.0f, alpha);
                    }
                    else if (colorIndex == 17) {
                        bold = true;
                        if (italic) {
                            if (texItalicBold != null) {
                                GlStateManager.bindTexture(texItalicBold.getGlTextureId());
                                currentData = boldItalicChars;
                            }
                        }
                        else if (texBold != null) {
                            GlStateManager.bindTexture(texBold.getGlTextureId());
                            currentData = boldChars;
                        }
                    }
                    else if (colorIndex == 18) {
                        strikethrough = true;
                    }
                    else if (colorIndex == 19) {
                        underline = true;
                    }
                    else if (colorIndex == 20) {
                        italic = true;
                        if (bold) {
                            if (texItalicBold != null) {
                                GlStateManager.bindTexture(texItalicBold.getGlTextureId());
                                currentData = boldItalicChars;
                            }
                        }
                        else if (texBold != null) {
                            GlStateManager.bindTexture(texItalic.getGlTextureId());
                            currentData = italicChars;
                        }
                    }
                    else if (colorIndex == 21) {
                        bold = false;
                        italic = false;
                        underline = false;
                        strikethrough = false;
                        GlStateManager.color((color >> 16 & 0xFF) / 255.0f, (color >> 8 & 0xFF) / 255.0f, (color & 0xFF) / 255.0f, alpha);
                        GlStateManager.bindTexture(tex.getGlTextureId());
                        currentData = charData;
                    }
                    ++i;
                }
                else if (character < currentData.length) {
                    if (character >= '\0') {
                        GL11.glBegin(4);
                        drawChar(currentData, character, (float)x, (float)y);
                        GL11.glEnd();
                        if (strikethrough) {
                            drawLine(x, y + currentData[character].height / 2, x + currentData[character].width - 8.0, y + currentData[character].height / 2, 1.0f);
                        }
                        if (underline) {
                            drawLine(x, y + currentData[character].height - 2.0, x + currentData[character].width - 8.0, y + currentData[character].height - 2.0, 1.0f);
                        }
                        x += (currentData[character].width - 8 + charOffset) * scale;
                    }
                }
            }
            GlStateManager.disableBlend();
            GL11.glScalef(2.0f, 2.0f, 2.0f);
            GL11.glShadeModel(7424);
            GL11.glDisable(2848);
            GL11.glHint(3155, 4352);
            GL11.glPopMatrix();
        }
        return (float)x / 2.0f;
    }
    
    @Override
    public int getStringWidth(final String text) {
        if (text == null) {
            return 0;
        }
        int width = 0;
        CharData[] currentData = charData;
        boolean bold = false;
        boolean italic = false;
        for (int size = text.length(), i = 0; i < size; ++i) {
            final char character = text.charAt(i);
            if (character == '§') {
                final int colorIndex = "0123456789abcdefklmnor".indexOf(character);
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                }
                else if (colorIndex == 17) {
                    bold = true;
                    currentData = (italic ? boldItalicChars : boldChars);
                }
                else if (colorIndex == 20) {
                    italic = true;
                    currentData = (bold ? boldItalicChars : italicChars);
                }
                else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    currentData = charData;
                }
                ++i;
            }
            else if (character < currentData.length) {
                if (character >= '\0') {
                    width += currentData[character].width - 8 + charOffset;
                }
            }
        }
        return width / 2;
    }
    
    @Override
    public void setFont(final Font font) {
        super.setFont(font);
        setupBoldItalicIDs();
    }
    
    @Override
    public void setAntiAlias(final boolean antiAlias) {
        super.setAntiAlias(antiAlias);
        setupBoldItalicIDs();
    }
    
    @Override
    public void setFractionalMetrics(final boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        setupBoldItalicIDs();
    }
    
    private void setupBoldItalicIDs() {
        texBold = setupTexture(font.deriveFont(1), antiAlias, fractionalMetrics, boldChars);
        texItalic = setupTexture(font.deriveFont(2), antiAlias, fractionalMetrics, italicChars);
        texItalicBold = setupTexture(font.deriveFont(3), antiAlias, fractionalMetrics, boldItalicChars);
    }
    
    private void drawLine(final double x, final double y, final double x1, final double y1, final float width) {
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glLineWidth(width);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x1, y1);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
    }
    
    public List<String> wrapWords(final String text, final double width) {
        final ArrayList<String> finalWords = new ArrayList<String>();
        if (getStringWidth(text) > width) {
            final String[] words = text.split(" ");
            String currentWord = "";
            char lastColorCode = '\uffff';
            for (final String word : words) {
                for (int i = 0; i < word.length(); ++i) {
                    final char c = word.toCharArray()[i];
                    if (c == '§') {
                        if (i < word.length() - 1) {
                            lastColorCode = word.toCharArray()[i + 1];
                        }
                    }
                }
                final StringBuilder stringBuilder = new StringBuilder();
                if (getStringWidth(stringBuilder.append(currentWord).append(word).append(" ").toString()) < width) {
                    currentWord = currentWord + word + " ";
                }
                else {
                    finalWords.add(currentWord);
                    currentWord = "§" + lastColorCode + word + " ";
                }
            }
            if (currentWord.length() > 0) {
                if (getStringWidth(currentWord) < width) {
                    finalWords.add("§" + lastColorCode + currentWord + " ");
                    currentWord = "";
                }
                else {
                    for (final String s : formatString(currentWord, width)) {
                        finalWords.add(s);
                    }
                }
            }
        }
        else {
            finalWords.add(text);
        }
        return finalWords;
    }
    
    public List<String> formatString(final String string, final double width) {
        final ArrayList<String> finalWords = new ArrayList<String>();
        String currentWord = "";
        char lastColorCode = '\uffff';
        final char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            final char c = chars[i];
            if (c == '§' && i < chars.length - 1) {
                lastColorCode = chars[i + 1];
            }
            final StringBuilder stringBuilder = new StringBuilder();
            if (getStringWidth(stringBuilder.append(currentWord).append(c).toString()) < width) {
                currentWord += c;
            }
            else {
                finalWords.add(currentWord);
                currentWord = "§" + lastColorCode + c;
            }
        }
        if (currentWord.length() > 0) {
            finalWords.add(currentWord);
        }
        return finalWords;
    }
    
    private void setupMinecraftColorcodes() {
        for (int index = 0; index < 32; ++index) {
            final int noClue = (index >> 3 & 0x1) * 85;
            int red = (index >> 2 & 0x1) * 170 + noClue;
            int green = (index >> 1 & 0x1) * 170 + noClue;
            int blue = (index >> 0 & 0x1) * 170 + noClue;
            if (index == 6) {
                red += 85;
            }
            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            colorCode[index] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF));
        }
    }
}
