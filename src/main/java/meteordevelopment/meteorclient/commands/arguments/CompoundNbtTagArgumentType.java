/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.commands.arguments;

import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.*;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.StringNbtReader;

import java.io.IOException;
import java.util.*;

//import static net.minecraft.nbt.StringNbtReader.EXPECTED_VALUE;

public class CompoundNbtTagArgumentType implements ArgumentType<DataTagManifest> {
    private static final CompoundNbtTagArgumentType INSTANCE = new CompoundNbtTagArgumentType();
    private static final Collection<String> EXAMPLES = List.of("{foo:bar}", "{foo:[aa, bb],bar:15}");

    public static CompoundNbtTagArgumentType create() {
        return INSTANCE;
    }

    public static DataTagManifest get(CommandContext<?> context) {
        return context.getArgument("nbt", DataTagManifest.class);
    }

    private CompoundNbtTagArgumentType() {}

    @Override
    public DataTagManifest parse(StringReader reader) throws CommandSyntaxException {
        return fromString(reader);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public DataTagManifest fromString(StringReader reader) {
        DataTagManifest manifest = new DataTagManifest();

        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        boolean readingKey = true;

        reader.skipWhitespace();
        int c;
        while (reader.getRemainingLength() > 0) {
            c = reader.read();
            char ch = (char) c;

            if (ch == '}') {
                break;
            }

            if (ch == ',' && !readingKey) {
                addDataTag(manifest, keyBuilder, valueBuilder);
                keyBuilder.setLength(0);
                valueBuilder.setLength(0);
                readingKey = true;
            } else if (ch == ':') {
                // Separator between key and value
                readingKey = false;
            } else {
                if (readingKey) {
                    keyBuilder.append(ch);
                } else {
                    valueBuilder.append(ch);
                }
            }
        }

        // Add the last key-value pair
        if (!readingKey && !keyBuilder.isEmpty() && !valueBuilder.isEmpty()) {
            addDataTag(manifest, keyBuilder, valueBuilder);
        }

        return manifest;
    }

    private void addDataTag(DataTagManifest manifest, StringBuilder keyBuilder, StringBuilder valueBuilder) {
        String key = keyBuilder.toString().trim();
        String value = valueBuilder.toString().trim();
        DataTag<?> dataTag = parseValue(value, key);
        manifest.addTag(dataTag);
    }

    private DataTag<?> parseValue(String value, String key) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return new DataTag<>(key, new BooleanDataAttribute(Boolean.parseBoolean(value)));
        } else if (value.matches("^-?\\d+$")) {  // Integer
            return new DataTag<>(key, new IntDataAttribute(Integer.parseInt(value)));
        } else if (value.matches("^-?\\d+\\.\\d+$")) {  // Float/Double
            return new DataTag<>(key, new DoubleDataAttribute(Double.parseDouble(value)));
        } else if (value.matches("^-?\\d+L$")) {  // Long (with L)
            return new DataTag<>(key, new LongDataAttribute(Long.parseLong(value.substring(0, value.length() - 1))));
        } else if (value.startsWith("\"") && value.endsWith("\"")) {  // String
            return new DataTag<>(key, new StringDataAttribute(value.substring(1, value.length() - 1)));
        } else if (value.contains(",")) {  // List of strings
            String[] items = value.split(",");
            List<StringDataAttribute> stringList = new ArrayList<>();
            for (String item : items) {
                stringList.add(new StringDataAttribute(item.trim()));
            }
            return new DataTag<>(key, new ListDataAttribute<>(stringList));
        } else {
            return new DataTag<>(key, new StringDataAttribute(value));
        }
    }
}
