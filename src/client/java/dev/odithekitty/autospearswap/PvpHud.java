package dev.odithekitty.autospearswap;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public final class PvpHud {
    private static final Deque<Long> LEFT_CLICKS = new ArrayDeque<>();
    private static final Deque<Long> RIGHT_CLICKS = new ArrayDeque<>();
    private static boolean wasLeftDown;
    private static boolean wasRightDown;
    private static long lastCombatAt;

    private PvpHud() {}

    public static void tick(MinecraftClient client) {
        if (client.player == null || client.options == null) {
            wasLeftDown = false;
            wasRightDown = false;
            return;
        }
        long now = System.currentTimeMillis();
        boolean left = client.options.attackKey.isPressed();
        boolean right = client.options.useKey.isPressed();
        if (left && !wasLeftDown) {
            LEFT_CLICKS.addLast(now);
            lastCombatAt = now;
        }
        if (right && !wasRightDown) RIGHT_CLICKS.addLast(now);
        wasLeftDown = left;
        wasRightDown = right;
        trim(LEFT_CLICKS, now);
        trim(RIGHT_CLICKS, now);
    }

    private static void trim(Deque<Long> clicks, long now) {
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000L) clicks.removeFirst();
    }

    public static void render(DrawContext context, MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null || client.textRenderer == null) return;
        if (AutoSpearSwapConfig.hudSpotify) {
            SpotifyService.pollIfNeeded();
            int right = context.getScaledWindowWidth() - 10;
            int cardWidth = 218;
            int left = right - cardWidth;
            context.fill(left, 8, right, 49, 0xCC160B11);
            context.fill(left, 8, right, 10, 0xFFE04A5B);
            context.drawTextWithShadow(client.textRenderer, Text.literal("NOW PLAYING  /  SPOTIFY"), left + 9, 14, 0xFFF2DDE1);
            String track = client.textRenderer.trimToWidth(SpotifyService.currentTrack(), cardWidth - 18);
            String artist = client.textRenderer.trimToWidth(SpotifyService.currentArtist(), cardWidth - 18);
            context.drawTextWithShadow(client.textRenderer, Text.literal(track), left + 9, 25, 0xFFFFFFFF);
            context.drawTextWithShadow(client.textRenderer, Text.literal(artist), left + 9, 37, 0xFFCCB9BE);
        }
        int x = AutoSpearSwapConfig.hudX;
        int y = AutoSpearSwapConfig.hudY;
        int color = 0xFFEAF6FF;
        if (AutoSpearSwapConfig.hudCoordinates) {
            draw(context, client, Text.literal(String.format("XYZ  %.1f  %.1f  %.1f", player.getX(), player.getY(), player.getZ())), x, y, color);
            y += 11;
        }
        if (AutoSpearSwapConfig.hudCps) {
            draw(context, client, Text.literal("CPS  L " + LEFT_CLICKS.size() + "  R " + RIGHT_CLICKS.size()), x, y, color);
            y += 11;
        }
        if (AutoSpearSwapConfig.hudArmor) {
            StringBuilder line = new StringBuilder("ARMOR ");
            ItemStack[] armor = {
                player.getEquippedStack(EquipmentSlot.HEAD),
                player.getEquippedStack(EquipmentSlot.CHEST),
                player.getEquippedStack(EquipmentSlot.LEGS),
                player.getEquippedStack(EquipmentSlot.FEET)
            };
            for (ItemStack stack : armor) {
                if (stack.isEmpty()) line.append("-- ");
                else if (stack.isDamageable()) {
                    int remaining = Math.max(0, stack.getMaxDamage() - stack.getDamage());
                    int percent = Math.round(remaining * 100.0f / stack.getMaxDamage());
                    line.append(percent).append("% ");
                } else line.append("OK ");
            }
            draw(context, client, Text.literal(line.toString().trim()), x, y, color);
            y += 11;
        }
        if (AutoSpearSwapConfig.hudCombatTimer && lastCombatAt > 0L) {
            long seconds = (System.currentTimeMillis() - lastCombatAt) / 1000L;
            draw(context, client, Text.literal("COMBAT  " + seconds + "s"), x, y, color);
            y += 11;
        }
        if (AutoSpearSwapConfig.hudKeystrokes) {
            drawKey(context, client, "W", client.options.forwardKey.isPressed(), x + 18, y, color);
            drawKey(context, client, "A", client.options.leftKey.isPressed(), x, y + 13, color);
            drawKey(context, client, "S", client.options.backKey.isPressed(), x + 18, y + 13, color);
            drawKey(context, client, "D", client.options.rightKey.isPressed(), x + 36, y + 13, color);
            drawKey(context, client, "LMB", client.options.attackKey.isPressed(), x, y + 26, color);
            drawKey(context, client, "RMB", client.options.useKey.isPressed(), x + 28, y + 26, color);
        }
        if (AutoSpearSwapConfig.hudSpeed) {
            double horizontal = Math.sqrt(player.getVelocity().x * player.getVelocity().x + player.getVelocity().z * player.getVelocity().z) * 20.0;
            draw(context, client, Text.literal(String.format("SPEED  %.2f m/s", horizontal)), x, y, color);
            y += 11;
        }
        if (AutoSpearSwapConfig.hudDirection) {
            String[] directions = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
            int index = Math.floorMod(Math.round(player.getYaw() / 45.0f), 8);
            draw(context, client, Text.literal("FACING  " + directions[index]), x, y, color);
            y += 11;
        }
        if (AutoSpearSwapConfig.hudBiome && client.world != null) {
            String biome = client.world.getBiome(player.getBlockPos()).getKey().map(k -> k.getValue().getPath()).orElse("unknown");
            draw(context, client, Text.literal("BIOME  " + biome), x, y, color);
            y += 11;
        }
        if (AutoSpearSwapConfig.hudPing && client.getNetworkHandler() != null) {
            var entry = client.getNetworkHandler().getPlayerListEntry(player.getUuid());
            if (entry != null) {
                draw(context, client, Text.literal("PING  " + entry.getLatency() + " ms"), x, y, color);
                y += 11;
            }
        }
        if (AutoSpearSwapConfig.hudHealth) {
            draw(context, client, Text.literal(String.format("HEALTH  %.1f / %.1f", player.getHealth(), player.getMaxHealth())), x, y, color);
            y += 11;
        }
        if (AutoSpearSwapConfig.hudFood) {
            draw(context, client, Text.literal("FOOD  " + player.getHungerManager().getFoodLevel() + "/20"), x, y, color);
            y += 11;
        }
        if (AutoSpearSwapConfig.hudHeldItem) {
            String held = player.getMainHandStack().isEmpty() ? "Empty" : player.getMainHandStack().getName().getString();
            draw(context, client, Text.literal("HELD  " + held), x, y, color);
            y += 11;
        }
        if (AutoSpearSwapConfig.hudEffects && !player.getStatusEffects().isEmpty()) {
            String effects = player.getStatusEffects().stream().map(e -> e.getEffectType().value().getName().getString()).limit(3).collect(java.util.stream.Collectors.joining(", "));
            draw(context, client, Text.literal("EFFECTS  " + effects), x, y, color);
            y += 11;
        }
        if (AutoSpearSwapConfig.hudFps) {
            draw(context, client, Text.literal("FPS  " + client.getCurrentFps()), x, y, color);
            y += 11;
        }
        if (AutoSpearSwapConfig.hudClock) {
            draw(context, client, Text.literal("TIME  " + java.time.LocalTime.now().withNano(0)), x, y, color);
            y += 11;
        }
        if (AutoSpearSwapConfig.hudTarget && client.crosshairTarget instanceof EntityHitResult hit
            && hit.getEntity() instanceof LivingEntity target && target != player) {
            String targetName = target.getName().getString();
            String targetHealth = String.format("%.1f HP", target.getHealth());
            String targetDistance = String.format("%.1f m", target.distanceTo(player));
            draw(context, client, Text.literal("TARGET  " + targetName + "  •  " + targetHealth + "  •  " + targetDistance), x, y, 0xFF65D6FF);
            y += 11;
        }
        if (AutoSpearSwapConfig.hudDurability) {
            ItemStack[] watched = {
                player.getMainHandStack(),
                player.getEquippedStack(EquipmentSlot.HEAD),
                player.getEquippedStack(EquipmentSlot.CHEST),
                player.getEquippedStack(EquipmentSlot.LEGS),
                player.getEquippedStack(EquipmentSlot.FEET)
            };
            String[] slots = {"Held item", "Helmet", "Chestplate", "Leggings", "Boots"};
            for (int i = 0; i < watched.length; i++) {
                ItemStack stack = watched[i];
                if (stack.isDamageable()) {
                    int remaining = stack.getMaxDamage() - stack.getDamage();
                    int percent = Math.round(remaining * 100.0f / stack.getMaxDamage());
                    if (percent <= 20) {
                        draw(context, client, Text.literal("LOW DURABILITY  " + slots[i] + "  " + percent + "%"), x, y, 0xFFFF7777);
                        y += 11;
                    }
                }
            }
        }
        NexusModuleRegistry.render(context, client, x, y, color);
        if (AutoSpearSwapConfig.lowHealthWarning && player.getHealth() <= 6.0f) {
            String warning = "LOW HEALTH";
            int width = client.textRenderer.getWidth(warning);
            context.drawTextWithShadow(client.textRenderer, warning, (context.getScaledWindowWidth() - width) / 2,
                context.getScaledWindowHeight() / 2 + 28, 0xFFFF5555);
        }
    }

    private static void drawKey(DrawContext context, MinecraftClient client, String label, boolean down, int x, int y, int color) {
        int width = Math.max(18, client.textRenderer.getWidth(label) + 8);
        int background = down ? 0xCC1689D5 : 0xAA101A25;
        context.fill(x, y, x + width, y + 11, background);
        context.drawCenteredTextWithShadow(client.textRenderer, label, x + width / 2, y + 2, down ? 0xFFFFFFFF : color);
    }

    private static void draw(DrawContext context, MinecraftClient client, Text text, int x, int y, int color) {
        context.drawTextWithShadow(client.textRenderer, text, x, y, color);
    }
}
