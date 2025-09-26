/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.world;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.util.IPoint3DMap;
import finalforeach.cosmicreach.util.Point3DMap;
import finalforeach.cosmicreach.world.Chunk;
import meteordevelopment.meteorclient.mixins.AccessorChunk;
import meteordevelopment.meteorclient.mixins.AccessorPoint3DMap;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class BlockEntityIterator implements Iterator<BlockEntity> {
    private final Iterator<Chunk> chunks;
    private Iterator<BlockEntity> blockEntities;

    public BlockEntityIterator() {
        chunks = new ChunkIterator(false);
        nextChunk();
    }

    private void nextChunk() {
        while (true) {
            if (!chunks.hasNext()) break;

            try {
                Point3DMap<BlockEntity> blockEntityMap = (Point3DMap<BlockEntity>) ((AccessorChunk)chunks.next()).getBlockEntities();
                if (!blockEntityMap.isEmpty()) {
                    blockEntities = iterator(blockEntityMap);
                    break;
                }
            } catch (Exception ignored) {}
        }
    }

    @Override
    public boolean hasNext() {
        if (blockEntities == null) return false;
        if (blockEntities.hasNext()) return true;

        nextChunk();

        return blockEntities.hasNext();
    }

    @Override
    public BlockEntity next() {
        return blockEntities.next();
    }

    public static <T> Iterator<T> iterator(Point3DMap<T> point3DMap) throws IllegalAccessException {
        return new Iterator<>() {
            private final Iterator<LongMap.Entry<IntMap<T>>> outerIterator = ((AccessorPoint3DMap)point3DMap).getMap().iterator();
            private Iterator<IntMap.Entry<T>> innerIterator = null;

            @Override
            public boolean hasNext() {
                while ((innerIterator == null || !innerIterator.hasNext()) && outerIterator.hasNext()) {
                    innerIterator = outerIterator.next().value.iterator();
                }
                return innerIterator != null && innerIterator.hasNext();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return innerIterator.next().value;
            }
        };
    }
}
