package au.lupine.earthy.fabric.mixin;

import au.lupine.earthy.fabric.module.ChatPreview;
import au.lupine.earthy.fabric.module.Session;
import au.lupine.earthy.fabric.object.config.Config;
import au.lupine.earthy.fabric.object.wrapper.ChatChannel;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditBox.class)
public abstract class EditBoxMixin extends AbstractWidget {

    @Shadow @Final private Font font;
    @Shadow private @Nullable String suggestion;
    @Shadow private int textX;
    @Shadow private int textY;
    @Shadow private String value;

    public EditBoxMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @Inject(
            method = "renderWidget(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            at     = @At("TAIL")
    )
    private void inject(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        Session session = Session.getInstance();
        if (!session.isPlayerOnEarthMC() || !session.isPlayerAuthenticated()) return;

        if (!Config.previewCurrentChatChannel) return;

        if (!this.value.isEmpty() || this.suggestion != null) return;

        ChatChannel current = ChatPreview.getInstance().getCurrentChatChannel();
        if (current == null) return;

        boolean inParty = ChatPreview.getInstance().isInPartyChat();
        String label = inParty ? current.getName() + " (party)" : current.getName();

        guiGraphics.drawString(
                this.font,
                label,
                this.textX - 1,
                this.textY,
                darkenColour(current.getColour()).value(),
                false
        );
    }

    @Unique
    private TextColor darkenColour(TextColor colour) {
        final int darken = 42;

        int red = Math.max(0, colour.red() - darken);
        int green = Math.max(0, colour.green() - darken);
        int blue = Math.max(0, colour.blue() - darken);

        return TextColor.color(red, green, blue);
    }
}
