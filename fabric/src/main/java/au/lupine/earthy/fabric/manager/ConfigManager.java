package au.lupine.earthy.fabric.manager;

import au.lupine.earthy.fabric.object.base.Manager;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import au.lupine.earthy.common.Earthy;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public class ConfigManager extends Manager {

    public static final ConfigClassHandler<ConfigManager> HANDLER = ConfigClassHandler.createBuilder(ConfigManager.class)
            .id(ResourceLocation.fromNamespaceAndPath(Earthy.ID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("earthy.json"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .build())
            .build();

    private static final String SETTINGS = "settings";

    public static ConfigManager getInstance() {
        return HANDLER.instance();
    }

    public Screen getScreen(Screen parent) {
        return HANDLER.generateGui().generateScreen(parent);
    }
}
