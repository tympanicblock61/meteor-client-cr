/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixins;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.spongepowered.asm.mixin.Mixin;
import finalforeach.cosmicreach.ui.UI;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(UI.class)
public interface AccessorUI {
    @Accessor("shapeRenderer")
    ShapeRenderer getShapeRenderer();
}
