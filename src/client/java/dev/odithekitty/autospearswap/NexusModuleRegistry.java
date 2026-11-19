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
        new Module("entity_target_type", "Target Type", "Combat", "Shows the type of entity under your crosshair"),
        new Module("player_name", "Player Name", "Player", "Displays your username"),
        new Module("attack_charge", "Attack Charge", "Combat", "Shows vanilla attack cooldown"),
        new Module("sprint_state", "Sprint State", "Movement", "Shows sprint status"),
        new Module("swim_state", "Swim State", "Movement", "Shows swimming status"),
        new Module("climb_state", "Climb State", "Movement", "Shows climbing status"),
        new Module("glide_state", "Glide State", "Movement", "Shows elytra gliding status"),
        new Module("vehicle_state", "Vehicle State", "Movement", "Shows whether you are riding"),
        new Module("fire_ticks", "Fire Timer", "Player", "Shows remaining fire ticks"),
        new Module("wet_state", "Wet Status", "Player", "Shows wet or dry status"),
        new Module("inventory_empty", "Empty Slots", "Inventory", "Counts empty inventory slots"),
        new Module("inventory_used", "Used Slots", "Inventory", "Counts occupied inventory slots"),
        new Module("selected_count", "Held Stack", "Inventory", "Shows selected stack count"),
        new Module("totem_count", "Totems", "Inventory", "Counts totems in inventory"),
        new Module("arrow_count", "Arrows", "Inventory", "Counts arrows in inventory"),
        new Module("golden_apple_count", "Golden Apples", "Inventory", "Counts golden apples in inventory"),
        new Module("offhand_item", "Offhand Item", "Inventory", "Shows offhand item name"),
        new Module("target_block", "Target Block", "World", "Shows the block under your crosshair"),
        new Module("target_block_coords", "Target Block Position", "World", "Shows target block coordinates"),
        new Module("target_entity_distance", "Target Entity Range", "Combat", "Shows distance to crosshair entity"),
        new Module("spawn_distance", "Spawn Distance", "World", "Shows distance from world spawn"),
        new Module("health_percent", "Health Percent", "Player", "Displays health as a percentage"),
        new Module("main_hand_durability", "Held Durability", "Inventory", "Shows remaining durability of held item"),
        new Module("offhand_durability", "Offhand Durability", "Inventory", "Shows remaining durability of offhand item"),
        new Module("head_durability", "Helmet Durability", "Inventory", "Shows remaining helmet durability"),
        new Module("chest_durability", "Chestplate Durability", "Inventory", "Shows remaining chestplate durability"),
        new Module("legs_durability", "Leggings Durability", "Inventory", "Shows remaining leggings durability"),
        new Module("boots_durability", "Boots Durability", "Inventory", "Shows remaining boots durability"),
        new Module("ender_pearls", "Ender Pearls", "Inventory", "Counts ender pearls"),
        new Module("firework_rockets", "Firework Rockets", "Inventory", "Counts firework rockets"),
        new Module("obsidian", "Obsidian", "Inventory", "Counts obsidian blocks"),
        new Module("end_crystals", "End Crystals", "Inventory", "Counts end crystals"),
        new Module("cobwebs", "Cobwebs", "Inventory", "Counts cobwebs"),
        new Module("golden_carrots", "Golden Carrots", "Inventory", "Counts golden carrots"),
        new Module("effect_count", "Active Effects", "Player", "Counts active potion effects"),
        new Module("player_x", "X Position", "World", "Shows precise X position"),
        new Module("player_y", "Y Position", "World", "Shows precise Y position"),
        new Module("player_z", "Z Position", "World", "Shows precise Z position"),
        new Module("chunk_section", "Chunk Section", "World", "Shows the vertical chunk section"),
        new Module("target_entity_name", "Target Name", "Combat", "Shows the crosshair entity name"),
        new Module("target_entity_health", "Target Health", "Combat", "Shows crosshair entity health")
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

    private static String durability(net.minecraft.item.ItemStack stack) {
        if (stack.isEmpty() || !stack.isDamageable()) return "N/A";
        int remaining = Math.max(0, stack.getMaxDamage() - stack.getDamage());
        return Math.round(remaining * 100.0f / stack.getMaxDamage()) + "%";
    }

    private static int countItem(PlayerEntity player, net.minecraft.item.Item item) {
        int total = 0;
        for (int i = 0; i < 36; i++) {
            var stack = player.getInventory().getStack(i);
            if (stack.isOf(item)) total += stack.getCount();
        }
        if (player.getOffHandStack().isOf(item)) total += player.getOffHandStack().getCount();
        return total;
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
            case "player_name" -> p.getName().getString();
            case "attack_charge" -> Math.round(p.getAttackCooldownProgress(0.5f) * 100) + "%";
            case "sprint_state" -> p.isSprinting() ? "SPRINTING" : "WALKING";
            case "swim_state" -> p.isSwimming() ? "SWIMMING" : "NOT SWIMMING";
            case "climb_state" -> p.isClimbing() ? "CLIMBING" : "NOT CLIMBING";
            case "glide_state" -> p.isGliding() ? "GLIDING" : "NOT GLIDING";
            case "vehicle_state" -> p.hasVehicle() ? p.getVehicle().getName().getString() : "ON FOOT";
            case "fire_ticks" -> p.getFireTicks() + " ticks";
            case "wet_state" -> p.isTouchingWaterOrRain() ? "WET" : "DRY";
            case "inventory_empty" -> {
                int empty = 0;
                for (int i = 0; i < 36; i++) if (p.getInventory().getStack(i).isEmpty()) empty++;
                yield empty + " slots";
            }
            case "inventory_used" -> {
                int used = 0;
                for (int i = 0; i < 36; i++) if (!p.getInventory().getStack(i).isEmpty()) used++;
                yield used + " / 36 slots";
            }
            case "selected_count" -> p.getMainHandStack().isEmpty() ? "0" : p.getMainHandStack().getCount() + " items";
            case "totem_count" -> countItem(p, net.minecraft.item.Items.TOTEM_OF_UNDYING) + "";
            case "arrow_count" -> (countItem(p, net.minecraft.item.Items.ARROW)
                + countItem(p, net.minecraft.item.Items.SPECTRAL_ARROW)
                + countItem(p, net.minecraft.item.Items.TIPPED_ARROW)) + "";
            case "golden_apple_count" -> (countItem(p, net.minecraft.item.Items.GOLDEN_APPLE)
                + countItem(p, net.minecraft.item.Items.ENCHANTED_GOLDEN_APPLE)) + "";
            case "offhand_item" -> p.getOffHandStack().isEmpty() ? "Empty" : p.getOffHandStack().getName().getString();
            case "target_block" -> c.crosshairTarget instanceof net.minecraft.util.hit.BlockHitResult block
                ? c.world.getBlockState(block.getBlockPos()).getBlock().getName().getString() : "None";
            case "target_block_coords" -> c.crosshairTarget instanceof net.minecraft.util.hit.BlockHitResult block
                ? block.getBlockPos().getX() + ", " + block.getBlockPos().getY() + ", " + block.getBlockPos().getZ() : "None";
            case "target_entity_distance" -> c.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult hit
                ? String.format("%.1f blocks", hit.getEntity().distanceTo(p)) : "None";
            case "spawn_distance" -> String.format("%.1f blocks", Math.sqrt(pos.getSquaredDistance(c.world.getSpawnPoint().getPos())));
            case "health_percent" -> Math.round(p.getHealth() * 100.0f / Math.max(1.0f, p.getMaxHealth())) + "%";
            case "main_hand_durability" -> durability(p.getMainHandStack());
            case "offhand_durability" -> durability(p.getOffHandStack());
            case "head_durability" -> durability(p.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD));
            case "chest_durability" -> durability(p.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST));
            case "legs_durability" -> durability(p.getEquippedStack(net.minecraft.entity.EquipmentSlot.LEGS));
            case "boots_durability" -> durability(p.getEquippedStack(net.minecraft.entity.EquipmentSlot.FEET));
            case "ender_pearls" -> Integer.toString(countItem(p, net.minecraft.item.Items.ENDER_PEARL));
            case "firework_rockets" -> Integer.toString(countItem(p, net.minecraft.item.Items.FIREWORK_ROCKET));
            case "obsidian" -> Integer.toString(countItem(p, net.minecraft.item.Items.OBSIDIAN));
            case "end_crystals" -> Integer.toString(countItem(p, net.minecraft.item.Items.END_CRYSTAL));
            case "cobwebs" -> Integer.toString(countItem(p, net.minecraft.item.Items.COBWEB));
            case "golden_carrots" -> Integer.toString(countItem(p, net.minecraft.item.Items.GOLDEN_CARROT));
            case "effect_count" -> p.getStatusEffects().size() + " active";
            case "player_x" -> String.format("%.2f", p.getX());
            case "player_y" -> String.format("%.2f", p.getY());
            case "player_z" -> String.format("%.2f", p.getZ());
            case "chunk_section" -> "Y " + (pos.getY() >> 4);
            case "target_entity_name" -> c.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult hit
                ? hit.getEntity().getName().getString() : "None";
            case "target_entity_health" -> c.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult hit
                && hit.getEntity() instanceof net.minecraft.entity.LivingEntity living
                ? String.format("%.1f / %.1f HP", living.getHealth(), living.getMaxHealth()) : "None";
            default -> null;
        };
    }
}
