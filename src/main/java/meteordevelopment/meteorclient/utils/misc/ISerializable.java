/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

import com.github.puzzle.game.items.data.DataTagManifest;
//import net.minecraft.nbt.NbtCompound;

public interface ISerializable<T> {
    DataTagManifest toTag();

    T fromTag(DataTagManifest tag);
}
