# Auto Spear Swap Client

Client-side Fabric mod for Minecraft 1.21.11 (Java 21).

## Included features
- Right Shift opens the in-game client menu.
- V switches to the configured spear hotbar slot; press V again to return to the slot you were using.
- B toggles the attribute-swap helper setting.
- Rebind the menu and spear swap keys in the menu.
- Choose the spear hotbar slot (1–9).
- Apply an FPS-friendly vanilla settings preset: minimal particles, clouds off, 50% entity render distance, zero biome blend, and entity shadows off.
- Restore the visual settings preset from the menu.
- Settings are stored in `config/autospearswap.json`; keybinds are stored by Minecraft in its normal options file.

## Custom sky resource packs
Minecraft/Fabric does not natively load OptiFine custom sky properties. Install **Skyboxify 3.3.1 for Minecraft 1.21.11 (Fabric)** alongside this mod to render compatible OptiFine-format sky resource packs. Put the resource pack in the normal `resourcepacks` folder and enable it in Minecraft's Resource Packs menu. Skyboxify is a separate mod and is not bundled in this jar.

## Install
1. Install Fabric Loader for Minecraft 1.21.11 and Fabric API.
2. Put the normal `autospearswap-1.0.0.jar` in `.minecraft/mods`.
3. For OptiFine-format custom skies, also install the matching Skyboxify build and its listed dependencies.

## Important
The spear hotbar helper is a deliberate manual swap/restore action. This build does not claim to perform automatic server-side lunge timing or force attribute changes on servers that block held-item swapping. FPS gains depend on your hardware, world, and settings; the preset trades some visual quality for lower rendering load.

## Build
Requires Java 21. Run `gradle build`. The main jar is in `build/libs/`; do not install the `-sources.jar`.
