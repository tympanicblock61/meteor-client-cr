/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.settings;

import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.ListDataAttribute;
import com.github.puzzle.game.items.data.attributes.StringDataAttribute;
import finalforeach.cosmicreach.networking.GamePacket;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import meteordevelopment.meteorclient.utils.network.PacketUtils;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtElement;
//import net.minecraft.nbt.NbtList;
//import net.minecraft.nbt.NbtString;
//import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PacketListSetting extends Setting<Set<Class<? extends GamePacket>>> {
    public final Predicate<Class<? extends GamePacket>> filter;
    private static List<String> suggestions;

    public PacketListSetting(String name, String description, Set<Class<? extends GamePacket>> defaultValue, Consumer<Set<Class<? extends GamePacket>>> onChanged, Consumer<Setting<Set<Class<? extends GamePacket>>>> onModuleActivated, Predicate<Class<? extends GamePacket>> filter, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.filter = filter;
    }

    @Override
    public void resetImpl() {
        value = new ObjectOpenHashSet<>(defaultValue);
    }

    @Override
    protected Set<Class<? extends GamePacket>> parseImpl(String str) {
        String[] values = str.split(",");
        Set<Class<? extends GamePacket>> packets = new ObjectOpenHashSet<>(values.length);

        try {
            for (String value : values) {
                Class<? extends GamePacket> packet = PacketUtils.getPacket(value.trim());
                if (packet != null && (filter == null || filter.test(packet))) packets.add(packet);
            }
        } catch (Exception ignored) {}

        return packets;
    }

    @Override
    protected boolean isValueValid(Set<Class<? extends GamePacket>> value) {
        return true;
    }

    @Override
    public List<String> getSuggestions() {
        if (suggestions == null) {
            suggestions = new ArrayList<>(PacketUtils.getC2SPackets().size() + PacketUtils.getS2CPackets().size());

            for (Class<? extends GamePacket> packet : PacketUtils.getC2SPackets()) {
                suggestions.add(PacketUtils.getName(packet));
            }

            for (Class<? extends GamePacket> packet : PacketUtils.getS2CPackets()) {
                suggestions.add(PacketUtils.getName(packet));
            }
        }

        return suggestions;
    }

    @Override
    public DataTagManifest save(DataTagManifest tag) {
        ListDataAttribute<StringDataAttribute> valueTag = new ListDataAttribute<>();
        List<StringDataAttribute> list_ = new ArrayList<>();
        for (Class<? extends GamePacket> packet : get()) {
            list_.add(new StringDataAttribute(PacketUtils.getName(packet)));
        }
        valueTag.setValue(list_);
        tag.addTag(new DataTag<>("value", valueTag));
        return tag;
    }

    @Override
    public Set<Class<? extends GamePacket>> load(DataTagManifest tag) {
        get().clear();
        List<StringDataAttribute> valueTag = tag.getTag("value").getTagAsType((Class<List<StringDataAttribute>>) (Class<?>) List.class).getValue();
        for (StringDataAttribute t : valueTag) {
            Class<? extends GamePacket> packet = PacketUtils.getPacket(t.getValue());
            if (packet != null && (filter == null || filter.test(packet))) get().add(packet);
        }
        return get();
    }

    public static class Builder extends SettingBuilder<Builder, Set<Class<? extends GamePacket>>, PacketListSetting> {
        private Predicate<Class<? extends GamePacket>> filter;

        public Builder() {
            super(new ObjectOpenHashSet<>(0));
        }

        public Builder filter(Predicate<Class<? extends GamePacket>> filter) {
            this.filter = filter;
            return this;
        }

        @Override
        public PacketListSetting build() {
            return new PacketListSetting(name, description, defaultValue, onChanged, onModuleActivated, filter, visible);
        }
    }
}
