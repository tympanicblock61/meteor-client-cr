/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixins;


import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.util.IPoint3DMap;
import finalforeach.cosmicreach.world.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Chunk.class)
public interface AccessorChunk {
    @Accessor("blockEntities")
    IPoint3DMap<BlockEntity> getBlockEntities();
}
