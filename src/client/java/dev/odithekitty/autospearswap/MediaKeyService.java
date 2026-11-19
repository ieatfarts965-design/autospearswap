package dev.odithekitty.autospearswap;

import java.io.IOException;
import java.util.Locale;

/**
 * Free, player-independent Windows media controls.
 * Sends standard Windows media virtual-key events; no music service API is used.
 */
public final class MediaKeyService {
    private static volatile String status = "Ready · uses your default media player";

    private MediaKeyService() {}

    public static String status() { return status; }

    private static void press(int virtualKey, String action) {
        if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
            status = "Global media controls currently require Windows";
            return;
        }
        status = "Sending " + action.toLowerCase(Locale.ROOT) + "…";
        Thread worker = new Thread(() -> {
            String source = "using System; using System.Runtime.InteropServices; "
                + "public static class OvertonightMediaKey { "
                + "[DllImport(\"user32.dll\")] public static extern void keybd_event(byte key, byte scan, uint flags, UIntPtr extra); }";
            String script = "$ErrorActionPreference='Stop'; Add-Type -TypeDefinition '" + source
                + "'; [OvertonightMediaKey]::keybd_event([byte]" + virtualKey
                + ",[byte]0,[uint32]0,[UIntPtr]::Zero); Start-Sleep -Milliseconds 35; "
                + "[OvertonightMediaKey]::keybd_event([byte]" + virtualKey
                + ",[byte]0,[uint32]2,[UIntPtr]::Zero)";
            Process process = null;
            try {
                process = new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive",
                    "-ExecutionPolicy", "Bypass", "-Command", script)
                    .redirectErrorStream(true).start();
                if (!process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                    status = "Windows media command timed out";
                } else if (process.exitValue() == 0) {
                    status = action + " · sent to Windows media session";
                } else {
                    status = "Windows media command failed (PowerShell)";
                }
            } catch (IOException error) {
                status = "PowerShell unavailable: " + error.getClass().getSimpleName();
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                status = "Media command interrupted";
            } finally {
                if (process != null) process.destroy();
            }
        }, "Overtonight-Media-Key");
        worker.setDaemon(true);
        worker.start();
    }

    public static void togglePlayback() { press(0xB3, "Play / pause"); }
    public static void next() { press(0xB0, "Next track"); }
    public static void previous() { press(0xB1, "Previous track"); }
}
