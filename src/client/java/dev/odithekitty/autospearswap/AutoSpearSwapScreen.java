package dev.odithekitty.autospearswap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.util.InputUtil;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.text.Text;

public final class AutoSpearSwapScreen extends Screen {
    private int centerX;
    private int top;
    private KeyBinding capturing;

    public AutoSpearSwapScreen() {
        super(Text.literal("NEXUS CLIENT"));
    }

    @Override
    protected void init() {
        centerX = width / 2;
        top = height / 2 - 92;

        addDrawableChild(ButtonWidget.builder(toggleText("Attribute swap helper", AutoSpearSwapConfig.attributeSwapEnabled), b -> {
            AutoSpearSwapConfig.attributeSwapEnabled = !AutoSpearSwapConfig.attributeSwapEnabled;
            AutoSpearSwapConfig.save();
            clearAndInit();
        }).dimensions(centerX - 115, top, 230, 20).build());
        top += 25;

        addDrawableChild(ButtonWidget.builder(Text.literal("Spear hotbar slot: " + (AutoSpearSwapConfig.spearSlot + 1) + "  >"), b -> {
            AutoSpearSwapConfig.spearSlot = (AutoSpearSwapConfig.spearSlot + 1) % 9;
            AutoSpearSwapConfig.save();
            clearAndInit();
        }).dimensions(centerX - 115, top, 230, 20).build());
        top += 25;

        addDrawableChild(ButtonWidget.builder(bindText("Swap / restore key", AutoSpearSwapClient.swapToSpearKey), b -> {
            capturing = AutoSpearSwapClient.swapToSpearKey;
            clearAndInit();
        }).dimensions(centerX - 115, top, 230, 20).build());
        top += 25;

        addDrawableChild(ButtonWidget.builder(bindText("Open menu key", AutoSpearSwapClient.openMenuKey), b -> {
            capturing = AutoSpearSwapClient.openMenuKey;
            clearAndInit();
        }).dimensions(centerX - 115, top, 230, 20).build());
        top += 32;

        addDrawableChild(ButtonWidget.builder(Text.literal("Apply FPS-friendly settings"), b -> {
            applyPerformancePreset();
            clearAndInit();
        }).dimensions(centerX - 115, top, 230, 20).build());
        top += 25;

        addDrawableChild(ButtonWidget.builder(Text.literal("Restore visual defaults"), b -> {
            restoreVisualDefaults();
            clearAndInit();
        }).dimensions(centerX - 115, top, 230, 20).build());
        top += 32;

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
            .dimensions(centerX - 115, top, 230, 20).build());
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
        return Text.literal((capturing == key ? "Press a key for " : "Rebind ") + name
            + (capturing == key ? "..." : ": " + key.getBoundKeyLocalizedText().getString()));
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (capturing != null) {
            capturing.setBoundKey(InputUtil.fromKeyCode(input));
            KeyBinding.updateKeysByCode();
            capturing = null;
            clearAndInit();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, centerX, height / 2 - 122, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
            Text.literal(capturing == null ? "Combat tools  •  Performance" : "Press the key you want to bind"),
            centerX, height / 2 - 107, 0xAFC8E8);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(null);
    }
}
