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
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.screen.slot.SlotActionType;
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
    public static KeyBinding autoWalkKey;
    private static int originalFov = -1;
    private static boolean wasZooming;
    private static double originalGamma = -1.0;
    private static int autoEatPreviousSlot = -1;
    private static int autoEatCooldown;
    private static boolean autoWalkLatched;
    private static boolean sneakLatched;
    private static int autoJumpCooldown;
    private static int autoSwimCooldown;
    private static int previousSlot = -1;
    private static int triggerBotTicks = 0;
    private static int lastHurtTime = 0;
    private static int jumpResetCooldown = 0;
    private static final Item[] LAST_HOTBAR_ITEMS = new Item[9];

    @Override
    public void onInitializeClient() {
        AutoSpearSwapConfig.load();
        autoWalkLatched = AutoSpearSwapConfig.autoWalk;
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
        autoWalkKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autospearswap.auto_walk", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PvpHud.tick(client);
            SpotifyService.pollIfNeeded();
            while (autoWalkKey.wasPressed()) {
                autoWalkLatched = !autoWalkLatched;
                AutoSpearSwapConfig.autoWalk = autoWalkLatched;
                if (!autoWalkLatched && client.options != null) client.options.forwardKey.setPressed(false);
                AutoSpearSwapConfig.save();
            }
            if (AutoSpearSwapConfig.autoWalk && client.options != null) {
                client.options.forwardKey.setPressed(client.player != null && client.currentScreen == null);
            }
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
                if (client.currentScreen == null && client.interactionManager != null) {
                    if (AutoSpearSwapConfig.autoArmor
                        && client.player.currentScreenHandler.getCursorStack().isEmpty()) {
                        EquipmentSlot[] armorSlots = {
                            EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                            EquipmentSlot.LEGS, EquipmentSlot.FEET
                        };
                        int[] handlerSlots = {5, 6, 7, 8};
                        for (int gearIndex = 0; gearIndex < armorSlots.length; gearIndex++) {
                            EquipmentSlot equipmentSlot = armorSlots[gearIndex];
                            if (!client.player.getEquippedStack(equipmentSlot).isEmpty()) continue;
                            boolean equipped = false;
                            for (int inventory = 9; inventory < 36; inventory++) {
                                var candidate = client.player.getInventory().getStack(inventory);
                                EquippableComponent equippable = candidate.get(DataComponentTypes.EQUIPPABLE);
                                if (equippable != null && equippable.slot() == equipmentSlot) {
                                    client.interactionManager.clickSlot(
                                        client.player.currentScreenHandler.syncId,
                                        inventory, 0, SlotActionType.PICKUP, client.player);
                                    client.interactionManager.clickSlot(
                                        client.player.currentScreenHandler.syncId,
                                        handlerSlots[gearIndex], 0, SlotActionType.PICKUP, client.player);
                                    equipped = true;
                                    break;
                                }
                            }
                            if (!equipped) {
                                for (int hotbar = 0; hotbar < 9; hotbar++) {
                                    var candidate = client.player.getInventory().getStack(hotbar);
                                    EquippableComponent equippable = candidate.get(DataComponentTypes.EQUIPPABLE);
                                    if (equippable != null && equippable.slot() == equipmentSlot) {
                                        client.interactionManager.clickSlot(
                                            client.player.currentScreenHandler.syncId,
                                            handlerSlots[gearIndex], hotbar, SlotActionType.SWAP, client.player);
                                        equipped = true;
                                        break;
                                    }
                                }
                            }
                            if (equipped) break;
                        }
                    }
                    if (AutoSpearSwapConfig.autoTotem
                        && !client.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                        int totemSlot = -1;
                        for (int slot = 0; slot < 36; slot++) {
                            if (client.player.getInventory().getStack(slot).isOf(Items.TOTEM_OF_UNDYING)) {
                                totemSlot = slot;
                                break;
                            }
                        }
                        if (totemSlot >= 0) {
                            int handlerSlot = totemSlot < 9 ? totemSlot + 36 : totemSlot;
                            client.interactionManager.clickSlot(
                                client.player.currentScreenHandler.syncId,
                                handlerSlot, 40, SlotActionType.SWAP, client.player);
                        }
                    }
                    if (AutoSpearSwapConfig.autoRefill
                        && client.player.currentScreenHandler.getCursorStack().isEmpty()) {
                        for (int hotbar = 0; hotbar < 9; hotbar++) {
                            var hotbarStack = client.player.getInventory().getStack(hotbar);
                            if (!hotbarStack.isEmpty()) {
                                LAST_HOTBAR_ITEMS[hotbar] = hotbarStack.getItem();
                                continue;
                            }
                            Item previousItem = LAST_HOTBAR_ITEMS[hotbar];
                            if (previousItem == null) continue;
                            for (int inventory = 9; inventory < 36; inventory++) {
                                var candidate = client.player.getInventory().getStack(inventory);
                                if (!candidate.isEmpty() && candidate.isOf(previousItem)) {
                                    client.interactionManager.clickSlot(
                                        client.player.currentScreenHandler.syncId,
                                        inventory, 0, SlotActionType.PICKUP, client.player);
                                    client.interactionManager.clickSlot(
                                        client.player.currentScreenHandler.syncId,
                                        hotbar + 36, 0, SlotActionType.PICKUP, client.player);
                                    if (!client.player.currentScreenHandler.getCursorStack().isEmpty()) {
                                        client.interactionManager.clickSlot(
                                            client.player.currentScreenHandler.syncId,
                                            inventory, 0, SlotActionType.PICKUP, client.player);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                if (jumpResetCooldown > 0) jumpResetCooldown--;
                if (autoJumpCooldown > 0) autoJumpCooldown--;
                if (autoSwimCooldown > 0) autoSwimCooldown--;
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
                if (AutoSpearSwapConfig.autoSwim && client.options.forwardKey.isPressed()
                    && client.player.isTouchingWater() && !client.player.hasVehicle()
                    && client.currentScreen == null && autoSwimCooldown == 0) {
                    client.player.jump();
                    autoSwimCooldown = 3;
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
