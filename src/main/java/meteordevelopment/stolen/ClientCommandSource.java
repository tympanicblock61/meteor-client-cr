/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.stolen;

import com.github.puzzle.core.registries.IRegistry;
import com.github.puzzle.core.registries.RegistryObject;
import com.github.puzzle.game.commands.PuzzleCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.accounts.Account;
import finalforeach.cosmicreach.audio.GameMusicManager;
import finalforeach.cosmicreach.audio.GameSong;
import finalforeach.cosmicreach.audio.SoundManager;
import finalforeach.cosmicreach.chat.IChat;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.networking.NetworkIdentity;
import finalforeach.cosmicreach.networking.client.ClientNetworkManager;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.world.World;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ClientCommandSource(Player player, World world, IChat chat) implements CommandSource {
    public ClientCommandSource(Player player, World world, IChat chat) {
        this.player = player;
        this.world = world;
        this.chat = chat;
    }

    @Nullable
    public NetworkIdentity getIdentity() {
        return ClientNetworkManager.CLIENT.identity;
    }

    public Player getPlayer() {
        return this.getIdentity().getPlayer();
    }

    public Account getAccount() {
        return this.player.getAccount();
    }

    public IChat getChat() {
        return this.chat;
    }

    public World getWorld() {
        return this.world;
    }

    public Player player() {
        return this.player;
    }

    public World world() {
        return this.world;
    }

    public IChat chat() {
        return this.chat;
    }

    @Override
    public Collection<String> getPlayerNames() {
        if (GameSingletons.world != null) return Arrays.stream(GameSingletons.world.players.items).map(Player::getUsername).collect(Collectors.toList());
        return new ArrayList<>();
    }

    @Override
    public Collection<String> getChatSuggestions() {
        return CommandSource.super.getChatSuggestions();
    }

    @Override
    public Collection<String> getEntitySuggestions() {
        return new ArrayList<>(); // TODO add entities
    }

    @Override
    public Stream<Identifier> getSoundIds() {
        return Arrays.stream(GameMusicManager.gameSongs.items).map((gameSong -> gameSong.id));
    }

    @Override
    public CompletableFuture<Suggestions> getCompletions(CommandContext<?> context) {
        return null;
    }

    @Override
    public Collection<RelativePosition> getBlockPositionSuggestions() {
        return CommandSource.super.getBlockPositionSuggestions();
    }

    @Override
    public Collection<RelativePosition> getPositionSuggestions() {
        return CommandSource.super.getPositionSuggestions();
    }

    @Override
    public void suggestIdentifiers(IRegistry<?> registry, SuggestedIdType suggestedIdType, SuggestionsBuilder builder) {
        CommandSource.super.suggestIdentifiers(registry, suggestedIdType, builder);
    }

    @Override
    public CompletableFuture<Suggestions> listIdSuggestions(RegistryObject<? extends IRegistry<?>> registryRef, SuggestedIdType suggestedIdType, SuggestionsBuilder builder, CommandContext<?> context) {
        return null;
    }
}
