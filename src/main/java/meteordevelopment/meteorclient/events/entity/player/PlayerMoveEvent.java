/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.entity.player;

//import net.minecraft.entity.MovementType;
//import net.minecraft.util.math.Vec3d;

import com.badlogic.gdx.math.Vector3;

public class PlayerMoveEvent {
    private static final PlayerMoveEvent INSTANCE = new PlayerMoveEvent();

    public Vector3 movement;

    public static PlayerMoveEvent get(Vector3 movement) {
        INSTANCE.movement = movement;
        return INSTANCE;
    }
}
