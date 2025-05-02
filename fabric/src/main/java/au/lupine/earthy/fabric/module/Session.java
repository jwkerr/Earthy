package au.lupine.earthy.fabric.module;

import au.lupine.earthy.fabric.EarthyFabric;
import au.lupine.earthy.fabric.object.base.Module;
import au.lupine.earthy.fabric.object.base.Tickable;
import au.lupine.earthy.fabric.object.wrapper.Authentication;
import au.lupine.emcapiclient.object.apiobject.Player;
import au.lupine.emcapiclient.object.identifier.NationIdentifier;
import au.lupine.emcapiclient.object.identifier.TownIdentifier;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class Session extends Module {

    private static Session instance;

    private static Authentication authentication = Authentication.UNAUTHENTICATED;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) instance = new Session();
        return instance;
    }

    @Override
    public void enable() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> Tickable.tick());

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!isPlayerOnEarthMC()) return;
            authenticate();
        });
    }

    public void authenticate() {
        CompletableFuture.runAsync(() -> {
            try {
                HttpResponse<String> response = HttpClient.newHttpClient().send(
                        HttpRequest.newBuilder().GET()
                                .uri(URI.create("https://gist.githubusercontent.com/jwkerr/4b6fcbd438fc546f23e57210077b00ce/raw/authenticated.json"))
                                .build(),
                        HttpResponse.BodyHandlers.ofString()
                );

                String body = response.body();
                UUID uuid = Minecraft.getInstance().getGameProfile().getId();

                JsonObject object = JsonParser.parseString(body).getAsJsonObject();
                if (isUUIDAuthenticated(uuid, object.get("users").getAsJsonArray(), null)) {
                    authentication = Authentication.AUTHENTICATED;
                    return;
                }

                Player player = EarthyFabric.getAPI().getPlayerByUUID(uuid);
                if (player == null) {
                    authentication = Authentication.FAILED;
                    return;
                }

                TownIdentifier town = player.getTown();
                NationIdentifier nation = player.getNation();

                if (town != null) {
                    if (isUUIDAuthenticated(town.getUUID(), object.get("towns").getAsJsonArray(), Instant.ofEpochMilli(player.getJoinedTownAt()))) {
                        authentication = Authentication.AUTHENTICATED;
                        return;
                    }

                    if (nation != null && isUUIDAuthenticated(nation.getUUID(), object.get("nations").getAsJsonArray(), Instant.ofEpochMilli(player.getJoinedTownAt()))) {
                        authentication = Authentication.AUTHENTICATED;
                        return;
                    }
                }

                authentication = Authentication.UNAUTHENTICATED;
            } catch (Exception e) {
                authentication = Authentication.FAILED;
            }
        });
    }

    private boolean isUUIDAuthenticated(UUID uuid, JsonArray array, @Nullable Instant time) {
        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();

            try {
                if (object.get("uuid").getAsString().equals(uuid.toString())) {
                    if (time == null) return true;

                    JsonElement minSecondsElement = object.get("min_seconds");
                    if (minSecondsElement == null) return true;

                    long minSeconds = minSecondsElement.getAsLong();
                    EarthyFabric.logInfo(String.valueOf(minSeconds));
                    EarthyFabric.logInfo(String.valueOf(Duration.between(time, Instant.now()).getSeconds()));
                    return Duration.between(time, Instant.now()).getSeconds() > minSeconds;
                }
            } catch (Exception ignored) {}
        }

        return false;
    }

    public boolean isPlayerAuthenticated() {
        return authentication.equals(Authentication.AUTHENTICATED);
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public boolean isPlayerOnEarthMC() {
        ClientPacketListener cpl = Minecraft.getInstance().getConnection();
        if (cpl == null) return false;

        String brand = cpl.serverBrand();
        if (brand == null) return false;

        ServerData server = cpl.getServerData();
        if (server == null) return false;

        String ip = server.ip;
        String[] split = ip.split("\\.");

        if (split.length <= 1) return false;

        int finalIndex = split.length - 1;
        return split[finalIndex - 1].equalsIgnoreCase("earthmc") &&
                split[finalIndex].equalsIgnoreCase("net") &&
                (
                        brand.equals("§aPremium activated! Thank you for the support!§r (Velocity)") ||
                        brand.equals("§cPremium not activated, /Premium§r (Velocity)")
                );
    }

    public boolean isPlayerInOverworld() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return false;

        return level.dimension().equals(ClientLevel.OVERWORLD);
    }
}
