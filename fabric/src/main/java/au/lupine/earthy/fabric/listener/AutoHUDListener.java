package au.lupine.earthy.fabric.listener;

import au.lupine.earthy.fabric.manager.SessionManager;
import au.lupine.earthy.fabric.object.base.Listener;
import au.lupine.earthy.fabric.object.config.Config;
import au.lupine.earthy.fabric.object.wrapper.HUDType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoHUDListener implements Listener {

    @Override
    public void register() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!SessionManager.getInstance().isPlayerOnEarthMC()) return;

            HUDType hud = Config.autoHUD;
            if (hud.equals(HUDType.NONE)) return;

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> handler.sendCommand(hud.getCommand()), 3L, TimeUnit.SECONDS);
        });
    }
}
