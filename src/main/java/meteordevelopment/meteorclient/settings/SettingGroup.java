/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.settings;

import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.BooleanDataAttribute;
import com.github.puzzle.game.items.data.attributes.DataTagManifestAttribute;
import com.github.puzzle.game.items.data.attributes.ListDataAttribute;
import com.github.puzzle.game.items.data.attributes.StringDataAttribute;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtElement;
//import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SettingGroup implements ISerializable<SettingGroup>, Iterable<Setting<?>> {
    public final String name;
    public boolean sectionExpanded;

    final List<Setting<?>> settings = new ArrayList<>(1);

    SettingGroup(String name, boolean sectionExpanded) {
        this.name = name;
        this.sectionExpanded = sectionExpanded;
    }

    public Setting<?> get(String name) {
        for (Setting<?> setting : this) {
            if (setting.name.equals(name)) return setting;
        }

        return null;
    }

    public <T> Setting<T> add(Setting<T> setting) {
        settings.add(setting);

        return setting;
    }

    public Setting<?> getByIndex(int index) {
        return settings.get(index);
    }

    @Override
    public @NotNull Iterator<Setting<?>> iterator() {
        return settings.iterator();
    }

    @Override
    public DataTagManifest toTag() {
        DataTagManifest tag = new DataTagManifest();
        tag.addTag(new DataTag<>("name", new StringDataAttribute(name)));
        tag.addTag(new DataTag<>("sectionExpanded", new BooleanDataAttribute(sectionExpanded)));
        ListDataAttribute<DataTagManifestAttribute> settingsTag = new ListDataAttribute<>();
        List<DataTagManifestAttribute> list_ = new ArrayList<>();
        for (Setting<?> setting : this) {
            if (setting.wasChanged()) {
                list_.add(new DataTagManifestAttribute(setting.toTag()));
            }
        }
        settingsTag.setValue(list_);
        tag.addTag(new DataTag<>("settings", settingsTag));
        return tag;
    }

    @Override
    public SettingGroup fromTag(DataTagManifest tag) {
        sectionExpanded = tag.getTag("sectionExpanded").getTagAsType(Boolean.TYPE).getValue();
        List<DataTagManifestAttribute> settingsTag = tag.getTag("settings").getTagAsType((Class<List<DataTagManifestAttribute>>) (Class<?>) List.class).getValue();
        for (DataTagManifestAttribute t : settingsTag) {
            DataTagManifest settingTag = t.getValue();


            Setting<?> setting = get(settingTag.getTag("name").getTagAsType(String.class).getValue());
            if (setting != null) setting.fromTag(settingTag);
        }
        return this;
    }
}
