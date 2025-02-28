package au.lupine.earthy.fabric.module;

import au.lupine.earthy.fabric.object.base.Module;
import au.lupine.earthy.fabric.object.config.Config;
import au.lupine.earthy.fabric.object.wrapper.HUDType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class AutoHUD extends Module {

    private static AutoHUD instance;

    private AutoHUD() {}

    public static AutoHUD getInstance() {
        if (instance == null) instance = new AutoHUD();
        return instance;
    }

    @Override
    public void enable() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            scheduler.schedule(() -> {
                if (!Session.getInstance().isPlayerOnEarthMC()) return;

                HUDType hud = Config.autoHUD;
                if (!hud.equals(HUDType.NONE)) handler.sendCommand(hud.getCommand());
            }, 6L, TimeUnit.SECONDS);
        });
    }
}
