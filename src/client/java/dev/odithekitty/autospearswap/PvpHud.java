package dev.odithekitty.autospearswap;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
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
        int x = 7;
        int y = 7;
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
