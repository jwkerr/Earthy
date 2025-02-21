package au.lupine.earthy.fabric.mixin;

import au.lupine.earthy.common.Earthy;
import au.lupine.earthy.fabric.manager.SessionManager;
import au.lupine.emcapiclient.object.apiobject.Player;
import au.lupine.emcapiclient.object.exception.FailedRequestException;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Inject(
            method = "applyPlayerInfoUpdate",
            at = @At("HEAD")
    )
    public void inject(ClientboundPlayerInfoUpdatePacket.Action action, ClientboundPlayerInfoUpdatePacket.Entry entry, PlayerInfo playerInfo, CallbackInfo ci) {
        if (action != ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER) return;

        UUID uuid = playerInfo.getProfile().getId();

        List<Player> players = SessionManager.getPlayerInfo();
        if (players.stream().map(Player::getUUID).anyMatch(currentUUID -> currentUUID.equals(uuid))) return;

        CompletableFuture.runAsync(() -> {
            try {
                Player player = Earthy.getAPI().getPlayerByUUID(uuid);
                players.add(player);
            } catch (FailedRequestException ignored) {}
        });
    }
}
