package au.lupine.earthy.fabric.object.config;

import au.lupine.earthy.fabric.EarthyFabric;
import au.lupine.earthy.fabric.object.wrapper.HUDType;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.autogen.EnumCycler;
import dev.isxander.yacl3.config.v2.api.autogen.StringField;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

public class Config {

    public static final ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(ResourceLocation.fromNamespaceAndPath(EarthyFabric.ID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("earthy.json"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .build())
            .build();

    private static final String SETTINGS = "settings";

    @SerialEntry
    @AutoGen(category = SETTINGS)
    @StringField
    public static String server = "aurora";

    @SerialEntry
    @AutoGen(category = SETTINGS)
    @Boolean(colored = true)
    public static boolean showAffiliationAboveHead = true;

    @SerialEntry
    @AutoGen(category = SETTINGS)
    @Boolean(colored = true)
    public static boolean previewCurrentChatChannel = true;

    @SerialEntry
    @AutoGen(category = SETTINGS)
    @Boolean(colored = true)
    public static boolean showUnobscuredPlayersOnMap = true;

    @SerialEntry
    @AutoGen(category = SETTINGS)
    @Boolean(colored = true)
    public static boolean warnWhenOverfishing = true;

    @SerialEntry
    @AutoGen(category = SETTINGS)
    @EnumCycler
    public static HUDType autoHUD = HUDType.PERM;
}
