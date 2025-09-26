/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.packets;

//import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;

import finalforeach.cosmicreach.networking.GamePacket;
import finalforeach.cosmicreach.networking.packets.sounds.PlaySound2DPacket;

public class PlaySoundPacketEvent {

    private static final PlaySoundPacketEvent INSTANCE = new PlaySoundPacketEvent();

    public GamePacket packet;
    // PlaySound3DPacket || PlaySound2DPacket;

    public static PlaySoundPacketEvent get(GamePacket packet) {
        INSTANCE.packet = packet;
        return INSTANCE;
    }
}
