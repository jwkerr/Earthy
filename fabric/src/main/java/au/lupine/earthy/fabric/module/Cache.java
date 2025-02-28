package au.lupine.earthy.fabric.module;

import au.lupine.earthy.fabric.EarthyFabric;
import au.lupine.earthy.fabric.object.base.Module;
import au.lupine.earthy.fabric.object.base.Tickable;
import au.lupine.emcapiclient.object.apiobject.Player;
import au.lupine.emcapiclient.object.apiobject.Town;
import au.lupine.emcapiclient.object.exception.FailedRequestException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

import java.util.List;
import java.util.concurrent.*;

public final class Cache extends Module {

    private static Cache instance;

    private static final List<Player> CACHED_PLAYERS = new CopyOnWriteArrayList<>();
    private static final List<Town> CACHED_TOWNS = new CopyOnWriteArrayList<>();

    private Cache() {}

    public static Cache getInstance() {
        if (instance == null) instance = new Cache();
        return instance;
    }

    @Override
    public void enable() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            scheduler.schedule(() -> {
                updateCachedPlayers();
            }, 6L, TimeUnit.SECONDS);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            CACHED_PLAYERS.clear();
            CACHED_TOWNS.clear();
        });

        Tickable.register(() -> {
            if (!Session.getInstance().isPlayerOnEarthMC()) return;

            updateCachedPlayers();
        }, 3L, TimeUnit.MINUTES);
    }

    private void updateCachedPlayers() {
        ClientPacketListener cpl = Minecraft.getInstance().getConnection();
        if (cpl == null) return;

        CompletableFuture.runAsync(() -> {
            try {
                List<Player> online = EarthyFabric.getAPI().getPlayersByUUIDs(cpl.getOnlinePlayers()
                        .stream()
                        .map(player -> player.getProfile().getId())
                        .toList()
                );

                CACHED_PLAYERS.clear();
                CACHED_PLAYERS.addAll(online);
            } catch (FailedRequestException ignored) {}
        });
    }

    private void updateCachedTowns() {
        CompletableFuture.runAsync(() -> {
            try {
                List<Town> towns = EarthyFabric.getAPI().getAllTowns();

                CACHED_TOWNS.clear();
                CACHED_TOWNS.addAll(towns);
            } catch (FailedRequestException ignored) {}
        });
    }

    public List<Player> getCachedPlayers() {
        return CACHED_PLAYERS;
    }

    public static List<Town> getCachedTowns() {
        return CACHED_TOWNS;
    }
}
