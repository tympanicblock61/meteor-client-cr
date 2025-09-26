/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.render;

//import net.minecraft.item.tooltip.TooltipData;
//import net.minecraft.item.ItemStack;


import finalforeach.cosmicreach.items.ItemStack;

import java.util.List;

public class TooltipDataEvent {
    private static final TooltipDataEvent INSTANCE = new TooltipDataEvent();

    public List<String> tooltipData;
    public ItemStack itemStack;

    public static TooltipDataEvent get(ItemStack itemStack) {
        INSTANCE.tooltipData = null;
        INSTANCE.itemStack = itemStack;
        return INSTANCE;
    }
}
