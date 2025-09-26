/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixins;


import com.badlogic.gdx.math.Vector3;
import finalforeach.cosmicreach.entities.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface AccessorEntity {
    @Accessor("acceleration")
    Vector3 getAcceleration();
}
