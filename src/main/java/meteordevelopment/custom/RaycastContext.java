/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.custom;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.world.World;
import meteordevelopment.meteorclient.gui.WidgetScreen;

import java.util.function.Predicate;


public class RaycastContext {
    public final Vector3 start;
    public final Vector3 end;
    public final FluidHandling fluid;
    public final World world;

    public RaycastContext(Vector3 start, Vector3 end, FluidHandling fluidHandling, World world) {
        this.start = start;
        this.end = end;
        this.fluid = fluidHandling;
        this.world = world;
    }

    public static enum FluidHandling {
        NONE((state) -> false),
        SOURCE_ONLY((blockState -> blockState.isFluid)),
        ANY((state) -> state.getItem() != null),
        WATER((state) -> state.isFluid && state.getBlock() == Block.WATER);

        private final Predicate<BlockState> predicate;

        FluidHandling(final Predicate<BlockState> predicate) {
            this.predicate = predicate;
        }

        public boolean handled(BlockState state) {
            return this.predicate.test(state);
        }
    }
}
