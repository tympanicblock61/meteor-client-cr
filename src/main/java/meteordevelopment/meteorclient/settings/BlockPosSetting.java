/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.settings;

import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.DataTagManifestAttribute;
import com.github.puzzle.game.items.data.attributes.IntDataAttribute;
import com.github.puzzle.game.items.data.attributes.ListDataAttribute;
import finalforeach.cosmicreach.blocks.BlockPosition;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.util.math.BlockPos;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BlockPosSetting extends Setting<BlockPosition> {
    public BlockPosSetting(String name, String description, BlockPosition defaultValue, Consumer<BlockPosition> onChanged, Consumer<Setting<BlockPosition>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected BlockPosition parseImpl(String str) {
        List<String> values = List.of(str.split(","));
        if (values.size() != 3) return null;

        BlockPosition bp = null;
        try {
            bp = BlockPosition.ofGlobalZoneless(Integer.parseInt(values.get(0)), Integer.parseInt(values.get(1)), Integer.parseInt(values.get(2)));
        }
        catch (NumberFormatException ignored) {}
        return bp;
    }

    @Override
    protected boolean isValueValid(BlockPosition value) {
        return true;
    }

    @Override
    protected DataTagManifest save(DataTagManifest tag) {
        ListDataAttribute<IntDataAttribute> list = new ListDataAttribute<>();
        List<IntDataAttribute> list_ = new ArrayList<>();
        int[] list_i = new int[] {value.getGlobalX(), value.getGlobalY(), value.getGlobalZ()};
        for (int i : list_i) {
            list_.add(new IntDataAttribute(i));
        }
        list.setValue(list_);
        tag.addTag(new DataTag<>("value", list));
        return tag;
    }

    @Override
    protected BlockPosition load(DataTagManifest tag) {
        List<IntDataAttribute> list = tag.getTag("value").getTagAsType((Class<List<IntDataAttribute>>) (Class<?>) List.class).getValue();
        set(BlockPosition.ofGlobalZoneless(list.get(0).getValue(), list.get(1).getValue(), list.get(2).getValue()));
        return get();
    }

    public static class Builder extends SettingBuilder<Builder, BlockPosition, BlockPosSetting> {
        public Builder() {
            super(BlockPosition.ofGlobalZoneless(0, 0, 0));
        }

        @Override
        public BlockPosSetting build() {
            return new BlockPosSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
