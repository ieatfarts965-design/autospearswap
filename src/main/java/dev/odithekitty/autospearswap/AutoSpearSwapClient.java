package dev.odithekitty.autospearswap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class AutoSpearSwapClient implements ClientModInitializer {
    public static KeyBinding openMenuKey;
    public static KeyBinding toggleAttributeSwapKey;
    public static KeyBinding toggleLungeSwapKey;

    @Override
    public void onInitializeClient() {
        AutoSpearSwapConfig.load();
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autospearswap.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT,
            KeyBinding.Category.create(net.minecraft.util.Identifier.of("autospearswap", "main"))));
        toggleAttributeSwapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autospearswap.toggle_attribute", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B,
            KeyBinding.Category.create(net.minecraft.util.Identifier.of("autospearswap", "main"))));
        toggleLungeSwapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autospearswap.toggle_lunge", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G,
            KeyBinding.Category.create(net.minecraft.util.Identifier.of("autospearswap", "main"))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new AutoSpearSwapScreen());
            }
            while (toggleAttributeSwapKey.wasPressed()) {
                AutoSpearSwapConfig.attributeSwapEnabled = !AutoSpearSwapConfig.attributeSwapEnabled;
                AutoSpearSwapConfig.save();
            }
            while (toggleLungeSwapKey.wasPressed()) {
                AutoSpearSwapConfig.lungeSwapEnabled = !AutoSpearSwapConfig.lungeSwapEnabled;
                AutoSpearSwapConfig.save();
            }
        });
    }
}
