package dev.odithekitty.autospearswap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.util.Hand;
import dev.odithekitty.autospearswap.mixin.MinecraftClientAccessor;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class AutoSpearSwapClient implements ClientModInitializer {
    public static KeyBinding openMenuKey;
    public static KeyBinding toggleAttributeSwapKey;
    public static KeyBinding swapToSpearKey;
    public static KeyBinding triggerBotKey;
    public static KeyBinding macroKey;
    public static KeyBinding toggleSneakKey;
    public static KeyBinding zoomKey;
    private static int originalFov = -1;
    private static boolean wasZooming;
    private static double originalGamma = -1.0;
    private static int autoEatPreviousSlot = -1;
    private static int autoEatCooldown;
    private static boolean sneakLatched;
    private static int autoJumpCooldown;
    private static int previousSlot = -1;
    private static int triggerBotTicks = 0;
    private static int lastHurtTime = 0;
    private static int jumpResetCooldown = 0;

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
        toggleSneakKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autospearswap.toggle_sneak", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, category));
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autospearswap.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PvpHud.tick(client);
            if (originalGamma < 0.0) originalGamma = client.options.getGamma().getValue();
            client.options.getGamma().setValue(AutoSpearSwapConfig.fullbright ? 1.0 : originalGamma);
            boolean zooming = AutoSpearSwapConfig.zoomEnabled && zoomKey.isPressed()
                && client.currentScreen == null;
            if (zooming && !wasZooming) originalFov = client.options.getFov().getValue();
            if (zooming) client.options.getFov().setValue(30);
            else if (wasZooming && originalFov >= 0) {
                client.options.getFov().setValue(originalFov);
                originalFov = -1;
            }
            wasZooming = zooming;
            if (AutoSpearSwapConfig.autoRespawn && client.player != null
                && client.player.isDead()) {
                client.player.requestRespawn();
            }
            if (client.player == null || client.world == null) {
                lastHurtTime = 0;
                jumpResetCooldown = 0;
                autoJumpCooldown = 0;
                sneakLatched = false;
            } else {
                if (jumpResetCooldown > 0) jumpResetCooldown--;
                if (autoJumpCooldown > 0) autoJumpCooldown--;
                if (autoEatCooldown > 0) autoEatCooldown--;
                if (!AutoSpearSwapConfig.autoEat) {
                    if (autoEatPreviousSlot >= 0) {
                        client.player.getInventory().setSelectedSlot(autoEatPreviousSlot);
                        autoEatPreviousSlot = -1;
                    }
                } else if (client.currentScreen == null && !client.player.isUsingItem()) {
                    int food = client.player.getHungerManager().getFoodLevel();
                    if (food >= 19 && autoEatPreviousSlot >= 0) {
                        client.player.getInventory().setSelectedSlot(autoEatPreviousSlot);
                        autoEatPreviousSlot = -1;
                    } else if (food <= 14 && autoEatCooldown == 0 && client.interactionManager != null) {
                        int selected = client.player.getInventory().getSelectedSlot();
                        int foodSlot = -1;
                        for (int slot = 0; slot < 9; slot++) {
                            if (client.player.getInventory().getStack(slot).contains(DataComponentTypes.FOOD)) {
                                foodSlot = slot;
                                break;
                            }
                        }
                        if (foodSlot >= 0) {
                            if (autoEatPreviousSlot < 0) autoEatPreviousSlot = selected;
                            client.player.getInventory().setSelectedSlot(foodSlot);
                            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
                            autoEatCooldown = 20;
                        }
                    }
                }
                while (toggleSneakKey.wasPressed()) sneakLatched = !sneakLatched;
                client.player.setSneaking(AutoSpearSwapConfig.toggleSneak && sneakLatched);
                if (!AutoSpearSwapConfig.toggleSneak) sneakLatched = false;
                if (AutoSpearSwapConfig.autoJump && client.options.forwardKey.isPressed()
                    && client.player.isOnGround() && !client.player.isSneaking()
                    && !client.player.hasVehicle() && client.currentScreen == null && autoJumpCooldown == 0) {
                    client.player.jump();
                    autoJumpCooldown = 2;
                }
                if (AutoSpearSwapConfig.autoTool && client.options.attackKey.isPressed()
                    && client.currentScreen == null && client.crosshairTarget instanceof BlockHitResult blockHit) {
                    BlockState state = client.world.getBlockState(blockHit.getBlockPos());
                    int bestSlot = client.player.getInventory().getSelectedSlot();
                    float bestSpeed = client.player.getInventory().getStack(bestSlot).getMiningSpeedMultiplier(state);
                    for (int slot = 0; slot < 9; slot++) {
                        float speed = client.player.getInventory().getStack(slot).getMiningSpeedMultiplier(state);
                        if (speed > bestSpeed) {
                            bestSpeed = speed;
                            bestSlot = slot;
                        }
                    }
                    client.player.getInventory().setSelectedSlot(bestSlot);
                }
                int hurt = client.player.hurtTime;
                if (AutoSpearSwapConfig.jumpResetEnabled && hurt > lastHurtTime
                    && client.player.isOnGround() && !client.player.isSneaking()
                    && !client.player.hasVehicle() && jumpResetCooldown == 0
                    && client.currentScreen == null) {
                    client.player.jump();
                    jumpResetCooldown = 6;
                }
                lastHurtTime = hurt;
            }
            MacroEngine.tick(client);
            while (macroKey.wasPressed()) MacroEngine.toggle();
            if (AutoSpearSwapConfig.triggerBotHoldMode) {
                boolean held = triggerBotKey.isPressed();
                if (AutoSpearSwapConfig.triggerBotEnabled != held) {
                    AutoSpearSwapConfig.triggerBotEnabled = held;
                    triggerBotTicks = 0;
                    AutoSpearSwapConfig.save();
                }
            } else while (triggerBotKey.wasPressed()) {
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
