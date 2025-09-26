/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.entity.player;

//import net.minecraft.fluid.FluidState;

import finalforeach.cosmicreach.blocks.BlockState;

public class CanWalkOnFluidEvent {
    private static final CanWalkOnFluidEvent INSTANCE = new CanWalkOnFluidEvent();

    public BlockState fluidState;
    public boolean walkOnFluid;

    public static CanWalkOnFluidEvent get(BlockState fluid) {
        INSTANCE.fluidState = fluid;
        INSTANCE.walkOnFluid = false;
        return INSTANCE;
    }
}
