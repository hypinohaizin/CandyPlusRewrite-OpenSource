package me.hypinohaizin.candyplusrewrite.asm;

import java.io.IOException;
import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

public class CandyAccessTransformer extends AccessTransformer
{
    public CandyAccessTransformer() throws IOException {
        super("META-INF/candyplusrewrite_at.cfg");
    }
}
