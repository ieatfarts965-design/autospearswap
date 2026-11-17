package dev.odithekitty.autospearswap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AutoSpearSwapConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("autospearswap.json");
    private static final Path PROFILES = FabricLoader.getInstance().getConfigDir().resolve("nexus-profiles");

    public static boolean attributeSwapEnabled = false;
    public static boolean lungeSwapEnabled = false;
    public static int spearSlot = 8;
    public static int maceSlot = 1;
    public static int lungeReturnDelayTicks = 1;
    public static int uiTheme = 5;
    public static int uiOpacity = 65;
    public static boolean hudCoordinates = true;
    public static boolean hudCps = true;
    public static boolean hudArmor = true;
    public static boolean hudKeystrokes = true;
    public static boolean hudCombatTimer = true;
    public static boolean lowHealthWarning = true;
    public static boolean autoSprint = false;
    public static boolean triggerBotEnabled = false;
    public static int triggerBotDelayTicks = 2;
    public static boolean jumpResetEnabled = false;
    public static boolean hudSpeed = true;
    public static boolean hudDirection = true;
    public static boolean hudBiome = false;
    public static boolean hudPing = true;
    public static boolean hudHealth = true;
    public static boolean hudFood = false;
    public static boolean hudHeldItem = false;
    public static boolean hudEffects = false;
    public static int hudX = 7;
    public static int hudY = 7;
    public static boolean triggerBotHoldMode = false;

    private AutoSpearSwapConfig() {}

    /** Conservative, HUD-focused profile intended to keep gameplay manual. */
    public static void applyLegitPreset() {
        triggerBotEnabled = false;
        jumpResetEnabled = false;
        attributeSwapEnabled = false;
        lungeSwapEnabled = false;
        autoSprint = false;
        hudCoordinates = true;
        hudCps = true;
        hudArmor = true;
        hudKeystrokes = true;
        hudCombatTimer = true;
        lowHealthWarning = true;
        hudSpeed = true;
        hudDirection = true;
        hudBiome = false;
        hudPing = true;
        hudHealth = true;
        hudFood = false;
        hudHeldItem = false;
        hudEffects = false;
        uiTheme = 5;
        uiOpacity = 82;
        save();
    }

    public static void saveProfile(String name) {
        try {
            save();
            Files.createDirectories(PROFILES);
            Files.copy(FILE, PROFILES.resolve(safeProfile(name) + ".json"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.err.println("[Nexus] Could not save profile: " + e.getMessage());
        }
    }

    public static void loadProfile(String name) {
        try {
            Path profile = PROFILES.resolve(safeProfile(name) + ".json");
            if (Files.exists(profile)) {
                Files.copy(profile, FILE, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                load();
            }
        } catch (Exception e) {
            System.err.println("[Nexus] Could not load profile: " + e.getMessage());
        }
    }

    private static String safeProfile(String name) {
        return name.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

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
                    uiTheme = Math.max(0, Math.min(8, data.uiTheme));
                    uiOpacity = Math.max(25, Math.min(90, data.uiOpacity));
                    hudCoordinates = data.hudCoordinates;
                    hudCps = data.hudCps;
                    hudArmor = data.hudArmor;
                    hudKeystrokes = data.hudKeystrokes;
                    hudCombatTimer = data.hudCombatTimer;
                    lowHealthWarning = data.lowHealthWarning;
                    autoSprint = data.autoSprint;
                    triggerBotEnabled = data.triggerBotEnabled;
                    triggerBotDelayTicks = Math.max(1, Math.min(10, data.triggerBotDelayTicks));
                    jumpResetEnabled = data.jumpResetEnabled;
                    hudSpeed = data.hudSpeed;
                    hudDirection = data.hudDirection;
                    hudBiome = data.hudBiome;
                    hudPing = data.hudPing;
                    hudHealth = data.hudHealth;
                    hudFood = data.hudFood;
                    hudHeldItem = data.hudHeldItem;
                    hudEffects = data.hudEffects;
                    hudX = Math.max(0, Math.min(10000, data.hudX));
                    hudY = Math.max(0, Math.min(10000, data.hudY));
                    triggerBotHoldMode = data.triggerBotHoldMode;
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
            d.triggerBotEnabled = triggerBotEnabled;
            d.triggerBotDelayTicks = triggerBotDelayTicks;
            d.jumpResetEnabled = jumpResetEnabled;
            d.hudSpeed = hudSpeed;
            d.hudDirection = hudDirection;
            d.hudBiome = hudBiome;
            d.hudPing = hudPing;
            d.hudHealth = hudHealth;
            d.hudFood = hudFood;
            d.hudHeldItem = hudHeldItem;
            d.hudEffects = hudEffects;
            d.hudX = hudX;
            d.hudY = hudY;
            d.triggerBotHoldMode = triggerBotHoldMode;
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
        int uiTheme = 5;
        int uiOpacity = 65;
        boolean hudCoordinates = true;
        boolean hudCps = true;
        boolean hudArmor = true;
        boolean hudKeystrokes = true;
        boolean hudCombatTimer = true;
        boolean lowHealthWarning = true;
        boolean autoSprint = false;
        boolean triggerBotEnabled = false;
        int triggerBotDelayTicks = 2;
        boolean jumpResetEnabled = false;
        boolean hudSpeed = true;
        boolean hudDirection = true;
        boolean hudBiome = false;
        boolean hudPing = true;
        boolean hudHealth = true;
        boolean hudFood = false;
        boolean hudHeldItem = false;
        boolean hudEffects = false;
        int hudX = 7;
        int hudY = 7;
        boolean triggerBotHoldMode = false;
    }
}
