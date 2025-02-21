package au.lupine.earthy.fabric.object;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HeadData {

    private final String value;
    private final String name;
    private final List<String> tags;

    public HeadData(@NotNull String value, @NotNull String name, @NotNull List<String> tags) {
        this.value = value;
        this.name = name;
        this.tags = tags;
    }

    public @NotNull String getValue() {
        return value;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull List<String> getTags() {
        return tags;
    }

    public @NotNull Component getFormatted() {
        TextComponent.Builder builder = Component.text();
        builder.append(Component.text(name, NamedTextColor.BLUE));

        if (!tags.isEmpty()) {
            builder.append(Component.text(" » ", NamedTextColor.DARK_GRAY));
            builder.append(Component.text(String.join(", ", tags), NamedTextColor.GRAY));
        }

        return builder.build();
    }
}
