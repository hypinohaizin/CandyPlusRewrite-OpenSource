package me.hypinohaizin.candyplusrewrite.utils;

import net.minecraft.util.ResourceLocation;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class CandyDynamicTexture extends DynamicTexture
{
    private final int Height;
    private final int Width;
    private final BufferedImage m_BufferedImage;
    private ResourceLocation m_TexturedLocation;
    private ImageFrame m_Frame;
    
    public CandyDynamicTexture(final BufferedImage bufferedImage, final int p_Height, final int p_Width) {
        super(bufferedImage);
        m_Frame = null;
        m_BufferedImage = bufferedImage;
        Height = p_Height;
        Width = p_Width;
    }
    
    public int GetHeight() {
        return Height;
    }
    
    public int GetWidth() {
        return Width;
    }
    
    public final DynamicTexture GetDynamicTexture() {
        return this;
    }
    
    public final BufferedImage GetBufferedImage() {
        return m_BufferedImage;
    }
    
    public void SetResourceLocation(final ResourceLocation dynamicTextureLocation) {
        m_TexturedLocation = dynamicTextureLocation;
    }
    
    public final ResourceLocation GetResourceLocation() {
        return m_TexturedLocation;
    }
    
    public void SetImageFrame(final ImageFrame p_Frame) {
        m_Frame = p_Frame;
    }
    
    public final ImageFrame GetFrame() {
        return m_Frame;
    }
}
