/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.world;

//import net.minecraft.block.BlockState;
//import net.minecraft.util.math.BlockPos;

import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;

public class BlockUpdateEvent {
    private static final BlockUpdateEvent INSTANCE = new BlockUpdateEvent();

    public BlockPosition pos;
    public BlockState oldState, newState;

    public static BlockUpdateEvent get(BlockPosition pos, BlockState oldState, BlockState newState) {
        INSTANCE.pos = pos;
        INSTANCE.oldState = oldState;
        INSTANCE.newState = newState;

        return INSTANCE;
    }
}
