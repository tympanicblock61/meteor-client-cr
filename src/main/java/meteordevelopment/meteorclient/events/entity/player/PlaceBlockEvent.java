/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.entity.player;

import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockPosition;
import meteordevelopment.meteorclient.events.Cancellable;
//import net.minecraft.block.Block;
//import net.minecraft.util.math.BlockPos;

public class PlaceBlockEvent extends Cancellable {
    private static final PlaceBlockEvent INSTANCE = new PlaceBlockEvent();

    public BlockPosition blockPos;
    public Block block;

    public static PlaceBlockEvent get(BlockPosition blockPos, Block block) {
        INSTANCE.setCancelled(false);
        INSTANCE.blockPos = blockPos;
        INSTANCE.block = block;
        return INSTANCE;
    }
}
