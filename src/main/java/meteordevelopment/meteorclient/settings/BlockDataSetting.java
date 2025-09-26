/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.settings;

import com.github.puzzle.game.PuzzleRegistries;
import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.DataTagManifestAttribute;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.util.Identifier;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import meteordevelopment.meteorclient.utils.misc.ICopyable;
import meteordevelopment.meteorclient.utils.misc.IGetter;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
//import net.minecraft.block.Block;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.registry.Registries;
//import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BlockDataSetting<T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> extends Setting<Map<Block, T>> {
    public final IGetter<T> defaultData;

    public BlockDataSetting(String name, String description, Map<Block, T> defaultValue, Consumer<Map<Block, T>> onChanged, Consumer<Setting<Map<Block, T>>> onModuleActivated, IGetter<T> defaultData, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.defaultData = defaultData;
    }

    @Override
    public void resetImpl() {
        value = new HashMap<>(defaultValue);
    }

    @Override
    protected Map<Block, T> parseImpl(String str) {
        return new HashMap<>(0);
    }

    @Override
    protected boolean isValueValid(Map<Block, T> value) {
        return true;
    }

    @Override
    protected DataTagManifest save(DataTagManifest tag) {
        DataTagManifest valueTag = new DataTagManifest();
        for (Block block : get().keySet()) {
            valueTag.addTag(new DataTag<>(block.getStringId(), new DataTagManifestAttribute(get().get(block).toTag())));
        }
        tag.addTag(new DataTag<>("value", new DataTagManifestAttribute(valueTag)));

        return tag;
    }

    @Override
    protected Map<Block, T> load(DataTagManifest tag) {
        get().clear();

        DataTagManifest valueTag = tag.getTag("value").getTagAsType(DataTagManifest.class).getValue();
        for (String key : valueTag.getKeys()) {
            get().put((Block) PuzzleRegistries.BLOCKS.get(Identifier.of(key)), defaultData.get().copy().fromTag(valueTag.getTag(key).getTagAsType(DataTagManifest.class).getValue()));
        }

        return get();
    }

    public static class Builder<T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> extends SettingBuilder<Builder<T>, Map<Block, T>, BlockDataSetting<T>> {
        private IGetter<T> defaultData;

        public Builder() {
            super(new HashMap<>(0));
        }

        public Builder<T> defaultData(IGetter<T> defaultData) {
            this.defaultData = defaultData;
            return this;
        }

        @Override
        public BlockDataSetting<T> build() {
            return new BlockDataSetting<>(name, description, defaultValue, onChanged, onModuleActivated, defaultData, visible);
        }
    }
}
