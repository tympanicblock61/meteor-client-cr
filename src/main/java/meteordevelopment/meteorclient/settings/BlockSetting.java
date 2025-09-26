/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.settings;

//import net.minecraft.block.Block;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.registry.Registries;
//import net.minecraft.util.Identifier;

import com.github.puzzle.game.PuzzleRegistries;
import com.github.puzzle.game.block.IModBlock;
import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.StringDataAttribute;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.util.Identifier;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BlockSetting extends Setting<Block> {
    public final Predicate<Block> filter;

    public BlockSetting(String name, String description, Block defaultValue, Consumer<Block> onChanged, Consumer<Setting<Block>> onModuleActivated, IVisible visible, Predicate<Block> filter) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.filter = filter;
    }

    @Override
    protected Block parseImpl(String str) {
        return (Block) PuzzleRegistries.BLOCKS.get(Identifier.of(str));
    }

    @Override
    protected boolean isValueValid(Block value) {
        return filter == null || filter.test(value);
    }

    @Override
    public Iterable<Identifier> getIdentifierSuggestions() {
        return PuzzleRegistries.BLOCKS.names();
    }

    @Override
    protected DataTagManifest save(DataTagManifest tag) {
        tag.addTag(new DataTag<>("value", new StringDataAttribute(get().getStringId())));

        return tag;
    }

    @Override
    protected Block load(DataTagManifest tag) {
        value = (Block) PuzzleRegistries.BLOCKS.get(Identifier.of(tag.getTag("value").getTagAsType(String.class).getValue()));

        if (filter != null && !filter.test(value)) {
            for (IModBlock block : PuzzleRegistries.BLOCKS) {
                if (filter.test((Block) block)) {
                    value = (Block) block;
                    break;
                }
            }
        }

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, Block, BlockSetting> {
        private Predicate<Block> filter;

        public Builder() {
            super(null);
        }

        public Builder filter(Predicate<Block> filter) {
            this.filter = filter;
            return this;
        }

        @Override
        public BlockSetting build() {
            return new BlockSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, filter);
        }
    }
}
