/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.constants.Direction;
import finalforeach.cosmicreach.entities.*;
import finalforeach.cosmicreach.entities.player.Gamemode;
import finalforeach.cosmicreach.entities.player.PlayerEntity;
import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.settings.ControlSettings;
import finalforeach.cosmicreach.settings.GraphicsSettings;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.world.World;
import finalforeach.cosmicreach.world.Zone;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
//import meteordevelopment.meteorclient.mixin.EntityTrackingSectionAccessor;
//import meteordevelopment.meteorclient.mixin.SectionedEntityCacheAccessor;
//import meteordevelopment.meteorclient.mixin.SimpleEntityLookupAccessor;
//import meteordevelopment.meteorclient.mixin.WorldAccessor;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.Blocks;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.client.network.PlayerListEntry;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityType;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.vehicle.BoatEntity;
//import net.minecraft.entity.vehicle.ChestBoatEntity;
//import net.minecraft.fluid.Fluid;
//import net.minecraft.fluid.Fluids;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Box;
//import net.minecraft.util.math.ChunkSectionPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.world.GameMode;
//import net.minecraft.world.entity.EntityLookup;
//import net.minecraft.world.entity.EntityTrackingSection;
//import net.minecraft.world.entity.SectionedEntityCache;
//import net.minecraft.world.entity.SimpleEntityLookup;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

//import static meteordevelopment.meteorclient.MeteorClient.mc;

public class EntityUtils {
    private static final BlockPosition testPos = BlockPosition.ofGlobalZoneless(0,0,0);

    private EntityUtils() {
    }

    public static float eyePosition(Entity entity) {
        return entity.position.y + entity.viewPositionOffset.y;
    }

    public static boolean isAttackable(Class<? extends Entity> type) {
        return type != ItemEntity.class && type != EntityLaserProjectile.class;
    }

    public static boolean isRideable(Class<? extends Entity> type) {
        return false;//type == EntityType.MINECART || BoatEntity.class.isAssignableFrom(type.getBaseClass()) || ChestBoatEntity.class.isAssignableFrom(type.getBaseClass()) || type == EntityType.CAMEL || type == EntityType.DONKEY || type == EntityType.HORSE || type == EntityType.LLAMA || type == EntityType.MULE || type == EntityType.PIG || type == EntityType.SKELETON_HORSE || type == EntityType.STRIDER || type == EntityType.ZOMBIE_HORSE;
    }

    public static float getTotalHealth(Entity target) {
        return target.hitpoints - target.getPendingDamage();
    }

    public static int getPing(PlayerEntity player) {

        return 0;
        //TODO fix, cannot do rn
//        if (mc.getNetworkHandler() == null) return 0;
//
//        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
//        if (playerListEntry == null) return 0;
//        return playerListEntry.getLatency();
    }

    public static Gamemode getGameMode(PlayerEntity player) {
//        if (player == null) return null;
//        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
//        if (playerListEntry == null) return null;
//        return playerListEntry.getGameMode();
        return player.player.gamemode;
    }

    public static boolean isAboveWater(Entity entity) {
        Vector3 pos = entity.getPosition();
        for (int i = 0; i < 64; i++) {
            BlockState state = entity.zone.getBlockState(pos);;
            if (!state.walkThrough) break;
            if (state.getBlock() == Block.WATER) {
                return true;
            }
            pos.add(0, -1, 0);
        }
        return false;
    }

    public static boolean isInRenderDistance(Entity entity) {
        if (entity == null) return false;
        return isInRenderDistance(entity.position.x, entity.position.z);
    }

    public static boolean isInRenderDistance(BlockEntity entity) {
        if (entity == null) return false;
        return isInRenderDistance(entity.getGlobalX(), entity.getGlobalZ());
    }

    public static boolean isInRenderDistance(BlockPosition pos) {
        if (pos == null) return false;
        return isInRenderDistance(pos.getGlobalX(), pos.getGlobalZ());
    }

    public static boolean isInRenderDistance(double posX, double posZ) {
        Camera camera = PlayerUtils.playerCamera();

        double x = Math.abs(camera.position.x - posX);
        double z = Math.abs(camera.position.z - posZ);
        double d = (GraphicsSettings.renderDistanceInChunks.getValue() + 1) * 16;

        return x < d && z < d;
    }

//    public static BlockPosition getCityBlock(PlayerEntity player) {
//        if (player == null) return null;
//
//        double bestDistanceSquared = 6 * 6;
//        Direction bestDirection = null;
//
//        for (Direction direction : new Direction[]{Direction.POS_X, Direction.NEG_X}) {
//            BlockPosition playerBlockPos = BlockPosition.ofGlobal(player.zone,(int)Math.floor(player.position.x), (int)Math.floor(player.position.y), (int)Math.floor(player.position.z));
//            testPos.set(playerBlockPos.getOffsetBlockPos(player.zone, direction));
//
//            Block block = player.zone.getBlockState(testPos.getGlobalX(),testPos.getGlobalY(),testPos.getGlobalZ()).getBlock();
//            if (block != Blocks.OBSIDIAN && block != Blocks.NETHERITE_BLOCK && block != Blocks.CRYING_OBSIDIAN
//                && block != Blocks.RESPAWN_ANCHOR && block != Blocks.ANCIENT_DEBRIS) continue;
//
//            double testDistanceSquared = PlayerUtils.squaredDistanceTo(testPos);
//            if (testDistanceSquared < bestDistanceSquared) {
//                bestDistanceSquared = testDistanceSquared;
//                bestDirection = direction;
//            }
//        }
//
//        if (bestDirection == null) return null;
//        return player.getBlockPos().offset(bestDirection);
//    }

    public static String getName(Entity entity) {
        if (entity == null) return null;
        if (entity instanceof PlayerEntity) return ((PlayerEntity) entity).player.getAccount().getDisplayName();
        return Identifier.of(entity.entityTypeId).getName();
    }

    public static Color getColorFromDistance(Entity entity) {
        // Credit to Icy from Stackoverflow
        Color distanceColor = new Color(255, 255, 255);
        double distance = PlayerUtils.distanceToCamera(entity);
        double percent = distance / 60;

        if (percent < 0 || percent > 1) {
            distanceColor.set(0, 255, 0, 255);
            return distanceColor;
        }

        int r, g;

        if (percent < 0.5) {
            r = 255;
            g = (int) (255 * percent / 0.5);  // Closer to 0.5, closer to yellow (255,255,0)
        } else {
            g = 255;
            r = 255 - (int) (255 * (percent - 0.5) / 0.5); // Closer to 1.0, closer to green (0,255,0)
        }

        distanceColor.set(r, g, 0, 255);
        return distanceColor;
    }

    public static boolean intersectsWithEntity(BoundingBox box, Predicate<Entity> predicate) {
        Collection<Zone> zones = GameSingletons.world.getZones();
        for (Zone zone : zones) {
            for (Entity entity : zone.getAllEntities()) {
                if (entity.globalBoundingBox.intersects(box) && predicate.test(entity)) {
                    return true;
                }
            }
        }
        return false;
    }
}
