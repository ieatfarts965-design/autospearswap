package dev.odithekitty.autospearswap;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public final class AutoSpearSwapScreen extends Screen {
    private int centerX;
    private int y;
    private KeyBinding capturing;

    public AutoSpearSwapScreen() {
        super(Text.literal("Auto Spear Swap"));
    }

    @Override
    protected void init() {
        centerX = width / 2;
        y = height / 2 - 88;
        addDrawableChild(ButtonWidget.builder(toggleText("Auto Attribute Swap", AutoSpearSwapConfig.attributeSwapEnabled), b -> {
            AutoSpearSwapConfig.attributeSwapEnabled = !AutoSpearSwapConfig.attributeSwapEnabled;
            AutoSpearSwapConfig.save(); clearAndInit();
        }).dimensions(centerX - 112, y, 224, 20).build());
        y += 28;
        addDrawableChild(ButtonWidget.builder(slotText("Spear slot", AutoSpearSwapConfig.spearSlot), b -> {
            AutoSpearSwapConfig.spearSlot = (AutoSpearSwapConfig.spearSlot + 1) % 9;
            AutoSpearSwapConfig.save(); clearAndInit();
        }).dimensions(centerX - 112, y, 224, 20).build());
        y += 30;
        addDrawableChild(ButtonWidget.builder(bindText("Menu key", AutoSpearSwapClient.openMenuKey), b -> {
            capturing = AutoSpearSwapClient.openMenuKey; clearAndInit();
        }).dimensions(centerX - 112, y, 224, 20).build());
        y += 25;
        addDrawableChild(ButtonWidget.builder(bindText("Auto swap key", AutoSpearSwapClient.toggleAttributeSwapKey), b -> {
            capturing = AutoSpearSwapClient.toggleAttributeSwapKey; clearAndInit();
        }).dimensions(centerX - 112, y, 224, 20).build());
        y += 30;
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
            .dimensions(centerX - 112, y, 224, 20).build());
    }

    private static Text toggleText(String name, boolean enabled) {
        return Text.literal(name + ": " + (enabled ? "ON" : "OFF"));
    }
    private static Text slotText(String name, int slot) {
        return Text.literal(name + ": " + (slot + 1) + " (click to cycle)");
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
        context.drawCenteredTextWithShadow(textRenderer, title, centerX, height / 2 - 118, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
            Text.literal(capturing == null ? "Configure automatic combat swapping" : "Press any key (ESC also binds Escape)"),
            centerX, height / 2 - 103, 0xAFC8E8);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(null);
    }
}
