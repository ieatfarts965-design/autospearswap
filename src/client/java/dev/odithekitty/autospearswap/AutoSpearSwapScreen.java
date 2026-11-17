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
    private static final int[] ACCENTS = {0xFF36CFFF, 0xFFB58CFF, 0xFF62E6A5, 0xFFFF7777, 0xFFFFC857};
    private static final String[] THEME_NAMES = {"Cyan", "Violet", "Mint", "Red", "Gold"};
    private KeyBinding capturing;
    private boolean openingResourcePacks;

    public AutoSpearSwapScreen() {
        super(Text.literal("NEXUS CLIENT"));
    }

    @Override
    protected void init() {
        int gap = 6;
        int buttonWidth = Math.min(142, (width - 30 - gap) / 2);
        int left = width / 2 - buttonWidth - gap / 2;
        int right = width / 2 + gap / 2;
        int row = Math.max(28, height / 2 - 180);
        int rowGap = 22;

        addButton(left, row, buttonWidth * 2 + gap,
            Text.literal("Triggerbot  •  " + (AutoSpearSwapConfig.triggerBotEnabled ? "ON" : "OFF") + "  •  VANILLA COOLDOWN"),
            b -> {
                AutoSpearSwapConfig.triggerBotEnabled = !AutoSpearSwapConfig.triggerBotEnabled;
                saveAndRefresh();
            });
        row += rowGap;

        addButton(left, row, buttonWidth, toggleText("Auto spear", AutoSpearSwapConfig.attributeSwapEnabled), b -> {
            AutoSpearSwapConfig.attributeSwapEnabled = !AutoSpearSwapConfig.attributeSwapEnabled;
            saveAndRefresh();
        });
        addButton(right, row, buttonWidth, Text.literal("Spear slot: " + (AutoSpearSwapConfig.spearSlot + 1)), b -> {
            AutoSpearSwapConfig.spearSlot = (AutoSpearSwapConfig.spearSlot + 1) % 9;
            saveAndRefresh();
        });

        row += rowGap;
        addButton(left, row, buttonWidth, bindText("Swap key", AutoSpearSwapClient.swapToSpearKey), b -> {
            capturing = AutoSpearSwapClient.swapToSpearKey;
            clearAndInit();
        });
        addButton(right, row, buttonWidth, bindText("Menu key", AutoSpearSwapClient.openMenuKey), b -> {
            capturing = AutoSpearSwapClient.openMenuKey;
            clearAndInit();
        });

        row += rowGap;
        addToggle(left, right, row, buttonWidth, "Coordinates", AutoSpearSwapConfig.hudCoordinates,
            () -> AutoSpearSwapConfig.hudCoordinates = !AutoSpearSwapConfig.hudCoordinates);
        row += rowGap;
        addToggle(left, right, row, buttonWidth, "CPS counter", AutoSpearSwapConfig.hudCps,
            () -> AutoSpearSwapConfig.hudCps = !AutoSpearSwapConfig.hudCps);
        row += rowGap;
        addToggle(left, right, row, buttonWidth, "Armor status", AutoSpearSwapConfig.hudArmor,
            () -> AutoSpearSwapConfig.hudArmor = !AutoSpearSwapConfig.hudArmor);
        row += rowGap;
        addToggle(left, right, row, buttonWidth, "Keystrokes", AutoSpearSwapConfig.hudKeystrokes,
            () -> AutoSpearSwapConfig.hudKeystrokes = !AutoSpearSwapConfig.hudKeystrokes);
        row += rowGap;
        addToggle(left, right, row, buttonWidth, "Combat timer", AutoSpearSwapConfig.hudCombatTimer,
            () -> AutoSpearSwapConfig.hudCombatTimer = !AutoSpearSwapConfig.hudCombatTimer);
        row += rowGap;
        addToggle(left, right, row, buttonWidth, "Low health alert", AutoSpearSwapConfig.lowHealthWarning,
            () -> AutoSpearSwapConfig.lowHealthWarning = !AutoSpearSwapConfig.lowHealthWarning);
        row += rowGap;
        addToggle(left, right, row, buttonWidth, "Auto sprint", AutoSpearSwapConfig.autoSprint,
            () -> AutoSpearSwapConfig.autoSprint = !AutoSpearSwapConfig.autoSprint);

        row += rowGap;
        addButton(left, row, buttonWidth, Text.literal("FPS boost preset"), b -> {
            applyPerformancePreset();
            saveAndRefresh();
        });
        addButton(right, row, buttonWidth, Text.literal("Visual defaults"), b -> {
            restoreVisualDefaults();
            saveAndRefresh();
        });

        row += rowGap;
        addButton(left, row, buttonWidth, Text.literal("Theme: " + THEME_NAMES[AutoSpearSwapConfig.uiTheme]), b -> {
            AutoSpearSwapConfig.uiTheme = (AutoSpearSwapConfig.uiTheme + 1) % THEME_NAMES.length;
            saveAndRefresh();
        });
        addButton(right, row, buttonWidth, Text.literal("Panel: " + AutoSpearSwapConfig.uiOpacity + "%"), b -> {
            AutoSpearSwapConfig.uiOpacity += 15;
            if (AutoSpearSwapConfig.uiOpacity > 85) AutoSpearSwapConfig.uiOpacity = 40;
            saveAndRefresh();
        });

        row += rowGap + 3;
        addButton(left, row, buttonWidth * 2 + gap, Text.literal("Open / import resource packs"), b -> openResourcePacks());
        row += rowGap + 3;
        addButton(left, row, buttonWidth * 2 + gap, Text.literal("Done"), b -> close());
    }

    private void addToggle(int left, int right, int y, int buttonWidth, String label, boolean enabled, Runnable toggle) {
        addButton(left, y, buttonWidth, toggleText(label, enabled), b -> {
            toggle.run();
            saveAndRefresh();
        });
    }

    private void addButton(int x, int y, int w, Text label, ButtonWidget.PressAction action) {
        addDrawableChild(ButtonWidget.builder(label, action).dimensions(x, y, w, 20).build());
    }

    private void saveAndRefresh() {
        AutoSpearSwapConfig.save();
        clearAndInit();
    }

    private void openResourcePacks() {
        if (openingResourcePacks || client == null) return;
        openingResourcePacks = true;
        MinecraftClient mc = client;
        Screen parent = this;
        // Rescan the actual resourcepacks directory before opening the picker so
        // packs copied in while Minecraft was running appear immediately.
        mc.getResourcePackManager().scanPacks();
        mc.setScreen(new PackScreen(
            mc.getResourcePackManager(),
            manager -> mc.reloadResources(),
            mc.getResourcePackDir(),
            Text.literal("Resource Packs")
        ) {
            @Override
            public void close() {
                openingResourcePacks = false;
                mc.setScreen(parent);
            }
        });
    }

    private static void applyPerformancePreset() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.options.getParticles().setValue(ParticlesMode.MINIMAL);
        client.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
        client.options.getEntityDistanceScaling().setValue(0.5);
        client.options.getBiomeBlendRadius().setValue(0);
        client.options.getEntityShadows().setValue(false);
        client.options.write();
    }

    private static void restoreVisualDefaults() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.options.getParticles().setValue(ParticlesMode.ALL);
        client.options.getCloudRenderMode().setValue(CloudRenderMode.FANCY);
        client.options.getEntityDistanceScaling().setValue(1.0);
        client.options.getBiomeBlendRadius().setValue(2);
        client.options.getEntityShadows().setValue(true);
        client.options.write();
    }

    private static Text toggleText(String name, boolean enabled) {
        return Text.literal(name + ": " + (enabled ? "ON" : "OFF"));
    }

    private Text bindText(String name, KeyBinding key) {
        return Text.literal((capturing == key ? "Press key..." : name + ": " + key.getBoundKeyLocalizedText().getString()));
    }

    private int accent() { return ACCENTS[AutoSpearSwapConfig.uiTheme]; }

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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int panelWidth = Math.min(340, width - 16);
        int panelHeight = Math.min(430, height - 12);
        int x = (width - panelWidth) / 2;
        int y = (height - panelHeight) / 2;
        int alpha = Math.round(255 * (AutoSpearSwapConfig.uiOpacity / 100.0f));
        context.fill(x, y, x + panelWidth, y + panelHeight, (alpha << 24) | 0x00091420);
        context.fill(x, y, x + panelWidth, y + 3, accent());
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, y + 8, accent());
        context.drawCenteredTextWithShadow(textRenderer,
            Text.literal(capturing == null ? "PVP HUD  •  TRIGGERBOT  •  COMBAT" : "Press the key you want to bind"),
            width / 2, y + 20, 0xFFD7E6F2);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(null);
    }
}
