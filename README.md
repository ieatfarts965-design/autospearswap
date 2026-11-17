# Auto Spear Swap

A client-side Fabric mod for Minecraft 1.21.11.

## Features
- Right Shift opens the custom settings screen.
- B toggles automatic attribute swapping.
- Rebind both controls directly in the in-game settings screen.
- Configure the spear hotbar slot (1–9).
- Settings persist in `config/autospearswap.json`.

When enabled, every normal attack is wrapped in a rapid selected-slot change to the configured spear slot, then the original held slot is restored. The swap and attack packets are sent in order so the server can process them in the same attack sequence.

## Build
Requires Java 21. Run `gradle build`. The jar is written to `build/libs/`. GitHub Actions builds the project on pushes to `main`.

## Server compatibility
Attribute swapping depends on the server allowing vanilla held-item attribute updates during attacks. Some server software patches this mechanic, so a client mod cannot force it to work where the server disables it.
