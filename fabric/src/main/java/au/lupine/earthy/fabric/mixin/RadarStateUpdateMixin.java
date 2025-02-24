package au.lupine.earthy.fabric.mixin;

import au.lupine.earthy.fabric.module.Lifecycle;
import au.lupine.earthy.fabric.object.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.hud.minimap.radar.state.RadarList;
import xaero.hud.minimap.radar.state.RadarStateUpdater;

import java.util.Set;

@Mixin(value = RadarStateUpdater.class)
public class RadarStateUpdateMixin {

    @Unique private static final Set<Block> TRANSPARENT_BLOCKS = Set.of(
            Blocks.GLASS,
            Blocks.WHITE_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS,
            Blocks.BROWN_STAINED_GLASS, Blocks.RED_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS,
            Blocks.LIME_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS,
            Blocks.BLUE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS, Blocks.PINK_STAINED_GLASS,
            Blocks.GLASS_PANE,
            Blocks.WHITE_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS_PANE,
            Blocks.BROWN_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS_PANE,
            Blocks.LIME_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
            Blocks.BLUE_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS_PANE,
            Blocks.LANTERN, Blocks.SOUL_LANTERN,
            Blocks.CHEST,
            Blocks.IRON_BARS,
            Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.WALL_TORCH, Blocks.SOUL_WALL_TORCH,
            Blocks.VINE, Blocks.GLOW_LICHEN,
            Blocks.MOSS_CARPET, Blocks.SNOW,
            Blocks.POINTED_DRIPSTONE,
            Blocks.GRINDSTONE,
            Blocks.DECORATED_POT,
            Blocks.SCAFFOLDING,
            Blocks.BREWING_STAND
    );

    @Unique private static final Set<TagKey<Block>> TRANSPARENT_BLOCK_TAGS = Set.of(
            BlockTags.BANNERS, BlockTags.BUTTONS, BlockTags.TRAPDOORS, BlockTags.ALL_SIGNS,
            BlockTags.PRESSURE_PLATES, BlockTags.FLOWERS, BlockTags.BEDS, BlockTags.AIR,
            BlockTags.CAMPFIRES, BlockTags.CANDLES, BlockTags.CLIMBABLE, BlockTags.WOOL_CARPETS,
            BlockTags.CROPS, BlockTags.DOORS, BlockTags.FLOWER_POTS, BlockTags.RAILS,
            BlockTags.REPLACEABLE, BlockTags.SAPLINGS, BlockTags.ANVIL, BlockTags.FENCES,
            BlockTags.WALLS, BlockTags.FENCE_GATES
    );

    @Redirect(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lxaero/hud/minimap/radar/state/RadarList;add(Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean redirect(RadarList instance, Entity entity) {
        Lifecycle lifecycle = Lifecycle.getInstance();

        if (!lifecycle.isPlayerOnEarthMC()) return instance.add(entity);

        if (!Config.showUnobscuredPlayersOnMap) return false;

        if (!(entity instanceof RemotePlayer other)) return false;

        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;

        if (player == null) return instance.add(entity);

        if (player.isSpectator()) return instance.add(other);

        if (!lifecycle.isPlayerInOverworld()) return false;

        ClientLevel level = client.level;

        if (shouldAddPlayer(level, player, other)) return instance.add(other);

        return false;
    }

    @Unique
    private boolean shouldAddPlayer(ClientLevel level, LocalPlayer player, Player other) {
        if (other.isCrouching()) return false;
        if (other.isInvisibleTo(player)) return false;

        return areBlocksAboveTransparent(level, other.position());
    }

    @Unique
    private boolean areBlocksAboveTransparent(ClientLevel level, Vec3 position) {
        int x = (int) position.x;
        int z = (int) position.z;

        nextBlock: for (int y = (int) Math.ceil(position.y); y <= level.getHeight(); y++) {
            BlockPos currentPos = new BlockPos(x, y, z);
            BlockState bs = level.getBlockState(currentPos);

            for (TagKey<Block> tag : TRANSPARENT_BLOCK_TAGS) {
                if (bs.is(tag)) continue nextBlock;
            }

            if (!TRANSPARENT_BLOCKS.contains(bs.getBlock())) return false;
        }

        return true;
    }
}
