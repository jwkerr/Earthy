package au.lupine.earthy.fabric.object;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Set;

public class ChatChannel {

    private final String name;
    private final TextColor colour;

    public static final ChatChannel GLOBAL = new ChatChannel("global", NamedTextColor.GRAY);
    public static final ChatChannel TOWN = new ChatChannel("town", NamedTextColor.AQUA);
    public static final ChatChannel NATION = new ChatChannel("nation", NamedTextColor.YELLOW);
    public static final ChatChannel LOCAL = new ChatChannel("local", TextColor.color(0x5BEA72));
    public static final ChatChannel STAFF = new ChatChannel("staff", TextColor.color(0xA80000));
    public static final ChatChannel TRADE = new ChatChannel("trade", NamedTextColor.AQUA);
    public static final ChatChannel PREMIUM = new ChatChannel("premium", TextColor.color(0xFC54FC));

    public static final ChatChannel PORTUGUESE = new ChatChannel("portuguese", TextColor.color(0x54FC54));
    public static final ChatChannel TURKISH = new ChatChannel("turkish", TextColor.color(0x54FC54));
    public static final ChatChannel SWEDISH = new ChatChannel("swedish", TextColor.color(0x54FC54));
    public static final ChatChannel GERMAN = new ChatChannel("german", TextColor.color(0x54FC54));
    public static final ChatChannel UKRAINIAN = new ChatChannel("ukrainian", TextColor.color(0x54FC54));
    public static final ChatChannel CHINESE = new ChatChannel("chinese", TextColor.color(0x54FC54));
    public static final ChatChannel FRENCH = new ChatChannel("french", TextColor.color(0x54FC54));
    public static final ChatChannel POLISH = new ChatChannel("polish", TextColor.color(0x54FC54));
    public static final ChatChannel RUSSIAN = new ChatChannel("russian", TextColor.color(0x54FC54));
    public static final ChatChannel SPANISH = new ChatChannel("spanish", TextColor.color(0x54FC54));
    public static final ChatChannel DUTCH = new ChatChannel("dutch", TextColor.color(0x54FC54));
    public static final ChatChannel JAPANESE = new ChatChannel("japanese", TextColor.color(0x54FC54));

    public static final Set<ChatChannel> CHANNELS = Set.of(
            GLOBAL, TOWN, NATION, LOCAL,
            STAFF, TRADE, PREMIUM, PORTUGUESE,
            TURKISH, SWEDISH, GERMAN, UKRAINIAN,
            CHINESE, FRENCH, POLISH, RUSSIAN,
            SPANISH, DUTCH, JAPANESE
    );

    public ChatChannel(String name, TextColor colour) {
        this.name = name;
        this.colour = colour;
    }

    public static ChatChannel getOrDefault(String name) {
        for (ChatChannel channel : CHANNELS) {
            if (channel.getName().equals(name)) return channel;
        }

        return new ChatChannel(name, NamedTextColor.GRAY);
    }

    public String getName() {
        return name;
    }

    public TextColor getColour() {
        return colour;
    }
}
