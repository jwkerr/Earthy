package au.lupine.earthy.fabric.mixin;

import au.lupine.earthy.fabric.module.ChatPreview;
import au.lupine.earthy.fabric.module.Lifecycle;
import au.lupine.earthy.fabric.object.config.Config;
import au.lupine.earthy.fabric.object.wrapper.ChatChannel;
import com.llamalad7.mixinextras.sugar.Local;
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
    @Shadow private String value;

    public EditBoxMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @Inject(
            method = "renderWidget",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;isEmpty()Z"
            )
    )
    public void inject(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci, @Local(ordinal = 5) int n, @Local(ordinal = 8) int q) {
        if (!Lifecycle.getInstance().isPlayerOnEarthMC()) return;

        if (!Config.previewCurrentChatChannel) return;

        if (!getMessage().equals(Component.translatable("chat.editBox"))) return;

        ChatChannel currentChannel = ChatPreview.getInstance().getCurrentChatChannel();
        if (currentChannel == null) return;

        if (this.suggestion == null && this.value.isEmpty())
            guiGraphics.drawString(this.font, currentChannel.getName(), q - 1, n, darkenColour(currentChannel.getColour()).value());
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
