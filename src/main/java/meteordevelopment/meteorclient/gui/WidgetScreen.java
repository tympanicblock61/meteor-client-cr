/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import finalforeach.cosmicreach.gamestates.GameState;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.renderer.GuiDebugRenderer;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WRoot;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static meteordevelopment.meteorclient.utils.Utils.getWindowHeight;
import static meteordevelopment.meteorclient.utils.Utils.getWindowWidth;
import static org.lwjgl.glfw.GLFW.*;

public abstract class WidgetScreen extends GameState implements InputProcessor {
    private static final GuiRenderer RENDERER = new GuiRenderer();
    private static final GuiDebugRenderer DEBUG_RENDERER = new GuiDebugRenderer();

    public Runnable taskAfterRender;
    protected Runnable enterAction;

    public GameState parent;
    private final WContainer root;

    protected final GuiTheme theme;

    public boolean locked, lockedAllowClose;
    private boolean closed;
    private boolean onClose;
    private boolean debug;

    private double lastMouseX, lastMouseY;

    public float animProgress;

    private List<Runnable> onClosed;

    protected boolean firstInit = true;

    public WidgetScreen(GuiTheme theme, String title) {
        //super(Text.literal(title));

        this.parent = GameState.currentGameState;
        this.root = new WFullScreenRoot();
        this.theme = theme;

        root.theme = theme;

        if (parent != null) {
            animProgress = 1;

            if (this instanceof TabScreen && parent instanceof TabScreen) {
                parent = ((TabScreen) parent).parent;
            }
        }
    }

    public <W extends WWidget> Cell<W> add(W widget) {
        return root.add(widget);
    }

    public void clear() {
        root.clear();
    }

    public void invalidate() {
        root.invalidate();
    }

    @Override
    public void create() {
        MeteorClient.EVENT_BUS.subscribe(this);

        closed = false;

        if (firstInit) {
            firstInit = false;
            initWidgets();
        }
    }

    public void close() {
        if (!locked || lockedAllowClose) {
            boolean preOnClose = onClose;
            onClose = true;

            removed();

            onClose = preOnClose;
        }
    }

    public abstract void initWidgets();

    public void reload() {
        clear();
        initWidgets();
    }

    public void onClosed(Runnable action) {
        if (onClosed == null) onClosed = new ArrayList<>(2);
        onClosed.add(action);
    }


    @Override
    public boolean touchDown(int mouseX, int mouseY, int pointer, int button) {
        if (locked) return false;

        double s = Gdx.graphics.getDensity();
        mouseX *= (int) s;
        mouseY *= (int) s;

        return root.mouseClicked(mouseX, mouseY, button, false);
    }

    @Override
    public boolean touchUp(int mouseX, int mouseY, int pointer, int button) {
        if (locked) return false;

        double s = Gdx.graphics.getDensity();
        mouseX *= (int) s;
        mouseY *= (int) s;

        return root.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseMoved(int mouseX, int mouseY) {
        if (locked) return false;

        double s = Gdx.graphics.getDensity();
        mouseX *= s;
        mouseY *= s;

        root.mouseMoved(mouseX, mouseY, lastMouseX, lastMouseY);

        lastMouseX = mouseX;
        lastMouseY = mouseY;
        return true;
    }


    @Override
    public boolean scrolled(float v, float v1) {
        if (locked) return false;

        return root.mouseScrolled(v);
    }

    @Override
    public boolean keyUp(int keyCode) {
        int modifiers = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
            modifiers |= GLFW_MOD_CONTROL;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            modifiers |= GLFW_MOD_SHIFT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT)) {
            modifiers |= GLFW_MOD_ALT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.CAPS_LOCK)) {
            modifiers |= GLFW_MOD_CAPS_LOCK;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_LOCK)) {
            modifiers |= GLFW_MOD_NUM_LOCK;
        }



        if (locked) return false;

        if ((modifiers == GLFW_MOD_CONTROL || modifiers == GLFW_MOD_SUPER) && keyCode == GLFW_KEY_9) {
            debug = !debug;
            return true;
        }

        if ((keyCode == GLFW_KEY_ENTER || keyCode == GLFW_KEY_KP_ENTER) && enterAction != null) {
            enterAction.run();
            return true;
        }

        return true;
    }

    @Override
    public boolean keyDown(int keyCode) {
        int modifiers = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
            modifiers |= GLFW_MOD_CONTROL;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            modifiers |= GLFW_MOD_SHIFT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT)) {
            modifiers |= GLFW_MOD_ALT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.CAPS_LOCK)) {
            modifiers |= GLFW_MOD_CAPS_LOCK;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_LOCK)) {
            modifiers |= GLFW_MOD_NUM_LOCK;
        }

        if (locked) return false;

        boolean shouldReturn = root.keyPressed(keyCode, modifiers);
        if (shouldReturn) return true;

        // Select next text box if TAB was pressed
        if (keyCode == GLFW_KEY_TAB) {
            AtomicReference<WTextBox> firstTextBox = new AtomicReference<>(null);
            AtomicBoolean done = new AtomicBoolean(false);
            AtomicBoolean foundFocused = new AtomicBoolean(false);

            loopWidgets(root, wWidget -> {
                if (done.get() || !(wWidget instanceof WTextBox textBox)) return;

                if (foundFocused.get()) {
                    textBox.setFocused(true);
                    textBox.setCursorMax();

                    done.set(true);
                } else {
                    if (textBox.isFocused()) {
                        textBox.setFocused(false);
                        foundFocused.set(true);
                    }
                }

                if (firstTextBox.get() == null) firstTextBox.set(textBox);
            });

            if (!done.get() && firstTextBox.get() != null) {
                firstTextBox.get().setFocused(true);
                firstTextBox.get().setCursorMax();
            }

            return true;
        }

        boolean control = modifiers == GLFW_MOD_CONTROL;

        if (control && keyCode == GLFW_KEY_C && toClipboard()) {
            return true;
        } else if (control && keyCode == GLFW_KEY_V && fromClipboard()) {
            reload();
            if (parent instanceof WidgetScreen wScreen) {
                wScreen.reload();
            }
            return true;
        }

        return false;
    }

    public void keyRepeated(int key, int modifiers) {
        if (locked) return;

        root.keyRepeated(key, modifiers);
    }

    @Override
    public boolean keyTyped(char c) {
        if (locked) return false;

        return root.charTyped(c);
    }

    // fake delta
    private float delta = 0;

    @Override
    public void render() {

        float scale = Gdx.graphics.getDensity();  // Equivalent to mc.getWindow().getScaleFactor()
        double mouseX = Gdx.input.getX() * scale;
        double mouseY = Gdx.input.getY() * scale;
        delta++;

        animProgress += (delta / 20 * 14);
        animProgress = Math.clamp(animProgress, 0, 1);

        GuiKeyEvents.canUseKeys = true;

        // Apply projection without scaling
        batch.begin();
        //Utils.unscaledProjection();

        onRenderBefore(batch, delta);

        RENDERER.theme = theme;
        theme.beforeRender();

        RENDERER.begin(batch);
        RENDERER.setAlpha(animProgress);
        root.render(RENDERER, mouseX, mouseY, delta / 20);
        RENDERER.setAlpha(1);
        RENDERER.end();

        boolean tooltip = RENDERER.renderTooltip(batch, mouseX, mouseY, delta / 20);

        if (debug) {
            batch.begin();
            DEBUG_RENDERER.render(root, batch.getProjectionMatrix());  // Render debug elements
            if (tooltip) DEBUG_RENDERER.render(RENDERER.tooltipWidget, batch.getProjectionMatrix());
            batch.end();
        }

        //Utils.scaledProjection();

        runAfterRenderTasks();
    }

    protected void runAfterRenderTasks() {
        if (taskAfterRender != null) {
            taskAfterRender.run();
            taskAfterRender = null;
        }
    }

    protected void onRenderBefore(Batch batch, float delta) {}

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        root.invalidate();
    }

    @Override
    public void switchAwayTo(GameState gameState) {
        if (!locked || lockedAllowClose) {
            boolean preOnClose = onClose;
            onClose = true;

            removed();

            onClose = preOnClose;
        }
        this.removed();
        super.switchAwayTo(gameState);
    }

    public void removed() {
        if (!closed || lockedAllowClose) {
            closed = true;
            onClosed();


            //Input.setCursorStyle(CursorStyle.Default);

            loopWidgets(root, widget -> {
                if (widget instanceof WTextBox textBox && textBox.isFocused()) textBox.setFocused(false);
            });

            MeteorClient.EVENT_BUS.unsubscribe(this);
            GuiKeyEvents.canUseKeys = true;

            if (onClosed != null) {
                for (Runnable action : onClosed) action.run();
            }

            if (onClose) {
                taskAfterRender = () -> {
                    locked = true;
                    GameState.switchToGameState(parent);
//                    mc.setScreen(parent);
                };
            }
        }
    }

    private void loopWidgets(WWidget widget, Consumer<WWidget> action) {
        action.accept(widget);

        if (widget instanceof WContainer) {
            for (Cell<?> cell : ((WContainer) widget).cells) loopWidgets(cell.widget(), action);
        }
    }

    protected void onClosed() {}

    public boolean toClipboard() {
        return false;
    }

    public boolean fromClipboard() {
        return false;
    }

//    @Override
//    public boolean shouldCloseOnEsc() {
//        return !locked || lockedAllowClose;
//    }
//
//    @Override
//    public boolean shouldPause() {
//        return false;
//    }

    private static class WFullScreenRoot extends WContainer implements WRoot {
        private boolean valid;

        @Override
        public void invalidate() {
            valid = false;
        }

        @Override
        protected void onCalculateSize() {
            width = getWindowWidth();
            height = getWindowHeight();
        }

        @Override
        protected void onCalculateWidgetPositions() {
            for (Cell<?> cell : cells) {
                cell.x = 0;
                cell.y = 0;

                cell.width = width;
                cell.height = height;

                cell.alignWidget();
            }
        }

        @Override
        public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            if (!valid) {
                calculateSize();
                calculateWidgetPositions();

                valid = true;
                mouseMoved(Gdx.input.getX(), Gdx.input.getY(), Gdx.input.getX(), Gdx.input.getY());
            }

            return super.render(renderer, mouseX, mouseY, delta);
        }
    }
}
