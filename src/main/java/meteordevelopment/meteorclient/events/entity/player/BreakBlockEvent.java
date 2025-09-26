/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.entity.player;

import finalforeach.cosmicreach.blocks.BlockPosition;
import meteordevelopment.meteorclient.events.Cancellable;
//import net.minecraft.util.math.BlockPos;

public class BreakBlockEvent extends Cancellable {
    private static final BreakBlockEvent INSTANCE = new BreakBlockEvent();

    public BlockPosition blockPos;

    public static BreakBlockEvent get(BlockPosition blockPos) {
        INSTANCE.setCancelled(false);
        INSTANCE.blockPos = blockPos;
        return INSTANCE;
    }
}
