package dev.odithekitty.autospearswap;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Locale;

/**
 * Free, player-independent desktop media controls.
 * Uses the operating system's media-key channel, so no Spotify account,
 * developer application, API key, or paid subscription is needed.
 */
public final class MediaKeyService {
    private static volatile String status = "Ready · uses your default media player";
    private static Robot robot;

    private MediaKeyService() {}

    public static String status() { return status; }

    private static synchronized void press(int keyCode, String action) {
        if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
            status = "Global media keys are currently enabled for Windows only";
            return;
        }
        try {
            if (robot == null) robot = new Robot();
            robot.keyPress(keyCode);
            robot.delay(45);
            robot.keyRelease(keyCode);
            status = action + " · sent to Windows media session";
        } catch (AWTException | SecurityException error) {
            status = "Media key unavailable: " + error.getClass().getSimpleName();
        }
    }

    public static void togglePlayback() { press(KeyEvent.VK_MEDIA_PLAY_PAUSE, "Play / pause"); }
    public static void next() { press(KeyEvent.VK_MEDIA_NEXT_TRACK, "Next track"); }
    public static void previous() { press(KeyEvent.VK_MEDIA_PREV_TRACK, "Previous track"); }
}
