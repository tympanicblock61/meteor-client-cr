/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.game;

import finalforeach.cosmicreach.gamestates.GameState;
import meteordevelopment.meteorclient.events.Cancellable;

public class OpenScreenEvent extends Cancellable {
    private static final OpenScreenEvent INSTANCE = new OpenScreenEvent();

    public GameState screen;

    public static OpenScreenEvent get(GameState screen) {
        INSTANCE.setCancelled(false);
        INSTANCE.screen = screen;
        return INSTANCE;
    }
}
