package dev.odithekitty.autospearswap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Identifier;
import java.util.function.BooleanSupplier;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.util.InputUtil;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.text.Text;

public final class AutoSpearSwapScreen extends Screen {
    private static final String[] TABS = {"Combat", "Movement", "HUD", "Visuals", "Performance", "Packs", "Skies", "Keys", "Macros", "Info", "Search", "Editor", "Spotify"};
    private static final String[] THEMES = {"Dropout", "Afterhours", "Ghostland", "Nocturne", "Static", "Bloodmoon", "VHS", "Blackout", "Ashes"};
    private static final int[] ACCENT = {0xFFF2E9E9, 0xFFE5BFC2, 0xFFFF5B69, 0xFFD9D0D0, 0xFFB73A4A, 0xFFFF7F83, 0xFFC8B9BB, 0xFFE8E1E1, 0xFF9D7D83};
    private static final int[] PANEL = {0xD90D080B, 0xD9160A10, 0xD90E090D, 0xD9180B10, 0xD910080B, 0xD91D0A10, 0xD90A080B, 0xD9100A0E, 0xD9140B0E};
    private static final int[] SIDE = {0xD91A0D12, 0xD9230E14, 0xD91C0D12, 0xD9261016, 0xD9180B0F, 0xD92A0C13, 0xD9140A0D, 0xD9200D12, 0xD91D0C10};
    private int tab;
    private KeyBinding capturing;
    private boolean openingPacks;
    private String moduleQuery = "";
    private TextFieldWidget searchField;
    private TextFieldWidget spotifyClientField;
    private int modulePage;

    public AutoSpearSwapScreen() { super(Text.literal("OVERTONIGHT CLIENT")); }

    @Override
    protected void init() {
        int pw = Math.min(540, width - 20), ph = Math.min(440, height - 20);
        int x = (width - pw) / 2, y = (height - ph) / 2;
        int sx = x + 12, sy = y + 58, sw = Math.min(112, pw / 4);
        for (int i = 0; i < TABS.length; i++) {
            final int index = i;
            add(sx, sy + i * 29, sw, 25, (tab == i ? "◆  " : "   ") + TABS[i],
                b -> { tab = index; clearAndInit(); });
        }
        int cx = sx + sw + 14, cw = pw - (cx - x) - 14, gap = 8, bw = (cw - gap) / 2;
        int y1 = y + 78, h = 30;
        switch (tab) {
            case 0 -> {
                toggle(cx, y1, bw, h, "Triggerbot", AutoSpearSwapConfig.triggerBotEnabled,
                    () -> AutoSpearSwapConfig.triggerBotEnabled = !AutoSpearSwapConfig.triggerBotEnabled);
                toggle(cx + bw + gap, y1, bw, h, "Auto spear", AutoSpearSwapConfig.attributeSwapEnabled,
                    () -> AutoSpearSwapConfig.attributeSwapEnabled = !AutoSpearSwapConfig.attributeSwapEnabled);
                add(cx, y1 + 39, cw, h, "Spear slot  •  " + (AutoSpearSwapConfig.spearSlot + 1), b -> {
                    AutoSpearSwapConfig.spearSlot = (AutoSpearSwapConfig.spearSlot + 1) % 9; save();
                });
                toggle(cx, y1 + 78, cw, h, "Jump reset", AutoSpearSwapConfig.jumpResetEnabled,
                    () -> AutoSpearSwapConfig.jumpResetEnabled = !AutoSpearSwapConfig.jumpResetEnabled);
            }
            case 1 -> {
                toggle(cx, y1, bw, h, "Auto sprint", AutoSpearSwapConfig.autoSprint,
                    () -> AutoSpearSwapConfig.autoSprint = !AutoSpearSwapConfig.autoSprint);
                toggle(cx + bw + gap, y1, bw, h, "Auto jump", AutoSpearSwapConfig.autoJump,
                    () -> AutoSpearSwapConfig.autoJump = !AutoSpearSwapConfig.autoJump);
                toggle(cx, y1 + 39, bw, h, "Auto tool", AutoSpearSwapConfig.autoTool,
                    () -> AutoSpearSwapConfig.autoTool = !AutoSpearSwapConfig.autoTool);
                toggle(cx + bw + gap, y1 + 39, bw, h, "Toggle sneak", AutoSpearSwapConfig.toggleSneak,
                    () -> AutoSpearSwapConfig.toggleSneak = !AutoSpearSwapConfig.toggleSneak);
                add(cx, y1 + 78, cw, h, "Toggle sneak key  •  " + keyText("Sneak", AutoSpearSwapClient.toggleSneakKey), b -> capture(AutoSpearSwapClient.toggleSneakKey));
                toggle(cx, y1 + 117, cw, h, "Auto Walk", AutoSpearSwapConfig.autoWalk, () -> AutoSpearSwapConfig.autoWalk = !AutoSpearSwapConfig.autoWalk);
                add(cx, y1 + 156, cw, h, keyText("Auto Walk key", AutoSpearSwapClient.autoWalkKey), b -> capture(AutoSpearSwapClient.autoWalkKey));
                toggle(cx, y1 + 195, cw, h, "Auto Swim", AutoSpearSwapConfig.autoSwim, () -> AutoSpearSwapConfig.autoSwim = !AutoSpearSwapConfig.autoSwim);
            }
            case 2 -> {
                toggle(cx, y1, bw, h, "Coordinates", AutoSpearSwapConfig.hudCoordinates, () -> AutoSpearSwapConfig.hudCoordinates = !AutoSpearSwapConfig.hudCoordinates);
                toggle(cx + bw + gap, y1, bw, h, "CPS counter", AutoSpearSwapConfig.hudCps, () -> AutoSpearSwapConfig.hudCps = !AutoSpearSwapConfig.hudCps);
                toggle(cx, y1 + 38, bw, h, "Armor status", AutoSpearSwapConfig.hudArmor, () -> AutoSpearSwapConfig.hudArmor = !AutoSpearSwapConfig.hudArmor);
                toggle(cx + bw + gap, y1 + 38, bw, h, "Keystrokes", AutoSpearSwapConfig.hudKeystrokes, () -> AutoSpearSwapConfig.hudKeystrokes = !AutoSpearSwapConfig.hudKeystrokes);
                toggle(cx, y1 + 76, bw, h, "Combat timer", AutoSpearSwapConfig.hudCombatTimer, () -> AutoSpearSwapConfig.hudCombatTimer = !AutoSpearSwapConfig.hudCombatTimer);
                toggle(cx + bw + gap, y1 + 76, bw, h, "Low health", AutoSpearSwapConfig.lowHealthWarning, () -> AutoSpearSwapConfig.lowHealthWarning = !AutoSpearSwapConfig.lowHealthWarning);
                toggle(cx, y1 + 114, bw, h, "Target HUD", AutoSpearSwapConfig.hudTarget, () -> AutoSpearSwapConfig.hudTarget = !AutoSpearSwapConfig.hudTarget);
                toggle(cx + bw + gap, y1 + 114, bw, h, "Durability alert", AutoSpearSwapConfig.hudDurability, () -> AutoSpearSwapConfig.hudDurability = !AutoSpearSwapConfig.hudDurability);
                toggle(cx, y1 + 152, bw, h, "FPS counter", AutoSpearSwapConfig.hudFps, () -> AutoSpearSwapConfig.hudFps = !AutoSpearSwapConfig.hudFps);
                toggle(cx + bw + gap, y1 + 152, bw, h, "Clock", AutoSpearSwapConfig.hudClock, () -> AutoSpearSwapConfig.hudClock = !AutoSpearSwapConfig.hudClock);
            }
            case 3 -> {
                add(cx, y1, bw, h, "Theme  •  " + THEMES[AutoSpearSwapConfig.uiTheme], b -> {
                    AutoSpearSwapConfig.uiTheme = (AutoSpearSwapConfig.uiTheme + 1) % THEMES.length; save();
                });
                add(cx + bw + gap, y1, bw, h, "Opacity  •  " + AutoSpearSwapConfig.uiOpacity + "%", b -> {
                    AutoSpearSwapConfig.uiOpacity += 15;
                    if (AutoSpearSwapConfig.uiOpacity > 85) AutoSpearSwapConfig.uiOpacity = 40;
                    save();
                });
                add(cx, y1 + 39, cw, h, "Cycle visual preset", b -> { visualPreset(); save(); });
                add(cx, y1 + 78, cw, h, "Apply LEGIT profile", b -> { AutoSpearSwapConfig.applyLegitPreset(); clearAndInit(); });
                add(cx, y1 + 117, cw, h, "Clarity-style theme", b -> { AutoSpearSwapConfig.uiTheme = 5; AutoSpearSwapConfig.uiOpacity = 82; save(); });
                toggle(cx, y1 + 156, bw, h, "Fullbright", AutoSpearSwapConfig.fullbright, () -> AutoSpearSwapConfig.fullbright = !AutoSpearSwapConfig.fullbright);
                toggle(cx + bw + gap, y1 + 156, bw, h, "Zoom", AutoSpearSwapConfig.zoomEnabled, () -> AutoSpearSwapConfig.zoomEnabled = !AutoSpearSwapConfig.zoomEnabled);
                toggle(cx, y1 + 195, cw, h, "Auto respawn", AutoSpearSwapConfig.autoRespawn, () -> AutoSpearSwapConfig.autoRespawn = !AutoSpearSwapConfig.autoRespawn);
                toggle(cx, y1 + 234, cw, h, "Auto eat", AutoSpearSwapConfig.autoEat, () -> AutoSpearSwapConfig.autoEat = !AutoSpearSwapConfig.autoEat);
            }
            case 4 -> {
                add(cx, y1, cw, h, "MAX FPS  •  aggressive", b -> { performance(); save(); });
                add(cx, y1 + 38, cw, h, "Balanced performance", b -> { balancedPerformance(); save(); });
                add(cx, y1 + 76, cw, h, "Visual quality", b -> { visualQuality(); save(); });
                add(cx, y1 + 114, cw, h, "Restore defaults", b -> { defaults(); save(); });
            }
            case 5 -> {
                add(cx, y1, cw, h, "Open resource pack manager", b -> packs());
                add(cx, y1 + 38, cw, h, "Open resource packs folder", b -> openPackFolder());
            }
            case 6 -> {
                add(cx, y1, cw, h, "Open resource packs folder", b -> openPackFolder());
                add(cx, y1 + 38, cw, h, "Open resource pack manager", b -> packs());
            }
            case 7 -> {
                add(cx, y1, cw, h, keyText("Menu key", AutoSpearSwapClient.openMenuKey), b -> capture(AutoSpearSwapClient.openMenuKey));
                add(cx, y1 + 38, cw, h, keyText("Spear key", AutoSpearSwapClient.swapToSpearKey), b -> capture(AutoSpearSwapClient.swapToSpearKey));
                add(cx, y1 + 76, cw, h, keyText("Triggerbot key", AutoSpearSwapClient.triggerBotKey), b -> capture(AutoSpearSwapClient.triggerBotKey));
                add(cx, y1 + 114, cw, h, keyText("Macro key", AutoSpearSwapClient.macroKey), b -> capture(AutoSpearSwapClient.macroKey));
                add(cx, y1 + 152, cw, h, "Triggerbot mode  •  " + (AutoSpearSwapConfig.triggerBotHoldMode ? "HOLD" : "TOGGLE"), b -> { AutoSpearSwapConfig.triggerBotHoldMode = !AutoSpearSwapConfig.triggerBotHoldMode; AutoSpearSwapConfig.triggerBotEnabled = false; save(); });
                add(cx, y1 + 190, cw, h, keyText("Zoom key", AutoSpearSwapClient.zoomKey), b -> capture(AutoSpearSwapClient.zoomKey));
                add(cx, y1 + 228, cw, h, keyText("Auto Walk key", AutoSpearSwapClient.autoWalkKey), b -> capture(AutoSpearSwapClient.autoWalkKey));
            }
            case 8 -> {
                add(cx, y1, bw, h, "Add Jump", b -> { MacroEngine.add("JUMP", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx + bw + gap, y1, bw, h, "Add Attack", b -> { MacroEngine.add("ATTACK", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx, y1 + 38, bw, h, "Add Use", b -> { MacroEngine.add("USE", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx + bw + gap, y1 + 38, bw, h, "Add Forward", b -> { MacroEngine.add("FORWARD", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx, y1 + 76, bw, h, "Add Back", b -> { MacroEngine.add("BACK", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx + bw + gap, y1 + 76, bw, h, "Add Left", b -> { MacroEngine.add("LEFT", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx, y1 + 114, bw, h, "Add Right", b -> { MacroEngine.add("RIGHT", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx + bw + gap, y1 + 114, bw, h, "Add Sprint", b -> { MacroEngine.add("SPRINT", MacroEngine.defaultTicks); clearAndInit(); });
                add(cx, y1 + 152, bw, h, "Delay  •  " + MacroEngine.defaultTicks + " ticks", b -> {
                    int[] delays = {1, 2, 3, 5, 8, 12, 20};
                    int next = 0;
                    for (int i = 0; i < delays.length; i++) if (delays[i] == MacroEngine.defaultTicks) next = (i + 1) % delays.length;
                    MacroEngine.defaultTicks = delays[next]; clearAndInit();
                });
                add(cx + bw + gap, y1 + 152, bw, h, "Undo step", b -> {
                    MacroEngine.undo();
                    clearAndInit();
                });
                add(cx, y1 + 190, bw, h, MacroEngine.isRunning() ? "Stop macro" : "Play macro", b -> {
                    MacroEngine.toggle();
                    if (MacroEngine.isRunning() && client != null) client.setScreen(null);
                    else clearAndInit();
                });
                add(cx + bw + gap, y1 + 190, bw, h, "Clear sequence", b -> { MacroEngine.clear(); clearAndInit(); });
            }
            case 9 -> {
                toggle(cx, y1, bw, h, "Movement speed", AutoSpearSwapConfig.hudSpeed, () -> AutoSpearSwapConfig.hudSpeed = !AutoSpearSwapConfig.hudSpeed);
                toggle(cx + bw + gap, y1, bw, h, "Direction", AutoSpearSwapConfig.hudDirection, () -> AutoSpearSwapConfig.hudDirection = !AutoSpearSwapConfig.hudDirection);
                toggle(cx, y1 + 38, bw, h, "Biome", AutoSpearSwapConfig.hudBiome, () -> AutoSpearSwapConfig.hudBiome = !AutoSpearSwapConfig.hudBiome);
                toggle(cx + bw + gap, y1 + 38, bw, h, "Server ping", AutoSpearSwapConfig.hudPing, () -> AutoSpearSwapConfig.hudPing = !AutoSpearSwapConfig.hudPing);
                toggle(cx, y1 + 76, bw, h, "Health", AutoSpearSwapConfig.hudHealth, () -> AutoSpearSwapConfig.hudHealth = !AutoSpearSwapConfig.hudHealth);
                toggle(cx + bw + gap, y1 + 76, bw, h, "Food", AutoSpearSwapConfig.hudFood, () -> AutoSpearSwapConfig.hudFood = !AutoSpearSwapConfig.hudFood);
                toggle(cx, y1 + 114, bw, h, "Held item", AutoSpearSwapConfig.hudHeldItem, () -> AutoSpearSwapConfig.hudHeldItem = !AutoSpearSwapConfig.hudHeldItem);
                toggle(cx + bw + gap, y1 + 114, bw, h, "Potion effects", AutoSpearSwapConfig.hudEffects, () -> AutoSpearSwapConfig.hudEffects = !AutoSpearSwapConfig.hudEffects);
                add(cx, y1 + 152, bw, h, "Save Legit", b -> AutoSpearSwapConfig.saveProfile("Legit"));
                add(cx + bw + gap, y1 + 152, bw, h, "Load Legit", b -> { AutoSpearSwapConfig.loadProfile("Legit"); clearAndInit(); });
                add(cx, y1 + 190, bw, h, "Save PvP", b -> AutoSpearSwapConfig.saveProfile("PvP"));
                add(cx + bw + gap, y1 + 190, bw, h, "Load PvP", b -> { AutoSpearSwapConfig.loadProfile("PvP"); clearAndInit(); });
                add(cx, y1 + 228, bw, h, "Save Visual", b -> AutoSpearSwapConfig.saveProfile("Visual"));
                add(cx + bw + gap, y1 + 228, bw, h, "Load Visual", b -> { AutoSpearSwapConfig.loadProfile("Visual"); clearAndInit(); });
                add(cx, y1 + 266, bw, h, "Save Performance", b -> AutoSpearSwapConfig.saveProfile("Performance"));
                add(cx + bw + gap, y1 + 266, bw, h, "Load Performance", b -> { AutoSpearSwapConfig.loadProfile("Performance"); clearAndInit(); });
            }
            case 10 -> {
                searchField = new TextFieldWidget(textRenderer, cx, y1, cw - 72, h, Text.literal("Search modules"));
                searchField.setMaxLength(32);
                searchField.setText(moduleQuery);
                searchField.setChangedListener(value -> moduleQuery = value);
                addDrawableChild(searchField);
                add(cx + cw - 66, y1, 66, h, "Apply", b -> { modulePage = 0; clearAndInit(); });
                String q = moduleQuery.toLowerCase(java.util.Locale.ROOT);
                java.util.List<Runnable> actions = new java.util.ArrayList<>();
                java.util.List<String> labels = new java.util.ArrayList<>();
                java.util.List<Boolean> states = new java.util.ArrayList<>();
                addSearchEntry(labels, states, actions, q, "Auto sprint", AutoSpearSwapConfig.autoSprint, () -> AutoSpearSwapConfig.autoSprint = !AutoSpearSwapConfig.autoSprint);
                addSearchEntry(labels, states, actions, q, "Auto jump", AutoSpearSwapConfig.autoJump, () -> AutoSpearSwapConfig.autoJump = !AutoSpearSwapConfig.autoJump);
                addSearchEntry(labels, states, actions, q, "Auto tool", AutoSpearSwapConfig.autoTool, () -> AutoSpearSwapConfig.autoTool = !AutoSpearSwapConfig.autoTool);
                addSearchEntry(labels, states, actions, q, "Toggle sneak", AutoSpearSwapConfig.toggleSneak, () -> AutoSpearSwapConfig.toggleSneak = !AutoSpearSwapConfig.toggleSneak);
                addSearchEntry(labels, states, actions, q, "Fullbright", AutoSpearSwapConfig.fullbright, () -> AutoSpearSwapConfig.fullbright = !AutoSpearSwapConfig.fullbright);
                addSearchEntry(labels, states, actions, q, "Zoom", AutoSpearSwapConfig.zoomEnabled, () -> AutoSpearSwapConfig.zoomEnabled = !AutoSpearSwapConfig.zoomEnabled);
                addSearchEntry(labels, states, actions, q, "Auto respawn", AutoSpearSwapConfig.autoRespawn, () -> AutoSpearSwapConfig.autoRespawn = !AutoSpearSwapConfig.autoRespawn);
                addSearchEntry(labels, states, actions, q, "Auto eat", AutoSpearSwapConfig.autoEat, () -> AutoSpearSwapConfig.autoEat = !AutoSpearSwapConfig.autoEat);
                addSearchEntry(labels, states, actions, q, "Auto walk", AutoSpearSwapConfig.autoWalk, () -> AutoSpearSwapConfig.autoWalk = !AutoSpearSwapConfig.autoWalk);
                addSearchEntry(labels, states, actions, q, "Auto swim", AutoSpearSwapConfig.autoSwim, () -> AutoSpearSwapConfig.autoSwim = !AutoSpearSwapConfig.autoSwim);
                addSearchEntry(labels, states, actions, q, "Triggerbot", AutoSpearSwapConfig.triggerBotEnabled, () -> AutoSpearSwapConfig.triggerBotEnabled = !AutoSpearSwapConfig.triggerBotEnabled);
                addSearchEntry(labels, states, actions, q, "Auto spear", AutoSpearSwapConfig.attributeSwapEnabled, () -> AutoSpearSwapConfig.attributeSwapEnabled = !AutoSpearSwapConfig.attributeSwapEnabled);
                addSearchEntry(labels, states, actions, q, "Jump reset", AutoSpearSwapConfig.jumpResetEnabled, () -> AutoSpearSwapConfig.jumpResetEnabled = !AutoSpearSwapConfig.jumpResetEnabled);
                addSearchEntry(labels, states, actions, q, "Coordinates", AutoSpearSwapConfig.hudCoordinates, () -> AutoSpearSwapConfig.hudCoordinates = !AutoSpearSwapConfig.hudCoordinates);
                addSearchEntry(labels, states, actions, q, "CPS counter", AutoSpearSwapConfig.hudCps, () -> AutoSpearSwapConfig.hudCps = !AutoSpearSwapConfig.hudCps);
                addSearchEntry(labels, states, actions, q, "Armor status", AutoSpearSwapConfig.hudArmor, () -> AutoSpearSwapConfig.hudArmor = !AutoSpearSwapConfig.hudArmor);
                addSearchEntry(labels, states, actions, q, "Keystrokes", AutoSpearSwapConfig.hudKeystrokes, () -> AutoSpearSwapConfig.hudKeystrokes = !AutoSpearSwapConfig.hudKeystrokes);
                addSearchEntry(labels, states, actions, q, "Combat timer", AutoSpearSwapConfig.hudCombatTimer, () -> AutoSpearSwapConfig.hudCombatTimer = !AutoSpearSwapConfig.hudCombatTimer);
                addSearchEntry(labels, states, actions, q, "Low health warning", AutoSpearSwapConfig.lowHealthWarning, () -> AutoSpearSwapConfig.lowHealthWarning = !AutoSpearSwapConfig.lowHealthWarning);
                addSearchEntry(labels, states, actions, q, "Target HUD", AutoSpearSwapConfig.hudTarget, () -> AutoSpearSwapConfig.hudTarget = !AutoSpearSwapConfig.hudTarget);
                addSearchEntry(labels, states, actions, q, "Durability alert", AutoSpearSwapConfig.hudDurability, () -> AutoSpearSwapConfig.hudDurability = !AutoSpearSwapConfig.hudDurability);
                addSearchEntry(labels, states, actions, q, "FPS counter", AutoSpearSwapConfig.hudFps, () -> AutoSpearSwapConfig.hudFps = !AutoSpearSwapConfig.hudFps);
                addSearchEntry(labels, states, actions, q, "Clock", AutoSpearSwapConfig.hudClock, () -> AutoSpearSwapConfig.hudClock = !AutoSpearSwapConfig.hudClock);
                addSearchEntry(labels, states, actions, q, "Movement speed", AutoSpearSwapConfig.hudSpeed, () -> AutoSpearSwapConfig.hudSpeed = !AutoSpearSwapConfig.hudSpeed);
                addSearchEntry(labels, states, actions, q, "Direction", AutoSpearSwapConfig.hudDirection, () -> AutoSpearSwapConfig.hudDirection = !AutoSpearSwapConfig.hudDirection);
                addSearchEntry(labels, states, actions, q, "Biome", AutoSpearSwapConfig.hudBiome, () -> AutoSpearSwapConfig.hudBiome = !AutoSpearSwapConfig.hudBiome);
                addSearchEntry(labels, states, actions, q, "Server ping", AutoSpearSwapConfig.hudPing, () -> AutoSpearSwapConfig.hudPing = !AutoSpearSwapConfig.hudPing);
                addSearchEntry(labels, states, actions, q, "Health", AutoSpearSwapConfig.hudHealth, () -> AutoSpearSwapConfig.hudHealth = !AutoSpearSwapConfig.hudHealth);
                addSearchEntry(labels, states, actions, q, "Food", AutoSpearSwapConfig.hudFood, () -> AutoSpearSwapConfig.hudFood = !AutoSpearSwapConfig.hudFood);
                addSearchEntry(labels, states, actions, q, "Held item", AutoSpearSwapConfig.hudHeldItem, () -> AutoSpearSwapConfig.hudHeldItem = !AutoSpearSwapConfig.hudHeldItem);
                addSearchEntry(labels, states, actions, q, "Potion effects", AutoSpearSwapConfig.hudEffects, () -> AutoSpearSwapConfig.hudEffects = !AutoSpearSwapConfig.hudEffects);
                for (NexusModuleRegistry.Module module : NexusModuleRegistry.modules()) {
                    addSearchEntry(labels, states, actions, q, module.name(), NexusModuleRegistry.enabled(module.id()), () -> NexusModuleRegistry.toggle(module.id()));
                }
                int pageSize = 12;
                int pages = Math.max(1, (labels.size() + pageSize - 1) / pageSize);
                modulePage = Math.min(modulePage, pages - 1);
                int from = modulePage * pageSize;
                for (int i = from; i < Math.min(labels.size(), from + pageSize); i++) {
                    final int index = i;
                    int bx = cx + ((i - from) % 2) * (bw + gap);
                    int by = y1 + 42 + ((i - from) / 2) * 35;
                    add(bx, by, bw, h, (states.get(i) ? "● " : "○ ") + labels.get(i), b -> {
                        actions.get(index).run();
                        AutoSpearSwapConfig.save();
                        clearAndInit();
                    });
                }
                add(cx, y + ph - 76, bw, 26, "◀ Previous", b -> { modulePage = Math.max(0, modulePage - 1); clearAndInit(); });
                add(cx + bw + gap, y + ph - 76, bw, 26, "Next ▶", b -> { modulePage++; clearAndInit(); });
                add(cx, y + ph - 48, cw, 22, "Page " + (modulePage + 1) + " / " + pages + "  •  " + labels.size() + " matching modules", b -> {});
            }
            case 12 -> {
                spotifyClientField = new TextFieldWidget(textRenderer, cx, y1, cw, h, Text.literal("Spotify Client ID"));
                spotifyClientField.setMaxLength(96);
                spotifyClientField.setText(AutoSpearSwapConfig.spotifyClientId == null ? "" : AutoSpearSwapConfig.spotifyClientId);
                addDrawableChild(spotifyClientField);
                add(cx, y1 + 38, bw, h, "Save Client ID", b -> {
                    AutoSpearSwapConfig.spotifyClientId = spotifyClientField.getText().trim();
                    AutoSpearSwapConfig.save();
                    clearAndInit();
                });
                add(cx + bw + gap, y1 + 38, bw, h, "Connect Spotify", b -> SpotifyService.connect());
                add(cx, y1 + 76, bw, h, "Refresh playback", b -> SpotifyService.refresh());
                add(cx + bw + gap, y1 + 76, bw, h, "Disconnect", b -> SpotifyService.disconnect());
                add(cx, y1 + 114, bw, h, "Previous track", b -> SpotifyService.previous());
                add(cx + bw + gap, y1 + 114, bw, h, "Next track", b -> SpotifyService.next());
                add(cx, y1 + 152, bw, h, "Play / Pause", b -> SpotifyService.togglePlayback());
                add(cx + bw + gap, y1 + 152, bw, h, "Play dropout", b -> SpotifyService.playDropout());
            }
            case 11 -> {
                add(cx, y1, cw, h, "Reset HUD position", b -> { AutoSpearSwapConfig.hudX = 7; AutoSpearSwapConfig.hudY = 7; save(); });
                add(cx, y1 + 38, bw, h, "Move left", b -> { AutoSpearSwapConfig.hudX = Math.max(0, AutoSpearSwapConfig.hudX - 5); save(); });
                add(cx + bw + gap, y1 + 38, bw, h, "Move right", b -> { AutoSpearSwapConfig.hudX += 5; save(); });
                add(cx, y1 + 76, bw, h, "Move up", b -> { AutoSpearSwapConfig.hudY = Math.max(0, AutoSpearSwapConfig.hudY - 5); save(); });
                add(cx + bw + gap, y1 + 76, bw, h, "Move down", b -> { AutoSpearSwapConfig.hudY += 5; save(); });
            }
        }
        add(x + pw - 82, y + ph - 38, 70, 26, "Close", b -> close());
    }

    private void addSearchEntry(java.util.List<String> labels, java.util.List<Boolean> states,
                                java.util.List<Runnable> actions, String query, String name,
                                boolean enabled, Runnable action) {
        if (query.isEmpty() || name.toLowerCase(java.util.Locale.ROOT).contains(query)) {
            labels.add(name);
            states.add(enabled);
            actions.add(action);
        }
    }

    private int searchModule(int x, int y, int w, int h, int gap, String query, String name,
                             BooleanSupplier enabled, Runnable action, int index) {
        if (!query.isEmpty() && !name.toLowerCase(java.util.Locale.ROOT).contains(query)) return index;
        int bx = x + (index % 2) * (w + gap);
        int by = y + (index / 2) * 35;
        add(bx, by, w, h, (enabled.getAsBoolean() ? "● " : "○ ") + name, b -> {
            action.run();
            AutoSpearSwapConfig.save();
            clearAndInit();
        });
        return index + 1;
    }

    private void add(int x, int y, int w, int h, String label, ButtonWidget.PressAction action) {
        addDrawableChild(ButtonWidget.builder(Text.literal(label), action).dimensions(x, y, w, h).build());
    }

    private void toggle(int x, int y, int w, int h, String label, boolean value, Runnable action) {
        add(x, y, w, h, (value ? "●  " : "○  ") + label + (value ? "   ON" : "   OFF"), b -> { action.run(); save(); });
    }

    private void save() { AutoSpearSwapConfig.save(); clearAndInit(); }
    private void capture(KeyBinding key) { capturing = key; clearAndInit(); }
    private String keyText(String name, KeyBinding key) {
        return (capturing == key ? "PRESS A KEY  •  " : name + "  •  ") + key.getBoundKeyLocalizedText().getString();
    }

    private void openPackFolder() {
        if (client == null) return;
        try {
            java.nio.file.Files.createDirectories(client.getResourcePackDir());
            net.minecraft.util.Util.getOperatingSystem().open(client.getResourcePackDir().toFile());
        } catch (Exception e) {
            System.err.println("[Nexus] Could not open resource pack folder: " + e.getMessage());
        }
    }

    private void packs() {
        if (openingPacks || client == null) return;
        openingPacks = true;
        MinecraftClient mc = client;
        Screen parent = this;
        mc.getResourcePackManager().scanPacks();
        mc.setScreen(new PackScreen(mc.getResourcePackManager(), manager -> mc.reloadResources(),
            mc.getResourcePackDir(), Text.literal("Resource Packs")) {
            @Override public void close() { openingPacks = false; mc.setScreen(parent); }
        });
    }

    private static void performance() {
        MinecraftClient c = MinecraftClient.getInstance();
        c.options.getParticles().setValue(ParticlesMode.MINIMAL);
        c.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
        c.options.getEntityDistanceScaling().setValue(0.25);
        c.options.getBiomeBlendRadius().setValue(0);
        c.options.getEntityShadows().setValue(false);
        c.options.write();
    }

    private static void balancedPerformance() {
        MinecraftClient c = MinecraftClient.getInstance();
        c.options.getParticles().setValue(ParticlesMode.DECREASED);
        c.options.getCloudRenderMode().setValue(CloudRenderMode.FAST);
        c.options.getEntityDistanceScaling().setValue(0.75);
        c.options.getBiomeBlendRadius().setValue(1);
        c.options.getEntityShadows().setValue(false);
        c.options.write();
    }

    private static void visualQuality() {
        MinecraftClient c = MinecraftClient.getInstance();
        c.options.getParticles().setValue(ParticlesMode.ALL);
        c.options.getCloudRenderMode().setValue(CloudRenderMode.FANCY);
        c.options.getEntityDistanceScaling().setValue(1.0);
        c.options.getBiomeBlendRadius().setValue(5);
        c.options.getEntityShadows().setValue(true);
        c.options.write();
    }

    private static int visualPresetIndex = 0;
    private static final String[] VISUAL_PRESETS = {"Clean", "Competitive", "Vibrant", "Classic", "Low Effects"};

    private static void visualPreset() {
        MinecraftClient c = MinecraftClient.getInstance();
        visualPresetIndex = (visualPresetIndex + 1) % VISUAL_PRESETS.length;
        switch (visualPresetIndex) {
            case 1 -> {
                c.options.getParticles().setValue(ParticlesMode.MINIMAL);
                c.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
                c.options.getEntityDistanceScaling().setValue(0.5);
                c.options.getEntityShadows().setValue(false);
            }
            case 2 -> {
                c.options.getParticles().setValue(ParticlesMode.ALL);
                c.options.getCloudRenderMode().setValue(CloudRenderMode.FANCY);
                c.options.getEntityDistanceScaling().setValue(1.0);
                c.options.getEntityShadows().setValue(true);
            }
            case 3 -> {
                c.options.getParticles().setValue(ParticlesMode.DECREASED);
            }
            case 4 -> {
                c.options.getParticles().setValue(ParticlesMode.MINIMAL);
                c.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
                c.options.getEntityDistanceScaling().setValue(0.75);
                c.options.getEntityShadows().setValue(false);
            }
            default -> defaults();
        }
        c.options.write();
    }

    private static void defaults() {
        MinecraftClient c = MinecraftClient.getInstance();
        c.options.getParticles().setValue(ParticlesMode.ALL);
        c.options.getCloudRenderMode().setValue(CloudRenderMode.FANCY);
        c.options.getEntityDistanceScaling().setValue(1.0);
        c.options.getBiomeBlendRadius().setValue(2);
        c.options.getEntityShadows().setValue(true);
        c.options.write();
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (capturing != null) {
            capturing.setBoundKey(InputUtil.fromKeyCode(input));
            KeyBinding.updateKeysByCode();
            capturing = null;
            AutoSpearSwapConfig.save();
            clearAndInit();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        renderBackground(ctx, mx, my, delta);
        ctx.drawTexture(RenderPipelines.GUI_TEXTURED,
            Identifier.of("autospearswap", "textures/gui/overtonight-cover.jpg"),
            0, 0, 0.0f, 0.0f, width, height, 640, 640, 0x72FFFFFF);
        ctx.fill(0, 0, width, height, 0x9A09060A);
        int t = Math.max(0, Math.min(8, AutoSpearSwapConfig.uiTheme));
        int pw = Math.min(540, width - 20), ph = Math.min(440, height - 20);
        int x = (width - pw) / 2, y = (height - ph) / 2;
        int a = Math.round(255 * (AutoSpearSwapConfig.uiOpacity / 100.0f));
        ctx.fill(x - 3, y - 3, x + pw + 3, y + ph + 3, 0x66000000);
        ctx.fill(x, y, x + pw, y + ph, (PANEL[t] & 0x00FFFFFF) | (a << 24));
        ctx.fill(x, y, x + pw, y + 3, ACCENT[t]);
        ctx.fill(x, y + 3, x + 3, y + ph, ACCENT[t] & 0x66FFFFFF);
        ctx.fill(x + 8, y + 54, x + 132, y + ph - 8, (SIDE[t] & 0x00FFFFFF) | (a << 24));
        ctx.fill(x + 10, y + 48, x + pw - 10, y + 49, 0x883D7898);
        ctx.fill(x + 144, y + 55, x + pw - 14, y + 56, 0x443D7898);
        ctx.fill(x + 144, y + ph - 50, x + pw - 14, y + ph - 49, 0x443D7898);
        ctx.drawTextWithShadow(textRenderer, Text.literal("OVERTONIGHT"), x + 16, y + 13, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("CLIENT  /  AFTERHOURS"), x + 16, y + 27, ACCENT[t]);
        ctx.drawTextWithShadow(textRenderer, Text.literal("EST. AFTER DARK"), x + pw - 90, y + 17, 0xFF9CB4C8);
        ctx.drawTextWithShadow(textRenderer, Text.literal(TABS[tab].toUpperCase()), x + 150, y + 24, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal(capturing == null ? "CONFIGURE YOUR CLIENT" : "PRESS A KEY TO REBIND"), x + 150, y + 38, 0xFF9CB4C8);
        if (tab == 11) {
            ctx.drawTextWithShadow(textRenderer, Text.literal("HUD POSITION"), x + 150, y + 154, ACCENT[t]);
            ctx.drawTextWithShadow(textRenderer, Text.literal("Use the controls to move your on-screen HUD."), x + 150, y + 174, 0xFFEAF6FF);
            ctx.drawTextWithShadow(textRenderer, Text.literal("X: " + AutoSpearSwapConfig.hudX + "   Y: " + AutoSpearSwapConfig.hudY), x + 150, y + 190, 0xFF9CB4C8);
            ctx.drawTextWithShadow(textRenderer, Text.literal("Drag the preview below to position the HUD."), x + 150, y + 214, 0xFF9CB4C8);
            int px = x + 150 + (int) ((double) AutoSpearSwapConfig.hudX / Math.max(1, width - 92) * (pw - 270));
            int py = y + 238 + (int) ((double) AutoSpearSwapConfig.hudY / Math.max(1, height - 24) * 60);
            ctx.fill(x + 150, y + 238, x + pw - 20, y + 322, 0xAA0A111A);
            ctx.fill(px, py, px + 92, py + 24, 0xFF20384B);
            ctx.drawTextWithShadow(textRenderer, Text.literal("OVERTONIGHT HUD"), px + 8, py + 8, ACCENT[t]);
        }
        if (tab == 6) {
            ctx.drawTextWithShadow(textRenderer, Text.literal("VANILLA SKY TEXTURES"), x + 150, y + 154, ACCENT[t]);
            ctx.drawTextWithShadow(textRenderer, Text.literal("Resource packs can replace the sun, moon and clouds."), x + 150, y + 174, 0xFFEAF6FF);
            ctx.drawTextWithShadow(textRenderer, Text.literal("Custom skyboxes need a compatible sky-rendering mod."), x + 150, y + 190, 0xFF9CB4C8);
            ctx.drawTextWithShadow(textRenderer, Text.literal("Use the folder button, add your pack, then reopen Packs."), x + 150, y + 214, 0xFF9CB4C8);
        }
        if (tab == 5) {
            ctx.drawTextWithShadow(textRenderer, Text.literal("RESOURCE PACKS"), x + 150, y + 154, ACCENT[t]);
            ctx.drawTextWithShadow(textRenderer, Text.literal("Drop .zip packs into the opened folder."), x + 150, y + 174, 0xFFEAF6FF);
            ctx.drawTextWithShadow(textRenderer, Text.literal("Reopen the manager to rescan, enable, then press Done."), x + 150, y + 190, 0xFF9CB4C8);
        }
        if (tab == 12) {
            SpotifyService.pollIfNeeded();
            String status = SpotifyService.status();
            if (status.length() > 48) status = status.substring(0, 48) + "…";
            ctx.drawTextWithShadow(textRenderer, Text.literal("SPOTIFY  /  " + status), x + 150, y + 292, ACCENT[t]);
            ctx.drawTextWithShadow(textRenderer, Text.literal(SpotifyService.currentTrack()), x + 150, y + 316, 0xFFFFFFFF);
            ctx.drawTextWithShadow(textRenderer, Text.literal(SpotifyService.currentArtist()), x + 150, y + 334, 0xFFE2D7D9);
            ctx.drawTextWithShadow(textRenderer, Text.literal(SpotifyService.currentAlbum()), x + 150, y + 352, 0xFFBBAEB1);
            ctx.drawTextWithShadow(textRenderer, Text.literal(SpotifyService.progressText()), x + 150, y + 370, ACCENT[t]);
            ctx.drawTextWithShadow(textRenderer, Text.literal("Controls use your Spotify Connect device."), x + 150, y + 388, 0xFFBBAEB1);
        }
        if (tab == 8) {
            String sequence = MacroEngine.STEPS.isEmpty() ? "No steps yet" : "Sequence: " +
                MacroEngine.STEPS.stream().map(s -> s.action + "(" + s.ticks + "t)").limit(5)
                    .collect(java.util.stream.Collectors.joining("  >  "));
            ctx.drawTextWithShadow(textRenderer, Text.literal(sequence), x + 150, y + 340, ACCENT[t]);
            ctx.drawTextWithShadow(textRenderer, Text.literal("F8  •  " + (MacroEngine.isRunning() ? "RUNNING" : "READY")), x + 150, y + 356, 0xFF9CB4C8);
        }
        super.render(ctx, mx, my, delta);
    }


    @Override public void close() { if (client != null) client.setScreen(null); }
}
