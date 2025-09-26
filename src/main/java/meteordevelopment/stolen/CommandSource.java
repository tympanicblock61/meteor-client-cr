/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.stolen;

import com.github.puzzle.core.registries.IRegistry;
import com.github.puzzle.core.registries.RegistryObject;
import com.github.puzzle.game.commands.PuzzleCommandSource;
import com.github.puzzle.game.resources.PuzzleGameAssetLoader;
import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import finalforeach.cosmicreach.util.Identifier;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface CommandSource extends PuzzleCommandSource {
    CharMatcher SUGGESTION_MATCH_PREFIX = CharMatcher.anyOf("._/");

    Collection<String> getPlayerNames();

    default Collection<String> getChatSuggestions() {
        return this.getPlayerNames();
    }

    default Collection<String> getEntitySuggestions() {
        return Collections.emptyList();
    }

    Stream<Identifier> getSoundIds();

    CompletableFuture<Suggestions> getCompletions(CommandContext<?> context);

    default Collection<RelativePosition> getBlockPositionSuggestions() {
        return Collections.singleton(CommandSource.RelativePosition.ZERO_WORLD);
    }

    default Collection<RelativePosition> getPositionSuggestions() {
        return Collections.singleton(CommandSource.RelativePosition.ZERO_WORLD);
    }

    default void suggestIdentifiers(IRegistry<?> registry, SuggestedIdType suggestedIdType, SuggestionsBuilder builder) {

        if (suggestedIdType.canSuggestTags()) {
            suggestIdentifiers(registry.names().stream(), builder, "#");
        }

        if (suggestedIdType.canSuggestElements()) {
            suggestIdentifiers(registry.names(), builder);
        }

    }

    CompletableFuture<Suggestions> listIdSuggestions(RegistryObject<? extends IRegistry<?>> registryRef, SuggestedIdType suggestedIdType, SuggestionsBuilder builder, CommandContext<?> context);


    static <T> void forEachMatching(Iterable<T> candidates, String remaining, Function<T, Identifier> identifier, Consumer<T> action) {
        boolean bl = remaining.indexOf(58) > -1;


        for (T object : candidates) {
            Identifier identifier2 = identifier.apply(object);
            if (bl) {
                String string = identifier2.toString();
                if (shouldSuggest(remaining, string)) {
                    action.accept(object);
                }
            } else if (shouldSuggest(remaining, identifier2.getNamespace()) || identifier2.getNamespace().equals("minecraft") && shouldSuggest(remaining, PuzzleGameAssetLoader.locateAsset(identifier2).file().getPath())) {
                action.accept(object);
            }
        }
    }

    static <T> void forEachMatching(Iterable<T> candidates, String remaining, String prefix, Function<T, Identifier> identifier, Consumer<T> action) {
        if (remaining.isEmpty()) {
            candidates.forEach(action);
        } else {
            String string = Strings.commonPrefix(remaining, prefix);
            if (!string.isEmpty()) {
                String string2 = remaining.substring(string.length());
                forEachMatching(candidates, string2, identifier, action);
            }
        }

    }

    static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> candidates, SuggestionsBuilder builder, String prefix) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        forEachMatching(candidates, string, prefix, (id) -> id, (id) -> builder.suggest(prefix + id));
        return builder.buildFuture();
    }

    static CompletableFuture<Suggestions> suggestIdentifiers(Stream<Identifier> candidates, SuggestionsBuilder builder, String prefix) {
        Objects.requireNonNull(candidates);
        return suggestIdentifiers(candidates::iterator, builder, prefix);
    }

    static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> candidates, SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        forEachMatching(candidates, string, (id) -> id, (id) -> builder.suggest(id.toString()));
        return builder.buildFuture();
    }

    static <T> CompletableFuture<Suggestions> suggestFromIdentifier(Iterable<T> candidates, SuggestionsBuilder builder, Function<T, Identifier> identifier, Function<T, Message> tooltip) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        forEachMatching(candidates, string, identifier, (object) -> builder.suggest(identifier.apply(object).toString(), tooltip.apply(object)));
        return builder.buildFuture();
    }

    static CompletableFuture<Suggestions> suggestIdentifiers(Stream<Identifier> candidates, SuggestionsBuilder builder) {
        Objects.requireNonNull(candidates);
        return suggestIdentifiers(candidates::iterator, builder);
    }

    static <T> CompletableFuture<Suggestions> suggestFromIdentifier(Stream<T> candidates, SuggestionsBuilder builder, Function<T, Identifier> identifier, Function<T, Message> tooltip) {
        Objects.requireNonNull(candidates);
        return suggestFromIdentifier(candidates::iterator, builder, identifier, tooltip);
    }

    public static CompletableFuture<Suggestions> suggestPositions(String remaining, Collection<RelativePosition> candidates, SuggestionsBuilder builder, Predicate<String> predicate) {
        List<String> suggestions = Lists.newArrayList();
        String[] parts = (remaining == null || remaining.isEmpty()) ? new String[0] : remaining.split(" ");

        for (RelativePosition position : candidates) {
            String x = String.valueOf(position.x);
            String y = String.valueOf(position.y);
            String z = String.valueOf(position.z);

            String[] possibleStrings = (parts.length == 0)
                    ? new String[] {x, x + " " + y, x + " " + y + " " + z}
                    : (parts.length == 1)
                    ? new String[] {parts[0] + " " + y + " " + z, parts[0] + " " + y}
                    : (parts.length == 2)
                    ? new String[] {parts[0] + " " + parts[1] + " " + z}
                    : new String[0];

            for (String suggestion : possibleStrings) {
                if (predicate.test(suggestion)) {
                    suggestions.add(suggestion);
                }
            }
        }

        return suggestMatching(suggestions, builder);
    }

    static CompletableFuture<Suggestions> suggestColumnPositions(String remaining, Collection<RelativePosition> candidates, SuggestionsBuilder builder, Predicate<String> predicate) {
        List<String> list = Lists.newArrayList();
        if (Strings.isNullOrEmpty(remaining)) {

            for (RelativePosition relativePosition : candidates) {
                String string = relativePosition.x + " " + relativePosition.z;
                if (predicate.test(string)) {
                    list.add(relativePosition.x);
                    list.add(string);
                }
            }
        } else {
            String[] strings = remaining.split(" ");
            if (strings.length == 1) {

                for (RelativePosition relativePosition2 : candidates) {
                    String string2 = strings[0] + " " + relativePosition2.z;
                    if (predicate.test(string2)) {
                        list.add(string2);
                    }
                }
            }
        }

        return suggestMatching(list, builder);
    }

    static CompletableFuture<Suggestions> suggestMatching(Iterable<String> candidates, SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);

        for (String string2 : candidates) {
            if (shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) {
                builder.suggest(string2);
            }
        }

        return builder.buildFuture();
    }

    static CompletableFuture<Suggestions> suggestMatching(Stream<String> candidates, SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        Stream<String> var10000 = candidates.filter((candidate) -> shouldSuggest(string, candidate.toLowerCase(Locale.ROOT)));
        Objects.requireNonNull(builder);
        var10000.forEach(builder::suggest);
        return builder.buildFuture();
    }

    static CompletableFuture<Suggestions> suggestMatching(String[] candidates, SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);

        for (String string2 : candidates) {
            if (shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) {
                builder.suggest(string2);
            }
        }

        return builder.buildFuture();
    }

    static <T> CompletableFuture<Suggestions> suggestMatching(Iterable<T> candidates, SuggestionsBuilder builder, Function<T, String> suggestionText, Function<T, Message> tooltip) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);

        for (T object : candidates) {
            String string2 = suggestionText.apply(object);
            if (shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) {
                builder.suggest(string2, tooltip.apply(object));
            }
        }

        return builder.buildFuture();
    }

    static boolean shouldSuggest(String remaining, String candidate) {
        int j;
        for(int i = 0; !candidate.startsWith(remaining, i); i = j + 1) {
            j = SUGGESTION_MATCH_PREFIX.indexIn(candidate, i);
            if (j < 0) {
                return false;
            }
        }

        return true;
    }

    record RelativePosition(String x, String y, String z) {
            public static final RelativePosition ZERO_LOCAL = new RelativePosition("^", "^", "^");
            public static final RelativePosition ZERO_WORLD = new RelativePosition("~", "~", "~");
    }

    enum SuggestedIdType {
        TAGS,
        ELEMENTS,
        ALL;

        SuggestedIdType() {
        }

        public boolean canSuggestTags() {
            return this == TAGS || this == ALL;
        }

        public boolean canSuggestElements() {
            return this == ELEMENTS || this == ALL;
        }
    }
}
