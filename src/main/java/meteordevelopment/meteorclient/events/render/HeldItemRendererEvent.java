/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.render;

//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.util.Hand;

import com.badlogic.gdx.math.Matrix4;

public class HeldItemRendererEvent {
    private static final HeldItemRendererEvent INSTANCE = new HeldItemRendererEvent();

    public Matrix4 matrix;

    public static HeldItemRendererEvent get(Matrix4 matrices) {
        INSTANCE.matrix = matrices;
        return INSTANCE;
    }
}
