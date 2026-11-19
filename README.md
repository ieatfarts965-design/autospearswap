# Overtonight Client 2.0.0

A client-side Fabric utility client for Minecraft Java 1.21.11 (Java 21), styled around the **Drop Out** artwork with a dark, muted burgundy interface.

## Open the menu

- Press **Right Shift** to open the Overtonight dashboard.
- Rebind the menu, spear, triggerbot, macro, zoom, sneak, and auto-walk keys in **Keys**.
- Settings and module states are saved in `config/autospearswap.json`.
- The bundled Drop Out artwork is used as the menu background and client icon.

## Dashboard

- Navigation for Combat, Movement, HUD, Visuals, Performance, Packs, Skies, Keys, Macros, Info, Search, Editor, and Music.
- Searchable, paginated module library.
- Nine muted Overtonight color presets and adjustable panel opacity.
- Saved Legit, PvP, Visual, and Performance profiles.

## Combat and movement

- Auto spear swap and configurable spear hotbar slot.
- Triggerbot with configurable delay and hold/toggle mode.
- Jump reset.
- Auto totem, auto refill, and auto armor.
- Auto sprint, auto jump, auto tool, toggle sneak, auto walk, and auto swim.
- Macro sequencer with editable action order, delays, and playback toggle.

These options automate normal client inputs and inventory interactions. Server rules and server-side checks still apply. Keep automation disabled on servers where it is not allowed.

## HUD and module library

- Coordinates, CPS, armor durability, keystrokes, combat timer, low-health warning, target HUD, durability alerts, FPS, clock, speed, direction, biome, ping, health, food, held item, and potion effects.
- **200 selectable readout modules**, including world/player details and individual counters for common blocks, tools, armor, crafting items, and resources.
- Module states can be searched and toggled independently.

## Music (free, no account required)

The Music page sends standard Windows media-key commands to the active Windows media session.

- Previous track, next track, and play/pause.
- Works with compatible desktop players and browser tabs that register with Windows media controls.
- No Spotify account, developer app, API key, subscription, or additional music service is required.
- This lightweight integration sends playback commands only. It does not read track titles, artists, album artwork, progress, or volume.
- Requires Windows.

## Custom skies

The **Skies** page links to [Nuit](https://modrinth.com/mod/YBz7DOs8), an optional Fabric skybox engine. Install Nuit and a compatible sky resource pack to render custom skyboxes. Overtonight itself does not implement a separate sky renderer.

## Resource packs

Use the native Minecraft resource-pack manager, or open the resource-pack folder from **Packs**. Enable a pack and press Done to reload resources.

## Performance

Performance presets adjust vanilla particle density, cloud quality, entity distance, biome blending, and entity shadows. Actual FPS gains depend on your hardware, world, and other installed mods.

## Build

Requires JDK 21. Run `gradle build` or `./gradlew build`.

Install the normal JAR from `build/libs/`. Do not install the `-sources.jar` or `-dev.jar`.

## Limitations

- This is a client-side mod; it cannot change server-side rules or guarantee that automated actions are accepted.
- Music metadata is not available through the global media-key bridge.
- Custom sky rendering is supplied by the optional Nuit mod and a compatible resource pack.
- HUD values are local client/game readouts; some network and combat values are estimates rather than authoritative server data.
