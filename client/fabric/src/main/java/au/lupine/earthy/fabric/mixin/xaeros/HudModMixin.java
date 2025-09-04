package au.lupine.earthy.fabric.mixin.xaeros;

import au.lupine.earthy.fabric.module.Session;
import au.lupine.earthy.fabric.object.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.HudMod;

@Mixin(value = HudMod.class, remap = false)
public abstract class HudModMixin {

    @Inject(method = "isFairPlay", at = @At("HEAD"), cancellable = true)
    private void inject(CallbackInfoReturnable<Boolean> cir) {
        Session session = Session.getInstance();
        if ((!session.isPlayerOnEarthMC()) && !Config.showUnobscuredPlayersOnMap) return;

        cir.setReturnValue(false);
    }
}
