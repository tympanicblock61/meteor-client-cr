/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixins;

import finalforeach.cosmicreach.entities.PlayerController;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.ui.UI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InGame.class)
public interface AccessorInGame {
    @Accessor("playerController")
    PlayerController getPlayerController();

    @Accessor("ui")
    UI getUI();
}
