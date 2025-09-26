/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.friends;

//import com.mojang.util.UndashedUuid;

import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.StringDataAttribute;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.entities.player.PlayerEntity;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class Friend implements ISerializable<Friend>, Comparable<Friend> {
    public volatile String name;
    private final @Nullable String id;
    private volatile boolean updating;

    public Friend(String name, @Nullable String id) {
        this.name = name;
        this.id = id;
    }

    public Friend(PlayerEntity entity) {
        this(entity.player.getAccount().getDisplayName(), entity.player.getAccount().getUniqueId());
    }
    public Friend(Player player) {
        this(player.getAccount().getDisplayName(), player.getAccount().getUniqueId());
    }

    public Friend(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public void updateInfo() {
// TODO update in the future when cr has skins
//        updating = true;
//        APIResponse res = Http.get("https://api.mojang.com/users/profiles/minecraft/" + name).sendJson(APIResponse.class);
//        if (res == null || res.name == null || res.id == null) return;
//        name = res.name;
//        id = UndashedUuid.fromStringLenient(res.id);
//        headTexture = PlayerHeadUtils.fetchHead(id);
//        updating = false;
    }

    @Override
    public DataTagManifest toTag() {
        DataTagManifest tag = new DataTagManifest();
        tag.addTag(new DataTag<>("name", new StringDataAttribute(name)));
        if (id != null) tag.addTag(new DataTag<>("id", new StringDataAttribute(id)));
        return tag;
    }

    @Override
    public Friend fromTag(DataTagManifest tag) {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend) o;
        return Objects.equals(name, friend.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(@NotNull Friend friend) {
        return name.compareTo(friend.name);
    }

    private static class APIResponse {
        String name, id;
    }
}
