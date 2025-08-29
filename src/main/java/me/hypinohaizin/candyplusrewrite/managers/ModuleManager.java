package me.hypinohaizin.candyplusrewrite.managers;

import java.util.*;

import me.hypinohaizin.candyplusrewrite.event.events.player.PlayerDeathEvent;
import me.hypinohaizin.candyplusrewrite.hud.modules.*;
import me.hypinohaizin.candyplusrewrite.module.combat.*;
import me.hypinohaizin.candyplusrewrite.module.exploit.*;
import me.hypinohaizin.candyplusrewrite.module.misc.*;
import me.hypinohaizin.candyplusrewrite.module.movement.*;
import me.hypinohaizin.candyplusrewrite.module.render.*;
import net.minecraft.entity.player.EntityPlayer;
import me.hypinohaizin.candyplusrewrite.event.events.network.PacketEvent;

import me.hypinohaizin.candyplusrewrite.module.Module;

public class ModuleManager extends Manager
{
    public static LinkedHashMap<Class<? extends Module>, Module> modulesClassMap;
    public static LinkedHashMap<String, Module> modulesNameMap;

    public List<Module> modules;

    public ModuleManager() {
        modules = new ArrayList<>();
    }

    @Override
    public void load() {
        //Combat
        register(new AntiBurrow());
        register(new Aura());
        register(new AntiRegear());
        register(new AnvilAura());
        register(new AutoChase());
        register(new AutoCity());
        register(new AutoCityRewrite());
        register(new AutoMend());
        register(new AutoSelfFill());
        register(new AutoTotem());
        register(new Blocker());
        register(new BowSpam());
        register(new CevBreaker());
        register(new CivBreaker());
        register(new CrystalAura());
        register(new HoleFill());
        register(new Quiver());
        register(new SelfAnvil());
        register(new TPAura());
        register(new Velocity());

        //Exploit
        register(new Burrow());
        register(new CornerClip());
        register(new InstantMine());
        register(new NoMiningTrace());
        register(new PacketCanceller());
        register(new PingBypass());
        register(new SilentPickel());
        register(new TrapPhase());
        register(new XCarry());

        //misc
        register(new AntiMapBan());
        register(new AntiUnicode());
        register(new AutoDrop());
        register(new AutoEZ());
        register(new ArmorAlert());
        register(new BaseFinder());
        register(new BuildRandom());
        register(new ChatLag());
        register(new ChatSuffix());
        register(new DiscordRPC());
        register(new DonkeyNotifier());
        register(new FakePlayer());
        register(new HoleBreakAlert());
        register(new InstantWither());
        register(new PopAnnouncer());
        register(new Refill());
        register(new Spammer());

        //movement
        register(new AntiHunger());
        register(new Blink());
        register(new BlockMove());
        register(new HoleTP());
        register(new NoFall());
        register(new FastStop());
        register(new Follow());
        register(new NoSlowdown());
        //register(new PhaseWalk());
        register(new PhaseFly());
        register(new ReverseStep());
        register(new Step());
        register(new Flight());
        register(new Speed());
        register(new TPCart());

        //render
        register(new Afterglow());
        register(new Animation());
        register(new BreadCrumbs());
        register(new BurrowESP());
        register(new CandyCrystal());
        register(new CityESP());
        register(new ClickGUI());
        register(new CrystalSpawns());
        register(new CustomFov());
        register(new EnchantmentColor());
        register(new Freecam());
        register(new HoleESP());
        register(new HUDEditor());
        register(new NoOverlay());
        register(new Notification());
        register(new SmallShield());
        //hud
        register(new Watermark());
        register(new Watermark2());
        register(new PvPResources());
        register(new PlayerList());
        register(new CombatInfo());
        register(new PlayerModel());
        register(new TargetHUD());
        register(new ModuleList());
        register(new InventoryViewer());
        register(new Welcomer());
    }

    public void register(final Module module) {
        modules.add(module);
        modulesClassMap.put(module.getClass(), module);
        modulesNameMap.put(module.getName().toLowerCase(Locale.ROOT), module);
    }

    public void onUpdate() {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onUpdate());
    }

    public void onTick() {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onTick());
    }

    public void onConnect() {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onConnect());
    }

    public void onRender2D() {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onRender2D());
    }

    public void onRender3D() {
        modules.stream().filter(m -> m.isEnable).forEach(m -> {
            ModuleManager.mc.profiler.startSection(m.name);
            m.onRender3D();
            ModuleManager.mc.profiler.endSection();
        });
    }

    public void onRender3D(final float ticks) {
        modules.stream().filter(m -> m.isEnable).forEach(m -> {
            ModuleManager.mc.profiler.startSection(m.name);
            m.onRender3D(ticks);
            ModuleManager.mc.profiler.endSection();
        });
    }

    public void onPacketSend(final PacketEvent.Send event) {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onPacketSend(event));
    }

    public void onPacketReceive(final PacketEvent.Receive event) {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onPacketReceive(event));
    }

    public void onTotemPop(final EntityPlayer player) {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onTotemPop(player));
    }

    public void onPlayerDeath(final PlayerDeathEvent event) {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onPlayerDeath(event));
    }

    public void onKeyInput(final int key) {
        modules.stream().filter(m -> m.key.getKey() == key).forEach(m -> m.toggle());
    }

    public List<Module> getModulesWithCategories(final Module.Categories c) {
        final List<Module> moduleList = new ArrayList<>();
        for (final Module m : modules) {
            if (m.category == c) {
                moduleList.add(m);
            }
        }
        return moduleList;
    }

    public Module getModuleWithName(final String name) {
        Module r = null;
        for (final Module m : modules) {
            if (m.name.equalsIgnoreCase(name)) {
                r = m;
            }
        }
        return r;
    }

    public Module getModuleWithClass(final Class<? extends Module> clazz) {
        Module r = null;
        for (final Module m : modules) {
            if (m.getClass() == clazz) {
                r = m;
            }
        }
        return r;
    }

    public static Collection<Module> getModules() {
        return ModuleManager.modulesClassMap.values();
    }

    public static ArrayList<Module> getModulesInCategory(final Module.Categories category) {
        final ArrayList<Module> list = new ArrayList<>();
        for (final Module module : modulesClassMap.values()) {
            if (module.category != category) continue;
            list.add(module);
        }
        return list;
    }

    public static <T extends Module> T getModule(final Class<T> clazz) {
        return (T) modulesClassMap.get(clazz);
    }

    public static Module getModule(final String name) {
        if (name == null) return null;
        return modulesNameMap.get(name.toLowerCase(Locale.ROOT));
    }

    public static boolean isModuleEnabled(final Class<? extends Module> clazz) {
        final Module module = getModule(clazz);
        return module != null && module.isEnable();
    }

    public static boolean isModuleEnabled(final String name) {
        final Module module = getModule(name);
        return module != null && module.isEnable();
    }

    static {
        modulesClassMap = new LinkedHashMap<>();
        modulesNameMap = new LinkedHashMap<>();
    }
}