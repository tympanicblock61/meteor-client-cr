/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.*;
import com.github.puzzle.util.MutablePair;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.savelib.crbin.ICRBinSerializable;
import finalforeach.cosmicreach.util.Identifier;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.utils.render.prompts.OkPrompt;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import static meteordevelopment.meteorclient.MeteorClient.mc;

public class DataTagUtils {
    private DataTagUtils() {
    }

    public static <T> DataTag.DataTagAttribute<T> fromRaw(T raw) {
        if (raw instanceof Integer) {
            return (DataTag.DataTagAttribute<T>) new IntDataAttribute((int)raw);
        } else if (raw instanceof Boolean) {
            return (DataTag.DataTagAttribute<T>) new BooleanDataAttribute((boolean) raw);
        } else if (raw instanceof Byte) {
            return (DataTag.DataTagAttribute<T>) new ByteDataAttribute((byte) raw);
        }else if (raw instanceof Character) {
            return (DataTag.DataTagAttribute<T>) new CharDataAttribute((char) raw);
        }else if (raw instanceof DataTagManifest) {
            return (DataTag.DataTagAttribute<T>) new DataTagManifestAttribute((DataTagManifest) raw);
        }else if (raw instanceof Double) {
            return (DataTag.DataTagAttribute<T>) new DoubleDataAttribute((double) raw);
        }else if (raw instanceof Float) {
            return (DataTag.DataTagAttribute<T>) new FloatDataAttribute((float) raw);
        }else if (raw instanceof Identifier) {
            return (DataTag.DataTagAttribute<T>) new IdentifierDataAttribute((Identifier) raw);
        }else if (raw instanceof Iterable<?>) {
            return (DataTag.DataTagAttribute<T>) DataTagUtils.listToTag((Iterable<? extends ISerializable<?>>) raw);
        }else if (raw instanceof Long) {
            return (DataTag.DataTagAttribute<T>) new LongDataAttribute((long) raw);
        }else if (raw instanceof MutablePair<?,?>) {
            return (DataTag.DataTagAttribute<T>) new PairAttribute<>((MutablePair) raw);
        }else if (raw instanceof Short) {
            return (DataTag.DataTagAttribute<T>) new ShortDataAttribute((short) raw);
        }else if (raw instanceof String) {
            return (DataTag.DataTagAttribute<T>) new StringDataAttribute((String) raw);
        }else if (raw instanceof Vector2) {
            return (DataTag.DataTagAttribute<T>) new Vector2DataAttribute((Vector2) raw);
        }else if (raw instanceof Vector3) {
            return (DataTag.DataTagAttribute<T>) new Vector3DataAttribute((Vector3) raw);
        }

        return (DataTag.DataTagAttribute<T>) new StringDataAttribute(raw.toString());
    }

    public static <T extends ISerializable<?>> ListDataAttribute<DataTagManifestAttribute> listToTag(Iterable<T> list) {
        ListDataAttribute<DataTagManifestAttribute> tag = new ListDataAttribute<>();
        List<DataTagManifestAttribute> list_ = new ArrayList<>();
        for (T value : list) {
            list_.add((DataTagManifestAttribute) fromRaw(value.toTag()));
        }
        tag.setValue(list_);
        return tag;
    }

    public static <T> List<T> listFromTag(ListDataAttribute<?> tag, ToValue<T> toItem) {
        List<T> list = new ArrayList<>(tag.getValue().size());
        for (ICRBinSerializable itemTag : tag.getValue()) {
            T value = toItem.toValue(itemTag);
            if (value != null) list.add(value);
        }
        return list;
    }

    public static <K, V extends ISerializable<?>> DataTagManifest mapToTag(Map<K, V> map) {
        DataTagManifest tag = new DataTagManifest();
        for (K key : map.keySet()) tag.addTag(new DataTag<>(key.toString(), new DataTagManifestAttribute(map.get(key).toTag())));
        return tag;
    }

    public static <K, V> Map<K, V> mapFromTag(DataTagManifest tag, ToKey<K> toKey, ToValue<V> toValue) {
        Map<K, V> map = new HashMap<>(tag.getKeys().size());
        for (String key : tag.getKeys()) map.put(toKey.toKey(key), toValue.toValue(tag.getTag(key)));
        return map;
    }

    public static boolean toClipboard(System<?> system) {
        return toClipboard(system.getName(), system.toTag());
    }

    public static boolean toClipboard(String name, DataTagManifest dataTag) {
        String preClipboard = Gdx.app.getClipboard().getContents();
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            CRBinSerializer cr = CRBinSerializer.getNew();
            dataTag.write(cr);
            Gdx.app.getClipboard().setContents(cr.toBase64());
            return true;
        } catch (Exception e) {
            MeteorClient.LOG.error("Error copying {} NBT to clipboard!", name);

            OkPrompt.create()
                .title(String.format("Error copying %s NBT to clipboard!", name))
                .message("This shouldn't happen, please report it.")
                .id("nbt-copying")
                .show();

            Gdx.app.getClipboard().setContents(preClipboard);
            return false;
        }
    }

    public static boolean fromClipboard(System<?> system) {
        DataTagManifest clipboard = fromClipboard(system.toTag());

        if (clipboard != null) {
            system.fromTag(clipboard);
            return true;
        }

        return false;
    }

    public static DataTagManifest fromClipboard(DataTagManifest schema) {
        try {
            CRBinDeserializer cr = CRBinDeserializer.fromBase64(Gdx.app.getClipboard().getContents());
            DataTagManifest pasted = new DataTagManifest();
            pasted.read(cr);
            for (String key : schema.getKeys()) if (!pasted.getKeys().contains(key)) return null;
            return pasted;
        } catch (Exception e) {
            MeteorClient.LOG.error("Invalid NBT data pasted!");

            OkPrompt.create()
                .title("Error pasting NBT data!")
                .message("Please check that the data you pasted is valid.")
                .id("nbt-pasting")
                .show();

            return null;
        }
    }

    public interface ToKey<T> {
        T toKey(String string);
    }

    public interface ToValue<T> {
        T toValue(ICRBinSerializable tag);
    }
}
