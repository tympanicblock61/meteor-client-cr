/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.settings;

//import net.minecraft.item.Item;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.registry.Registries;
//import net.minecraft.util.Identifier;

import com.github.puzzle.game.PuzzleRegistries;
import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.StringDataAttribute;
import finalforeach.cosmicreach.items.Item;
import finalforeach.cosmicreach.util.Identifier;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemSetting extends Setting<Item> {
    public final Predicate<Item> filter;

    public ItemSetting(String name, String description, Item defaultValue, Consumer<Item> onChanged, Consumer<Setting<Item>> onModuleActivated, IVisible visible, Predicate<Item> filter) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.filter = filter;
    }

    @Override
    protected Item parseImpl(String str) {
        return Item.allItems.get(str);
    }

    @Override
    protected boolean isValueValid(Item value) {
        return filter == null || filter.test(value);
    }

    @Override
    public Iterable<Identifier> getIdentifierSuggestions() {
        return Arrays.stream(Item.allItems.keys().toArray().items).map(Identifier::of).collect(Collectors.toList());
    }

    @Override
    public DataTagManifest save(DataTagManifest tag) {
        tag.addTag(new DataTag<>("value", new StringDataAttribute(get().getID())));
        return tag;
    }

    @Override
    public Item load(DataTagManifest tag) {
        value = Item.allItems.get(Identifier.of(tag.getTag("value").getTagAsType(String.class).getValue()).toString());

        if (filter != null && !filter.test(value)) {
            for (Item item : Item.allItems.values()) {
                if (filter.test(item)) {
                    value = item;
                    break;
                }
            }
        }

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, Item, ItemSetting> {
        private Predicate<Item> filter;

        public Builder() {
            super(null);
        }

        public Builder filter(Predicate<Item> filter) {
            this.filter = filter;
            return this;
        }

        @Override
        public ItemSetting build() {
            return new ItemSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, filter);
        }
    }
}
