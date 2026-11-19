package dev.odithekitty.autospearswap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Spotify Connect remote for Overtonight Client.
 * Uses OAuth Authorization Code + PKCE; no client secret is embedded or stored.
 * Spotify remains the audio player. This module only reads metadata and sends remote controls.
 */
public final class SpotifyService {
    private static final String REDIRECT_URI = "http://127.0.0.1:8888/callback";
    private static final String SCOPES = "user-read-playback-state user-modify-playback-state";
    private static final String DROPOUT_TRACK_URI = "spotify:track:5h8bFExaOKRSeYVMEwOWpq";
    private static final Path TOKEN_FILE = FabricLoader.getInstance().getConfigDir().resolve("overtonight-spotify.json");
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private static final SecureRandom RANDOM = new SecureRandom();

    private static volatile String accessToken = "";
    private static volatile String refreshToken = "";
    private static volatile String state = "Not connected";
    private static volatile String track = "Nothing playing";
    private static volatile String artist = "Connect Spotify to see your music";
    private static volatile String album = "Overtonight Client";
    private static volatile boolean playing;
    private static volatile long progressMs;
    private static volatile long durationMs;
    private static volatile long expiresAt;
    private static volatile long lastPollAt;
    private static volatile String pendingVerifier = "";
    private static volatile String pendingState = "";
    private static volatile HttpServer callbackServer;

    private SpotifyService() {}

    public static void load() {
        try {
            if (Files.exists(TOKEN_FILE)) {
                JsonObject saved = JsonParser.parseString(Files.readString(TOKEN_FILE)).getAsJsonObject();
                refreshToken = saved.has("refresh_token") ? saved.get("refresh_token").getAsString() : "";
                if (!refreshToken.isBlank()) state = "Spotify account saved · refresh to connect";
            }
        } catch (Exception e) {
            state = "Saved Spotify session could not be read";
        }
    }

    public static String status() { return state; }
    public static String currentTrack() { return track; }
    public static String currentArtist() { return artist; }
    public static String currentAlbum() { return album; }
    public static boolean isPlaying() { return playing; }
    public static boolean isConnected() { return !refreshToken.isBlank() || !accessToken.isBlank(); }

    public static String progressText() {
        return formatTime(progressMs) + " / " + formatTime(durationMs);
    }

    private static String formatTime(long millis) {
        long seconds = Math.max(0, millis / 1000);
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    public static void connect() {
        String clientId = AutoSpearSwapConfig.spotifyClientId == null ? "" : AutoSpearSwapConfig.spotifyClientId.trim();
        if (clientId.isBlank()) {
            state = "Enter and save your Spotify Client ID first";
            return;
        }
        try {
            if (callbackServer != null) callbackServer.stop(0);
            byte[] verifierBytes = new byte[48];
            RANDOM.nextBytes(verifierBytes);
            pendingVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(verifierBytes);
            byte[] stateBytes = new byte[24];
            RANDOM.nextBytes(stateBytes);
            pendingState = Base64.getUrlEncoder().withoutPadding().encodeToString(stateBytes);
            String challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(
                MessageDigest.getInstance("SHA-256").digest(pendingVerifier.getBytes(StandardCharsets.US_ASCII)));

            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8888), 0);
            callbackServer = server;
            server.createContext("/callback", SpotifyService::handleCallback);
            server.start();

            String url = "https://accounts.spotify.com/authorize"
                + "?client_id=" + enc(clientId)
                + "&response_type=code"
                + "&redirect_uri=" + enc(REDIRECT_URI)
                + "&scope=" + enc(SCOPES)
                + "&code_challenge_method=S256"
                + "&code_challenge=" + enc(challenge)
                + "&state=" + enc(pendingState);
            state = "Waiting for Spotify authorization in your browser…";
            Util.getOperatingSystem().open(URI.create(url));
        } catch (Exception e) {
            state = "Could not start Spotify login: " + safeMessage(e);
            stopCallbackServer();
        }
    }

    private static void handleCallback(HttpExchange exchange) throws IOException {
        Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
        String returnedState = query.getOrDefault("state", "");
        String code = query.getOrDefault("code", "");
        String error = query.getOrDefault("error", "");
        boolean valid = !pendingState.isBlank() && pendingState.equals(returnedState);
        String response;
        if (!valid) {
            response = "<html><body><h2>Overtonight Client</h2><p>Spotify login state did not match. You can close this tab.</p></body></html>";
            state = "Spotify login rejected: state mismatch";
        } else if (!error.isBlank()) {
            response = "<html><body><h2>Overtonight Client</h2><p>Spotify access was not granted. You can close this tab.</p></body></html>";
            state = "Spotify authorization was cancelled";
        } else if (code.isBlank()) {
            response = "<html><body><h2>Overtonight Client</h2><p>No authorization code received.</p></body></html>";
            state = "Spotify returned no authorization code";
        } else {
            response = "<html><body style='background:#10090d;color:#eee;font:16px sans-serif'><h2>Overtonight Client</h2><p>Spotify connected. You can close this tab and return to Minecraft.</p></body></html>";
            state = "Spotify approved · exchanging token…";
            String verifier = pendingVerifier;
            CompletableFuture.runAsync(() -> exchangeCode(code, verifier));
        }
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
        stopCallbackServer();
    }

    private static synchronized void exchangeCode(String code, String verifier) {
        try {
            String form = "client_id=" + enc(AutoSpearSwapConfig.spotifyClientId.trim())
                + "&grant_type=authorization_code"
                + "&code=" + enc(code)
                + "&redirect_uri=" + enc(REDIRECT_URI)
                + "&code_verifier=" + enc(verifier);
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://accounts.spotify.com/api/token"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IOException("Spotify token exchange returned HTTP " + response.statusCode());
            }
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            accessToken = json.get("access_token").getAsString();
            if (json.has("refresh_token")) refreshToken = json.get("refresh_token").getAsString();
            long seconds = json.has("expires_in") ? json.get("expires_in").getAsLong() : 3600;
            expiresAt = System.currentTimeMillis() + Math.max(60, seconds - 60) * 1000L;
            saveTokens();
            state = "Connected to Spotify";
            refreshPlaybackNow();
        } catch (Exception e) {
            state = "Spotify login failed: " + safeMessage(e);
        }
    }

    private static void saveTokens() {
        try {
            Files.createDirectories(TOKEN_FILE.getParent());
            JsonObject json = new JsonObject();
            json.addProperty("refresh_token", refreshToken);
            Files.writeString(TOKEN_FILE, json.toString());
        } catch (Exception e) {
            state = "Connected, but could not save Spotify session";
        }
    }

    private static synchronized boolean refreshAccessToken() {
        if (refreshToken.isBlank()) return false;
        String clientId = AutoSpearSwapConfig.spotifyClientId == null ? "" : AutoSpearSwapConfig.spotifyClientId.trim();
        if (clientId.isBlank()) {
            state = "Save your Spotify Client ID to refresh the session";
            return false;
        }
        try {
            String form = "client_id=" + enc(clientId)
                + "&grant_type=refresh_token"
                + "&refresh_token=" + enc(refreshToken);
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://accounts.spotify.com/api/token"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IOException("Spotify refresh returned HTTP " + response.statusCode());
            }
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            accessToken = json.get("access_token").getAsString();
            if (json.has("refresh_token")) refreshToken = json.get("refresh_token").getAsString();
            long seconds = json.has("expires_in") ? json.get("expires_in").getAsLong() : 3600;
            expiresAt = System.currentTimeMillis() + Math.max(60, seconds - 60) * 1000L;
            saveTokens();
            return true;
        } catch (Exception e) {
            state = "Spotify session expired: " + safeMessage(e);
            return false;
        }
    }

    private static String token() {
        if (!accessToken.isBlank() && System.currentTimeMillis() < expiresAt) return accessToken;
        return refreshAccessToken() ? accessToken : "";
    }

    private static HttpResponse<String> api(String method, String path, String jsonBody) throws Exception {
        String bearer = token();
        if (bearer.isBlank()) throw new IOException("Connect your Spotify account first");
        HttpResponse<String> response = sendApi(method, path, jsonBody, bearer);
        if (response.statusCode() == 401 && refreshAccessToken()) {
            response = sendApi(method, path, jsonBody, accessToken);
        }
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            String message = "Spotify API returned HTTP " + response.statusCode();
            if (response.statusCode() == 403) message += " · playback control may require Premium";
            if (response.statusCode() == 404) message += " · open Spotify on a device first";
            throw new IOException(message);
        }
        return response;
    }

    private static HttpResponse<String> sendApi(String method, String path, String body, String bearer) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("https://api.spotify.com/v1" + path))
            .timeout(Duration.ofSeconds(20))
            .header("Authorization", "Bearer " + bearer);
        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(body));
        }
        return HTTP.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    public static void pollIfNeeded() {
        if (!isConnected()) return;
        long now = System.currentTimeMillis();
        if (now - lastPollAt < 6000) return;
        lastPollAt = now;
        CompletableFuture.runAsync(SpotifyService::refreshPlaybackNow);
    }

    public static void refresh() {
        CompletableFuture.runAsync(SpotifyService::refreshPlaybackNow);
    }

    private static void refreshPlaybackNow() {
        try {
            HttpResponse<String> response = api("GET", "/me/player", null);
            if (response.statusCode() == 204 || response.body() == null || response.body().isBlank()) {
                track = "Nothing playing";
                artist = "Open Spotify and start a track";
                album = "";
                playing = false;
                progressMs = 0;
                durationMs = 0;
                state = "Connected · no active playback";
                return;
            }
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            playing = root.has("is_playing") && root.get("is_playing").getAsBoolean();
            progressMs = root.has("progress_ms") && !root.get("progress_ms").isJsonNull()
                ? root.get("progress_ms").getAsLong() : 0;
            if (root.has("item") && root.get("item").isJsonObject()) {
                JsonObject item = root.getAsJsonObject("item");
                track = item.has("name") ? item.get("name").getAsString() : "Unknown track";
                durationMs = item.has("duration_ms") ? item.get("duration_ms").getAsLong() : 0;
                JsonArray artists = item.has("artists") ? item.getAsJsonArray("artists") : new JsonArray();
                StringBuilder names = new StringBuilder();
                for (int i = 0; i < artists.size(); i++) {
                    if (i > 0) names.append(", ");
                    names.append(artists.get(i).getAsJsonObject().get("name").getAsString());
                }
                artist = names.length() == 0 ? "Unknown artist" : names.toString();
                album = item.has("album") ? item.getAsJsonObject("album").get("name").getAsString() : "";
                state = playing ? "Playing on Spotify" : "Spotify paused";
            } else {
                track = "Nothing playing";
                artist = "No active track";
                album = "";
                durationMs = 0;
                state = "Connected · no active playback";
            }
        } catch (Exception e) {
            state = "Spotify: " + safeMessage(e);
        }
    }

    public static void togglePlayback() {
        command(() -> {
            String endpoint = playing ? "/me/player/pause" : "/me/player/play";
            api("PUT", endpoint, "{}");
            refreshPlaybackNow();
        });
    }

    public static void next() { command(() -> { api("POST", "/me/player/next", null); refreshPlaybackNow(); }); }
    public static void previous() { command(() -> { api("POST", "/me/player/previous", null); refreshPlaybackNow(); }); }

    public static void playDropout() {
        command(() -> {
            JsonObject body = new JsonObject();
            JsonArray uris = new JsonArray();
            uris.add(DROPOUT_TRACK_URI);
            body.add("uris", uris);
            api("PUT", "/me/player/play", body.toString());
            refreshPlaybackNow();
        });
    }

    private static void command(CheckedAction action) {
        CompletableFuture.runAsync(() -> {
            try {
                action.run();
            } catch (Exception e) {
                state = "Spotify: " + safeMessage(e);
            }
        });
    }

    public static void disconnect() {
        accessToken = "";
        refreshToken = "";
        expiresAt = 0;
        track = "Nothing playing";
        artist = "Connect Spotify to see your music";
        album = "Overtonight Client";
        playing = false;
        progressMs = 0;
        durationMs = 0;
        state = "Not connected";
        try { Files.deleteIfExists(TOKEN_FILE); } catch (IOException ignored) {}
    }

    private static void stopCallbackServer() {
        HttpServer server = callbackServer;
        callbackServer = null;
        if (server != null) {
            try { server.stop(0); } catch (Exception ignored) {}
        }
    }

    private static Map<String, String> parseQuery(String raw) {
        Map<String, String> values = new HashMap<>();
        if (raw == null || raw.isBlank()) return values;
        for (String part : raw.split("&")) {
            String[] pair = part.split("=", 2);
            String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
            String value = pair.length > 1 ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8) : "";
            values.put(key, value);
        }
        return values;
    }

    private static String enc(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String safeMessage(Exception e) {
        String message = e.getMessage();
        return message == null || message.isBlank() ? e.getClass().getSimpleName() : message;
    }

    @FunctionalInterface
    private interface CheckedAction { void run() throws Exception; }
}
