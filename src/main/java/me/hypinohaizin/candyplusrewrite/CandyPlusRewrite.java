package me.hypinohaizin.candyplusrewrite;

import me.hypinohaizin.candyplusrewrite.command.CommandManager;
import me.hypinohaizin.candyplusrewrite.mainmenu.MainMenu;
import me.hypinohaizin.candyplusrewrite.managers.*;
import me.hypinohaizin.loader.Loader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.Display;
import org.apache.logging.log4j.Logger;

@Mod(modid = CandyPlusRewrite.MODID, name = CandyPlusRewrite.NAME, version = CandyPlusRewrite.VERSION)
public class CandyPlusRewrite
{
    public static final String MODID = "candyplusrewrite";
    public static final String NAME = "Candy+ Rewrite";
    public static final String NAME2 = "Candy+R";
    public static final String VERSION = "0.3.5";
    public static final String NV = NAME + " v" + VERSION;
    public static ModuleManager m_module;
    public static EventManager m_event;
    public static FontManager m_font;
    public static HoleManager m_hole;
    public static RpcManager m_rpc;
    public static RotateManager m_rotate;
    public static NotificationManager m_notif;
    private static Logger logger;
    private static boolean savedConfig;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        CandyPlusRewrite.logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        try {
            Loader.load();
        } catch (Exception e) {
            logger.log(Level.FATAL, "Failed to initialize CandyPlusRewrite loader. The game will now exit.", e);
            Minecraft.getMinecraft().shutdown();
            return;
        }

        Info("loading Candy+ Rewrite ...");
        Display.setTitle(CandyPlusRewrite.NV);
        //Info("Event Loading...");
        CandyPlusRewrite.m_event.load();
        //Info("Modules Loading...");
        CandyPlusRewrite.m_module.load();
        //Info("Font Loading...");
        CandyPlusRewrite.m_font.load();
        //Info("Misc Loading...");
        CandyPlusRewrite.m_hole.load();
        CandyPlusRewrite.m_rpc.load();
        CandyPlusRewrite.m_rotate.load();
        CandyPlusRewrite.m_notif.load();
        //Info("CommandLoading...");
        CommandManager.init();
        Info("Loading Configs...");
        ConfigManager.loadConfigs();
        Info("Successfully Load CandyPlusRewrite!");

        if (event.getSide().isClient()) {
            Minecraft.getMinecraft().displayGuiScreen(new MainMenu());
            MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
        }
    }

    public static void unload() {
        if (!CandyPlusRewrite.savedConfig) {
            Info("Saving Configs...");
            ConfigManager.saveConfigs();
            Info("Successfully Save Configs!");
            CandyPlusRewrite.savedConfig = true;
        }
    }

    public static void Info(final String msg) {
        if (CandyPlusRewrite.logger == null) {
            return;
        }
        CandyPlusRewrite.logger.info(msg);
    }

    public static void Log(final Level level, final String msg) {
        CandyPlusRewrite.logger.log(level, msg);
    }

    static {
        CandyPlusRewrite.m_module = new ModuleManager();
        CandyPlusRewrite.m_event = new EventManager();
        CandyPlusRewrite.m_font = new FontManager();
        CandyPlusRewrite.m_hole = new HoleManager();
        CandyPlusRewrite.m_rpc = new RpcManager();
        CandyPlusRewrite.m_rotate = new RotateManager();
        CandyPlusRewrite.m_notif = new NotificationManager();
        CandyPlusRewrite.savedConfig = false;
    }
    public static class GuiEventHandler {
        @SubscribeEvent
        public void onGuiOpen(GuiOpenEvent event) {
            if (event.getGui() instanceof GuiMainMenu) {
                event.setGui(new MainMenu());
            }
        }
    }
}