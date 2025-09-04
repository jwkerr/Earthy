package au.lupine.earthy.fabric.object.base;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public abstract class Module {

    public Module() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> disable());
    }

    public void enable() {}

    public void disable() {}
}
