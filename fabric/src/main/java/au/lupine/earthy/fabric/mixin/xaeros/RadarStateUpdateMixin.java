package au.lupine.earthy.fabric.mixin.xaeros;

import au.lupine.earthy.fabric.EarthyFabric;
import au.lupine.earthy.fabric.module.Session;
import au.lupine.earthy.fabric.object.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.hud.minimap.radar.state.RadarList;
import xaero.hud.minimap.radar.state.RadarStateUpdater;

@Mixin(value = RadarStateUpdater.class)
public abstract class RadarStateUpdateMixin {

    @Unique private static final int HIDE_UNDER_LIGHT_LEVEL = 15;

    @Redirect(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lxaero/hud/minimap/radar/state/RadarList;add(Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean redirect(RadarList instance, Entity entity) {
        if (!Session.getInstance().isPlayerOnEarthMC()) return instance.add(entity);

        if (!Config.showUnobscuredPlayersOnMap) return false;

        if (!(entity instanceof RemotePlayer other)) return false;

        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) return instance.add(entity);

        ClientLevel level = client.level;
        if (level == null) return false;

        if (player.isSpectator()) return instance.add(other);

        if (!level.dimensionType().hasSkyLight()) return false;

        int brightness = level.getBrightness(LightLayer.SKY, BlockPos.containing(other.getEyePosition()));
        if (brightness < HIDE_UNDER_LIGHT_LEVEL) return false;

        level.getSkyDarken();
        if (other.isInvisibleTo(player)) return false;
        if (other.isCrouching()) return false;

        return instance.add(other);
    }
}
