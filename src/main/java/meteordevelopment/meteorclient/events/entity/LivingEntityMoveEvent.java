/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.entity;

//import net.minecraft.entity.LivingEntity;
//import net.minecraft.util.math.Vec3d;

import com.badlogic.gdx.math.Vector3;
import finalforeach.cosmicreach.entities.Entity;

public class LivingEntityMoveEvent {
    private static final LivingEntityMoveEvent INSTANCE = new LivingEntityMoveEvent();

    public Entity entity;
    public Vector3 movement;

    public static LivingEntityMoveEvent get(Entity entity, Vector3 movement) {
        INSTANCE.entity = entity;
        INSTANCE.movement = movement;
        return INSTANCE;
    }
}
