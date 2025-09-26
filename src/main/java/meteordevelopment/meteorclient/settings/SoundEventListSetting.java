/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.settings;

//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtElement;
//import net.minecraft.nbt.NbtList;
//import net.minecraft.nbt.NbtString;
//import net.minecraft.registry.Registries;
//import net.minecraft.sound.SoundEvent;
//import net.minecraft.util.Identifier;

import com.github.puzzle.core.registries.GenericRegistry;
import com.github.puzzle.core.registries.IRegistry;
import com.github.puzzle.game.PuzzleRegistries;
import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.ListDataAttribute;
import com.github.puzzle.game.items.data.attributes.StringDataAttribute;
import de.pottgames.tuningfork.SoundBuffer;
import finalforeach.cosmicreach.GameAssetLoader;
import finalforeach.cosmicreach.networking.packets.sounds.PlaySound2DPacket;
import finalforeach.cosmicreach.util.Identifier;
import meteordevelopment.meteorclient.MeteorClient;

import javax.xml.crypto.Data;
import java.util.*;
import java.util.function.Consumer;

public class SoundEventListSetting extends Setting<List<SoundBuffer>> {

    public static Map<SoundBuffer, Identifier> SOUNDS_R = new HashMap<>();
    public static IRegistry<SoundBuffer> SOUNDS = new GenericRegistry<>(Identifier.of("sounds"));



    public SoundEventListSetting(String name, String description, List<SoundBuffer> defaultValue, Consumer<List<SoundBuffer>> onChanged, Consumer<Setting<List<SoundBuffer>>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        if (SOUNDS.names().isEmpty()) {
            GameAssetLoader.forEachAsset("base/sounds/", ".ogg", (path, file) -> {
                SoundBuffer sound = GameAssetLoader.getSound(path);
                Identifier id = Identifier.of("base", file.name());
                SOUNDS.store(id, sound);
                SOUNDS_R.put(sound, id);
            });
        }
    }

    @Override
    public void resetImpl() {
        value = new ArrayList<>(defaultValue);
    }

    @Override
    protected List<SoundBuffer> parseImpl(String str) {
        String[] values = str.split(",");
        List<SoundBuffer> sounds = new ArrayList<>(values.length);
        for (String value : values) {
            SoundBuffer sound = SOUNDS.get(Identifier.of("base", value));
            if (sound != null) sounds.add(sound);
        }
        return sounds;
    }

    @Override
    protected boolean isValueValid(List<SoundBuffer> value) {
        return true;
    }

    @Override
    public Iterable<Identifier> getIdentifierSuggestions() {
        return SOUNDS.names();
    }

    @Override
    public DataTagManifest save(DataTagManifest tag) {
        ListDataAttribute<StringDataAttribute> valueTag = new ListDataAttribute<>();
        List<StringDataAttribute> list_ = new ArrayList<>();
        for (SoundBuffer sound : get()) {
            Identifier id = SOUNDS_R.get(sound);
            if (id != null) list_.add(new StringDataAttribute(id.toString()));
        }
        valueTag.setValue(list_);
        tag.addTag(new DataTag<>("value", valueTag));
        return tag;
    }

    @Override
    public List<SoundBuffer> load(DataTagManifest tag) {
        get().clear();
        List<StringDataAttribute> valueTag = tag.getTag("value").getTagAsType((Class<List<StringDataAttribute>>) (Class<?>) List.class).getValue();
        for (StringDataAttribute tagI : valueTag) {
            SoundBuffer soundEvent = SOUNDS.get(Identifier.of(tagI.getValue()));
            if (soundEvent != null) get().add(soundEvent);
        }
        return get();
    }

    public static class Builder extends SettingBuilder<Builder, List<SoundBuffer>, SoundEventListSetting> {
        public Builder() {
            super(new ArrayList<>(0));
        }

        public Builder defaultValue(SoundBuffer... defaults) {
            return defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList<>());
        }

        @Override
        public SoundEventListSetting build() {
            return new SoundEventListSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
