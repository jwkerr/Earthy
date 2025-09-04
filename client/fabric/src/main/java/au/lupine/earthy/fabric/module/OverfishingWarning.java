package au.lupine.earthy.fabric.module;

import au.lupine.earthy.fabric.object.base.Module;
import au.lupine.earthy.fabric.object.config.Config;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public final class OverfishingWarning extends Module {

    private static OverfishingWarning instance;

    private OverfishingWarning() {}

    public static OverfishingWarning getInstance() {
        if (instance == null) instance = new OverfishingWarning();
        return instance;
    }

    @Override
    public void enable() {
        MinecraftClientAudiences audiences = MinecraftClientAudiences.of();

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            Session session = Session.getInstance();
            if (!session.isPlayerOnEarthMC()) return;

            if (!Config.warnWhenOverfishing) return;

            Component component;
            try {
                component = audiences.asAdventure(message);
            } catch (Exception e) {
                return;
            }

            String string = PlainTextComponentSerializer.plainText().serialize(component);
            Audience audience = audiences.audience();

            if (string.equals("You sense that there might not be many fish left in this area. Try fishing at least 3 blocks away.")) {
                audience.playSound(Sound.sound(
                                Key.key("block.note_block.flute"),
                                Sound.Source.MASTER,
                                1.0F,
                                0.65F
                        )
                );
            }

            if (string.equals("This area is suffering from overfishing, cast your rod in a different spot for more fish. At least 3 blocks away.")) {
                audience.playSound(Sound.sound(
                                Key.key("block.note_block.flute"),
                                Sound.Source.MASTER,
                                1.0F,
                                0.85F
                        )
                );
            }
        });
    }
}
