package au.lupine.earthy.fabric;

import au.lupine.earthy.common.Earthy;
import au.lupine.earthy.fabric.manager.HeadDataManager;
import au.lupine.earthy.fabric.manager.SessionManager;
import au.lupine.earthy.fabric.object.base.Manager;
import au.lupine.earthy.fabric.object.listener.ClientStoppingListener;
import au.lupine.earthy.fabric.object.tickable.InspectionTickable;
import au.lupine.earthy.fabric.object.listener.TickableListener;
import au.lupine.earthy.fabric.manager.ConfigManager;
import au.lupine.earthy.common.object.Listener;
import au.lupine.earthy.common.object.Tickable;
import net.fabricmc.api.ClientModInitializer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EarthyFabric implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Earthy.ID);
    private static final String LOG_PREFIX = "[Earthy] ";

    public static final List<Manager> MANAGERS = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        ConfigManager.HANDLER.load();

        registerManagers(
                ConfigManager.getInstance(),
                HeadDataManager.getInstance(),
                SessionManager.getInstance()
        );

        for (Manager manager : MANAGERS) {
            manager.enable();
        }

        registerListeners(
                new ClientStoppingListener(),
                new TickableListener(),
                SessionManager.getInstance()
        );

        registerTickables(
                new InspectionTickable()
        );
    }

    private void registerManagers(@NotNull Manager... managers) {
        for (Manager manager : managers) {
            if (!MANAGERS.contains(manager)) MANAGERS.add(manager);
        }
    }

    public static void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            listener.register();
            logInfo("Event listener " + listener.getClass().getSimpleName() + " registered");
        }
    }

    public static void registerTickables(Tickable... tickables) {
        for (Tickable tickable : tickables) {
            tickable.register();
            logInfo("Tickable " + tickable.getClass().getSimpleName() + " registered");
        }
    }

    public static void logInfo(String msg) {
        LOGGER.info(LOG_PREFIX + "{}", msg);
    }
}
