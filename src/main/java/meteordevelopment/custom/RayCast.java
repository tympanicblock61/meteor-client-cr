/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.custom;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.world.World;

public class RayCast {

    public static HitResult raycast(RaycastContext context) {
        Vector3 direction = context.end.sub(context.start).nor();
        float stepSize = 1f;
        Vector3 currentPosition = context.start;
        while (currentPosition.dst(context.end) > stepSize) {
            BlockState state = context.world.getDefaultZone().getBlockState(currentPosition);
            Block block = state.getBlock();
            if (block != null && !state.walkThrough) {
                return new HitResult(state, HitResult.Type.BLOCK);
            }

            if (block != null && context.fluid.handled(state)) {
                return new HitResult(state, HitResult.Type.FLUID);
            }

            Entity entity = findEntityInRay(currentPosition, context.world);
            if (entity != null) {
                return new HitResult(entity, HitResult.Type.ENTITY);
            }

            currentPosition = currentPosition.add(direction.scl(stepSize));
        }

        return new HitResult(null, HitResult.Type.MISS);
    }

    private static Entity findEntityInRay(Vector3 pos, World world) {
        Array<Entity> entities = world.getDefaultZone().getAllEntities();
        for (Entity entity : entities) {
            if (entity.position.dst(pos) < 1) {
                return entity;
            }
        }
        return null;
    }

    public static class HitResult {
        public enum Type {
            MISS, BLOCK, FLUID, ENTITY
        }

        private final Object hitObject;
        private final Type type;

        public HitResult(Object hitObject, Type type) {
            this.hitObject = hitObject;
            this.type = type;
        }

        public Type getType() {
            return type;
        }

        public Object getHitObject() {
            return hitObject;
        }
    }
}
