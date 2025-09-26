/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import meteordevelopment.meteorclient.utils.Utils;
//import net.minecraft.client.gui.DrawContext;

public class Render2DEvent {
    private static final Render2DEvent INSTANCE = new Render2DEvent();

    public Batch batch;
    public int screenWidth, screenHeight;
    public double frameTime;
    public float tickDelta;

    public static Render2DEvent get(Batch batch, int screenWidth, int screenHeight, float tickDelta) {
        INSTANCE.batch = batch;
        INSTANCE.screenWidth = screenWidth;
        INSTANCE.screenHeight = screenHeight;
        INSTANCE.frameTime = Utils.frameTime;
        INSTANCE.tickDelta = tickDelta;
        return INSTANCE;
    }
}
