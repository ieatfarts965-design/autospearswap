# Overtonight Client 2.0.0

A client-side Fabric utility client for Minecraft Java 1.21.11 (Java 21), with a dark burgundy/black **Drop Out** visual identity.

## Open the menu

- Press **Right Shift** to open the Overtonight dashboard.
- Rebind the menu, spear, triggerbot, macro, zoom, sneak, and auto-walk keys in **Keys**.
- Settings and module states are saved in `config/autospearswap.json`.
- The bundled Drop Out artwork is used as the menu background and the client icon.

## Dashboard

- Left navigation for Combat, Movement, HUD, Visuals, Performance, Packs, Skies, Keys, Macros, Info, Search, Editor, and Music.
- Searchable, paginated module list.
- Four-column card layout on wide screens, with narrower layouts on smaller windows.
- Dark Overtonight palette, multiple muted accent presets, and adjustable opacity.
- Saved Legit, PvP, Visual, and Performance profiles.

## Combat and movement

- Auto spear swap and configurable spear hotbar slot.
- Triggerbot with configurable delay and hold/toggle mode.
- Jump reset.
- Auto totem, auto refill, and auto armor.
- Auto sprint, auto jump, auto tool, toggle sneak, auto walk, and auto swim.
- Macro sequencer with editable action order, delays, and playback toggle.

These options automate normal client inputs/inventory interactions. Server rules and server-side checks still apply. Keep automation disabled on servers where it is not allowed.

## HUD and module library

- Coordinates, CPS, armor durability, keystrokes, combat timer, low-health warning, target HUD, durability alerts, FPS, clock, speed, direction, biome, ping, health, food, held item, and potion effects.
- Additional world/player/inventory readouts include time/day, dimension, light level, chunk and block coordinates, velocity, fall distance, air, experience, absorption, armor points, weather, saturation, inventory counts, target details, durability, and cardinal direction.
- The searchable registry contains **200 selectable readout modules**. The latest expansion adds 120 individual inventory counters for common blocks, tools, armor, crafting items, and resources.
- Module states are stored independently and can be searched from the Search page.

## Music controls (free)

The **Music** page sends the standard previous, next, and play/pause media keys to Windows. It can control whichever app currently owns the Windows media session, such as a supported browser tab or desktop player.

- No Spotify account, Spotify developer app, API key, subscription, or additional music app is required.
- The current implementation sends global media-key commands. It does **not** read track title, artist, album artwork, playback progress, or volume.
- Windows is required for the global media-key bridge.

## Custom skies

Overtonight provides a **Skies** setup page and links to [Nuit](https://modrinth.com/mod/YBz7DOs8), a separate Fabric skybox engine with Minecraft 1.21.11 support. Install Nuit and a Nuit-compatible sky resource pack to render custom skyboxes. Overtonight itself does not implement a separate sky renderer.

## Resource packs

Use the native Minecraft resource-pack manager, or open the resource-pack folder from **Packs**. Enable a pack and press Done to reload resources.

## Performance

Performance presets adjust vanilla particle density, cloud quality, entity distance, biome blending, and entity shadows. Actual FPS gains depend on your hardware, world, and other installed mods.

## Build

Requires JDK 21. Run `gradle build` or `./gradlew build`.

Install the normal JAR from `build/libs/`. Do not install the `-sources.jar` or `-dev.jar`.

## Limitations

- This is a client-side mod; it cannot change server-side rules or guarantee that automated actions are accepted.
- The Music page currently controls playback but does not retrieve now-playing metadata.
- Custom sky rendering is supplied by the optional Nuit dependency and a compatible resource pack, not by a built-in Overtonight renderer.
- The HUD reports local game/client values; some network and combat values are estimates rather than authoritative server data.
