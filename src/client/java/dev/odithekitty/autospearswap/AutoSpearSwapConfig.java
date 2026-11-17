package dev.odithekitty.autospearswap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AutoSpearSwapConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("autospearswap.json");

    public static boolean attributeSwapEnabled = false;
    public static boolean lungeSwapEnabled = false;
    public static int spearSlot = 8;
    public static int maceSlot = 1;
    public static int lungeReturnDelayTicks = 1;
    public static int uiTheme = 0;
    public static int uiOpacity = 65;
    public static boolean hudCoordinates = true;
    public static boolean hudCps = true;
    public static boolean hudArmor = true;
    public static boolean hudKeystrokes = true;
    public static boolean hudCombatTimer = true;
    public static boolean lowHealthWarning = true;
    public static boolean autoSprint = false;

    private AutoSpearSwapConfig() {}

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                Data data = GSON.fromJson(Files.readString(FILE), Data.class);
                if (data != null) {
                    attributeSwapEnabled = data.attributeSwapEnabled;
                    lungeSwapEnabled = data.lungeSwapEnabled;
                    spearSlot = clamp(data.spearSlot);
                    maceSlot = clamp(data.maceSlot);
                    lungeReturnDelayTicks = Math.max(0, Math.min(10, data.lungeReturnDelayTicks));
                    uiTheme = Math.max(0, Math.min(4, data.uiTheme));
                    uiOpacity = Math.max(25, Math.min(90, data.uiOpacity));
                    hudCoordinates = data.hudCoordinates;
                    hudCps = data.hudCps;
                    hudArmor = data.hudArmor;
                    hudKeystrokes = data.hudKeystrokes;
                    hudCombatTimer = data.hudCombatTimer;
                    lowHealthWarning = data.lowHealthWarning;
                    autoSprint = data.autoSprint;
                }
            } else {
                save();
            }
        } catch (Exception e) {
            System.err.println("[AutoSpearSwap] Could not load config: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Data d = new Data();
            d.attributeSwapEnabled = attributeSwapEnabled;
            d.lungeSwapEnabled = lungeSwapEnabled;
            d.spearSlot = spearSlot;
            d.maceSlot = maceSlot;
            d.lungeReturnDelayTicks = lungeReturnDelayTicks;
            d.uiTheme = uiTheme;
            d.uiOpacity = uiOpacity;
            d.hudCoordinates = hudCoordinates;
            d.hudCps = hudCps;
            d.hudArmor = hudArmor;
            d.hudKeystrokes = hudKeystrokes;
            d.hudCombatTimer = hudCombatTimer;
            d.lowHealthWarning = lowHealthWarning;
            d.autoSprint = autoSprint;
            Files.writeString(FILE, GSON.toJson(d));
        } catch (Exception e) {
            System.err.println("[AutoSpearSwap] Could not save config: " + e.getMessage());
        }
    }

    private static int clamp(int slot) {
        return Math.max(0, Math.min(8, slot));
    }

    private static final class Data {
        boolean attributeSwapEnabled;
        boolean lungeSwapEnabled;
        int spearSlot = 8;
        int maceSlot = 1;
        int lungeReturnDelayTicks = 1;
        int uiTheme = 0;
        int uiOpacity = 65;
        boolean hudCoordinates = true;
        boolean hudCps = true;
        boolean hudArmor = true;
        boolean hudKeystrokes = true;
        boolean hudCombatTimer = true;
        boolean lowHealthWarning = true;
        boolean autoSprint = false;
    }
}
