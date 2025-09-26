/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.waypoints;

import com.badlogic.gdx.graphics.Texture;
import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.DataTagManifestAttribute;
import com.github.puzzle.game.items.data.attributes.StringDataAttribute;
import finalforeach.cosmicreach.blocks.BlockPosition;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Waypoint implements ISerializable<Waypoint> {
    public final Settings settings = new Settings();

    private final SettingGroup sgVisual = settings.createGroup("Visual");
    private final SettingGroup sgPosition = settings.createGroup("Position");

    public Setting<String> name = sgVisual.add(new StringSetting.Builder()
        .name("name")
        .description("The name of the waypoint.")
        .defaultValue("Home")
        .build()
    );

    public Setting<String> icon = sgVisual.add(new ProvidedStringSetting.Builder()
        .name("icon")
        .description("The icon of the waypoint.")
        .defaultValue("Square")
        .supplier(() -> Waypoints.BUILTIN_ICONS)
        .onChanged(v -> validateIcon())
        .build()
    );

    public Setting<SettingColor> color = sgVisual.add(new ColorSetting.Builder()
        .name("color")
        .description("The color of the waypoint.")
        .defaultValue(MeteorClient.ADDON.color.toSetting())
        .build()
    );

    public Setting<Boolean> visible = sgVisual.add(new BoolSetting.Builder()
        .name("visible")
        .description("Whether to show the waypoint.")
        .defaultValue(true)
        .build()
    );

    public Setting<Integer> maxVisible = sgVisual.add(new IntSetting.Builder()
        .name("max-visible-distance")
        .description("How far away to render the waypoint.")
        .defaultValue(5000)
        .build()
    );

    public Setting<Double> scale = sgVisual.add(new DoubleSetting.Builder()
        .name("scale")
        .description("The scale of the waypoint.")
        .defaultValue(1)
        .build()
    );

    public Setting<BlockPosition> pos = sgPosition.add(new BlockPosSetting.Builder()
        .name("location")
        .description("The location of the waypoint.")
        .defaultValue(BlockPosition.ofGlobalZoneless(0,0,0))
        .build()
    );

    public Setting<Dimension> dimension = sgPosition.add(new EnumSetting.Builder<Dimension>()
        .name("dimension")
        .description("Which dimension the waypoint is in.")
        .defaultValue(Dimension.Overworld)
        .build()
    );

    public Setting<Boolean> opposite = sgPosition.add(new BoolSetting.Builder()
        .name("opposite-dimension")
        .description("Whether to show the waypoint in the opposite dimension.")
        .defaultValue(true)
        .visible(() -> dimension.get() != Dimension.End)
        .build()
    );

    public final UUID uuid;

    private Waypoint() {
        uuid = UUID.randomUUID();
    }

    public Waypoint(DataTagManifest tag) {

        if (tag.hasTag("uuid")) uuid = UUID.fromString(tag.getTag("uuid").getTagAsType(String.class).getValue());
        else uuid = UUID.randomUUID();

        fromTag(tag);
    }

    public void renderIcon(double x, double y, double a, double size) {
        Texture texture = Waypoints.get().icons.get(icon.get());
        if (texture == null) return;

        int preA = color.get().a;
        color.get().a *= (int) a;

        GL.bindTexture(texture.glTarget);
        Renderer2D.TEXTURE.begin();
        Renderer2D.TEXTURE.texQuad(x, y, size, size, color.get());
        Renderer2D.TEXTURE.render(null);

        color.get().a = preA;
    }

    public BlockPosition getPos() {
        //Dimension dim = dimension.get();

//        Dimension currentDim = PlayerUtils.getDimension();
//        if (dim == currentDim || dim.equals(Dimension.End)) return this.pos.get();
//
//        return switch (dim) {
//            case Overworld -> BlockPosition.ofGlobalZoneless(pos.getX() / 8, pos.getY(), pos.getZ() / 8);
//            case Nether -> BlockPosition.ofGlobalZoneless(pos.getX() * 8, pos.getY(), pos.getZ() * 8);
//            default -> null;
//        };

        return this.pos.get();
    }

    private void validateIcon() {
        Map<String, Texture> icons = Waypoints.get().icons;

        Texture texture = icons.get(icon.get());
        if (texture == null && !icons.isEmpty()) {
            icon.set(icons.keySet().iterator().next());
        }
    }

    public static class Builder {
        private String name = "", icon = "";
        private BlockPosition pos = BlockPosition.ofGlobalZoneless(0,0,0);
        private Dimension dimension = Dimension.Overworld;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder pos(BlockPosition pos) {
            this.pos = pos;
            return this;
        }

        public Builder dimension(Dimension dimension) {
            this.dimension = dimension;
            return this;
        }

        public Waypoint build() {
            Waypoint waypoint = new Waypoint();

            if (!name.equals(waypoint.name.getDefaultValue())) waypoint.name.set(name);
            if (!icon.equals(waypoint.icon.getDefaultValue())) waypoint.icon.set(icon);
            if (!pos.equals(waypoint.pos.getDefaultValue())) waypoint.pos.set(pos);
            if (!dimension.equals(waypoint.dimension.getDefaultValue())) waypoint.dimension.set(dimension);

            return waypoint;
        }
    }

    @Override
    public DataTagManifest toTag() {
        DataTagManifest tag = new DataTagManifest();

        tag.addTag(new DataTag<>("uuid", new StringDataAttribute(uuid.toString())));
        tag.addTag(new DataTag<>("settings", new DataTagManifestAttribute(settings.toTag())));

        return tag;
    }

    @Override
    public Waypoint fromTag(DataTagManifest tag) {

        if (tag.hasTag("settings")) {
            settings.fromTag(tag.getTag("settings").getTagAsType(DataTagManifest.class).getValue());
        }

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Waypoint waypoint = (Waypoint) o;
        return Objects.equals(uuid, waypoint.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    @Override
    public String toString() {
        return name.get();
    }
}
