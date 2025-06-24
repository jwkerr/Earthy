package au.lupine.earthy.fabric.module;

import au.lupine.earthy.fabric.object.base.Module;
import au.lupine.earthy.fabric.object.wrapper.ChatChannel;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatPreview extends Module {

    private static ChatPreview instance;

    private static ChatChannel currentChatChannel;
    private static boolean inPartyChat = false;

    private ChatPreview() {}

    public static ChatPreview getInstance() {
        if (instance == null) instance = new ChatPreview();
        return instance;
    }

    @Override
    public void enable() {
        MinecraftClientAudiences audiences = MinecraftClientAudiences.of();

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            Session session = Session.getInstance();
            if (!session.isPlayerOnEarthMC() || !session.isPlayerAuthenticated()) return;

            Component component;
            try {
                component = audiences.asAdventure(message);
            } catch (Exception e) {
                return;
            }

            String string = PlainTextComponentSerializer.plainText().serialize(component);

            TextColor colour = component.color();
            if (string.startsWith("You are currently in") && colour != null && colour.compareTo(NamedTextColor.GOLD) == 0)
                currentChatChannel = parseCurrentChatChannel(string);

            String youHaveJoined = "» You have joined the channel: ";
            if (string.startsWith(youHaveJoined)) {
                String cut = string.replace(youHaveJoined, "");
                currentChatChannel = ChatChannel.getOrDefault(cut.substring(0, cut.length() - 1));
            }

            if (string.equals("(mcMMO-Chat) Your chat messages will now be automatically delivered to the Party chat channel."))
                inPartyChat = true;

            if (string.equals("(mcMMO-Chat) Your chat messages will no longer be automatically delivered to specific chat channels."))
                inPartyChat = false;

            if (string.equals("You have left that party"))
                inPartyChat = false;
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> inPartyChat = false);
    }

    public ChatChannel getCurrentChatChannel() {
        return currentChatChannel;
    }

    public boolean isInPartyChat() {
        return inPartyChat;
    }

    private ChatChannel parseCurrentChatChannel(String message) {
        Pattern pattern = Pattern.compile("(\\w+) \\(write\\)");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String name = matcher.group(1);
            if (name == null) return ChatChannel.GLOBAL;

            return ChatChannel.getOrDefault(name);
        } else {
            return ChatChannel.GLOBAL;
        }
    }
}
