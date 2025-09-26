/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.widgets;

import com.badlogic.gdx.Gdx;
import finalforeach.cosmicreach.gamestates.GameState;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.utils.render.color.Color;

public abstract class WTopBar extends WHorizontalList {
    protected abstract Color getButtonColor(boolean pressed, boolean hovered);

    protected abstract Color getNameColor();

    public WTopBar() {
        spacing = 0;
    }

    @Override
    public void init() {
        for (Tab tab : Tabs.get()) {
            add(new WTopBarButton(tab));
        }
    }

    protected class WTopBarButton extends WPressable {
        private final Tab tab;

        public WTopBarButton(Tab tab) {
            this.tab = tab;
        }

        @Override
        protected void onCalculateSize() {
            double pad = pad();

            width = pad + theme.textWidth(tab.name) + pad;
            height = pad + theme.textHeight() + pad;
        }

        @Override
        protected void onPressed(int button) {
            GameState screen = GameState.currentGameState;

            if (!(screen instanceof TabScreen) || ((TabScreen) screen).tab != tab) {
                double mouseX = Gdx.input.getX();
                double mouseY = Gdx.input.getY();

                tab.openScreen(theme);
                Gdx.input.setCursorPosition((int) mouseX, (int) mouseY);
                //glfwSetCursorPos(mc.getWindow().getHandle(), mouseX, mouseY);
            }
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            double pad = pad();
            Color color = getButtonColor(pressed || (GameState.currentGameState instanceof TabScreen && ((TabScreen) GameState.currentGameState).tab == tab), mouseOver);

            renderer.quad(x, y, width, height, color);
            renderer.text(tab.name, x + pad, y + pad, getNameColor(), false);
        }
    }
}
