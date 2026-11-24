package com.premclient;

import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

/** Tiny config: which HUD modules are on. Saved to config/premclient.properties */
public final class PremConfig {
    public enum Module {
        FPS("FPS"), CPS("CPS"), PING("Ping"), COORDS("Coordinates"),
        DIRECTION("Direction"), KEYSTROKES("Keystrokes"), ARMOR("Armor Durability");

        public final String label;
        Module(String label) { this.label = label; }
    }

    private static final Map<Module, Boolean> ENABLED = new EnumMap<>(Module.class);
    private static boolean panels = true;

    static {
        for (Module m : Module.values()) ENABLED.put(m, true);
    }

    private PremConfig() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("premclient.properties");
    }

    public static boolean on(Module m) { return ENABLED.getOrDefault(m, true); }

    public static void toggle(Module m) { ENABLED.put(m, !on(m)); save(); }

    public static boolean panels() { return panels; }

    public static void togglePanels() { panels = !panels; save(); }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        try (InputStream in = Files.newInputStream(f)) {
            Properties p = new Properties();
            p.load(in);
            for (Module m : Module.values()) {
                String v = p.getProperty(m.name());
                if (v != null) ENABLED.put(m, Boolean.parseBoolean(v));
            }
            String pv = p.getProperty("PANELS");
            if (pv != null) panels = Boolean.parseBoolean(pv);
        } catch (Exception ignored) { }
    }

    public static void save() {
        try (OutputStream out = Files.newOutputStream(file())) {
            Properties p = new Properties();
            for (Module m : Module.values()) p.setProperty(m.name(), Boolean.toString(on(m)));
            p.setProperty("PANELS", Boolean.toString(panels));
            p.store(out, "PREM Client");
        } catch (Exception ignored) { }
    }
}
