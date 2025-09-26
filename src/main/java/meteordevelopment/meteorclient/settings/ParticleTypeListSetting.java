/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.settings;

//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtElement;
//import net.minecraft.nbt.NbtList;
//import net.minecraft.nbt.NbtString;
//import net.minecraft.particle.ParticleEffect;
//import net.minecraft.particle.ParticleType;
//import net.minecraft.registry.Registries;
//import net.minecraft.util.Identifier;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

// TODO FIX THIS

public class ParticleTypeListSetting/* extends Setting<List<ParticleShader.ParticleType>>*/ {
//    public ParticleTypeListSetting(String name, String description, List<ParticleShader.ParticleType> defaultValue, Consumer<List<ParticleShader.ParticleType>> onChanged, Consumer<Setting<List<ParticleShader.ParticleType>>> onModuleActivated, IVisible visible) {
//        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
//    }
//
//    @Override
//    public void resetImpl() {
//        value = new ArrayList<>(defaultValue);
//    }
//
//    @Override
//    protected List<ParticleShader.ParticleType> parseImpl(String str) {
//        String[] values = str.split(",");
//        List<ParticleShader.ParticleType> particleTypes = new ArrayList<>(values.length);
//
//        try {
//            for (String value : values) {
//
//                ParticleShader.ParticleType particleType = parseId(Registries.PARTICLE_TYPE, value);
//                if (particleType instanceof ParticleEffect) particleTypes.add(particleType);
//            }
//        } catch (Exception ignored) {}
//
//        return particleTypes;
//    }
//
//    @Override
//    protected boolean isValueValid(List<ParticleShader.ParticleType> value) {
//        return true;
//    }
//
//    @Override
//    public Iterable<Identifier> getIdentifierSuggestions() {
//        return Registries.PARTICLE_TYPE.getIds();
//    }
//
//    @Override
//    public NbtCompound save(NbtCompound tag) {
//        NbtList valueTag = new NbtList();
//        for (ParticleShader.ParticleType particleType : get()) {
//            Identifier id = Registries.PARTICLE_TYPE.getId(particleType);
//            if (id != null) valueTag.add(NbtString.of(id.toString()));
//        }
//        tag.put("value", valueTag);
//
//        return tag;
//    }
//
//    @Override
//    public List<ParticleShader.ParticleType> load(NbtCompound tag) {
//        get().clear();
//
//        NbtList valueTag = tag.getList("value", 8);
//        for (NbtElement tagI : valueTag) {
//            ParticleShader.ParticleType particleType = Registries.PARTICLE_TYPE.get(Identifier.of(tagI.asString()));
//            if (particleType != null) get().add(particleType);
//        }
//
//        return get();
//    }
//
//    public static class Builder extends SettingBuilder<Builder, List<ParticleShader.ParticleType>, ParticleTypeListSetting> {
//        public Builder() {
//            super(new ArrayList<>(0));
//        }
//
//        public Builder defaultValue(ParticleShader.ParticleType... defaults) {
//            return defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList<>());
//        }
//
//        @Override
//        public ParticleTypeListSetting build() {
//            return new ParticleTypeListSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
//        }
//    }
}
