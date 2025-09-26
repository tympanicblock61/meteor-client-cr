/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.friends;

//import com.mojang.util.UndashedUuid;
import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.DataTagManifestAttribute;
import com.github.puzzle.game.items.data.attributes.StringDataAttribute;
import finalforeach.cosmicreach.entities.player.PlayerEntity;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
//import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.misc.DataTagUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
//import net.minecraft.client.network.PlayerListEntry;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Friends extends System<Friends> implements Iterable<Friend> {
    private final List<Friend> friends = new ArrayList<>();

    public Friends() {
        super("friends");
    }

    public static Friends get() {
        return Systems.get(Friends.class);
    }

    public boolean add(Friend friend) {
        if (friend.name.isEmpty() || friend.name.contains(" ")) return false;

        if (!friends.contains(friend)) {
            friends.add(friend);
            save();

            return true;
        }

        return false;
    }

    public boolean remove(Friend friend) {
        if (friends.remove(friend)) {
            save();
            return true;
        }

        return false;
    }

    public Friend get(String name) {
        for (Friend friend : friends) {
            if (friend.name.equalsIgnoreCase(name)) {
                return friend;
            }
        }

        return null;
    }

    public Friend get(PlayerEntity player) {
        return get(player.player.getAccount().getDisplayName());
    }

    public boolean isFriend(PlayerEntity player) {
        return player != null && get(player) != null;
    }

    public boolean shouldAttack(PlayerEntity player) {
        return !isFriend(player);
    }

    public int count() {
        return friends.size();
    }

    public boolean isEmpty() {
        return friends.isEmpty();
    }

    @Override
    public @NotNull Iterator<Friend> iterator() {
        return friends.iterator();
    }

    @Override
    public DataTagManifest toTag() {
        DataTagManifest tag = new DataTagManifest();

        tag.addTag(new DataTag<>("friends", DataTagUtils.listToTag(friends)));

        return tag;
    }

    @Override
    public Friends fromTag(DataTagManifest tag) {
        friends.clear();

        List<DataTagManifestAttribute> friends_ = tag.getTag("friends").getTagAsType((Class<List<DataTagManifestAttribute>>) (Class<?>) List.class).getValue();



        for (DataTagManifestAttribute itemTag : friends_) {
            DataTagManifest friendTag = itemTag.getValue();
            if (!friendTag.hasTag("name")) continue;

            String name = friendTag.getTag("name").getTagAsType(String.class).getValue();
            if (get(name) != null) continue;

            String id = friendTag.getTag("id").getTagAsType(String.class).getValue();
            Friend friend = !id.isBlank()
                ? new Friend(name, id)
                : new Friend(name);

            friends.add(friend);
        }

        Collections.sort(friends);

        MeteorExecutor.execute(() -> friends.forEach(Friend::updateInfo));

        return this;
    }
}
