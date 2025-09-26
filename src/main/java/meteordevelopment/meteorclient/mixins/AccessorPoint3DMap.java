/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixins;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import finalforeach.cosmicreach.util.Point3DMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Point3DMap.class)
public interface AccessorPoint3DMap {
    @Accessor("map")
    LongMap<IntMap<T>> getMap();
}
