package dev.odithekitty.autospearswap;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

/** Small, user-editable action sequencer. It drives normal client key bindings. */
public final class MacroEngine {
    public static final List<Step> STEPS = new ArrayList<>();
    private static int index;
    private static int elapsed;
    private static KeyBinding active;
    private static boolean running;

    private MacroEngine() {}

    public static void add(String action, int ticks) {
        STEPS.add(new Step(action, Math.max(1, Math.min(100, ticks))));
    }

    public static void clear() {
        stop();
        STEPS.clear();
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
