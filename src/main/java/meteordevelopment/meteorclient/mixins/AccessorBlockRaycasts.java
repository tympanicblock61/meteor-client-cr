/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixins;

import finalforeach.cosmicreach.BlockRaycasts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockRaycasts.class)
public interface AccessorBlockRaycasts {
    @Accessor("maximumRaycastDist")
    float getRaycastDist();
}
