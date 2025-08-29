package me.hypinohaizin.loader.mixin;

import me.hypinohaizin.candyplusrewrite.asm.CandyAccessTransformer;
import me.hypinohaizin.candyplusrewrite.asm.CandyTransformer;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.launch.MixinBootstrap;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions({"me.hypinohaizin.candyplusrewrite.asm"})
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("CandyPlusRewrite")
@IFMLLoadingPlugin.SortingIndex(1001)
public class MixinLoader implements IFMLLoadingPlugin {
    private static boolean isObfuscatedEnvironment;
    public MixinLoader() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.candyplusrewrite.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }
    public String[] getASMTransformerClass() {
        return new String[] {
                CandyTransformer.class.getName() };
    }
    public String getModContainerClass() {
        return null;
    }
    public String getSetupClass() {
        return null;
    }
    public void injectData(final Map<String, Object> data) {
    }
    public String getAccessTransformerClass() {
        return CandyAccessTransformer.class.getName();
    }
    static {
        isObfuscatedEnvironment = false;
    }
}
