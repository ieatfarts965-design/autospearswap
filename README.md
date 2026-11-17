# Auto Spear Swap Client

Fabric client mod for Minecraft 1.21.11 (Java 21).

## Client menu
- Right Shift opens the Nexus Client menu.
- B toggles automatic spear selection while holding use; V manually swaps to the configured spear slot and back.
- Rebind the menu and manual swap keys in the menu.
- Choose the spear hotbar slot (1–9).
- Apply an FPS-friendly vanilla settings preset or restore visual defaults.
- Customize the menu accent theme (Cyan, Violet, Mint, Red, Gold) and panel opacity. These preferences persist in `config/autospearswap.json`.

## Import and enable resource packs
The menu's **Open / import resource packs** button opens Minecraft's native Resource Packs screen. You can:
1. Drag a resource-pack ZIP from Windows File Explorer directly onto that screen to import it, or place the ZIP in the instance's `resourcepacks` folder.
2. Move the pack from Available to Selected and click Done.
3. The pack reloads through Minecraft's normal resource reload.

The three user-provided packs are compatible with Minecraft 1.21.11 (pack format 75):
- `Tightfault-SkyWars-32x-1.21.11.zip`
- `Kitty-Pack-Cheese-Edition-128x-1.21.11.zip`
- `Winter kitty ❄️HD 1.21.11.zip`

They are intentionally not bundled into the mod JAR (together they are about 190 MB). Import them from your downloaded ZIPs using the menu above.

For OptiFine-format custom sky properties, install Skyboxify for Minecraft 1.21.11 Fabric separately. Sky rendering is handled by Skyboxify; the resource pack menu here imports/enables packs through Minecraft.

## Install
1. Install Fabric Loader for Minecraft 1.21.11 and Fabric API.
2. Put the normal `autospearswap-1.1.0.jar` in `.minecraft/mods`.
3. Install Skyboxify as well if you need OptiFine-format custom skies.

## Notes
FPS improvements depend on the world and hardware; the preset reduces rendering quality to lower some rendering work. The spear helper cannot guarantee server-side combat timing or bypass server restrictions.

## Build
Requires Java 21. Run `gradle build`. Install the main JAR in `build/libs/`, not the `-sources.jar`.
