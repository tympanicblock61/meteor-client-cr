/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.settings;

import com.badlogic.gdx.math.Vector3;
import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.DataTagManifestAttribute;
import com.github.puzzle.game.items.data.attributes.FloatDataAttribute;
//import net.minecraft.nbt.NbtCompound;
//import org.joml.Vector3;

import java.util.function.Consumer;

public class Vector3Setting extends Setting<Vector3> {
    public final double min, max;
    public final double sliderMin, sliderMax;
    public final boolean onSliderRelease;
    public final int decimalPlaces;
    public final boolean noSlider;

    public Vector3Setting(String name, String description, Vector3 defaultValue, Consumer<Vector3> onChanged, Consumer<Setting<Vector3>> onModuleActivated, IVisible visible, double min, double max, double sliderMin, double sliderMax, boolean onSliderRelease, int decimalPlaces, boolean noSlider) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.min = min;
        this.max = max;
        this.sliderMin = sliderMin;
        this.sliderMax = sliderMax;
        this.decimalPlaces = decimalPlaces;
        this.onSliderRelease = onSliderRelease;
        this.noSlider = noSlider;
    }

    public boolean set(float x, float y, float z) {
        value.set(x, y, z);
        return super.set(value);
    }

    @Override
    protected void resetImpl() {
        if (value == null) value = new Vector3();
        value.set(defaultValue);
    }

    @Override
    protected Vector3 parseImpl(String str) {
        try {
            String[] strs = str.split(" ");
            return new Vector3(Float.parseFloat(strs[0]), Float.parseFloat(strs[1]), Float.parseFloat(strs[2]));
        } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    protected boolean isValueValid(Vector3 value) {
        return value.x >= min && value.x <= max && value.y >= min && value.y <= max && value.z >= min && value.z <= max;
    }

    @Override
    protected DataTagManifest save(DataTagManifest tag) {
        DataTagManifest valueTag = new DataTagManifest();
        valueTag.addTag(new DataTag<>("x", new FloatDataAttribute(get().x)));
        valueTag.addTag(new DataTag<>("y", new FloatDataAttribute(get().y)));
        valueTag.addTag(new DataTag<>("z", new FloatDataAttribute(get().z)));
        tag.addTag(new DataTag<>("value", new DataTagManifestAttribute(valueTag)));
        return tag;
    }

    @Override
    protected Vector3 load(DataTagManifest tag) {
        DataTagManifest valueTag = tag.getTag("value").getTagAsType(DataTagManifest.class).getValue();
        set(valueTag.getTag("x").getTagAsType(Float.TYPE).getValue(), valueTag.getTag("y").getTagAsType(Float.TYPE).getValue(), valueTag.getTag("z").getTagAsType(Float.TYPE).getValue());
        return get();
    }

    public static class Builder extends SettingBuilder<Builder, Vector3, Vector3Setting> {
        public double min = Double.NEGATIVE_INFINITY, max = Double.POSITIVE_INFINITY;
        public double sliderMin = 0, sliderMax = 10;
        public boolean onSliderRelease = false;
        public int decimalPlaces = 3;
        public boolean noSlider = false;

        public Builder() {
            super(new Vector3());
        }

        @Override
        public Builder defaultValue(Vector3 defaultValue) {
            this.defaultValue.set(defaultValue);
            return this;
        }

        public Builder defaultValue(float x, float y, float z) {
            this.defaultValue.set(x, y, z);
            return this;
        }

        public Builder min(double min) {
            this.min = min;
            return this;
        }

        public Builder max(double max) {
            this.max = max;
            return this;
        }

        public Builder range(double min, double max) {
            this.min = Math.min(min, max);
            this.max = Math.max(min, max);
            return this;
        }

        public Builder sliderMin(double min) {
            this.sliderMin = min;
            return this;
        }

        public Builder sliderMax(double max) {
            this.sliderMax = max;
            return this;
        }

        public Builder sliderRange(double min, double max) {
            this.sliderMin = min;
            this.sliderMax = max;
            return this;
        }

        public Builder onSliderRelease() {
            onSliderRelease = true;
            return this;
        }

        public Builder decimalPlaces(int decimalPlaces) {
            this.decimalPlaces = decimalPlaces;
            return this;
        }

        public Builder noSlider() {
            noSlider = true;
            return this;
        }

        @Override
        public Vector3Setting build() {
            return new Vector3Setting(name, description, defaultValue, onChanged, onModuleActivated, visible, min, max, sliderMin, sliderMax, onSliderRelease, decimalPlaces, noSlider);
        }
    }
}
