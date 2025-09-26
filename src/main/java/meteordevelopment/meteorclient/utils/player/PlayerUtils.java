/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.github.puzzle.game.util.BlockSelectionUtil;
import finalforeach.cosmicreach.BlockRaycasts;
import finalforeach.cosmicreach.BlockSelection;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.constants.Direction;
import finalforeach.cosmicreach.entities.PlayerController;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.entities.player.Gamemode;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.entities.player.PlayerEntity;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.networking.client.ClientNetworkManager;
import finalforeach.cosmicreach.networking.packets.entities.PlayerPositionPacket;
import finalforeach.cosmicreach.settings.ControlSettings;
import finalforeach.cosmicreach.settings.Controls;
import finalforeach.cosmicreach.world.World;
import meteordevelopment.custom.RayCast;
import meteordevelopment.custom.RaycastContext;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.mixins.*;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.NoFall;
import meteordevelopment.meteorclient.systems.modules.player.Reach;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.BedBlockEntity;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.client.network.PlayerListEntry;
//import net.minecraft.component.DataComponentTypes;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.decoration.EndCrystalEntity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.PotionItem;
//import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
//import net.minecraft.util.hit.HitResult;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.Vec3d;
//import net.minecraft.world.GameMode;
//import net.minecraft.world.RaycastContext;

//import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.MeteorClient.client;
import static meteordevelopment.meteorclient.utils.Utils.WHITE;

public class PlayerUtils {
    private static final float diagonal = (float) (1 / Math.sqrt(2));
    private static final Vector3 horizontalVelocity = new Vector3(0, 0, 0);
    private static final Color color = new Color();

    public static Boolean isUsingItem;

    private PlayerUtils() {
    }

    public static BlockPosition getBlockPos() {
        PlayerEntity player = (PlayerEntity) client.getLocalPlayer().getEntity();
        return BlockPosition.ofGlobal(player.zone,(int)Math.floor(player.position.x), (int)Math.floor(player.position.y), (int)Math.floor(player.position.z));
    }

    public static Camera playerCamera() {
        PlayerController controller = ((AccessorInGame)InGame.IN_GAME).getPlayerController();
        return ((AccessorPlayerController)controller).getPlayerCamera();
    }

    public static float playerReach() {
        if (Modules.get().isActive(Reach.class)) {
            Reach reach = Modules.get().get(Reach.class);
            return (float) reach.entityReach();
        }
        BlockRaycasts casts = ((AccessorBlockSelection)InGame.IN_GAME.blockSelection).getRaycasts();
        return ((AccessorBlockRaycasts) casts).getRaycastDist();
    }

    public static float fallDistance(Entity entity) {
        return entity.position.y < entity.lastPosition.y ? entity.lastPosition.y - entity.position.y : 0;
    }

    public static float playerYaw() {
        Vector3 direction;

//TODO figure out which direction view is correct
//        Camera camera = playerCamera();
//        direction = camera.direction
        Entity player = client.getLocalPlayer().getEntity();
        direction = player.viewDirection;
        /*
            https://stackoverflow.com/questions/2782647/how-to-get-yaw-pitch-and-roll-from-a-3d-vector
            pitch = asin(-d.Y);
            yaw = atan2(d.X, d.Z)
         */

        return MathUtils.atan2(direction.x, direction.z) * MathUtils.radiansToDegrees;
    }

    public static float playerPitch() {
        Vector3 direction;

        //TODO figure out which direction view is correct
//        Camera camera = playerCamera();
//        direction = camera.direction;
        Entity player = client.getLocalPlayer().getEntity();
        direction = player.viewDirection;

        /*
            https://stackoverflow.com/questions/2782647/how-to-get-yaw-pitch-and-roll-from-a-3d-vector
            pitch = asin(-d.Y);
            yaw = atan2(d.X, d.Z)
         */

        return MathUtils.asin(-direction.y) * MathUtils.radiansToDegrees;
    }

    public static int wrapDegrees(int degrees) {
        int i = degrees % 360;
        if (i >= 180) {
            i -= 360;
        }

        if (i < -180) {
            i += 360;
        }

        return i;
    }

    public static float wrapDegrees(float degrees) {
        float i = degrees % 360;
        if (i >= 180) {
            i -= 360;
        }

        if (i < -180) {
            i += 360;
        }

        return i;
    }

    public static Color getPlayerColor(PlayerEntity entity, Color defaultColor) {
        if (Friends.get().isFriend(entity)) {
            return color.set(Config.get().friendColor.get()).a(defaultColor.a);
        }

        if (Config.get().useTeamColor.get() /*&& !color.set().equals(WHITE)*/) {
            return color.a(defaultColor.a);
        }

        return defaultColor;
    }

    private static Vector3 fromPolar(float pitch, float yaw) {
        float f = MathUtils.cos(-yaw * 0.017453292F - 3.1415927F);
        float g = MathUtils.sin(-yaw * 0.017453292F - 3.1415927F);
        float h = -MathUtils.cos(-pitch * 0.017453292F);
        float i = MathUtils.sin(-pitch * 0.017453292F);
        return new Vector3(g * h, i, f * h);
    }

    public static Vector3 getHorizontalVelocity(float bps) {
        try {
            float yaw = playerYaw();

            Vector3 forward = fromPolar(0, yaw);
            Vector3 right = fromPolar(0, yaw + 90);
            float velX = 0;
            float velZ = 0;

            boolean a = false;
            if (ControlSettings.keyForward.isPressed()) {
                velX += forward.x / 20 * bps;
                velZ += forward.z / 20 * bps;
                a = true;
            }
            if (ControlSettings.keyBackward.isPressed()) {
                velX -= forward.x / 20 * bps;
                velZ -= forward.z / 20 * bps;
                a = true;
            }

            boolean b = false;
            if (ControlSettings.keyRight.isPressed()) {
                velX += right.x / 20 * bps;
                velZ += right.z / 20 * bps;
                b = true;
            }
            if (ControlSettings.keyLeft.isPressed()) {
                velX -= right.x / 20 * bps;
                velZ -= right.z / 20 * bps;
                b = true;
            }

            if (a && b) {
                velX *= diagonal;
                velZ *= diagonal;
            }

            horizontalVelocity.x = velX;
            horizontalVelocity.z = velZ;
            //((IVec3d) horizontalVelocity).meteor$setXZ(velX, velZ);
            return horizontalVelocity;
        } catch(Exception e) {
            return Vector3.Zero;
        }
    }

    public static void centerPlayer() {
        Player player = client.getLocalPlayer();
        float x = MathUtils.floor(player.getPosition().x) + 0.5f;
        float z = MathUtils.floor(player.getPosition().z) + 0.5f;

        player.setPosition(x, player.getPosition().y, z);
        ClientNetworkManager.CLIENT.identity.send(new PlayerPositionPacket(player));
    }

    //TODO check this works
    public static boolean canSeeEntity(Entity entity) {
        Vector3 vec1 = new Vector3(0, 0, 0);
        Vector3 vec2 = new Vector3(0, 0, 0);

        Player player = client.getLocalPlayer();

        vec1.x = player.getPosition().x;
        vec1.y = player.getPosition().y + player.standingBoundingBox.getHeight();
        vec1.z = player.getPosition().z;

        vec2.x = entity.position.x;
        vec2.y = entity.position.y;
        vec2.z = entity.position.z;


        // no fucking raycast?
        // GameSingletons.world.

        boolean canSeeFeet = RayCast.raycast(new RaycastContext(vec1, vec2, RaycastContext.FluidHandling.NONE, GameSingletons.world)).getType() == RayCast.HitResult.Type.MISS;
        //boolean canSeeFeet = mc.world.raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS;

        vec2.x = entity.position.x;
        vec2.y = entity.position.y + entity.localBoundingBox.getHeight();
        vec2.z = entity.position.z;
        boolean canSeeEyes = RayCast.raycast(new RaycastContext(vec1, vec2, RaycastContext.FluidHandling.NONE, GameSingletons.world)).getType() == RayCast.HitResult.Type.MISS;
        //boolean canSeeEyes = mc.world.raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS;

        return canSeeFeet || canSeeEyes;
    }

    public static float[] calculateAngle(Vector3 target) {

        Player player = client.getLocalPlayer();

        Vector3 eyesPos = new Vector3(player.getPosition().x, player.getPosition().y + player.standingBoundingBox.getHeight(), player.getPosition().z);

        double dX = target.x - eyesPos.x;
        double dY = (target.y - eyesPos.y) * -1.0D;
        double dZ = target.z - eyesPos.z;

        double dist = Math.sqrt(dX * dX + dZ * dZ);

        return new float[]{(float) wrapDegrees((int) (Math.toDegrees(Math.atan2(dZ, dX)) - 90.0D)), (float) wrapDegrees((int) Math.toDegrees(Math.atan2(dY, dist)))};
    }

    public static boolean shouldPause(boolean ifBreaking, boolean ifEating) {
        if (ifBreaking && BlockUtils.getBreakStage() > 0) return true;
        if (ifEating && (isUsingItem && true/*is food*/)) return true;
        return false;
    }

    public static boolean isMoving() {
        return client.getLocalPlayer().getEntity().velocity != Vector3.Zero;
    }

    public static boolean isSprinting() {
        return client.getLocalPlayer().isSprinting && isMoving();
    }


    // TODO fix this it should check blast resistance not air
    public static boolean isInHole(boolean doubles) {
        if (!Utils.canUpdate()) return false;

        BlockPosition blockPos = getBlockPos();
        int air = 0;

        for (Direction direction : Direction.values()) {
            if (direction == Direction.POS_Y) continue;

            BlockState state = blockPos.getOffsetBlockPos(blockPos.getZone(), direction).getBlockState();;

            if (state.getBlock() == Block.AIR) {
                if (!doubles || direction == Direction.NEG_Y) return false;

                air++;

                for (Direction dir : Direction.values()) {
                    if (dir == ((AccessorDirection)direction).getOpposite() /*direction.getOpposite()*/ || dir == Direction.POS_Y)
                            continue;
                    BlockState blockState1 = blockPos.getOffsetBlockPos(blockPos.getZone(), direction).getOffsetBlockPos(blockPos.getZone(), dir).getBlockState(); //mc.world.getBlockState(blockPos.offset(direction).offset(dir));

                    if (blockState1.getBlock() == Block.AIR) {
                        return false;
                    }
                }
            }
        }

        return air < 2;
    }

    public static float possibleHealthReductions() {
        return possibleHealthReductions(true, true);
    }

    public static float possibleHealthReductions(boolean entities, boolean fall) {
        float damageTaken = 0;
        Player player = client.getLocalPlayer();
        PlayerEntity playerEntity = (PlayerEntity) player.getEntity();

        // TODO fix later
//        if (entities) {
//            for (Entity entity : player.getZone().getAllEntities()) {
//                // Check for end crystals
//                if (entity instanceof EndCrystalEntity) {
//                    float crystalDamage = DamageUtils.crystalDamage(mc.player, entity.getPos());
//                    if (crystalDamage > damageTaken) damageTaken = crystalDamage;
//                }
//                // Check for players holding swords
//                else if (entity instanceof PlayerEntity player && !Friends.get().isFriend(player) && isWithin(entity, 5)) {
//                    float attackDamage = DamageUtils.getAttackDamage(player, mc.player);
//                    if (attackDamage > damageTaken) damageTaken = attackDamage;
//                }
//            }
//
//            // Check for beds if in nether
//            if (PlayerUtils.getDimension() != Dimension.Overworld) {
//                for (BlockEntity blockEntity : Utils.blockEntities()) {
//                    BlockPosition bp = blockEntity.getPos();
//                    Vector3 pos = new Vector3(bp.getX(), bp.getY(), bp.getZ());
//
//                    if (blockEntity instanceof BedBlockEntity) {
//                        float explosionDamage = DamageUtils.bedDamage(mc.player, pos);
//                        if (explosionDamage > damageTaken) damageTaken = explosionDamage;
//                    }
//                }
//            }
//        }

        // Check for fall distance with water check
        if (fall) {
            if (!Modules.get().isActive(NoFall.class) && fallDistance(playerEntity) > 3) {
                float damage = DamageUtils.fallDamage(playerEntity);

                if (damage > damageTaken && !EntityUtils.isAboveWater(playerEntity)) {
                    damageTaken = damage;
                }
            }
        }

        return damageTaken;
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(squaredDistance(x1, y1, z1, x2, y2, z2));
    }

    public static double distanceTo(Entity entity) {
        return distanceTo(entity.position.x, entity.position.y, entity.position.z);
    }

    public static double distanceTo(BlockPosition blockPos) {
        return distanceTo(blockPos.getGlobalX(), blockPos.getGlobalY(), blockPos.getGlobalZ());
    }

    public static double distanceTo(Vector3 vec3d) {
        return distanceTo(vec3d.x, vec3d.y, vec3d.z);
    }

    public static double distanceTo(double x, double y, double z) {
        return Math.sqrt(squaredDistanceTo(x, y, z));
    }

    public static double squaredDistanceTo(Entity entity) {
        return squaredDistanceTo(entity.position.x, entity.position.y, entity.position.z);
    }

    public static double squaredDistanceTo(BlockPosition blockPos) {
        return squaredDistanceTo(blockPos.getGlobalX(), blockPos.getGlobalY(), blockPos.getGlobalZ());
    }

    public static double squaredDistanceTo(double x, double y, double z) {
        Entity player = client.getLocalPlayer().getEntity();
        return squaredDistance(player.position.x, player.position.y, player.position.z, x, y, z);
    }

    public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double f = x1 - x2;
        double g = y1 - y2;
        double h = z1 - z2;
        return Math.fma(f, f, Math.fma(g, g, h * h));
    }

    public static boolean isWithin(Entity entity, double r) {
        return squaredDistanceTo(entity.position.x, entity.position.y, entity.position.z) <= r * r;
    }

    public static boolean isWithin(Vector3 vec3d, double r) {
        return squaredDistanceTo(vec3d.x, vec3d.y, vec3d.z) <= r * r;
    }

    public static boolean isWithin(BlockPosition blockPos, double r) {
        return squaredDistanceTo(blockPos.getGlobalX(), blockPos.getGlobalY(), blockPos.getGlobalZ()) <= r * r;
    }

    public static boolean isWithin(double x, double y, double z, double r) {
        return squaredDistanceTo(x, y, z) <= r * r;
    }

    public static double distanceToCamera(double x, double y, double z) {
        return Math.sqrt(squaredDistanceToCamera(x, y, z));
    }

    public static double distanceToCamera(Entity entity) {
        return distanceToCamera(entity.position.x, EntityUtils.eyePosition(entity), entity.position.z);
    }

    public static double squaredDistanceToCamera(double x, double y, double z) {
        Vector3 cameraPos = playerCamera().position;
        return squaredDistance(cameraPos.x, cameraPos.y, cameraPos.z, x, y, z);
    }

    public static double squaredDistanceToCamera(Entity entity) {
        return squaredDistanceToCamera(entity.position.x, EntityUtils.eyePosition(entity), entity.position.z);
    }

    public static boolean isWithinCamera(Entity entity, double r) {
        return squaredDistanceToCamera(entity.position.x, entity.position.y, entity.position.z) <= r * r;
    }

    public static boolean isWithinCamera(Vector3 vec3d, double r) {
        return squaredDistanceToCamera(vec3d.x, vec3d.y, vec3d.z) <= r * r;
    }

    public static boolean isWithinCamera(BlockPosition blockPos, double r) {
        return squaredDistanceToCamera(blockPos.getGlobalX(), blockPos.getGlobalY(), blockPos.getGlobalZ()) <= r * r;
    }

    public static boolean isWithinCamera(double x, double y, double z, double r) {
        return squaredDistanceToCamera(x, y, z) <= r * r;
    }

    public static boolean isWithinReach(Entity entity) {
        return isWithinReach(entity.position.x, entity.position.y, entity.position.z);
    }

    public static boolean isWithinReach(Vector3 vec3d) {
        return isWithinReach(vec3d.x, vec3d.y, vec3d.z);
    }

    public static boolean isWithinReach(BlockPosition blockPos) {
        return isWithinReach(blockPos.getGlobalX(), blockPos.getGlobalY(), blockPos.getGlobalZ());
    }

    public static boolean isWithinReach(double x, double y, double z) {
        Entity player = client.getLocalPlayer().getEntity();
        float playerReach = playerReach(); // block interaction range


        return squaredDistance(player.position.x, player.position.y + player.globalBoundingBox.getHeight(), player.position.z, x, y, z) <= playerReach * playerReach;
    }

//    public static Dimension getDimension() {
//        if (mc.world == null) return Dimension.Overworld;
//
//        return switch (mc.world.getRegistryKey().getValue().getPath()) {
//            case "the_nether" -> Dimension.Nether;
//            case "the_end" -> Dimension.End;
//            default -> Dimension.Overworld;
//        };
//    }

    public static Gamemode getGameMode() {
        if (client.getLocalPlayer() == null) return null;
        return client.getLocalPlayer().gamemode;
    }

    public static float getTotalHealth() {
        return client.getLocalPlayer().getEntity().hitpoints;
    }

    public static boolean isAlive() {
        return !client.getLocalPlayer().isDead() && client.getLocalPlayer().getEntity().hitpoints > 0;
    }

//    public static int getPing() {
//        if (mc.getNetworkHandler() == null) return 0;
//
//        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
//        if (playerListEntry == null) return 0;
//        return playerListEntry.getLatency();
//    }
}
