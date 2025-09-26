/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.List;
import java.util.stream.Collectors;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;


public class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T> {
    private final List<String> enumNames;
    private final Class<T> enumClass;

    public EnumArgumentType(Class<T> enumClass) {
        this.enumClass = enumClass;
        this.enumNames = Stream.of(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString().toUpperCase();
        try {
            return Enum.valueOf(enumClass, name);
        } catch (IllegalArgumentException e) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().createWithContext(reader, name);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return suggest(builder, enumNames);
    }

    private CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder, List<String> options) {
        return builder.buildFuture();
    }
}

