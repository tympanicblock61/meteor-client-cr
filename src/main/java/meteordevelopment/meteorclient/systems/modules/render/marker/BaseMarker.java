/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render.marker;

import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.DataTagManifestAttribute;
import finalforeach.cosmicreach.gamestates.GameState;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.MarkerScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;


public abstract class BaseMarker implements ISerializable<BaseMarker> {
    public final Settings settings = new Settings();

    protected final SettingGroup sgBase = settings.createGroup("Base");

    public final Setting<String> name = sgBase.add(new StringSetting.Builder()
        .name("name")
        .description("Custom name for this marker.")
        .build()
    );

    protected final Setting<String> description = sgBase.add(new StringSetting.Builder()
        .name("description")
        .description("Custom description for this marker.")
        .build()
    );

    private final Setting<Boolean> active = sgBase.add(new BoolSetting.Builder()
        .name("active")
        .description("Is this marker visible.")
        .defaultValue(false)
        .build()
    );

    public BaseMarker(String name) {
        this.name.set(name);
    }

    protected void render(Render3DEvent event) {}

    protected void tick() {}

    public GameState getScreen(GuiTheme theme) {
        return new MarkerScreen(theme, this);
    }

    public WWidget getWidget(GuiTheme theme) {
        return null;
    }

    public String getName() {
        return name.get();
    }

    public String getTypeName() {
        return null;
    }

    public boolean isActive() {
        return active.get();
    }

    public boolean isVisible() {
        return isActive();
    }


    public void toggle() {
        active.set(!active.get());
    }

    @Override
    public DataTagManifest toTag() {
        DataTagManifest tag = new DataTagManifest();
        tag.addTag(new DataTag<>("settings", new DataTagManifestAttribute(settings.toTag())));
        return tag;
    }

    @Override
    public BaseMarker fromTag(DataTagManifest tag) {
        DataTagManifest settingsTag = tag.getTag("settings").getTagAsType(DataTagManifest.class).getValue();
        if (settingsTag != null) settings.fromTag(settingsTag);
        return this;
    }
}
