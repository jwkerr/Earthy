package au.lupine.earthy.fabric;

import au.lupine.earthy.fabric.module.*;
import au.lupine.earthy.fabric.object.base.Module;
import au.lupine.earthy.fabric.object.config.Config;
import au.lupine.emcapiclient.EMCAPIClient;
import au.lupine.emcapiclient.object.wrapper.Server;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EarthyFabric implements ClientModInitializer {

    public static final String ID = "earthy";

    private static final Logger LOGGER = LoggerFactory.getLogger(ID);
    private static final String LOG_PREFIX = "[Earthy] ";

    public static final List<Module> MODULES = List.of(
            AutoHUD.getInstance(),
            Cache.getInstance(),
            ChatPreview.getInstance(),
            Inspector.getInstance(),
            Session.getInstance()
    );

    private static EMCAPIClient api;

    @Override
    public void onInitializeClient() {
        Config.HANDLER.load();

        api = new EMCAPIClient(new Server(Config.server.toLowerCase()));

        for (Module module : MODULES) {
            module.enable();
        }
    }

    /**
     * @return A library giving ease of access to most of EarthMC's API
     */
    public static EMCAPIClient getAPI() {
        return api;
    }

    public static void logInfo(String msg) {
        LOGGER.info(LOG_PREFIX + "{}", msg);
    }
}
