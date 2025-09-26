/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.entities.PlayerController;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.items.Item;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.rendering.items.ItemModel;
import finalforeach.cosmicreach.rendering.items.ItemRenderer;
import finalforeach.cosmicreach.ui.FontRenderer;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
//import net.minecraft.client.gui.DrawContext;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Vec3d;
//import org.joml.Matrix4f;
//import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import static meteordevelopment.meteorclient.MeteorClient.mc;

public class RenderUtils {
    public static Vector3 center;

    private static final Pool<RenderBlock> renderBlockPool = new Pool<>(RenderBlock::new);
    private static final List<RenderBlock> renderBlocks = new ArrayList<>();

    private RenderUtils() {
    }

    @PostInit
    public static void init() {
        MeteorClient.EVENT_BUS.subscribe(RenderUtils.class);
    }

    // Items
    public static void drawItem(Batch batch, ItemStack itemStack, int x, int y, float scale, boolean overlay, String countOverride) {
        Matrix4 previous = batch.getProjectionMatrix();
        Matrix4 matrices = new Matrix4();
        matrices.scale(scale, scale, 1f);
        batch.setProjectionMatrix(matrices);

        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);

        Item item = itemStack.getItem();
        ItemModel model = ItemRenderer.getModel(item, true);
        Camera cam = model.getItemSlotCamera();
        cam.position.x = scaledX;
        cam.position.y = scaledY;
        cam.position.z = 0; // TODO check this
        ItemRenderer.drawItem(cam, item);

        if (overlay) {
            // render item stack?
            if (itemStack.amount != 1) {
                FontRenderer.drawText(batch, InGame.IN_GAME.newUiViewport, ""+itemStack.amount, scaledX, scaledY, true);
            }
        }
        batch.setProjectionMatrix(previous);
    }

    public static void drawItem(Batch batch, ItemStack itemStack, int x, int y, float scale, boolean overlay) {
        drawItem(batch, itemStack, x, y, scale, overlay, null);
    }

    public static void updateScreenCenter(Matrix4 projection, Matrix4 view) {
        Matrix4 invProjection = new Matrix4(projection).inv();
        Matrix4 invView = new Matrix4(view).inv();

        Vector4 center4 = new Vector4(0, 0, 0, 1);
        // center4.mul(invProjection);
        center4.set(
                invProjection.val[0] * center4.x + invProjection.val[4] * center4.y + invProjection.val[8] * center4.z + invProjection.val[12] * center4.w,
                invProjection.val[1] * center4.x + invProjection.val[5] * center4.y + invProjection.val[9] * center4.z + invProjection.val[13] * center4.w,
                invProjection.val[2] * center4.x + invProjection.val[6] * center4.y + invProjection.val[10] * center4.z + invProjection.val[14] * center4.w,
                invProjection.val[3] * center4.x + invProjection.val[7] * center4.y + invProjection.val[11] * center4.z + invProjection.val[15] * center4.w
        );
        // center4.mul(invView);
        center4.set(
                invView.val[0] * center4.x + invView.val[4] * center4.y + invView.val[8] * center4.z + invView.val[12] * center4.w,
                invView.val[1] * center4.x + invView.val[5] * center4.y + invView.val[9] * center4.z + invView.val[13] * center4.w,
                invView.val[2] * center4.x + invView.val[6] * center4.y + invView.val[10] * center4.z + invView.val[14] * center4.w,
                invView.val[3] * center4.x + invView.val[7] * center4.y + invView.val[11] * center4.z + invView.val[15] * center4.w
        );

        // center4.div(center4.w);
        center4.set(center4.x / center4.w, center4.y / center4.w, center4.z / center4.w, center4.w / center4.w);

        Camera plyrCam = PlayerUtils.playerCamera();

        Vector3 camera = plyrCam.position;
        center = new Vector3(camera.x + center4.x, camera.y + center4.y, camera.z + center4.z);
    }

    public static void renderTickingBlock(BlockPosition blockPos, Color sideColor, Color lineColor, ShapeMode shapeMode, int excludeDir, int duration, boolean fade, boolean shrink) {
        // Ensure there aren't multiple fading blocks in one pos
        Iterator<RenderBlock> iterator = renderBlocks.iterator();
        while (iterator.hasNext()) {
            RenderBlock next = iterator.next();
            if (next.pos == (blockPos)) {
                iterator.remove();
                renderBlockPool.free(next);
            }
        }

        renderBlocks.add(renderBlockPool.get().set(blockPos, sideColor, lineColor, shapeMode, excludeDir, duration, fade, shrink));
    }

    @EventHandler
    private static void onTick(TickEvent.Pre event) {
        if (renderBlocks.isEmpty()) return;

        renderBlocks.forEach(RenderBlock::tick);

        Iterator<RenderBlock> iterator = renderBlocks.iterator();
        while (iterator.hasNext()) {
            RenderBlock next = iterator.next();
            if (next.ticks <= 0) {
                iterator.remove();
                renderBlockPool.free(next);
            }
        }
    }

    @EventHandler
    private static void onRender(Render3DEvent event) {
        renderBlocks.forEach(block -> block.render(event));
    }

    public static class RenderBlock {
        public BlockPosition pos = BlockPosition.ofGlobalZoneless(0,0,0);

        public Color sideColor, lineColor;
        public ShapeMode shapeMode;
        public int excludeDir;

        public int ticks, duration;
        public boolean fade, shrink;

        public RenderBlock set(BlockPosition blockPos, Color sideColor, Color lineColor, ShapeMode shapeMode, int excludeDir, int duration, boolean fade, boolean shrink) {
            pos.set(blockPos);
            this.sideColor = sideColor;
            this.lineColor = lineColor;
            this.shapeMode = shapeMode;
            this.excludeDir = excludeDir;
            this.fade = fade;
            this.shrink = shrink;
            this.ticks = duration;
            this.duration = duration;

            return this;
        }

        public void tick() {
            ticks--;
        }

        public void render(Render3DEvent event) {
            int preSideA = sideColor.a;
            int preLineA = lineColor.a;
            double x1 = pos.getGlobalX(), y1 = pos.getGlobalY(), z1 = pos.getGlobalZ(),
                   x2 = pos.getGlobalX() + 1, y2 = pos.getGlobalY() + 1, z2 = pos.getGlobalZ() + 1;

            double d = (double) (ticks - event.tickDelta) / duration;

            if (fade) {
                sideColor.a = (int) (sideColor.a * d);
                lineColor.a = (int) (lineColor.a * d);
            }
            if (shrink) {
                x1 += d; y1 += d; z1 += d;
                x2 -= d; y2 -= d; z2 -= d;
            }

            event.renderer.box(x1, y1, z1, x2, y2, z2, sideColor, lineColor, shapeMode, excludeDir);

            sideColor.a = preSideA;
            lineColor.a = preLineA;
        }
    }
}

