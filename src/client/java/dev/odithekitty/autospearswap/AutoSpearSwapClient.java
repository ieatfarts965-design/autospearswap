package dev.odithekitty.autospearswap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class AutoSpearSwapClient implements ClientModInitializer {
    public static KeyBinding openMenuKey;
    public static KeyBinding toggleAttributeSwapKey;
    public static KeyBinding swapToSpearKey;
    private static int previousSlot = -1;

    @Override
    public void onInitializeClient() {
        AutoSpearSwapConfig.load();
        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("autospearswap", "main"));
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autospearswap.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));
        toggleAttributeSwapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autospearswap.toggle_attribute", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, category));
        swapToSpearKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autospearswap.swap_spear", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new AutoSpearSwapScreen());
            }
            while (toggleAttributeSwapKey.wasPressed()) {
                AutoSpearSwapConfig.attributeSwapEnabled = !AutoSpearSwapConfig.attributeSwapEnabled;
                AutoSpearSwapConfig.save();
            }
            while (swapToSpearKey.wasPressed()) {
                if (client.player == null) continue;
                int selected = client.player.getInventory().getSelectedSlot();
                if (previousSlot < 0) {
                    if (selected != AutoSpearSwapConfig.spearSlot) {
                        previousSlot = selected;
                        client.player.getInventory().setSelectedSlot(AutoSpearSwapConfig.spearSlot);
                    }
                } else {
                    client.player.getInventory().setSelectedSlot(previousSlot);
                    previousSlot = -1;
                }
            }

            // When enabled, holding the normal use key temporarily selects the configured spear.
            if (client.player != null && AutoSpearSwapConfig.attributeSwapEnabled) {
                boolean using = client.options.useKey.isPressed();
                if (using && previousSlot < 0) {
                    int selected = client.player.getInventory().getSelectedSlot();
                    if (selected != AutoSpearSwapConfig.spearSlot) {
                        previousSlot = selected;
                        client.player.getInventory().setSelectedSlot(AutoSpearSwapConfig.spearSlot);
                    }
                } else if (!using && previousSlot >= 0) {
                    client.player.getInventory().setSelectedSlot(previousSlot);
                    previousSlot = -1;
                }
            }
        });
    }
}
