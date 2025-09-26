/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.settings;

//import com.mojang.serialization.Lifecycle;
import com.github.puzzle.core.registries.GenericRegistry;
import com.github.puzzle.core.registries.IRegistry;
import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.ListDataAttribute;
import com.github.puzzle.game.items.data.attributes.StringDataAttribute;
import finalforeach.cosmicreach.blockentities.*;
import finalforeach.cosmicreach.util.Identifier;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import meteordevelopment.meteorclient.MeteorClient;
//import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtElement;
//import net.minecraft.nbt.NbtList;
//import net.minecraft.nbt.NbtString;
//import net.minecraft.registry.Registries;
//import net.minecraft.registry.Registry;
//import net.minecraft.registry.RegistryKey;
//import net.minecraft.registry.SimpleRegistry;
//import net.minecraft.registry.entry.RegistryEntry;
//import net.minecraft.registry.entry.RegistryEntryList;
//import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class StorageBlockListSetting extends Setting<List<Identifier>> {
    public static final Identifier[] STORAGE_BLOCKS = new Identifier[]{
        Identifier.of(BlockEntityFurnace.BLOCK_ENTITY_ID),
        Identifier.of(BlockEntityItemContainer.BLOCK_ENTITY_ID),
    };

    public static final IRegistry<Identifier> REGISTRY = new SRegistry();

    public StorageBlockListSetting(String name, String description, List<Identifier> defaultValue, Consumer<List<Identifier>> onChanged, Consumer<Setting<List<Identifier>>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    public void resetImpl() {
        value = new ArrayList<>(defaultValue);
    }

    @Override
    protected List<Identifier> parseImpl(String str) {
        String[] values = str.split(",");
        List<Identifier> blocks = new ArrayList<>(values.length);
        for (String value : values) {
            Identifier block = Identifier.of(str);
            if (List.of(STORAGE_BLOCKS).contains(block)) blocks.add(block);
        }
        return blocks;
    }

    @Override
    protected boolean isValueValid(List<Identifier> value) {
        return true;
    }

    @Override
    public Iterable<Identifier> getIdentifierSuggestions() {
        return List.of(STORAGE_BLOCKS);
    }

    @Override
    public DataTagManifest save(DataTagManifest tag) {
        ListDataAttribute<StringDataAttribute> valueTag = new ListDataAttribute<>();
        List<StringDataAttribute> list_ = new ArrayList<>();
        for (Identifier type : get()) {
            list_.add(new StringDataAttribute(type.toString()));
        }
        valueTag.setValue(list_);
        tag.addTag(new DataTag<>("value", valueTag));
        return tag;
    }

    @Override
    public List<Identifier> load(DataTagManifest tag) {
        get().clear();
        List<StringDataAttribute> valueTag = tag.getTag("value").getTagAsType((Class<List<StringDataAttribute>>) (Class<?>) List.class).getValue();
        for (StringDataAttribute tagI : valueTag) {
            Identifier type = Identifier.of(tagI.getValue());
            if (List.of(STORAGE_BLOCKS).contains(type)) get().add(type);
        }
        return get();
    }

    public static class Builder extends SettingBuilder<Builder, List<Identifier>, StorageBlockListSetting> {
        public Builder() {
            super(new ArrayList<>(0));
        }

        public Builder defaultValue(Identifier... defaults) {
            return defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList<>());
        }

        @Override
        public StorageBlockListSetting build() {
            return new StorageBlockListSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }

    private static class SRegistry extends GenericRegistry<Identifier> {
        public SRegistry() {
            super(MeteorClient.identifier("storage-blocks"));
        }

        public int size() {
            return STORAGE_BLOCKS.length;
        }

        @NotNull
        @Override
        public Iterator<Identifier> iterator() {
            return ObjectIterators.wrap(STORAGE_BLOCKS);
        }
    }
}
