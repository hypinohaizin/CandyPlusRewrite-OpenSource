package me.hypinohaizin.candyplusrewrite.module.render;

import java.util.List;
import me.hypinohaizin.candyplusrewrite.utils.RenderUtil3D;
import me.hypinohaizin.candyplusrewrite.utils.HoleUtil;
import me.hypinohaizin.candyplusrewrite.utils.PlayerUtil;
import net.minecraft.util.math.BlockPos;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import java.awt.Color;
import me.hypinohaizin.candyplusrewrite.setting.Setting;
import me.hypinohaizin.candyplusrewrite.module.Module;

public class HoleESP extends Module
{
    public Setting<Float> range;
    public Setting<Color> obby;
    public Setting<Color> bedrock;
    public Setting<type> renderType;
    public Setting<Boolean> outline;
    public Setting<Float> width;
    
    public HoleESP() {
        super("HoleESP", Categories.RENDER, false, false);
        range = register(new Setting<>("Range", 10.0f, 12.0f, 1.0f));
        obby = register(new Setting<>("ObbyColor", new Color(230, 50, 50, 100)));
        bedrock = register(new Setting<>("BedrockColor", new Color(230, 150, 50, 100)));
        renderType = register(new Setting<>("RenderType", type.Down));
        outline = register(new Setting<>("Outline", false));
        width = register(new Setting<>("Width", 3.0f, 6.0f, 0.1f, v -> outline.getValue()));
    }
    
    @Override
    public void onRender3D() {
        try {
            final List<BlockPos> holes = CandyPlusRewrite.m_hole.getHoles();
            for (final BlockPos hole : holes) {
                if (PlayerUtil.getDistance(hole) > range.getValue()) {
                    continue;
                }
                Color color = obby.getValue();
                if (HoleUtil.isBedrockHole(hole)) {
                    color = bedrock.getValue();
                }
                if (renderType.getValue() == type.Full) {
                    RenderUtil3D.drawBox(hole, 1.0, color, 63);
                }
                else {
                    RenderUtil3D.drawBox(hole, 1.0, color, 1);
                }
                if (!outline.getValue()) {
                    continue;
                }
                RenderUtil3D.drawBoundingBox(hole, 1.0, width.getValue(), color);
            }
        }
        catch (final Exception ex) {}
    }
    
    public enum type
    {
        Full, 
        Down
    }
}
