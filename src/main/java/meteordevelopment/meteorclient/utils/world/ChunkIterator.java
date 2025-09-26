/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.world;

//import finalforeach.cosmicreach.GameSingletons;
//import finalforeach.cosmicreach.util.Point3DMap;

import finalforeach.cosmicreach.world.Chunk;
import finalforeach.cosmicreach.world.Region;
import finalforeach.cosmicreach.world.Zone;

import java.util.Iterator;

import static meteordevelopment.meteorclient.MeteorClient.client;

public class ChunkIterator implements Iterator<Chunk> {
    // TODO replace with accessor
    private final Zone zone = client.getLocalPlayer().getZone();
    private final Region[] map = zone.getRegions();
    private final boolean onlyWithLoadedNeighbours;
    private int regionIndex = 0;
    private int chunkIndex = 0;
    private Chunk chunk;

    public ChunkIterator(boolean onlyWithLoadedNeighbours) {
        this.onlyWithLoadedNeighbours = onlyWithLoadedNeighbours;

        getNext();
    }

    private Chunk getNext() {
        Chunk prev = chunk;
        chunk = null;

        while (regionIndex < map.length) { // Iterate through regions
            Region region = map[regionIndex];

            while (chunkIndex < region.getChunks().size) {
                chunk = region.getChunks().get(chunkIndex++);
                if (chunk != null && (!onlyWithLoadedNeighbours || isInRadius(chunk))) return chunk;
                return prev;
            }
            chunkIndex = 0;
            regionIndex++;
        }
        return prev;
    }

    private boolean isInRadius(Chunk chunk) {
        int x = chunk.chunkX;
        int y = chunk.chunkY;
        int z = chunk.chunkZ;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    Chunk neighbor = zone.getChunkAtChunkCoords(x + dx, y + dy, z + dz);
                    if (neighbor != null) return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean hasNext() {
        return chunk != null;
    }

    @Override
    public Chunk next() {
        return getNext();
    }
}
