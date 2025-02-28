package au.lupine.earthy.fabric.module;

import au.lupine.earthy.fabric.object.base.Module;
import au.lupine.earthy.fabric.object.base.Tickable;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;

public final class Session extends Module {

    private static Session instance;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) instance = new Session();
        return instance;
    }

    @Override
    public void enable() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> Tickable.tick());
    }

    public boolean isPlayerOnEarthMC() {
        ClientPacketListener cpl = Minecraft.getInstance().getConnection();
        if (cpl == null) return false;

        String brand = cpl.serverBrand();
        if (brand == null) return false;

        ServerData server = cpl.getServerData();
        if (server == null) return false;

        String ip = server.ip;
        String[] split = ip.split("\\.");

        if (split.length <= 1) return false;

        int finalIndex = split.length - 1;
        return split[finalIndex - 1].equalsIgnoreCase("earthmc") &&
                split[finalIndex].equalsIgnoreCase("net") &&
                (
                        brand.equals("§aPremium activated! Thank you for the support!§r (Velocity)") ||
                        brand.equals("§cPremium not activated, /Premium§r (Velocity)")
                );
    }

    public boolean isPlayerInOverworld() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return false;

        return level.dimension().equals(ClientLevel.OVERWORLD);
    }
}
