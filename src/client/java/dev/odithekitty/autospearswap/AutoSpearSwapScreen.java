package dev.odithekitty.autospearswap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.util.InputUtil;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.text.Text;

public final class AutoSpearSwapScreen extends Screen {
    private static final String[] TABS = {"Combat", "Movement", "HUD", "Visuals", "Performance", "Packs", "Keys", "Macros"};
    private static final String[] THEMES = {"Nexus", "Violet", "Mint", "Crimson", "Gold"};
    private static final int[] ACCENT = {0xFF27C7FF, 0xFFB18CFF, 0xFF63E6B0, 0xFFFF647C, 0xFFFFC857};
    private static final int[] PANEL = {0xF0091728, 0xF0151028, 0xF00B211D, 0xF0201018, 0xF0221A0B};
    private static final int[] SIDE = {0xF00B2034, 0xF01D1635, 0xF0103028, 0xF02A1420, 0xF02C220F};
    private int tab;
    private KeyBinding capturing;
    private boolean openingPacks;

    public AutoSpearSwapScreen() { super(Text.literal("NEXUS CLIENT")); }

    @Override
    protected void init() {
        int pw = Math.min(540, width - 20), ph = Math.min(440, height - 20);
        int x = (width - pw) / 2, y = (height - ph) / 2;
        int sx = x + 12, sy = y + 58, sw = Math.min(112, pw / 4);
        for (int i = 0; i < TABS.length; i++) {
            final int index = i;
            add(sx, sy + i * 32, sw, 27, (tab == i ? "◆  " : "   ") + TABS[i],
                b -> { tab = index; clearAndInit(); });
        }
        int cx = sx + sw + 14, cw = pw - (cx - x) - 14, gap = 8, bw = (cw - gap) / 2;
        int y1 = y + 78, h = 30;
        switch (tab) {
            case 0 -> {
                toggle(cx, y1, bw, h, "Triggerbot", AutoSpearSwapConfig.triggerBotEnabled,
                    () -> AutoSpearSwapConfig.triggerBotEnabled = !AutoSpearSwapConfig.triggerBotEnabled);
                toggle(cx + bw + gap, y1, bw, h, "Auto spear", AutoSpearSwapConfig.attributeSwapEnabled,
                    () -> AutoSpearSwapConfig.attributeSwapEnabled = !AutoSpearSwapConfig.attributeSwapEnabled);
                add(cx, y1 + 39, cw, h, "Spear slot  •  " + (AutoSpearSwapConfig.spearSlot + 1), b -> {
                    AutoSpearSwapConfig.spearSlot = (AutoSpearSwapConfig.spearSlot + 1) % 9; save();
                });
            }
            case 1 -> toggle(cx, y1, bw, h, "Auto sprint", AutoSpearSwapConfig.autoSprint,
                () -> AutoSpearSwapConfig.autoSprint = !AutoSpearSwapConfig.autoSprint);
            case 2 -> {
                toggle(cx, y1, bw, h, "Coordinates", AutoSpearSwapConfig.hudCoordinates, () -> AutoSpearSwapConfig.hudCoordinates = !AutoSpearSwapConfig.hudCoordinates);
                toggle(cx + bw + gap, y1, bw, h, "CPS counter", AutoSpearSwapConfig.hudCps, () -> AutoSpearSwapConfig.hudCps = !AutoSpearSwapConfig.hudCps);
                toggle(cx, y1 + 38, bw, h, "Armor status", AutoSpearSwapConfig.hudArmor, () -> AutoSpearSwapConfig.hudArmor = !AutoSpearSwapConfig.hudArmor);
                toggle(cx + bw + gap, y1 + 38, bw, h, "Keystrokes", AutoSpearSwapConfig.hudKeystrokes, () -> AutoSpearSwapConfig.hudKeystrokes = !AutoSpearSwapConfig.hudKeystrokes);
                toggle(cx, y1 + 76, bw, h, "Combat timer", AutoSpearSwapConfig.hudCombatTimer, () -> AutoSpearSwapConfig.hudCombatTimer = !AutoSpearSwapConfig.hudCombatTimer);
                toggle(cx + bw + gap, y1 + 76, bw, h, "Low health", AutoSpearSwapConfig.lowHealthWarning, () -> AutoSpearSwapConfig.lowHealthWarning = !AutoSpearSwapConfig.lowHealthWarning);
            }
            case 3 -> {
                add(cx, y1, bw, h, "Theme  •  " + THEMES[AutoSpearSwapConfig.uiTheme], b -> {
                    AutoSpearSwapConfig.uiTheme = (AutoSpearSwapConfig.uiTheme + 1) % THEMES.length; save();
                });
                add(cx + bw + gap, y1, bw, h, "Opacity  •  " + AutoSpearSwapConfig.uiOpacity + "%", b -> {
                    AutoSpearSwapConfig.uiOpacity += 15;
                    if (AutoSpearSwapConfig.uiOpacity > 85) AutoSpearSwapConfig.uiOpacity = 40;
                    save();
                });
                add(cx, y1 + 39, cw, h, "Cycle visual preset", b -> { visualPreset(); save(); });
            }
            case 4 -> {
                add(cx, y1, cw, h, "Apply FPS preset", b -> { performance(); save(); });
                add(cx, y1 + 38, cw, h, "Restore visual defaults", b -> { defaults(); save(); });
            }
            case 5 -> add(cx, y1, cw, h, "Open resource pack manager", b -> packs());
            case 6 -> {
                add(cx, y1, cw, h, keyText("Menu key", AutoSpearSwapClient.openMenuKey), b -> capture(AutoSpearSwapClient.openMenuKey));
                add(cx, y1 + 38, cw, h, keyText("Spear key", AutoSpearSwapClient.swapToSpearKey), b -> capture(AutoSpearSwapClient.swapToSpearKey));
                add(cx, y1 + 76, cw, h, keyText("Triggerbot key", AutoSpearSwapClient.triggerBotKey), b -> capture(AutoSpearSwapClient.triggerBotKey));
                add(cx, y1 + 114, cw, h, keyText("Macro key", AutoSpearSwapClient.macroKey), b -> capture(AutoSpearSwapClient.macroKey));
            }
            case 7 -> {
                add(cx, y1, bw, h, "Add Jump", b -> { MacroEngine.add("JUMP", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx + bw + gap, y1, bw, h, "Add Attack", b -> { MacroEngine.add("ATTACK", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx, y1 + 38, bw, h, "Add Use", b -> { MacroEngine.add("USE", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx + bw + gap, y1 + 38, bw, h, "Add Forward", b -> { MacroEngine.add("FORWARD", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx, y1 + 76, bw, h, "Add Back", b -> { MacroEngine.add("BACK", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx + bw + gap, y1 + 76, bw, h, "Add Left", b -> { MacroEngine.add("LEFT", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx, y1 + 114, bw, h, "Add Right", b -> { MacroEngine.add("RIGHT", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx + bw + gap, y1 + 114, bw, h, "Add Sprint", b -> { MacroEngine.add("SPRINT", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx, y1 + 152, bw, h, "Delay  •  " + MacroEngine.defaultTicks + " ticks", b -> {
                    int[] delays = {1, 2, 3, 5, 8, 12, 20};
                    int next = 0;
                    for (int i = 0; i < delays.length; i++) if (delays[i] == MacroEngine.defaultTicks) next = (i + 1) % delays.length;
                    MacroEngine.defaultTicks = delays[next]; clearAndInit();
                });
                add(cx + bw + gap, y1 + 152, bw, h, "Undo step", b -> {
                    if (!MacroEngine.STEPS.isEmpty()) { MacroEngine.STEPS.remove(MacroEngine.STEPS.size() - 1); }
                    clearAndInit();
                });
                add(cx, y1 + 190, bw, h, MacroEngine.isRunning() ? "Stop macro" : "Play macro", b -> {
                    MacroEngine.toggle();
                    if (MacroEngine.isRunning() && client != null) client.setScreen(null);
                    else clearAndInit();
                });
                add(cx + bw + gap, y1 + 190, bw, h, "Clear sequence", b -> { MacroEngine.clear(); clearAndInit(); });
            }
        }
        add(x + pw - 82, y + ph - 38, 70, 26, "Close", b -> close());
    }

    private void add(int x, int y, int w, int h, String label, ButtonWidget.PressAction action) {
        addDrawableChild(ButtonWidget.builder(Text.literal(label), action).dimensions(x, y, w, h).build());
    }

    private void toggle(int x, int y, int w, int h, String label, boolean value, Runnable action) {
        add(x, y, w, h, (value ? "●  " : "○  ") + label + (value ? "   ON" : "   OFF"), b -> { action.run(); save(); });
    }

    private void save() { AutoSpearSwapConfig.save(); clearAndInit(); }
    private void capture(KeyBinding key) { capturing = key; clearAndInit(); }
    private String keyText(String name, KeyBinding key) {
        return (capturing == key ? "PRESS A KEY  •  " : name + "  •  ") + key.getBoundKeyLocalizedText().getString();
    }

    private void packs() {
        if (openingPacks || client == null) return;
        openingPacks = true;
        MinecraftClient mc = client;
        Screen parent = this;
        mc.getResourcePackManager().scanPacks();
        mc.setScreen(new PackScreen(mc.getResourcePackManager(), manager -> mc.reloadResources(),
            mc.getResourcePackDir(), Text.literal("Resource Packs")) {
            @Override public void close() { openingPacks = false; mc.setScreen(parent); }
        });
    }

    private static void performance() {
        MinecraftClient c = MinecraftClient.getInstance();
        c.options.getParticles().setValue(ParticlesMode.MINIMAL);
        c.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
        c.options.getEntityDistanceScaling().setValue(0.5);
        c.options.getBiomeBlendRadius().setValue(0);
        c.options.getEntityShadows().setValue(false);
        c.options.write();
    }

    private static int visualPresetIndex = 0;
    private static final String[] VISUAL_PRESETS = {"Clean", "Competitive", "Vibrant", "Classic", "Low Effects"};

    private static void visualPreset() {
        MinecraftClient c = MinecraftClient.getInstance();
        visualPresetIndex = (visualPresetIndex + 1) % VISUAL_PRESETS.length;
        switch (visualPresetIndex) {
            case 1 -> {
                c.options.getParticles().setValue(ParticlesMode.MINIMAL);
                c.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
                c.options.getEntityDistanceScaling().setValue(0.5);
                c.options.getEntityShadows().setValue(false);
            }
            case 2 -> {
                c.options.getParticles().setValue(ParticlesMode.ALL);
                c.options.getCloudRenderMode().setValue(CloudRenderMode.FANCY);
                c.options.getEntityDistanceScaling().setValue(1.0);
                c.options.getEntityShadows().setValue(true);
            }
            case 3 -> {
                c.options.getParticles().setValue(ParticlesMode.DECREASED);
            }
            case 4 -> {
                c.options.getParticles().setValue(ParticlesMode.MINIMAL);
                c.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
                c.options.getEntityDistanceScaling().setValue(0.75);
                c.options.getEntityShadows().setValue(false);
            }
            default -> defaults();
        }
        c.options.write();
    }

    private static void defaults() {
        MinecraftClient c = MinecraftClient.getInstance();
        c.options.getParticles().setValue(ParticlesMode.ALL);
        c.options.getCloudRenderMode().setValue(CloudRenderMode.FANCY);
        c.options.getEntityDistanceScaling().setValue(1.0);
        c.options.getBiomeBlendRadius().setValue(2);
        c.options.getEntityShadows().setValue(true);
        c.options.write();
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (capturing != null) {
            capturing.setBoundKey(InputUtil.fromKeyCode(input));
            KeyBinding.updateKeysByCode();
            capturing = null;
            AutoSpearSwapConfig.save();
            clearAndInit();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        renderBackground(ctx, mx, my, delta);
        int t = Math.max(0, Math.min(4, AutoSpearSwapConfig.uiTheme));
        int pw = Math.min(540, width - 20), ph = Math.min(440, height - 20);
        int x = (width - pw) / 2, y = (height - ph) / 2;
        int a = Math.round(255 * (AutoSpearSwapConfig.uiOpacity / 100.0f));
        ctx.fill(x, y, x + pw, y + ph, (PANEL[t] & 0x00FFFFFF) | (a << 24));
        ctx.fill(x, y, x + pw, y + 3, ACCENT[t]);
        ctx.fill(x + 8, y + 54, x + 132, y + ph - 48, (SIDE[t] & 0x00FFFFFF) | (a << 24));
        ctx.fill(x + 10, y + 48, x + pw - 10, y + 49, 0x553D7898);
        ctx.drawTextWithShadow(textRenderer, Text.literal("NEXUS"), x + 16, y + 13, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("CLIENT  /  MODULE CENTER"), x + 16, y + 27, ACCENT[t]);
        ctx.drawTextWithShadow(textRenderer, Text.literal(TABS[tab].toUpperCase()), x + 150, y + 24, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal(capturing == null ? "CONFIGURE YOUR CLIENT" : "PRESS A KEY TO REBIND"), x + 150, y + 38, 0xFF9CB4C8);
        if (tab == 7) {
            String sequence = MacroEngine.STEPS.isEmpty() ? "No steps yet" : "Sequence: " +
                MacroEngine.STEPS.stream().map(s -> s.action + "(" + s.ticks + "t)").limit(5)
                    .collect(java.util.stream.Collectors.joining("  >  "));
            ctx.drawTextWithShadow(textRenderer, Text.literal(sequence), x + 150, y + 340, ACCENT[t]);
            ctx.drawTextWithShadow(textRenderer, Text.literal("F8  •  " + (MacroEngine.isRunning() ? "RUNNING" : "READY")), x + 150, y + 356, 0xFF9CB4C8);
        }
        super.render(ctx, mx, my, delta);
    }

    @Override public void close() { if (client != null) client.setScreen(null); }
}
