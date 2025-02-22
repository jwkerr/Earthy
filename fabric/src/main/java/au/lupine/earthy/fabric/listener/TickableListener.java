package au.lupine.earthy.fabric.listener;

import au.lupine.earthy.fabric.object.base.Listener;
import au.lupine.earthy.fabric.object.base.Tickable;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class TickableListener implements Listener {

    @Override
    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> Tickable.tick());
    }
}
