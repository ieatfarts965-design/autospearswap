package dev.odithekitty.autospearswap;

import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/** Real, data-backed world and player readouts exposed as independent client modules. */
public final class NexusModuleRegistry {
    public record Module(String id, String name, String category, String description) {}
    private static final List<Module> MODULES = List.of(
        new Module("world_time", "World Time", "World", "Shows the in-game clock"),
        new Module("world_day", "World Day", "World", "Shows the current world day"),
        new Module("dimension", "Dimension", "World", "Shows the current dimension"),
        new Module("light_level", "Light Level", "World", "Shows block light at your feet"),
        new Module("chunk_coords", "Chunk Coordinates", "World", "Shows chunk X and Z"),
        new Module("block_coords", "Block Coordinates", "World", "Shows exact block position"),
        new Module("rotation", "Rotation", "Player", "Shows yaw and pitch"),
        new Module("velocity", "Velocity", "Movement", "Shows horizontal and vertical velocity"),
        new Module("fall_distance", "Fall Distance", "Movement", "Shows current fall distance"),
        new Module("air_supply", "Air Supply", "Player", "Shows remaining air"),
        new Module("experience", "Experience", "Player", "Shows level and progress"),
        new Module("absorption", "Absorption", "Player", "Shows absorption hearts"),
        new Module("armor_points", "Armor Points", "Player", "Shows armor points"),
        new Module("player_state", "Player State", "Movement", "Shows ground, water, or air state"),
        new Module("weather", "Weather", "World", "Shows current weather state"),
        new Module("saturation", "Saturation", "Player", "Shows food saturation"),
        new Module("hotbar_slot", "Selected Slot", "Inventory", "Shows selected hotbar slot"),
        new Module("world_age", "World Age", "World", "Shows total world time in days and ticks"),
        new Module("nether_coords", "Nether Coordinates", "World", "Converts coordinates between Overworld and Nether"),
        new Module("entity_target_type", "Target Type", "Combat", "Shows the type of entity under your crosshair")
    );

    private NexusModuleRegistry() {}
    public static List<Module> modules() { return MODULES; }
    public static boolean enabled(String id) { return AutoSpearSwapConfig.extraModules.getOrDefault(id, false); }
    public static void toggle(String id) { AutoSpearSwapConfig.extraModules.put(id, !enabled(id)); }

    public static void render(DrawContext ctx, MinecraftClient client, int x, int y, int color) {
        PlayerEntity p = client.player;
        if (p == null || client.world == null) return;
        BlockPos pos = p.getBlockPos();
        for (Module module : MODULES) {
            if (!enabled(module.id())) continue;
            String value = value(module.id(), client, p, pos);
            if (value != null) {
                ctx.drawTextWithShadow(client.textRenderer, module.name().toUpperCase() + "  " + value, x, y, color);
                y += 11;
            }
        }
    }

    private static String value(String id, MinecraftClient c, PlayerEntity p, BlockPos pos) {
        return switch (id) {
            case "world_time" -> {
                long t = Math.floorMod(c.world.getTimeOfDay(), 24000L);
                int hour = (int)((t / 1000 + 6) % 24);
                int minute = (int)((t % 1000) * 60 / 1000);
                yield String.format("%02d:%02d", hour, minute);
            }
            case "world_day" -> "Day " + (c.world.getTime() / 24000L + 1);
            case "dimension" -> c.world.getRegistryKey().getValue().getPath();
            case "light_level" -> Integer.toString(c.world.getLightLevel(pos));
            case "chunk_coords" -> (pos.getX() >> 4) + ", " + (pos.getZ() >> 4);
            case "block_coords" -> pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
            case "rotation" -> String.format("Yaw %.1f  Pitch %.1f", p.getYaw(), p.getPitch());
            case "velocity" -> String.format("H %.3f  V %.3f", Math.sqrt(p.getVelocity().x * p.getVelocity().x + p.getVelocity().z * p.getVelocity().z), p.getVelocity().y);
            case "fall_distance" -> String.format("%.1f blocks", p.fallDistance);
            case "air_supply" -> p.getAir() + " / " + p.getMaxAir();
            case "experience" -> "Lv " + p.experienceLevel + "  " + Math.round(p.experienceProgress * 100) + "%";
            case "absorption" -> String.format("%.1f hearts", p.getAbsorptionAmount() / 2.0f);
            case "armor_points" -> p.getArmor() + " / 20";
            case "player_state" -> p.isTouchingWater() ? "WATER" : p.isOnGround() ? "GROUNDED" : "AIRBORNE";
            case "weather" -> c.world.isThundering() ? "THUNDER" : c.world.isRaining() ? "RAIN" : "CLEAR";
            case "saturation" -> String.format("%.1f", p.getHungerManager().getSaturationLevel());
            case "hotbar_slot" -> "Slot " + (p.getInventory().getSelectedSlot() + 1);
            case "world_age" -> c.world.getTime() + " ticks";
            case "nether_coords" -> {
                String dim = c.world.getRegistryKey().getValue().getPath();
                if ("overworld".equals(dim)) yield String.format("Nether %.1f, %.1f", p.getX() / 8.0, p.getZ() / 8.0);
                if ("the_nether".equals(dim)) yield String.format("Overworld %.1f, %.1f", p.getX() * 8.0, p.getZ() * 8.0);
                yield "N/A";
            }
            case "entity_target_type" -> c.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult hit
                ? hit.getEntity().getType().getName().getString() : "None";
            default -> null;
        };
    }
}
