package me.hypinohaizin.candyplusrewrite.utils;

import java.awt.image.BufferedImage;

public class ImageFrame
{
    private final int delay;
    private final BufferedImage image;
    private final String disposal;
    private final int width;
    private final int height;
    
    public ImageFrame(final BufferedImage image, final int delay, final String disposal, final int width, final int height) {
        this.image = image;
        this.delay = delay;
        this.disposal = disposal;
        this.width = width;
        this.height = height;
    }
    
    public ImageFrame(final BufferedImage image) {
        this.image = image;
        delay = -1;
        disposal = null;
        width = -1;
        height = -1;
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public int getDelay() {
        return delay;
    }
    
    public String getDisposal() {
        return disposal;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}
