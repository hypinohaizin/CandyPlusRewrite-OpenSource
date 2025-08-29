package me.hypinohaizin.candyplusrewrite.asm;

import me.hypinohaizin.candyplusrewrite.asm.impl.PatchEntityRenderer;
import java.util.ArrayList;

import me.hypinohaizin.candyplusrewrite.asm.api.ClassPatch;
import java.util.List;
import net.minecraft.launchwrapper.IClassTransformer;

public class CandyTransformer implements IClassTransformer
{
    public static final List<ClassPatch> patches;
    
    public byte[] transform(final String name, final String transformedName, final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        for (final ClassPatch it : CandyTransformer.patches) {
            if (it.className.equals(transformedName)) {
                return it.transform(bytes);
            }
        }
        return bytes;
    }
    
    static {
        (patches = new ArrayList<>()).add(new PatchEntityRenderer());
    }
}
