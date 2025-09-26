/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixins;

import com.badlogic.gdx.graphics.Texture;
import finalforeach.cosmicreach.BlockRaycasts;
import finalforeach.cosmicreach.BlockSelection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockSelection.class)
public interface AccessorBlockSelection {
    @Accessor("blockRaycasts")
    BlockRaycasts getRaycasts();

    @Accessor("breakTex")
    Texture[] getBreakTex();

    @Accessor("breakingTime")
    float getBreakingTime();
}
