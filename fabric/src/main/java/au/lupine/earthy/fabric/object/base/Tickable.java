package au.lupine.earthy.fabric.object.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface Tickable {

    List<Tickable> TICKABLES = new ArrayList<>();
    Map<Tickable, Integer> TICKS_PASSED = new HashMap<>();

    /**
     * @return The Minecraft ticks between each time this object is ticked
     */
    default int getInterval() {
        return 0;
    }

    static void tick() {
        for (Tickable tickable : TICKABLES) {
            int ticksPassed = TICKS_PASSED.getOrDefault(tickable, 0);

            if (ticksPassed >= tickable.getInterval()) {
                tickable.onTick();
                TICKS_PASSED.put(tickable, 0);
                continue;
            }

            TICKS_PASSED.put(tickable, ticksPassed + 1);
        }
    }

    default void startTick() {
        TICKABLES.add(this);
    }

    void onTick();
}
