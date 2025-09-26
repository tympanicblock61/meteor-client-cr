/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.commands.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.entities.player.Player;
import meteordevelopment.stolen.CommandSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

//import static meteordevelopment.meteorclient.MeteorClient.client;
//import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerListEntryArgumentType implements ArgumentType<Player> {
    private static final PlayerListEntryArgumentType INSTANCE = new PlayerListEntryArgumentType();
    private static final DynamicCommandExceptionType NO_SUCH_PLAYER = new DynamicCommandExceptionType(name -> new LiteralMessage("Player list entry with name " + name + " doesn't exist."));

    private static final Collection<String> EXAMPLES = List.of("seasnail8169", "MineGame159");

    public static PlayerListEntryArgumentType create() {
        return INSTANCE;
    }

    public static Player get(CommandContext<?> context) {
        return context.getArgument("player", Player.class);
    }

    private PlayerListEntryArgumentType() {}

    @Override
    public Player parse(StringReader reader) throws CommandSyntaxException {
        String argument = reader.readString();



        Player playerListEntry = null;

        for (Player p : GameSingletons.world.players.items) {
            if (p.getAccount().getDisplayName().equalsIgnoreCase(argument)) {
                playerListEntry = p;
                break;
            }
        }
        if (playerListEntry == null) throw NO_SUCH_PLAYER.create(argument);

        return playerListEntry;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        //mc.getNetworkHandler().getPlayerList().stream().map(playerListEntry -> playerListEntry.getProfile().getName())

        return CommandSource.suggestMatching(Arrays.stream(GameSingletons.world.players.items).map(player -> player.getAccount().getDisplayName()), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
