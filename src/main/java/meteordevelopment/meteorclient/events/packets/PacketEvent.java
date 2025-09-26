/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.packets;

import com.github.puzzle.game.networking.packet.NetworkHandler;
import finalforeach.cosmicreach.networking.GamePacket;
import finalforeach.cosmicreach.networking.client.ClientNetworkManager;
import finalforeach.cosmicreach.networking.client.netty.NettyClient;
import meteordevelopment.meteorclient.events.Cancellable;
import org.checkerframework.checker.units.qual.N;
//import net.minecraft.network.ClientConnection;
//import net.minecraft.network.packet.Packet;

public class PacketEvent {
    public static class Receive extends Cancellable {
        public GamePacket packet;
        public NettyClient connection;

        public Receive(GamePacket packet, NettyClient connection) {
            this.setCancelled(false);
            this.packet = packet;
            this.connection = connection;
        }
    }

    public static class Send extends Cancellable {
        public GamePacket packet;
        public NettyClient connection;

        public Send(GamePacket packet, NettyClient connection) {
            this.setCancelled(false);
            this.packet = packet;
            this.connection = connection;
        }
    }

    public static class Sent {
        public GamePacket packet;
        public NettyClient connection;

        public Sent(GamePacket packet, NettyClient connection) {
            this.packet = packet;
            this.connection = connection;
        }
    }
}
