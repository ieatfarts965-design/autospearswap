package dev.odithekitty.autospearswap;

import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

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
        new Module("diamond_horse_armor", "Obsidian", "Inventory", "Counts obsidian blocks"),
        new Module("end_crystals", "End Crystals", "Inventory", "Counts end crystals"),
        new Module("cobwebs", "Cobwebs", "Inventory", "Counts cobwebs"),
        new Module("golden_carrots", "Golden Carrots", "Inventory", "Counts golden carrots"),
        new Module("effect_count", "Active Effects", "Player", "Counts active potion effects"),
        new Module("player_x", "X Position", "World", "Shows precise X position"),
        new Module("player_y", "Y Position", "World", "Shows precise Y position"),
        new Module("player_z", "Z Position", "World", "Shows precise Z position"),
        new Module("chunk_section", "Chunk Section", "World", "Shows the vertical chunk section"),
        new Module("target_entity_name", "Target Name", "Combat", "Shows the crosshair entity name"),
        new Module("target_entity_health", "Target Health", "Combat", "Shows crosshair entity health"),
        new Module("hunger_level", "Hunger Level", "Player", "Shows food points out of 20"),
        new Module("hunger_percent", "Hunger Percent", "Player", "Shows hunger as a percentage"),
        new Module("mainhand_count", "Main Hand Count", "Inventory", "Shows held stack count"),
        new Module("offhand_count", "Offhand Count", "Inventory", "Shows offhand stack count"),
        new Module("experience_progress", "XP Progress", "Player", "Shows progress to next level"),
        new Module("yaw_cardinal", "Cardinal Direction", "Player", "Shows north south east or west"),
        new Module("chunk_x", "Chunk X", "World", "Shows current chunk X coordinate"),
        new Module("chunk_z", "Chunk Z", "World", "Shows current chunk Z coordinate"),
        new Module("chunk_local_x", "Local Chunk X", "World", "Shows X position within current chunk"),
        new Module("chunk_local_z", "Local Chunk Z", "World", "Shows Z position within current chunk"),
        new Module("velocity_x", "Velocity X", "Movement", "Shows horizontal X velocity"),
        new Module("velocity_y", "Velocity Y", "Movement", "Shows vertical velocity"),
        new Module("velocity_z", "Velocity Z", "Movement", "Shows horizontal Z velocity"),
        new Module("target_block_distance", "Block Target Range", "World", "Shows distance to targeted block"),
        new Module("target_block_face", "Target Block Face", "World", "Shows targeted block face"),
        new Module("target_entity_is_player", "Target Is Player", "Combat", "Shows whether the target is a player"),
        new Module("target_entity_alive", "Target Alive", "Combat", "Shows whether target entity is alive"),
        new Module("target_entity_absorption", "Target Absorption", "Combat", "Shows target absorption hearts"),
        new Module("day_phase", "Day Phase", "World", "Shows sunrise daytime sunset or night"),
        new Module("air_percent", "Air Percent", "Player", "Shows remaining air as percentage"),
        new Module("item_acacia_boat", "Acacia Boat Count", "Inventory", "Counts acacia boat in your inventory"),
        new Module("item_acacia_chest_boat", "Acacia Chest Boat Count", "Inventory", "Counts acacia chest boat in your inventory"),
        new Module("item_apple", "Apple Count", "Inventory", "Counts apple in your inventory"),
        new Module("item_armor_stand", "Armor Stand Count", "Inventory", "Counts armor stand in your inventory"),
        new Module("item_baked_potato", "Baked Potato Count", "Inventory", "Counts baked potato in your inventory"),
        new Module("item_bamboo", "Bamboo Count", "Inventory", "Counts bamboo in your inventory"),
        new Module("item_barrel", "Barrel Count", "Inventory", "Counts barrel in your inventory"),
        new Module("item_beacon", "Beacon Count", "Inventory", "Counts beacon in your inventory"),
        new Module("item_beef", "Beef Count", "Inventory", "Counts beef in your inventory"),
        new Module("item_beetroot", "Beetroot Count", "Inventory", "Counts beetroot in your inventory"),
        new Module("item_beetroot_seeds", "Beetroot Seeds Count", "Inventory", "Counts beetroot seeds in your inventory"),
        new Module("item_beetroot_soup", "Beetroot Soup Count", "Inventory", "Counts beetroot soup in your inventory"),
        new Module("item_birch_boat", "Birch Boat Count", "Inventory", "Counts birch boat in your inventory"),
        new Module("item_blaze_powder", "Blaze Powder Count", "Inventory", "Counts blaze powder in your inventory"),
        new Module("item_blaze_rod", "Blaze Rod Count", "Inventory", "Counts blaze rod in your inventory"),
        new Module("item_blue_ice", "Blue Ice Count", "Inventory", "Counts blue ice in your inventory"),
        new Module("item_bone", "Bone Count", "Inventory", "Counts bone in your inventory"),
        new Module("item_bone_meal", "Bone Meal Count", "Inventory", "Counts bone meal in your inventory"),
        new Module("item_book", "Book Count", "Inventory", "Counts book in your inventory"),
        new Module("item_bookshelf", "Bookshelf Count", "Inventory", "Counts bookshelf in your inventory"),
        new Module("item_bow", "Bow Count", "Inventory", "Counts bow in your inventory"),
        new Module("item_bread", "Bread Count", "Inventory", "Counts bread in your inventory"),
        new Module("item_breeze_rod", "Breeze Rod Count", "Inventory", "Counts breeze rod in your inventory"),
        new Module("item_brick", "Brick Count", "Inventory", "Counts brick in your inventory"),
        new Module("item_bucket", "Bucket Count", "Inventory", "Counts bucket in your inventory"),
        new Module("item_bundle", "Bundle Count", "Inventory", "Counts bundle in your inventory"),
        new Module("item_cake", "Cake Count", "Inventory", "Counts cake in your inventory"),
        new Module("item_carrot", "Carrot Count", "Inventory", "Counts carrot in your inventory"),
        new Module("item_carrot_on_a_stick", "Carrot On A Stick Count", "Inventory", "Counts carrot on a stick in your inventory"),
        new Module("item_chain", "Chain Count", "Inventory", "Counts chain in your inventory"),
        new Module("item_charcoal", "Charcoal Count", "Inventory", "Counts charcoal in your inventory"),
        new Module("item_cherry_boat", "Cherry Boat Count", "Inventory", "Counts cherry boat in your inventory"),
        new Module("item_chest_minecart", "Chest Minecart Count", "Inventory", "Counts chest minecart in your inventory"),
        new Module("item_chicken", "Chicken Count", "Inventory", "Counts chicken in your inventory"),
        new Module("item_chorus_fruit", "Chorus Fruit Count", "Inventory", "Counts chorus fruit in your inventory"),
        new Module("item_clay_ball", "Clay Ball Count", "Inventory", "Counts clay ball in your inventory"),
        new Module("item_clock", "Clock Count", "Inventory", "Counts clock in your inventory"),
        new Module("item_coal", "Coal Count", "Inventory", "Counts coal in your inventory"),
        new Module("item_cobbled_deepslate", "Cobbled Deepslate Count", "Inventory", "Counts cobbled deepslate in your inventory"),
        new Module("item_cobblestone", "Cobblestone Count", "Inventory", "Counts cobblestone in your inventory"),
        new Module("item_cocoa_beans", "Cocoa Beans Count", "Inventory", "Counts cocoa beans in your inventory"),
        new Module("item_copper_ingot", "Copper Ingot Count", "Inventory", "Counts copper ingot in your inventory"),
        new Module("item_copper_nugget", "Copper Nugget Count", "Inventory", "Counts copper nugget in your inventory"),
        new Module("item_crossbow", "Crossbow Count", "Inventory", "Counts crossbow in your inventory"),
        new Module("item_crying_obsidian", "Crying Diamond Horse Armor Count", "Inventory", "Counts crying obsidian in your inventory"),
        new Module("item_diamond", "Diamond Count", "Inventory", "Counts diamond in your inventory"),
        new Module("item_diamond_axe", "Diamond Axe Count", "Inventory", "Counts diamond axe in your inventory"),
        new Module("item_diamond_block", "Diamond Block Count", "Inventory", "Counts diamond block in your inventory"),
        new Module("item_diamond_boots", "Diamond Boots Count", "Inventory", "Counts diamond boots in your inventory"),
        new Module("item_diamond_chestplate", "Diamond Chestplate Count", "Inventory", "Counts diamond chestplate in your inventory"),
        new Module("item_diamond_helmet", "Diamond Helmet Count", "Inventory", "Counts diamond helmet in your inventory"),
        new Module("item_diamond_hoe", "Diamond Hoe Count", "Inventory", "Counts diamond hoe in your inventory"),
        new Module("item_diamond_leggings", "Diamond Leggings Count", "Inventory", "Counts diamond leggings in your inventory"),
        new Module("item_diamond_pickaxe", "Diamond Pickaxe Count", "Inventory", "Counts diamond pickaxe in your inventory"),
        new Module("item_diamond_shovel", "Diamond Shovel Count", "Inventory", "Counts diamond shovel in your inventory"),
        new Module("item_diamond_sword", "Diamond Sword Count", "Inventory", "Counts diamond sword in your inventory"),
        new Module("item_dried_kelp", "Dried Kelp Count", "Inventory", "Counts dried kelp in your inventory"),
        new Module("item_egg", "Egg Count", "Inventory", "Counts egg in your inventory"),
        new Module("item_elytra", "Elytra Count", "Inventory", "Counts elytra in your inventory"),
        new Module("item_emerald", "Emerald Count", "Inventory", "Counts emerald in your inventory"),
        new Module("item_emerald_block", "Emerald Block Count", "Inventory", "Counts emerald block in your inventory"),
        new Module("item_enchanted_book", "Enchanted Book Count", "Inventory", "Counts enchanted book in your inventory"),
        new Module("item_dragon_breath", "Dragon Breath Count", "Inventory", "Counts dragon breath in your inventory"),
        new Module("item_ender_chest", "Ender Chest Count", "Inventory", "Counts ender chest in your inventory"),
        new Module("item_ender_eye", "Ender Eye Count", "Inventory", "Counts ender eye in your inventory"),
        new Module("item_campfire", "Campfire Count", "Inventory", "Counts campfire in your inventory"),
        new Module("item_end_rod", "End Rod Count", "Inventory", "Counts end rod in your inventory"),
        new Module("item_experience_bottle", "Experience Bottle Count", "Inventory", "Counts experience bottle in your inventory"),
        new Module("item_feather", "Feather Count", "Inventory", "Counts feather in your inventory"),
        new Module("item_fermented_spider_eye", "Fermented Spider Eye Count", "Inventory", "Counts fermented spider eye in your inventory"),
        new Module("item_fire_charge", "Fire Charge Count", "Inventory", "Counts fire charge in your inventory"),
        new Module("item_anvil", "Anvil Count", "Inventory", "Counts anvil in your inventory"),
        new Module("item_flint", "Flint Count", "Inventory", "Counts flint in your inventory"),
        new Module("item_flint_and_steel", "Flint And Steel Count", "Inventory", "Counts flint and steel in your inventory"),
        new Module("item_ghast_tear", "Ghast Tear Count", "Inventory", "Counts ghast tear in your inventory"),
        new Module("item_glass_bottle", "Glass Bottle Count", "Inventory", "Counts glass bottle in your inventory"),
        new Module("item_glowstone_dust", "Glowstone Dust Count", "Inventory", "Counts glowstone dust in your inventory"),
        new Module("item_gold_ingot", "Gold Ingot Count", "Inventory", "Counts gold ingot in your inventory"),
        new Module("item_gold_nugget", "Gold Nugget Count", "Inventory", "Counts gold nugget in your inventory"),
        new Module("item_ancient_debris", "Ancient Debris Count", "Inventory", "Counts ancient debris in your inventory"),
        new Module("item_amethyst_shard", "Amethyst Shard Count", "Inventory", "Counts amethyst shard in your inventory"),
        new Module("item_golden_sword", "Golden Sword Count", "Inventory", "Counts golden sword in your inventory"),
        new Module("item_golden_pickaxe", "Golden Pickaxe Count", "Inventory", "Counts golden pickaxe in your inventory"),
        new Module("item_gunpowder", "Gunpowder Count", "Inventory", "Counts gunpowder in your inventory"),
        new Module("item_honey_bottle", "Honey Bottle Count", "Inventory", "Counts honey bottle in your inventory"),
        new Module("item_honeycomb", "Honeycomb Count", "Inventory", "Counts honeycomb in your inventory"),
        new Module("item_hopper", "Hopper Count", "Inventory", "Counts hopper in your inventory"),
        new Module("item_iron_ingot", "Iron Ingot Count", "Inventory", "Counts iron ingot in your inventory"),
        new Module("item_iron_nugget", "Iron Nugget Count", "Inventory", "Counts iron nugget in your inventory"),
        new Module("item_iron_block", "Iron Block Count", "Inventory", "Counts iron block in your inventory"),
        new Module("item_iron_axe", "Iron Axe Count", "Inventory", "Counts iron axe in your inventory"),
        new Module("item_iron_pickaxe", "Iron Pickaxe Count", "Inventory", "Counts iron pickaxe in your inventory"),
        new Module("item_iron_sword", "Iron Sword Count", "Inventory", "Counts iron sword in your inventory"),
        new Module("item_iron_helmet", "Iron Helmet Count", "Inventory", "Counts iron helmet in your inventory"),
        new Module("item_iron_chestplate", "Iron Chestplate Count", "Inventory", "Counts iron chestplate in your inventory"),
        new Module("item_iron_leggings", "Iron Leggings Count", "Inventory", "Counts iron leggings in your inventory"),
        new Module("item_iron_boots", "Iron Boots Count", "Inventory", "Counts iron boots in your inventory"),
        new Module("item_lapis_lazuli", "Lapis Lazuli Count", "Inventory", "Counts lapis lazuli in your inventory"),
        new Module("item_lead", "Lead Count", "Inventory", "Counts lead in your inventory"),
        new Module("item_leather", "Leather Count", "Inventory", "Counts leather in your inventory"),
        new Module("item_leather_boots", "Leather Boots Count", "Inventory", "Counts leather boots in your inventory"),
        new Module("item_leather_chestplate", "Leather Chestplate Count", "Inventory", "Counts leather chestplate in your inventory"),
        new Module("item_leather_helmet", "Leather Helmet Count", "Inventory", "Counts leather helmet in your inventory"),
        new Module("item_leather_leggings", "Leather Leggings Count", "Inventory", "Counts leather leggings in your inventory"),
        new Module("item_magma_cream", "Magma Cream Count", "Inventory", "Counts magma cream in your inventory"),
        new Module("item_map", "Map Count", "Inventory", "Counts map in your inventory"),
        new Module("item_melon_slice", "Melon Slice Count", "Inventory", "Counts melon slice in your inventory"),
        new Module("item_milk_bucket", "Milk Bucket Count", "Inventory", "Counts milk bucket in your inventory"),
        new Module("item_minecart", "Minecart Count", "Inventory", "Counts minecart in your inventory"),
        new Module("item_nautilus_shell", "Nautilus Shell Count", "Inventory", "Counts nautilus shell in your inventory"),
        new Module("item_netherite_ingot", "Netherite Ingot Count", "Inventory", "Counts netherite ingot in your inventory"),
        new Module("item_netherite_scrap", "Netherite Scrap Count", "Inventory", "Counts netherite scrap in your inventory"),
        new Module("item_nether_star", "Nether Star Count", "Inventory", "Counts nether star in your inventory"),
        new Module("item_diamond_horse_armor", "Diamond Horse Armor Count", "Inventory", "Counts diamond horse armor in your inventory"),
        new Module("item_paper", "Paper Count", "Inventory", "Counts paper in your inventory"),
        new Module("item_phantom_membrane", "Phantom Membrane Count", "Inventory", "Counts phantom membrane in your inventory"),
        new Module("item_poppy", "Poppy Count", "Inventory", "Counts poppy in your inventory"),
        new Module("item_prismarine_crystals", "Prismarine Crystals Count", "Inventory", "Counts prismarine crystals in your inventory"),
        new Module("item_prismarine_shard", "Prismarine Shard Count", "Inventory", "Counts prismarine shard in your inventory"),
        new Module("item_quartz", "Quartz Count", "Inventory", "Counts quartz in your inventory")
    );

    private NexusModuleRegistry() {}
    public static List<Module> modules() { return MODULES; }
    public static boolean enabled(String id) { return AutoSpearSwapConfig.extraModules.getOrDefault(id, false); }
    public static void toggle(String id) { AutoSpearSwapConfig.extraModules.put(id, !enabled(id)); }

    public static void render(DrawContext ctx, MinecraftClient client, int x, int y, int color) {
        PlayerEntity p = client.player;
        if (p == null || client.world == null) return;
        BlockPos pos = p.getBlockPos();
        java.util.List<String> lines = new java.util.ArrayList<>();
        for (Module module : MODULES) {
            if (!enabled(module.id())) continue;
            String value = value(module.id(), client, p, pos);
            if (value != null) lines.add(module.name().toUpperCase() + "  " + value);
        }
        if (lines.isEmpty()) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int availableRows = Math.max(1, (screenHeight - y - 10) / 11);
        int maxColumns = Math.max(1, (screenWidth - x - 8) / 200);
        int requiredColumns = (lines.size() + availableRows - 1) / availableRows;
        int columns = Math.max(1, Math.min(maxColumns, requiredColumns));
        int rowsPerColumn = (lines.size() + columns - 1) / columns;

        for (int i = 0; i < lines.size(); i++) {
            int column = i / rowsPerColumn;
            int row = i % rowsPerColumn;
            int drawX = x + column * 200;
            int drawY = y + row * 11;
            if (drawY + 10 > screenHeight || drawX + 196 > screenWidth) continue;
            ctx.drawTextWithShadow(client.textRenderer, lines.get(i), drawX, drawY, color);
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

    private static int countItemId(PlayerEntity player, String path) {
        var item = Registries.ITEM.get(Identifier.of("minecraft", path));
        return item == net.minecraft.item.Items.AIR ? 0 : countItem(player, item);
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
            case "diamond_horse_armor" -> Integer.toString(countItem(p, net.minecraft.item.Items.OBSIDIAN));
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
            case "hunger_level" -> p.getHungerManager().getFoodLevel() + " / 20";
            case "hunger_percent" -> Math.round(p.getHungerManager().getFoodLevel() * 5.0f) + "%";
            case "mainhand_count" -> p.getMainHandStack().getCount() + " items";
            case "offhand_count" -> p.getOffHandStack().getCount() + " items";
            case "experience_progress" -> Math.round(p.experienceProgress * 100) + "%";
            case "yaw_cardinal" -> {
                int direction = Math.floorMod((int)Math.floor(p.getYaw() / 90.0f + 0.5f), 4);
                yield switch (direction) { case 0 -> "SOUTH"; case 1 -> "WEST"; case 2 -> "NORTH"; default -> "EAST"; };
            }
            case "chunk_x" -> Integer.toString(pos.getX() >> 4);
            case "chunk_z" -> Integer.toString(pos.getZ() >> 4);
            case "chunk_local_x" -> Integer.toString(Math.floorMod(pos.getX(), 16));
            case "chunk_local_z" -> Integer.toString(Math.floorMod(pos.getZ(), 16));
            case "velocity_x" -> String.format("%.3f", p.getVelocity().x);
            case "velocity_y" -> String.format("%.3f", p.getVelocity().y);
            case "velocity_z" -> String.format("%.3f", p.getVelocity().z);
            case "target_block_distance" -> c.crosshairTarget instanceof net.minecraft.util.hit.BlockHitResult block
                ? String.format("%.1f blocks", Math.sqrt(
                    Math.pow(p.getX() - block.getBlockPos().getX() - 0.5, 2)
                    + Math.pow(p.getY() - block.getBlockPos().getY() - 0.5, 2)
                    + Math.pow(p.getZ() - block.getBlockPos().getZ() - 0.5, 2))) : "None";
            case "target_block_face" -> c.crosshairTarget instanceof net.minecraft.util.hit.BlockHitResult block
                ? block.getSide().toString().toLowerCase(java.util.Locale.ROOT) : "None";
            case "target_entity_is_player" -> c.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult hit
                ? (hit.getEntity() instanceof PlayerEntity ? "YES" : "NO") : "None";
            case "target_entity_alive" -> c.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult hit
                ? (hit.getEntity().isAlive() ? "ALIVE" : "DEAD") : "None";
            case "target_entity_absorption" -> c.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult hit
                && hit.getEntity() instanceof net.minecraft.entity.LivingEntity living
                ? String.format("%.1f hearts", living.getAbsorptionAmount() / 2.0f) : "None";
            case "day_phase" -> {
                int hour = (int)((Math.floorMod(c.world.getTimeOfDay(), 24000L) / 1000 + 6) % 24);
                yield hour < 6 ? "NIGHT" : hour < 8 ? "SUNRISE" : hour < 17 ? "DAY" : hour < 19 ? "SUNSET" : "NIGHT";
            }
            case "air_percent" -> Math.round(Math.max(0, p.getAir()) * 100.0f / Math.max(1, p.getMaxAir())) + "%";
            case "item_acacia_boat" -> Integer.toString(countItemId(p, "acacia_boat"));
            case "item_acacia_chest_boat" -> Integer.toString(countItemId(p, "acacia_chest_boat"));
            case "item_apple" -> Integer.toString(countItemId(p, "apple"));
            case "item_armor_stand" -> Integer.toString(countItemId(p, "armor_stand"));
            case "item_baked_potato" -> Integer.toString(countItemId(p, "baked_potato"));
            case "item_bamboo" -> Integer.toString(countItemId(p, "bamboo"));
            case "item_barrel" -> Integer.toString(countItemId(p, "barrel"));
            case "item_beacon" -> Integer.toString(countItemId(p, "beacon"));
            case "item_beef" -> Integer.toString(countItemId(p, "beef"));
            case "item_beetroot" -> Integer.toString(countItemId(p, "beetroot"));
            case "item_beetroot_seeds" -> Integer.toString(countItemId(p, "beetroot_seeds"));
            case "item_beetroot_soup" -> Integer.toString(countItemId(p, "beetroot_soup"));
            case "item_birch_boat" -> Integer.toString(countItemId(p, "birch_boat"));
            case "item_blaze_powder" -> Integer.toString(countItemId(p, "blaze_powder"));
            case "item_blaze_rod" -> Integer.toString(countItemId(p, "blaze_rod"));
            case "item_blue_ice" -> Integer.toString(countItemId(p, "blue_ice"));
            case "item_bone" -> Integer.toString(countItemId(p, "bone"));
            case "item_bone_meal" -> Integer.toString(countItemId(p, "bone_meal"));
            case "item_book" -> Integer.toString(countItemId(p, "book"));
            case "item_bookshelf" -> Integer.toString(countItemId(p, "bookshelf"));
            case "item_bow" -> Integer.toString(countItemId(p, "bow"));
            case "item_bread" -> Integer.toString(countItemId(p, "bread"));
            case "item_breeze_rod" -> Integer.toString(countItemId(p, "breeze_rod"));
            case "item_brick" -> Integer.toString(countItemId(p, "brick"));
            case "item_bucket" -> Integer.toString(countItemId(p, "bucket"));
            case "item_bundle" -> Integer.toString(countItemId(p, "bundle"));
            case "item_cake" -> Integer.toString(countItemId(p, "cake"));
            case "item_carrot" -> Integer.toString(countItemId(p, "carrot"));
            case "item_carrot_on_a_stick" -> Integer.toString(countItemId(p, "carrot_on_a_stick"));
            case "item_chain" -> Integer.toString(countItemId(p, "chain"));
            case "item_charcoal" -> Integer.toString(countItemId(p, "charcoal"));
            case "item_cherry_boat" -> Integer.toString(countItemId(p, "cherry_boat"));
            case "item_chest_minecart" -> Integer.toString(countItemId(p, "chest_minecart"));
            case "item_chicken" -> Integer.toString(countItemId(p, "chicken"));
            case "item_chorus_fruit" -> Integer.toString(countItemId(p, "chorus_fruit"));
            case "item_clay_ball" -> Integer.toString(countItemId(p, "clay_ball"));
            case "item_clock" -> Integer.toString(countItemId(p, "clock"));
            case "item_coal" -> Integer.toString(countItemId(p, "coal"));
            case "item_cobbled_deepslate" -> Integer.toString(countItemId(p, "cobbled_deepslate"));
            case "item_cobblestone" -> Integer.toString(countItemId(p, "cobblestone"));
            case "item_cocoa_beans" -> Integer.toString(countItemId(p, "cocoa_beans"));
            case "item_copper_ingot" -> Integer.toString(countItemId(p, "copper_ingot"));
            case "item_copper_nugget" -> Integer.toString(countItemId(p, "copper_nugget"));
            case "item_crossbow" -> Integer.toString(countItemId(p, "crossbow"));
            case "item_crying_obsidian" -> Integer.toString(countItemId(p, "crying_obsidian"));
            case "item_diamond" -> Integer.toString(countItemId(p, "diamond"));
            case "item_diamond_axe" -> Integer.toString(countItemId(p, "diamond_axe"));
            case "item_diamond_block" -> Integer.toString(countItemId(p, "diamond_block"));
            case "item_diamond_boots" -> Integer.toString(countItemId(p, "diamond_boots"));
            case "item_diamond_chestplate" -> Integer.toString(countItemId(p, "diamond_chestplate"));
            case "item_diamond_helmet" -> Integer.toString(countItemId(p, "diamond_helmet"));
            case "item_diamond_hoe" -> Integer.toString(countItemId(p, "diamond_hoe"));
            case "item_diamond_leggings" -> Integer.toString(countItemId(p, "diamond_leggings"));
            case "item_diamond_pickaxe" -> Integer.toString(countItemId(p, "diamond_pickaxe"));
            case "item_diamond_shovel" -> Integer.toString(countItemId(p, "diamond_shovel"));
            case "item_diamond_sword" -> Integer.toString(countItemId(p, "diamond_sword"));
            case "item_dried_kelp" -> Integer.toString(countItemId(p, "dried_kelp"));
            case "item_egg" -> Integer.toString(countItemId(p, "egg"));
            case "item_elytra" -> Integer.toString(countItemId(p, "elytra"));
            case "item_emerald" -> Integer.toString(countItemId(p, "emerald"));
            case "item_emerald_block" -> Integer.toString(countItemId(p, "emerald_block"));
            case "item_enchanted_book" -> Integer.toString(countItemId(p, "enchanted_book"));
            case "item_dragon_breath" -> Integer.toString(countItemId(p, "dragon_breath"));
            case "item_ender_chest" -> Integer.toString(countItemId(p, "ender_chest"));
            case "item_ender_eye" -> Integer.toString(countItemId(p, "ender_eye"));
            case "item_campfire" -> Integer.toString(countItemId(p, "campfire"));
            case "item_end_rod" -> Integer.toString(countItemId(p, "end_rod"));
            case "item_experience_bottle" -> Integer.toString(countItemId(p, "experience_bottle"));
            case "item_feather" -> Integer.toString(countItemId(p, "feather"));
            case "item_fermented_spider_eye" -> Integer.toString(countItemId(p, "fermented_spider_eye"));
            case "item_fire_charge" -> Integer.toString(countItemId(p, "fire_charge"));
            case "item_anvil" -> Integer.toString(countItemId(p, "anvil"));
            case "item_flint" -> Integer.toString(countItemId(p, "flint"));
            case "item_flint_and_steel" -> Integer.toString(countItemId(p, "flint_and_steel"));
            case "item_ghast_tear" -> Integer.toString(countItemId(p, "ghast_tear"));
            case "item_glass_bottle" -> Integer.toString(countItemId(p, "glass_bottle"));
            case "item_glowstone_dust" -> Integer.toString(countItemId(p, "glowstone_dust"));
            case "item_gold_ingot" -> Integer.toString(countItemId(p, "gold_ingot"));
            case "item_gold_nugget" -> Integer.toString(countItemId(p, "gold_nugget"));
            case "item_ancient_debris" -> Integer.toString(countItemId(p, "ancient_debris"));
            case "item_amethyst_shard" -> Integer.toString(countItemId(p, "amethyst_shard"));
            case "item_golden_sword" -> Integer.toString(countItemId(p, "golden_sword"));
            case "item_golden_pickaxe" -> Integer.toString(countItemId(p, "golden_pickaxe"));
            case "item_gunpowder" -> Integer.toString(countItemId(p, "gunpowder"));
            case "item_honey_bottle" -> Integer.toString(countItemId(p, "honey_bottle"));
            case "item_honeycomb" -> Integer.toString(countItemId(p, "honeycomb"));
            case "item_hopper" -> Integer.toString(countItemId(p, "hopper"));
            case "item_iron_ingot" -> Integer.toString(countItemId(p, "iron_ingot"));
            case "item_iron_nugget" -> Integer.toString(countItemId(p, "iron_nugget"));
            case "item_iron_block" -> Integer.toString(countItemId(p, "iron_block"));
            case "item_iron_axe" -> Integer.toString(countItemId(p, "iron_axe"));
            case "item_iron_pickaxe" -> Integer.toString(countItemId(p, "iron_pickaxe"));
            case "item_iron_sword" -> Integer.toString(countItemId(p, "iron_sword"));
            case "item_iron_helmet" -> Integer.toString(countItemId(p, "iron_helmet"));
            case "item_iron_chestplate" -> Integer.toString(countItemId(p, "iron_chestplate"));
            case "item_iron_leggings" -> Integer.toString(countItemId(p, "iron_leggings"));
            case "item_iron_boots" -> Integer.toString(countItemId(p, "iron_boots"));
            case "item_lapis_lazuli" -> Integer.toString(countItemId(p, "lapis_lazuli"));
            case "item_lead" -> Integer.toString(countItemId(p, "lead"));
            case "item_leather" -> Integer.toString(countItemId(p, "leather"));
            case "item_leather_boots" -> Integer.toString(countItemId(p, "leather_boots"));
            case "item_leather_chestplate" -> Integer.toString(countItemId(p, "leather_chestplate"));
            case "item_leather_helmet" -> Integer.toString(countItemId(p, "leather_helmet"));
            case "item_leather_leggings" -> Integer.toString(countItemId(p, "leather_leggings"));
            case "item_magma_cream" -> Integer.toString(countItemId(p, "magma_cream"));
            case "item_map" -> Integer.toString(countItemId(p, "map"));
            case "item_melon_slice" -> Integer.toString(countItemId(p, "melon_slice"));
            case "item_milk_bucket" -> Integer.toString(countItemId(p, "milk_bucket"));
            case "item_minecart" -> Integer.toString(countItemId(p, "minecart"));
            case "item_nautilus_shell" -> Integer.toString(countItemId(p, "nautilus_shell"));
            case "item_netherite_ingot" -> Integer.toString(countItemId(p, "netherite_ingot"));
            case "item_netherite_scrap" -> Integer.toString(countItemId(p, "netherite_scrap"));
            case "item_nether_star" -> Integer.toString(countItemId(p, "nether_star"));
            case "item_diamond_horse_armor" -> Integer.toString(countItemId(p, "diamond_horse_armor"));
            case "item_paper" -> Integer.toString(countItemId(p, "paper"));
            case "item_phantom_membrane" -> Integer.toString(countItemId(p, "phantom_membrane"));
            case "item_poppy" -> Integer.toString(countItemId(p, "poppy"));
            case "item_prismarine_crystals" -> Integer.toString(countItemId(p, "prismarine_crystals"));
            case "item_prismarine_shard" -> Integer.toString(countItemId(p, "prismarine_shard"));
            case "item_quartz" -> Integer.toString(countItemId(p, "quartz"));
            default -> null;
        };
    }
}
