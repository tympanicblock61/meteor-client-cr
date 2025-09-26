/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.world;

//import net.minecraft.client.network.ServerAddress;
//import net.minecraft.client.network.ServerInfo;

import finalforeach.cosmicreach.settings.ServerSettings;

public class ServerConnectBeginEvent {
    private static final ServerConnectBeginEvent INSTANCE = new ServerConnectBeginEvent();
    public String address;
    public ServerSettings info;

    public static ServerConnectBeginEvent get(String address, ServerSettings info) {
        INSTANCE.address = address;
        INSTANCE.info = info;
        return INSTANCE;
    }
}
