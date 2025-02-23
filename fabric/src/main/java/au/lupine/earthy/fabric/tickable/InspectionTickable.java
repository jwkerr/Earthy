package au.lupine.earthy.fabric.tickable;

import au.lupine.earthy.fabric.manager.HeadDataManager;
import au.lupine.earthy.fabric.manager.SessionManager;
import au.lupine.earthy.fabric.object.wrapper.HeadData;
import au.lupine.earthy.fabric.object.NBTTraversal;
import au.lupine.earthy.fabric.object.base.Tickable;
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
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class InspectionTickable implements Tickable {

    private static final KeyMapping INSPECT_KEY = KeyBindingHelper.registerKeyBinding(
            new KeyMapping("key.earthy.inspect", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.earthy")
    );

    @Override
    public void onTick() {
        while (INSPECT_KEY.consumeClick()) inspect();
    }

    private void inspect() {
        Minecraft client = Minecraft.getInstance();

        LocalPlayer player = client.player;
        if (player == null) return;

        ClientLevel level = client.level;
        if (level == null) return;

        switch (getTarget(player)) {
            case EntityHitResult ehr -> {
                Entity entity = ehr.getEntity();
                if (entity instanceof Player other && SessionManager.getInstance().isPlayerOnEarthMC()) inspectPlayer(other);

                EntityDataAccessor accessor = new EntityDataAccessor(entity);
                sendHeadData(player, getHeadData(accessor));
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
            heads.add(HeadDataManager.getInstance().getHeadInfoByValue(result));
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

    private @NotNull HitResult getTarget(LocalPlayer player) {
        ClientLevel level = player.clientLevel;

        Vec3 eyePos = player.getEyePosition(1.0F);
        player.getRotationVector().normalized().scale(64);
        Vec3 direction = player.getViewVector(1.0F).normalize().scale(64);

        Vec3 endPos = eyePos.add(direction);
        AABB box = new AABB(eyePos, endPos);

        EntityHitResult ehr = ProjectileUtil.getEntityHitResult(level, player, eyePos, endPos, box, entity -> true);
        BlockHitResult bhr = (BlockHitResult) player.pick(64, 1.0F, false);

        if (ehr == null) return bhr;

        return ehr.distanceTo(player) < bhr.distanceTo(player) ? ehr : bhr;
    }
}
