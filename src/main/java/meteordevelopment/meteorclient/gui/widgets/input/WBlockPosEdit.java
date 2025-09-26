/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.widgets.input;

import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.InGame;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.InteractBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.marker.Marker;
import meteordevelopment.orbit.EventHandler;
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.util.hit.HitResult;
//import net.minecraft.util.math.BlockPos;

import static meteordevelopment.meteorclient.MeteorClient.client;
//import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.Utils.canUpdate;

public class WBlockPosEdit extends WHorizontalList {
    public Runnable action;
    public Runnable actionOnRelease;

    private WTextBox textBoxX, textBoxY, textBoxZ;

    private GameState previousScreen;

    private BlockPosition value;
    private BlockPosition lastValue;

    private boolean clicking;

    public WBlockPosEdit(BlockPosition value) {
        this.value = value;
    }

    @Override
    public void init() {
        addTextBox();

        if (canUpdate()) {
            WButton click = add(theme.button("Click")).expandX().widget();
            click.action = () -> {
                String sb = "Click!\nRight click to pick a new position.\nLeft click to cancel.";
                Modules.get().get(Marker.class).info(sb);

                clicking = true;
                MeteorClient.EVENT_BUS.subscribe(this);
                previousScreen = GameState.currentGameState;
                GameState.switchToGameState(GameState.IN_GAME);
            };

            WButton here = add(theme.button("Set Here")).expandX().widget();
            here.action = () -> {
                lastValue = value;
                Player player = client.getLocalPlayer();
                set(BlockPosition.ofGlobal(player.getZone(), (int) player.getPosition().x, (int) player.getPosition().y, (int) player.getPosition().z));
                newValueCheck();

                clear();
                init();
            };
        }
    }

    @EventHandler
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        if (clicking) {
            clicking = false;
            event.cancel();
            MeteorClient.EVENT_BUS.unsubscribe(this);
            GameState.switchToGameState(previousScreen);
        }
    }

    @EventHandler
    private void onInteractBlock(InteractBlockEvent event) {
        if (clicking) {
            // TODO fix
//            if (event.result.getType() == HitResult.Type.MISS) return;
//            lastValue = value;
//            set(event.result.getBlockPos());
            newValueCheck();

            clear();
            init();

            clicking = false;
            event.cancel();
            MeteorClient.EVENT_BUS.unsubscribe(this);
            GameState.switchToGameState(previousScreen);
        }
    }

    private boolean filter(String text, char c) {
        boolean good;
        boolean validate = true;

        if (c == '-' && text.isEmpty()) {
            good = true;
            validate = false;
        }
        else good = Character.isDigit(c);

        if (good && validate) {
            try {
                Integer.parseInt(text + c);
            } catch (NumberFormatException ignored) {
                good = false;
            }
        }

        return good;
    }

    public BlockPosition get() {
        return value;
    }

    public void set(BlockPosition value) {
        this.value = value;
    }

    private void addTextBox() {
        textBoxX = add(theme.textBox(Integer.toString(value.getGlobalX()), this::filter)).minWidth(75).widget();
        textBoxY = add(theme.textBox(Integer.toString(value.getGlobalY()), this::filter)).minWidth(75).widget();
        textBoxZ = add(theme.textBox(Integer.toString(value.getGlobalZ()), this::filter)).minWidth(75).widget();

        textBoxX.actionOnUnfocused = () -> {
            lastValue = value;
            if (textBoxX.get().isEmpty()) set(BlockPosition.ofGlobalZoneless(0, 0, 0));
            else {
                try {
                    set(BlockPosition.ofGlobalZoneless(Integer.parseInt(textBoxX.get()), value.getGlobalY(), value.getGlobalZ()));
                } catch (NumberFormatException ignored) {}
            }
            newValueCheck();
        };

        textBoxY.actionOnUnfocused = () -> {
            lastValue = value;
            if (textBoxY.get().isEmpty()) set(BlockPosition.ofGlobalZoneless(0, 0, 0));
            else {
                try {
                    set(BlockPosition.ofGlobalZoneless(value.getGlobalX(), Integer.parseInt(textBoxY.get()), value.getGlobalZ()));
                } catch (NumberFormatException ignored) {}
            }
            newValueCheck();
        };

        textBoxZ.actionOnUnfocused = () -> {
            lastValue = value;
            if (textBoxZ.get().isEmpty()) set(BlockPosition.ofGlobalZoneless(0, 0, 0));
            else {
                try {
                    set(BlockPosition.ofGlobalZoneless(value.getGlobalX(), value.getGlobalY(), Integer.parseInt(textBoxZ.get())));
                } catch (NumberFormatException ignored) {}
            }
            newValueCheck();
        };
    }

    private void newValueCheck() {
        if (value != lastValue) {
            if (action != null) action.run();
            if (actionOnRelease != null) actionOnRelease.run();
        }
    }
}
