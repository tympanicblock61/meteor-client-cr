/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

import com.badlogic.gdx.graphics.g3d.particles.ParticleShader;
import finalforeach.cosmicreach.audio.GameMusicManager;
import finalforeach.cosmicreach.audio.GameSong;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.items.Item;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.lang.Lang;
import finalforeach.cosmicreach.util.Identifier;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.ResourcePacksReloadedEvent;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.orbit.EventHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Names {
    private static final Map<Item, String> itemNames = new Reference2ObjectOpenHashMap<>(128);
    private static final Map<Block, String> blockNames = new Reference2ObjectOpenHashMap<>(128);
    private static final Map<Class<? extends Entity>, String> entityTypeNames = new Reference2ObjectOpenHashMap<>(64);
    private static final Map<ParticleShader.ParticleType, String> particleTypesNames = new Reference2ObjectOpenHashMap<>(64);
    private static final Map<Identifier, String> soundNames = new HashMap<>(64);

    private Names() {
    }

    @PreInit
    public static void init() {
        MeteorClient.EVENT_BUS.subscribe(Names.class);
    }

    @EventHandler
    private static void onResourcePacksReloaded(ResourcePacksReloadedEvent event) {
        itemNames.clear();
        blockNames.clear();
        entityTypeNames.clear();
        particleTypesNames.clear();
        soundNames.clear();
    }

    public static String get(Item item) {
        System.out.println(Lang.get(item.getID()));
        return itemNames.computeIfAbsent(item, Item::getName);
    }

    public static String get(Block block) {
        return blockNames.computeIfAbsent(block, Block::getName);
    }

    public static String get(Entity entityType) {
        return entityTypeNames.computeIfAbsent(entityType.getClass(), entityType1 -> entityType.entityTypeId);
    }

    public static String get(ParticleShader.ParticleType type) {
        return particleTypesNames.computeIfAbsent(type, ParticleShader.ParticleType::name);
    }

    public static String getSoundName(Identifier id) {
        return soundNames.computeIfAbsent(id, identifier -> {
            GameSong song = Arrays.stream(GameMusicManager.gameSongs.items).filter((gameSong -> gameSong.id == id)).findAny().orElse(null);

            if (song != null) {
                return song.fileName.substring(0, song.fileName.indexOf("."));
            }
            return id.toString();
        });
    }

    public static String get(ItemStack stack) {
        return stack.getName();
    }
}
