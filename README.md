# Auto Spear Swap

A client-side Fabric mod for Minecraft 1.21.11.

## Features
- Right Shift opens the custom settings screen.
- B toggles automatic attribute swapping.
- G toggles lunge-swap mode (the lunge action handler is a planned follow-up).
- Configure the spear and primary hotbar slots in the GUI.
- Settings persist in `config/autospearswap.json`.

## Build
Requires Java 21. Run `gradle build` or use the included GitHub Actions workflow. The jar is written to `build/libs/`.

## Important
Attribute swapping relies on server-side handling of rapid held-slot changes and attacks. Some server software patches this vanilla mechanic, so client automation cannot force it to work where the server disables it.
