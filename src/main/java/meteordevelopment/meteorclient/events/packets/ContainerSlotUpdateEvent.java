/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.packets;

//import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;

import finalforeach.cosmicreach.networking.packets.items.SlotSyncPacket;

public class ContainerSlotUpdateEvent {
    private static final ContainerSlotUpdateEvent INSTANCE = new ContainerSlotUpdateEvent();

    public SlotSyncPacket packet;

    public static ContainerSlotUpdateEvent get(SlotSyncPacket packet) {
        INSTANCE.packet = packet;
        return INSTANCE;
    }
}
