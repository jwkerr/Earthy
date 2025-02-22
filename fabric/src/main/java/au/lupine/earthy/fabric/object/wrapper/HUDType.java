package au.lupine.earthy.fabric.object.wrapper;

import dev.isxander.yacl3.config.v2.api.autogen.EnumCycler;
import org.jetbrains.annotations.Nullable;

public enum HUDType implements EnumCycler.CyclableEnum<HUDType> {
    NONE(null),
    PERM("towny:plot perm hud"),
    MAP("towny:towny map hud");

    private final String command;

    HUDType(String command) {
        this.command = command;
    }

    public @Nullable String getCommand() {
        return command;
    }

    @Override
    public HUDType[] allowedValues() {
        return values();
    }
}
