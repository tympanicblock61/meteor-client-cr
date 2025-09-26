/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.packets;

import finalforeach.cosmicreach.networking.packets.items.SlotSyncPacket;
//import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;

public class InventoryEvent {
    private static final InventoryEvent INSTANCE = new InventoryEvent();

    public SlotSyncPacket packet;

    public static InventoryEvent get(SlotSyncPacket packet) {
        INSTANCE.packet = packet;
        return INSTANCE;
    }
}
