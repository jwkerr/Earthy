package au.lupine.earthy.fabric.object.listener;

import au.lupine.earthy.common.object.Listener;
import au.lupine.earthy.common.object.Tickable;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class TickableListener implements Listener {

    @Override
    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> Tickable.tick());
    }
}
