package au.lupine.earthy.fabric.module;

import au.lupine.earthy.fabric.object.NBTTraversal;
import au.lupine.earthy.fabric.object.base.Module;
import au.lupine.earthy.fabric.object.base.Tickable;
import au.lupine.earthy.fabric.object.wrapper.HeadData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.commands.data.BlockDataAccessor;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class Inspector extends Module {

    private static Inspector instance;

    private static final KeyMapping INSPECT_KEY = KeyBindingHelper.registerKeyBinding(
            new KeyMapping("key.earthy.inspect", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.earthy")
    );

    private static final List<String> HEAD_CATEGORIES = List.of("alphabet", "animals", "blocks", "decoration", "food-drinks", "humanoid", "humans", "miscellaneous", "monsters", "plants");
    private static final Map<String, HeadData> HEAD_MAP = new HashMap<>();

    private Inspector() {}

    public static Inspector getInstance() {
        if (instance == null) instance = new Inspector();
        return instance;
    }

    @Override
    public void enable() {
        Consumer<String> loadHeads = category -> {
            InputStream is = Inspector.class.getClassLoader().getResourceAsStream("assets/earthy/categories/" + category + ".json");
            if (is == null) return;

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();

            for (JsonElement element : array) {
                JsonObject object = element.getAsJsonObject();
                String value = object.get("value").getAsString();
                String name = object.get("name").getAsString();

                String tagsString = object.get("tags").getAsString();

                List<String> tags;
                if (tagsString.isEmpty()) {
                    tags = List.of();
                } else {
                    tags = List.of(tagsString.split(","));
                }

                HeadData head = new HeadData(value, name, tags);
                HEAD_MAP.put(value, head);
            }
        };

        HEAD_CATEGORIES.forEach(loadHeads);

        Tickable.register(() -> {
            while (INSPECT_KEY.consumeClick()) inspect();
        });
    }

    public @Nullable HeadData getHeadInfoByValue(@NotNull String value) {
        return HEAD_MAP.get(value);
    }

    private void inspect() {
        Minecraft client = Minecraft.getInstance();

        LocalPlayer player = client.player;
        if (player == null) return;

        ClientLevel level = client.level;
        if (level == null) return;

        switch (ProjectileUtil.getHitResultOnViewVector(player, entity -> true, 64D)) {
            case EntityHitResult ehr -> {
                Entity entity = ehr.getEntity();

                EntityDataAccessor accessor = new EntityDataAccessor(entity);

                boolean isPlayerOnEarthMC = Session.getInstance().isPlayerOnEarthMC();

                if (entity instanceof Player && isPlayerOnEarthMC && player.isCrouching()) {
                    sendHeadData(player, getHeadData(accessor));
                } else if (entity instanceof Player other && isPlayerOnEarthMC) {
                    inspectPlayer(other);
                } else {
                    sendHeadData(player, getHeadData(accessor));
                }
            }
            case BlockHitResult bhr -> {
                BlockPos pos = bhr.getBlockPos();
                BlockEntity entity = level.getBlockEntity(pos);
                if (entity == null) return;

                BlockDataAccessor accessor = new BlockDataAccessor(entity, pos);
                sendHeadData(player, getHeadData(accessor));
            }
            default -> {}
        }
    }

    private List<HeadData> getHeadData(@NotNull DataAccessor accessor) {
        List<HeadData> heads = new ArrayList<>();

        CompoundTag nbt;
        try {
            nbt = accessor.getData();
        } catch (CommandSyntaxException e) {
            return heads;
        }

        NBTTraversal traversal = new NBTTraversal(nbt, "value", "Value");
        traversal.traverse();

        for (String result : traversal.getResult()) {
            heads.add(Inspector.getInstance().getHeadInfoByValue(result));
        }

        return heads;
    }

    private void sendHeadData(LocalPlayer player, List<HeadData> heads) {
        for (HeadData head : heads) {
            MinecraftClientAudiences audiences = MinecraftClientAudiences.of();
            if (head == null) {
                player.displayClientMessage(
                        audiences.asNative(Component.translatable("msg.earthy.head_not_available", NamedTextColor.RED)),
                        true
                );
            } else {
                player.displayClientMessage(
                        audiences.asNative(head.getFormatted()),
                        heads.size() <= 1
                );
            }
        }
    }

    private void inspectPlayer(Player player) {
        Minecraft client = Minecraft.getInstance();

        LocalPlayer local = client.player;
        if (local == null) return;

        if (player.isInvisibleTo(local)) return;

        ClientPacketListener cpl = client.getConnection();
        if (cpl == null) return;

        cpl.sendCommand("towny:res " + player.getName().getString());
    }
}
