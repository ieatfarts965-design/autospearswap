# Nexus Client 1.3.0

Client-side Fabric utility client with category-based settings and a custom branded icon for Minecraft 1.21.11 (Java 21).

## Nexus interface\n- Seven navigation pages: Combat, Movement, HUD, Visuals, Performance, Packs, and Keys.\n- Five full-panel themes: Nexus, Violet, Mint, Crimson, and Gold.\n- Adjustable panel opacity and persistent settings.\n\n## Open the menu
- Press **Right Shift** to open the Nexus Client menu.
- Rebind the menu and spear-swap keys from the menu.
- Settings are saved in `config/autospearswap.json`.

## Combat tools
- **Auto spear swap:** temporarily selects your configured spear for an attack and restores the previous hotbar slot.
- **Spear slot:** select any hotbar slot 1–9.
- **Auto sprint:** automatically sprints while moving forward when the player is not sneaking or riding.

## Triggerbot
- Toggle with **G** or from the Nexus menu.
- Automatically uses Minecraft's normal attack routine when another live, non-spectator player is under the crosshair.
- Waits for vanilla's fully charged attack cooldown before attempting an attack; server rules still apply.
- Disabled by default and only targets players, never animals or villagers.

## PvP HUD
Each module can be toggled independently:
- **Coordinates:** live XYZ display.
- **CPS:** left- and right-clicks per second.
- **Armor status:** remaining durability percentage for equipped armor.
- **Keystrokes:** WASD and mouse buttons, with pressed-state highlighting.
- **Combat timer:** seconds since your last attack input.
- **Low health alert:** warning when health reaches 3 hearts or lower.

## Performance and appearance
- FPS-friendly preset reduces particles, clouds, entity distance, biome blending, and entity shadows.
- Restore visual defaults button.
- Five accent themes and adjustable panel opacity.
- Native Minecraft resource-pack screen for importing and enabling resource packs.

## Build
Requires JDK 21. Run `gradle build` or `./gradlew build`.
Install the normal JAR from `build/libs/`; do not install the `-sources.jar` or `-dev.jar`.

## Notes
This is a client-side utility mod. HUD values are local estimates: CPS counts input presses, and the combat timer tracks attack input rather than confirmed server hits. It does not bypass server-side combat rules or guarantee attack timing.
