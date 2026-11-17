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
                }
            } else save();
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
            Files.writeString(FILE, GSON.toJson(d));
        } catch (Exception e) {
            System.err.println("[AutoSpearSwap] Could not save config: " + e.getMessage());
        }
    }

    private static int clamp(int slot) { return Math.max(0, Math.min(8, slot)); }
    private static final class Data {
        boolean attributeSwapEnabled;
        boolean lungeSwapEnabled;
        int spearSlot = 8;
        int maceSlot = 1;
        int lungeReturnDelayTicks = 1;
    }
}
