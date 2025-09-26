/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.settings;

//import net.minecraft.nbt.NbtCompound;

import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.BooleanDataAttribute;
import com.github.puzzle.game.items.data.attributes.DataTagManifestAttribute;

import java.util.List;
import java.util.function.Consumer;

public class BoolSetting extends Setting<Boolean> {
    private static final List<String> SUGGESTIONS = List.of("true", "false", "toggle");

    private BoolSetting(String name, String description, Boolean defaultValue, Consumer<Boolean> onChanged, Consumer<Setting<Boolean>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected Boolean parseImpl(String str) {
        if (str.equalsIgnoreCase("true") || str.equalsIgnoreCase("1")) return true;
        else if (str.equalsIgnoreCase("false") || str.equalsIgnoreCase("0")) return false;
        else if (str.equalsIgnoreCase("toggle")) return !get();
        return null;
    }

    @Override
    protected boolean isValueValid(Boolean value) {
        return true;
    }

    @Override
    public List<String> getSuggestions() {
        return SUGGESTIONS;
    }

    @Override
    public DataTagManifest save(DataTagManifest tag) {
        tag.addTag(new DataTag<>("value", new BooleanDataAttribute(get())));
        return tag;
    }

    @Override
    public Boolean load(DataTagManifest tag) {
        set(tag.getTag("value").getTagAsType(Boolean.TYPE).getValue());
        return get();
    }

    public static class Builder extends SettingBuilder<Builder, Boolean, BoolSetting> {
        public Builder() {
            super(false);
        }

        @Override
        public BoolSetting build() {
            return new BoolSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
