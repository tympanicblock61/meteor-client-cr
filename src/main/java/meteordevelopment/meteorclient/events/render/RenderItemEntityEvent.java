/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.render;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import finalforeach.cosmicreach.entities.ItemEntity;
import meteordevelopment.meteorclient.events.Cancellable;
import meteordevelopment.meteorclient.mixininterface.IEntityRenderState;
//import net.minecraft.client.item.ItemModelManager;
//import net.minecraft.client.render.VertexConsumerProvider;
//import net.minecraft.client.render.entity.state.ItemEntityRenderState;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.entity.ItemEntity;

public class RenderItemEntityEvent extends Cancellable {
    private static final RenderItemEntityEvent INSTANCE = new RenderItemEntityEvent();

    public ItemEntity itemEntity;
//    public ItemEntityRenderState renderState;
    public float tickDelta;
    public Matrix4 matrixStack;
//    public VertexConsumerProvider vertexConsumerProvider;
    public int light;
//    public ItemModelManager itemModelManager;

    public static RenderItemEntityEvent get(ItemEntity itemEntity, float tickDelta, Matrix4 matrixStack, int light) {
        INSTANCE.setCancelled(false);
        INSTANCE.itemEntity = itemEntity;
        INSTANCE.tickDelta = tickDelta;
        INSTANCE.matrixStack = matrixStack;
        INSTANCE.light = light;
        return INSTANCE;
    }
}
