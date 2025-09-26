/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.networking.client.ClientNetworkManager;
import finalforeach.cosmicreach.networking.packets.entities.PlayerPositionPacket;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.Target;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.orbit.EventHandler;
//import net.minecraft.entity.Entity;
//import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.client;

//import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Rotations {
    private static final Pool<Rotation> rotationPool = new Pool<>(Rotation::new);
    private static final List<Rotation> rotations = new ArrayList<>();
    public static float serverYaw;
    public static float serverPitch;
    public static int rotationTimer;
    private static float preYaw, prePitch;
    private static int i = 0;

    private static Rotation lastRotation;
    private static int lastRotationTimer;
    private static boolean sentLastRotation;
    public static boolean rotating = false;

    private Rotations() {
    }

    @PreInit
    public static void init() {
        MeteorClient.EVENT_BUS.subscribe(Rotations.class);
    }

    public static void rotate(float yaw, float pitch, int priority, boolean clientSide, Runnable callback) {
        Rotation rotation = rotationPool.get();
        rotation.set(yaw, pitch, priority, clientSide, callback);

        int i = 0;
        for (; i < rotations.size(); i++) {
            if (priority > rotations.get(i).priority) break;
        }

        rotations.add(i, rotation);
    }

    public static void rotate(float yaw, float pitch, int priority, Runnable callback) {
        rotate(yaw, pitch, priority, false, callback);
    }

    public static void rotate(float yaw, float pitch, Runnable callback) {
        rotate(yaw, pitch, 0, callback);
    }

    public static void rotate(float yaw, float pitch, int priority) {
        rotate(yaw, pitch, priority, null);
    }

    public static void rotate(float yaw, float pitch) {
        rotate(yaw, pitch, 0, null);
    }

    private static void resetLastRotation() {
        if (lastRotation != null) {
            rotationPool.free(lastRotation);

            lastRotation = null;
            lastRotationTimer = 0;
        }
    }

    @EventHandler
    private static void onSendMovementPacketsPre(SendMovementPacketsEvent.Pre event) {
        if (client.getLocalPlayer() == null) return;
        sentLastRotation = false;

        if (!rotations.isEmpty()) {
            rotating = true;
            resetLastRotation();

            Rotation rotation = rotations.get(i);
            setupMovementPacketRotation(rotation);

            if (rotations.size() > 1) rotationPool.free(rotation);

            i++;
        } else if (lastRotation != null) {
            if (lastRotationTimer >= Config.get().rotationHoldTicks.get()) {
                resetLastRotation();
                rotating = false;
            } else {
                setupMovementPacketRotation(lastRotation);
                sentLastRotation = true;

                lastRotationTimer++;
            }
        }
    }

    private static void setupMovementPacketRotation(Rotation rotation) {
        setClientRotation(rotation);
        setCamRotation(rotation.yaw, rotation.pitch);
    }

    private static void setClientRotation(Rotation rotation) {
        preYaw = PlayerUtils.playerYaw();
        prePitch = PlayerUtils.playerPitch();


        //TODO figure out which direction view is correct

//        Camera camera = PlayerUtils.playerCamera();
//        camera.view.rotate(Vector3.Y, (float) rotation.yaw);
//        camera.view.rotate(Vector3.X, (float) rotation.pitch);
        Entity player = client.getLocalPlayer().getEntity();
        player.viewDirection.rotate(Vector3.Y, preYaw);
        player.viewDirection.rotate(Vector3.X, prePitch);
    }

    @EventHandler
    private static void onSendMovementPacketsPost(SendMovementPacketsEvent.Post event) {
        if (!rotations.isEmpty()) {
            if (client.getLocalPlayer() != null) {
                rotations.get(i - 1).runCallback();

                if (rotations.size() == 1) lastRotation = rotations.get(i - 1);

                resetPreRotation();
            }

            for (; i < rotations.size(); i++) {
                Rotation rotation = rotations.get(i);

                setCamRotation(rotation.yaw, rotation.pitch);
                if (rotation.clientSide) setClientRotation(rotation);
                rotation.sendPacket();
                if (rotation.clientSide) resetPreRotation();

                if (i == rotations.size() - 1) lastRotation = rotation;
                else rotationPool.free(rotation);
            }

            rotations.clear();
            i = 0;
        } else if (sentLastRotation) {
            resetPreRotation();
        }
    }

    private static void resetPreRotation() {
//        Camera camera = PlayerUtils.playerCamera();
//        camera.view.rotate(Vector3.Y, preYaw);
//        camera.view.rotate(Vector3.X, prePitch);
        //TODO figure out which direction view is correct
        Entity player = client.getLocalPlayer().getEntity();
        player.viewDirection.rotate(Vector3.Y, preYaw);
        player.viewDirection.rotate(Vector3.X, prePitch);
    }

    @EventHandler
    private static void onTick(TickEvent.Pre event) {
        rotationTimer++;
    }

    public static float getYaw(Entity entity) {
        Entity player = client.getLocalPlayer().getEntity();
        return PlayerUtils.playerYaw() +
            PlayerUtils.wrapDegrees((float) Math.toDegrees(Math.atan2(entity.position.z - player.position.z, entity.position.x - player.position.x)) - 90f - PlayerUtils.playerYaw());
    }

    public static float getYaw(Vector3 pos) {
        Entity player = client.getLocalPlayer().getEntity();
        return PlayerUtils.playerYaw() + PlayerUtils.wrapDegrees((float) Math.toDegrees(Math.atan2(pos.z - player.position.z, pos.x - player.position.x)) - 90f - PlayerUtils.playerYaw());
    }

    public static float getPitch(Vector3 pos) {
        Entity player = client.getLocalPlayer().getEntity();

        float diffX = pos.x - player.position.x;
        float diffY = pos.y - EntityUtils.eyePosition(player);
        float diffZ = pos.z - player.position.z;

        float diffXZ = (float) Math.sqrt(diffX * diffX + diffZ * diffZ);

        return PlayerUtils.playerPitch() + PlayerUtils.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - PlayerUtils.playerPitch());
    }

    public static float getPitch(Entity entity, Target target) {
        Entity player = client.getLocalPlayer().getEntity();
        float y;
        if (target == Target.Head) y = EntityUtils.eyePosition(entity);
        else if (target == Target.Body) y = entity.position.y + entity.globalBoundingBox.getHeight() / 2;
        else y = entity.position.y;

        float diffX = entity.position.x - player.position.x;
        float diffY = y - (player.position.y + player.globalBoundingBox.getHeight());
        float diffZ = entity.position.z - player.position.z;

        float diffXZ = (float) Math.sqrt(diffX * diffX + diffZ * diffZ);

        return PlayerUtils.playerPitch() + PlayerUtils.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - PlayerUtils.playerPitch());
    }

    public static float getPitch(Entity entity) {
        return getPitch(entity, Target.Body);
    }

    public static float getYaw(BlockPosition pos) {
        Entity player = client.getLocalPlayer().getEntity();
        return PlayerUtils.playerYaw() + PlayerUtils.wrapDegrees((float) Math.toDegrees(Math.atan2(pos.getGlobalZ() + 0.5 - player.position.z, pos.getGlobalX() + 0.5 - player.position.x)) - 90f - PlayerUtils.playerYaw());
    }

    public static float getPitch(BlockPosition pos) {
        Entity player = client.getLocalPlayer().getEntity();

        float diffX = pos.getGlobalX() + 0.5f - player.position.x;
        float diffY = pos.getGlobalY() + 0.5f - (player.position.y + player.globalBoundingBox.getHeight());
        float diffZ = pos.getGlobalZ() + 0.5f - player.position.z;

        float diffXZ = (float) Math.sqrt(diffX * diffX + diffZ * diffZ);

        return PlayerUtils.playerPitch() + PlayerUtils.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - PlayerUtils.playerPitch());
    }

    public static void setCamRotation(float yaw, float pitch) {
        serverYaw = (float) yaw;
        serverPitch = (float) pitch;
        rotationTimer = 0;
    }

    private static class Rotation {
        public float yaw, pitch;
        public int priority;
        public boolean clientSide;
        public Runnable callback;

        public void set(float yaw, float pitch, int priority, boolean clientSide, Runnable callback) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.priority = priority;
            this.clientSide = clientSide;
            this.callback = callback;
        }

        public void sendPacket() {
            PlayerPositionPacket packet = new PlayerPositionPacket(client.getLocalPlayer());
            packet.viewDir.rotate(Vector3.Y, yaw);
            packet.viewDir.rotate(Vector3.X, pitch);
            ClientNetworkManager.CLIENT.identity.send(packet);
            runCallback();
        }

        public void runCallback() {
            if (callback != null) callback.run();
        }
    }
}
