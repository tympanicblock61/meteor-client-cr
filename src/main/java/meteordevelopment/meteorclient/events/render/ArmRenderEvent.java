/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.render;


//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.util.Hand;

import com.badlogic.gdx.math.Matrix4;

public class ArmRenderEvent {
    public static ArmRenderEvent INSTANCE = new ArmRenderEvent();

    public Matrix4 matrix;

    public static ArmRenderEvent get(Matrix4 matrices) {
        INSTANCE.matrix = matrices;

        return INSTANCE;
    }
}
