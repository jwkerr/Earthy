package au.lupine.earthy.fabric.object.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Tickable {

    private static final List<Tickable> TICKABLES = new ArrayList<>();
    private static final Map<Tickable, Integer> TICKS_PASSED = new HashMap<>();

    private final Runnable runnable;
    private final long interval;

    public Tickable(Runnable runnable, long interval) {
        this.runnable = runnable;
        this.interval = interval;
    }

    public static Tickable of(Runnable runnable, long interval) {
        return new Tickable(runnable, interval);
    }

    public static Tickable of(Runnable runnable) {
        return of(runnable, 0L);
    }

    public Runnable getRunnable() {
        return runnable;
    }

    /**
     * @return The Minecraft ticks between each time this object is ticked
     */
    public long getInterval() {
        return interval;
    }

    public static void tick() {
        for (Tickable tickable : TICKABLES) {
            int ticksPassed = TICKS_PASSED.getOrDefault(tickable, 0);

            if (ticksPassed >= tickable.getInterval()) {
                tickable.getRunnable().run();
                TICKS_PASSED.put(tickable, 0);
                continue;
            }

            TICKS_PASSED.put(tickable, ticksPassed + 1);
        }
    }

    public static Tickable register(@NotNull Tickable tickable) {
        TICKABLES.add(tickable);
        return tickable;
    }

    public static Tickable of(@NotNull Runnable runnable, long time, @NotNull TimeUnit unit) {
        long ticks = asTicks(time, unit);
        return new Tickable(runnable, ticks);
    }

    public static Tickable register(@NotNull Runnable runnable, long interval) {
        return register(Tickable.of(runnable, interval));
    }

    public static Tickable register(@NotNull Runnable runnable) {
        return register(Tickable.of(runnable));
    }

    public static Tickable register(@NotNull Runnable runnable, long time, @NotNull TimeUnit unit) {
        return register(Tickable.of(runnable, time, unit));
    }

    public static Tickable unregister(@Nullable Tickable tickable) {
        TICKABLES.remove(tickable);
        return tickable;
    }

    private static long asTicks(long time, @NotNull TimeUnit unit) {
        return Math.round(unit.toSeconds(time) * 20);
    }
}
