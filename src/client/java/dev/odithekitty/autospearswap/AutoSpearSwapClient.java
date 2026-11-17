package dev.odithekitty.autospearswap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import dev.odithekitty.autospearswap.mixin.MinecraftClientAccessor;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class AutoSpearSwapClient implements ClientModInitializer {
    public static KeyBinding openMenuKey;
    public static KeyBinding toggleAttributeSwapKey;
    public static KeyBinding swapToSpearKey;
    public static KeyBinding triggerBotKey;
    public static KeyBinding macroKey;
    private static int previousSlot = -1;
    private static int triggerBotTicks = 0;

    @Override
    public void onInitializeClient() {
        AutoSpearSwapConfig.load();
        MacroEngine.load();
        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("autospearswap", "main"));
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autospearswap.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));
        toggleAttributeSwapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autospearswap.toggle_attribute", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, category));
        swapToSpearKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autospearswap.swap_spear", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, category));
        triggerBotKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autospearswap.trigger_bot", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, category));
        macroKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autospearswap.macro", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F8, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PvpHud.tick(client);
            MacroEngine.tick(client);
            while (macroKey.wasPressed()) MacroEngine.toggle();
            while (triggerBotKey.wasPressed()) {
                AutoSpearSwapConfig.triggerBotEnabled = !AutoSpearSwapConfig.triggerBotEnabled;
                triggerBotTicks = 0;
                AutoSpearSwapConfig.save();
            }
            if (AutoSpearSwapConfig.triggerBotEnabled && client.player != null && client.world != null
                && client.currentScreen == null && client.crosshairTarget instanceof EntityHitResult hit) {
                Entity target = hit.getEntity();
                if (target instanceof PlayerEntity targetPlayer && targetPlayer != client.player
                    && targetPlayer.isAlive() && !targetPlayer.isSpectator()) {
                    // Wait for vanilla's attack cooldown to be fully charged. The server
                    // remains authoritative and can still reject an attack if its own
                    // cooldown or combat rules do not permit it.
                    if (client.player.getAttackCooldownProgress(0.5F) >= 1.0F) {
                        ((MinecraftClientAccessor) client).autospearswap$invokeDoAttack();
                    }
                } else triggerBotTicks = 0;
            } else triggerBotTicks = 0;
            if (client.player != null && AutoSpearSwapConfig.autoSprint && client.options.forwardKey.isPressed()
                && !client.player.isSneaking() && !client.player.hasVehicle()) {
                client.player.setSprinting(true);
            }
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
