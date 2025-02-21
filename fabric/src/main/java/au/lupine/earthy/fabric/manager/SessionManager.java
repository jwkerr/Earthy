package au.lupine.earthy.fabric.manager;

import au.lupine.earthy.common.Earthy;
import au.lupine.earthy.common.object.Listener;
import au.lupine.earthy.common.object.Tickable;
import au.lupine.earthy.fabric.EarthyFabric;
import au.lupine.earthy.fabric.object.base.Manager;
import au.lupine.emcapiclient.object.apiobject.Player;
import au.lupine.emcapiclient.object.exception.FailedRequestException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

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
            if (!isPlayerOnEarthMC()) return;

            List<Player> online = getOnlinePlayers();
            PLAYER_INFO.clear();
            PLAYER_INFO.addAll(online);
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

        List<Player> online = getOnlinePlayers();
        PLAYER_INFO.clear();
        PLAYER_INFO.addAll(online);
    }

    private List<Player> getOnlinePlayers() {
        List<Player> online = new ArrayList<>();

        ClientPacketListener cpl = Minecraft.getInstance().getConnection();
        if (cpl == null) return online;

        CompletableFuture.runAsync(() -> {
            try {
                List<Player> all = Earthy.getAPI().getPlayersByUUIDs(cpl.getOnlinePlayers()
                        .stream()
                        .map(player -> player.getProfile().getId())
                        .toList()
                );

                online.addAll(all);
            } catch (FailedRequestException ignored) {}
        });

        return online;
    }

    public boolean isPlayerOnEarthMC() {
        ServerData server = Minecraft.getInstance().getCurrentServer();
        if (server == null) return false;

        String ip = server.ip;
        String[] split = ip.split("\\.");

        if (split.length <= 1) return false;

        int finalIndex = split.length - 1;
        return split[finalIndex - 1].equalsIgnoreCase("earthmc") && split[finalIndex].equalsIgnoreCase("net");
    }

    public boolean isPlayerInOverworld() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return false;

        return level.dimension().equals(ClientLevel.OVERWORLD);
    }
}
