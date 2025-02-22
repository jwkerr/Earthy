package au.lupine.earthy.fabric.manager;

import au.lupine.earthy.fabric.EarthyFabric;
import au.lupine.earthy.fabric.object.base.Listener;
import au.lupine.earthy.fabric.object.base.Manager;
import au.lupine.earthy.fabric.object.base.Tickable;
import au.lupine.emcapiclient.object.apiobject.Player;
import au.lupine.emcapiclient.object.exception.FailedRequestException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;

import java.util.List;
import java.util.concurrent.*;

public class SessionManager extends Manager implements Listener, Tickable {

    private static SessionManager instance;

    private static final List<Player> PLAYER_INFO = new CopyOnWriteArrayList<>();

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public static List<Player> getPlayerInfo() {
        return PLAYER_INFO;
    }

    @Override
    public void register() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            scheduler.schedule(() -> {
                if (!isPlayerOnEarthMC()) return;

                updateOnlinePlayers();
            }, 6L, TimeUnit.SECONDS);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> PLAYER_INFO.clear());
    }

    @Override
    public int getInterval() {
        return 6000; // 5 minutes
    }

    @Override
    public void onTick() {
        if (!isPlayerOnEarthMC()) return;

        updateOnlinePlayers();
    }

    private void updateOnlinePlayers() {
        ClientPacketListener cpl = Minecraft.getInstance().getConnection();
        if (cpl == null) return;

        CompletableFuture.runAsync(() -> {
            try {
                List<Player> online = EarthyFabric.getAPI().getPlayersByUUIDs(cpl.getOnlinePlayers()
                        .stream()
                        .map(player -> player.getProfile().getId())
                        .toList()
                );

                PLAYER_INFO.clear();
                PLAYER_INFO.addAll(online);
            } catch (FailedRequestException ignored) {}
        });
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
