/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.utils;

import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.BooleanDataAttribute;
import com.github.puzzle.game.items.data.attributes.DoubleDataAttribute;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
//import net.minecraft.nbt.NbtCompound;

public class WindowConfig implements ISerializable<WindowConfig> {
    public boolean expanded = true;
    public double x = -1;
    public double y = -1;

    // Saving

    @Override
    public DataTagManifest toTag() {
        DataTagManifest tag = new DataTagManifest();
        tag.addTag(new DataTag<>("expanded", new BooleanDataAttribute(expanded)));
        tag.addTag(new DataTag<>("x", new DoubleDataAttribute(x)));
        tag.addTag(new DataTag<>("y", new DoubleDataAttribute(y)));
        return tag;
    }

    @Override
    public WindowConfig fromTag(DataTagManifest tag) {
        expanded = tag.getTag("expanded").getTagAsType(Boolean.TYPE).getValue();
        x = tag.getTag("x").getTagAsType(Double.TYPE).getValue();
        y = tag.getTag("y").getTagAsType(Double.TYPE).getValue();
        return this;
    }
}
