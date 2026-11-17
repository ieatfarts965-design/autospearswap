package dev.odithekitty.autospearswap;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class AutoSpearSwapScreen extends Screen {
    private int centerX;
    private int y;

    public AutoSpearSwapScreen() {
        super(Text.literal("Auto Spear Swap"));
    }

    @Override
    protected void init() {
        centerX = width / 2;
        y = height / 2 - 82;
        addDrawableChild(ButtonWidget.builder(toggleText("Attribute Swap", AutoSpearSwapConfig.attributeSwapEnabled), b -> {
            AutoSpearSwapConfig.attributeSwapEnabled = !AutoSpearSwapConfig.attributeSwapEnabled;
            AutoSpearSwapConfig.save(); clearAndInit();
        }).dimensions(centerX - 105, y, 210, 20).build());
        y += 26;
        addDrawableChild(ButtonWidget.builder(toggleText("Lunge Swap", AutoSpearSwapConfig.lungeSwapEnabled), b -> {
            AutoSpearSwapConfig.lungeSwapEnabled = !AutoSpearSwapConfig.lungeSwapEnabled;
            AutoSpearSwapConfig.save(); clearAndInit();
        }).dimensions(centerX - 105, y, 210, 20).build());
        y += 34;
        addDrawableChild(ButtonWidget.builder(slotText("Spear slot", AutoSpearSwapConfig.spearSlot), b -> {
            AutoSpearSwapConfig.spearSlot = (AutoSpearSwapConfig.spearSlot + 1) % 9;
            AutoSpearSwapConfig.save(); clearAndInit();
        }).dimensions(centerX - 105, y, 210, 20).build());
        y += 26;
        addDrawableChild(ButtonWidget.builder(slotText("Mace / primary slot", AutoSpearSwapConfig.maceSlot), b -> {
            AutoSpearSwapConfig.maceSlot = (AutoSpearSwapConfig.maceSlot + 1) % 9;
            AutoSpearSwapConfig.save(); clearAndInit();
        }).dimensions(centerX - 105, y, 210, 20).build());
        y += 34;
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
            .dimensions(centerX - 105, y, 210, 20).build());
    }

    private static Text toggleText(String name, boolean enabled) {
        return Text.literal(name + ": " + (enabled ? "ON" : "OFF"));
    }
    private static Text slotText(String name, int slot) {
        return Text.literal(name + ": " + (slot + 1) + " (click to change)");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, centerX, height / 2 - 112, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
            Text.literal("Configure combat swaps and hotbar slots"), centerX, height / 2 - 98, 0xAFC8E8);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(null);
    }
}
