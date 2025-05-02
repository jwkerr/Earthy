package au.lupine.earthy.fabric.mixin;

import au.lupine.earthy.fabric.module.Cache;
import au.lupine.earthy.fabric.module.Session;
import au.lupine.earthy.fabric.object.config.Config;
import au.lupine.emcapiclient.object.apiobject.Player;
import com.mojang.blaze3d.vertex.PoseStack;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.NoSuchElementException;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel playerModel, float f) {
        super(context, playerModel, f);
    }

    @Inject(
            method = "renderNameTag(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
                    ordinal = 1
            )
    )
    private void inject(PlayerRenderState playerRenderState, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        Session session = Session.getInstance();
        if (!session.isPlayerOnEarthMC() || !session.isPlayerAuthenticated()) return;

        if (!Config.showAffiliationAboveHead) return;

        Player player;
        try {
            player = Cache.getInstance().getCachedPlayers().stream().filter(current -> {
                String[] split = component.getString().split(" ");
                String name = split[split.length - 1];

                return current.getName().equals(name);
            }).toList().getFirst();
        } catch (NoSuchElementException e) {
            return;
        }

        if (player == null) return;

        Component townyText = MinecraftClientAudiences.of().asNative(createTownyComponent(player));

        poseStack.pushPose();
        poseStack.scale(0.75F, 0.75F, 0.75F);
        poseStack.translate(0F, 0.6F, 0F);

        super.renderNameTag(playerRenderState, townyText, poseStack, multiBufferSource, i);
        poseStack.popPose();
        poseStack.translate(0D, 0.1225D, 0D);
    }

    @Unique
    private net.kyori.adventure.text.Component createTownyComponent(Player player) {
        if (!player.hasTown()) return net.kyori.adventure.text.Component.translatable("msg.earthy.nomad", NamedTextColor.DARK_AQUA);

        TextComponent.Builder builder = net.kyori.adventure.text.Component.text();

        if (player.isMayor()) {
            NamedTextColor colour;
            if (player.isKing()) {
                colour = NamedTextColor.GOLD;
            } else {
                colour = NamedTextColor.DARK_AQUA;
            }

            builder.append(net.kyori.adventure.text.Component.text("\uD83D\uDC51", colour));
            builder.appendSpace();
        }

        builder.append(net.kyori.adventure.text.Component.text("[", NamedTextColor.GRAY));

        if (player.hasNation()) {
            builder.append(net.kyori.adventure.text.Component.text(player.getNation().getName(), NamedTextColor.GOLD));
            builder.append(net.kyori.adventure.text.Component.text("|", NamedTextColor.GRAY));
        }

        builder.append(net.kyori.adventure.text.Component.text(player.getTown().getName(), NamedTextColor.DARK_AQUA));

        builder.append(net.kyori.adventure.text.Component.text("]", NamedTextColor.GRAY));

        return builder.build();
    }
}
