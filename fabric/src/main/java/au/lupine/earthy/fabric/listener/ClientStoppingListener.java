package au.lupine.earthy.fabric.listener;

import au.lupine.earthy.fabric.EarthyFabric;
import au.lupine.earthy.fabric.object.base.Listener;
import au.lupine.earthy.fabric.object.base.Manager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class ClientStoppingListener implements Listener {

    @Override
    public void register() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            for (Manager manager : EarthyFabric.MANAGERS) {
                manager.disable();
            }
        });
    }
}
