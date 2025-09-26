/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixins;

import com.badlogic.gdx.graphics.Camera;
import finalforeach.cosmicreach.entities.PlayerController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerController.class)
public interface AccessorPlayerController {
    @Accessor("playerCam")
    Camera getPlayerCamera();
}
