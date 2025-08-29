package me.hypinohaizin.candyplusrewrite.managers;

import java.util.List;
import me.hypinohaizin.candyplusrewrite.CandyPlusRewrite;
import me.hypinohaizin.candyplusrewrite.module.Module;
import java.io.File;

public class ConfigManager
{
    public static void saveConfigs() {
        final String folder = "candyplusrewrite/";
        final File dir = new File(folder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (final Module.Categories category : Module.Categories.values()) {
            final File categoryDir = new File(folder + category.name().toLowerCase());
            if (!categoryDir.exists()) {
                categoryDir.mkdirs();
            }
        }
        final List<Module> modules = CandyPlusRewrite.m_module.modules;
        for (final Module module : modules) {
            try {
                module.saveConfig();
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void loadConfigs() {
        final List<Module> modules = CandyPlusRewrite.m_module.modules;
        for (final Module module : modules) {
            try {
                module.loadConfig();
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
