package au.lupine.earthy.fabric.manager;

import au.lupine.earthy.fabric.object.HeadData;
import au.lupine.earthy.fabric.object.base.Manager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeadDataManager extends Manager {

    private static HeadDataManager instance;

    private static final List<String> HEAD_CATEGORIES = List.of("alphabet", "animals", "blocks", "decoration", "food-drinks", "humanoid", "humans", "miscellaneous", "monsters", "plants");
    private static final Map<String, HeadData> HEAD_MAP = new HashMap<>();

    private HeadDataManager() {}

    public static HeadDataManager getInstance() {
        if (instance == null) instance = new HeadDataManager();
        return instance;
    }

    @Override
    public void enable() {
        for (String category : HEAD_CATEGORIES) {
            InputStream is = HeadDataManager.class.getClassLoader().getResourceAsStream("assets/earthy/categories/" + category + ".json");
            if (is == null) continue;

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();

            for (JsonElement element : array) {
                JsonObject object = element.getAsJsonObject();
                String value = object.get("value").getAsString();
                String name = object.get("name").getAsString();
                List<String> tags = List.of(object.get("tags").getAsString().split(","));

                HeadData vh = new HeadData(value, name, tags);
                HEAD_MAP.put(value, vh);
            }
        }
    }

    public @Nullable HeadData getHeadInfoByValue(@NotNull String value) {
        return HEAD_MAP.get(value);
    }
}
