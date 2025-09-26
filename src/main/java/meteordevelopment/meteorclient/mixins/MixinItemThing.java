/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixins;

import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.items.ItemSlot;
import finalforeach.cosmicreach.items.ItemThing;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemThing.class)
public class MixinItemThing {
    @Inject(method = "useItem", at = @At("HEAD"))
    public void before_useItem(ItemSlot itemSlot, Player player, BlockPosition targetBlockPos, CallbackInfo ci) {
        PlayerUtils.isUsingItem = true;
    }

    @Inject(method = "useItem", at = @At("TAIL"))
    public void after_useItem(ItemSlot itemSlot, Player player, BlockPosition targetBlockPos, CallbackInfo ci) {
        PlayerUtils.isUsingItem = false;
    }
}
