/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.settings;

import com.github.puzzle.core.loader.util.Reflection;
import com.github.puzzle.core.registries.GenericRegistry;
import com.github.puzzle.core.registries.IRegistry;
import com.github.puzzle.core.registries.MapRegistry;
import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.ListDataAttribute;
import com.github.puzzle.game.items.data.attributes.StringDataAttribute;
import finalforeach.cosmicreach.util.Identifier;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
//import net.minecraft.entity.EntityType;
//import net.minecraft.entity.SpawnGroup;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtElement;
//import net.minecraft.nbt.NbtList;
//import net.minecraft.nbt.NbtString;
//import net.minecraft.registry.Registries;
//import net.minecraft.util.Identifier;

import finalforeach.cosmicreach.entities.Entity;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EntityTypeListSetting extends Setting<Set<Class<? extends Entity>>> {
    static {
        addEntities();
    }

    public final Predicate<Class<? extends Entity>> filter;
    private List<String> suggestions;
    //private final static List<String> groups = List.of("animal", "wateranimal", "monster", "ambient", "misc");

    public EntityTypeListSetting(String name, String description, Set<Class<? extends Entity>> defaultValue, Consumer<Set<Class<? extends Entity>>> onChanged, Consumer<Setting<Set<Class<? extends Entity>>>> onModuleActivated, IVisible visible, Predicate<Class<? extends Entity>> filter) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.filter = filter;
    }

    @Override
    public void resetImpl() {
        value = new ObjectOpenHashSet<>(defaultValue);
    }

    @Override
    protected Set<Class<? extends Entity>> parseImpl(String str) {
        String[] values = str.split(",");
        Set<Class<? extends Entity>> entities = new ObjectOpenHashSet<>(values.length);
        Set<Class<? extends Entity>> entitiesSubClasses = null;

        try {
            for (String value : values) {
                Class<? extends Entity> entity = EntityRegistry.get(Identifier.of(value));
                if (entity != null) entities.add(entity);
                else {
                    if (entitiesSubClasses == null) {
                        Reflections reflections = new Reflections(Entity.class.getPackage().toString());
                        entitiesSubClasses=reflections.getSubTypesOf(Entity.class);
                    }
                    for (Class<? extends Entity> entity_ : entitiesSubClasses) {
                        String id = getIdentifier(entity_);
                        if (id.equals(value)) {
                            EntityRegistry.store(Identifier.of(getIdentifier(entity_)), entity_);
                            entities.add(entity_);
                            break;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        return entities;
    }

    @Override
    protected boolean isValueValid(Set<Class<? extends Entity>> value) {
        return true;
    }

    @Override
    public List<String> getSuggestions() {
        if (suggestions == null) {
            suggestions = new ArrayList<>();
            for (Class<? extends Entity> entityType : EntityRegistry) {
                if (filter == null || filter.test(entityType)) suggestions.add(getIdentifier(entityType));
            }
        }

        return suggestions;
    }

    @Override
    public DataTagManifest save(DataTagManifest tag) {
        ListDataAttribute<StringDataAttribute> valueTag = new ListDataAttribute<>();
        List<StringDataAttribute> list_ = new ArrayList<>();
        for (Class<? extends Entity> entityType : get()) {
            list_.add(new StringDataAttribute(getIdentifier(entityType)));
        }
        valueTag.setValue(list_);
        tag.addTag(new DataTag<>("value", valueTag));
        return tag;
    }

    @Override
    public Set<Class<? extends Entity>> load(DataTagManifest tag) {
        get().clear();
        List<StringDataAttribute> valueTag = tag.getTag("value").getTagAsType((Class<List<StringDataAttribute>>) (Class<?>) List.class).getValue();
        for (StringDataAttribute tagI : valueTag) {
            Class<? extends Entity> type = EntityRegistry.get(Identifier.of(tagI.getValue()));
            if (filter == null || filter.test(type)) get().add(type);
        }
        return get();
    }

    public static class Builder extends SettingBuilder<Builder, Set<Class<? extends Entity>>, EntityTypeListSetting> {
        private Predicate<Class<? extends Entity>> filter;

        public Builder() {
            super(new ObjectOpenHashSet<>(0));
        }

        public Builder defaultValue(Class<? extends Entity>... defaults) {
            return defaultValue(defaults != null ? new ObjectOpenHashSet<>(defaults) : new ObjectOpenHashSet<>(0));
        }

        public Builder onlyAttackable() {
            filter = EntityUtils::isAttackable;
            return this;
        }

        public Builder filter(Predicate<Class<? extends Entity>> filter){
            this.filter = filter;
            return this;
        }

        @Override
        public EntityTypeListSetting build() {
            return new EntityTypeListSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, filter);
        }
    }

    private static final IRegistry<Class<? extends Entity>> EntityRegistry = new MapRegistry<>(Identifier.of("meteor", "entities"), new HashMap<>(), true, true);

    private static void addEntities() {
        Reflections reflections = new Reflections(Entity.class.getPackage().toString());
        Set<Class<? extends Entity>> entities = reflections.getSubTypesOf(Entity.class);
        for (Class<? extends Entity> entity : entities) {
            EntityRegistry.store(Identifier.of(getIdentifier(entity)), entity);
        }
    }

    private static String getIdentifier(Class<? extends Entity> entity) {
        try {
            Field entityTypeField = entity.getDeclaredField("ENTITY_TYPE_ID");
            return (String) entityTypeField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            return entity.getName();
        }
    }
}
