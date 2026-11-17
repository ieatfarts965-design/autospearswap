package dev.odithekitty.autospearswap;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

/** Small, user-editable action sequencer. It drives normal client key bindings. */
public final class MacroEngine {
    public static final List<Step> STEPS = new ArrayList<>();
    private static int index;
    private static int elapsed;
    private static KeyBinding active;
    private static boolean running;
    public static int defaultTicks = 2;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("nexus-macros.json");

    private MacroEngine() {}

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                Step[] saved = GSON.fromJson(Files.readString(FILE), Step[].class);
                STEPS.clear();
                if (saved != null) for (Step step : saved) {
                    if (step != null && step.action != null && resolveAction(step.action))
                        STEPS.add(new Step(step.action, Math.max(1, Math.min(100, step.ticks))));
                }
            }
        } catch (Exception e) {
            System.err.println("[Nexus] Could not load macros: " + e.getMessage());
        }
    }

    private static boolean resolveAction(String action) {
        return List.of("JUMP", "ATTACK", "USE", "FORWARD", "BACK", "LEFT", "RIGHT", "SPRINT", "SNEAK").contains(action);
    }

    private static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(STEPS));
        } catch (Exception e) {
            System.err.println("[Nexus] Could not save macros: " + e.getMessage());
        }
    }

    public static void add(String action, int ticks) {
        STEPS.add(new Step(action, Math.max(1, Math.min(100, ticks))));
        save();
    }

    public static void undo() {
        if (!STEPS.isEmpty()) { STEPS.remove(STEPS.size() - 1); save(); }
    }

    public static void clear() {
        stop();
        STEPS.clear();
        save();
    }

    public static void toggle() {
        if (running) stop();
        else if (!STEPS.isEmpty()) {
            index = 0;
            elapsed = 0;
            running = true;
        }
    }

    public static boolean isRunning() { return running; }

    public static void tick(MinecraftClient client) {
        if (!running || client.player == null || client.world == null || client.currentScreen != null) {
            if (running && client.currentScreen != null) stop();
            return;
        }
        if (index >= STEPS.size()) { stop(); return; }
        Step step = STEPS.get(index);
        KeyBinding next = resolve(client, step.action);
        if (active != next) {
            release();
            active = next;
            if (active != null) active.setPressed(true);
        }
        elapsed++;
        if (elapsed >= step.ticks) {
            release();
            active = null;
            index++;
            elapsed = 0;
            if (index >= STEPS.size()) stop();
        }
    }

    public static void stop() {
        release();
        active = null;
        running = false;
        index = 0;
        elapsed = 0;
    }

    private static void release() {
        if (active != null) active.setPressed(false);
    }

    private static KeyBinding resolve(MinecraftClient c, String action) {
        return switch (action) {
            case "JUMP" -> c.options.jumpKey;
            case "ATTACK" -> c.options.attackKey;
            case "USE" -> c.options.useKey;
            case "FORWARD" -> c.options.forwardKey;
            case "BACK" -> c.options.backKey;
            case "LEFT" -> c.options.leftKey;
            case "RIGHT" -> c.options.rightKey;
            case "SPRINT" -> c.options.sprintKey;
            case "SNEAK" -> c.options.sneakKey;
            default -> null;
        };
    }

    public static final class Step {
        public String action;
        public int ticks;
        public Step(String action, int ticks) { this.action = action; this.ticks = ticks; }
    }
}
