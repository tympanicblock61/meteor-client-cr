/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.macros;

import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.DataTagManifestAttribute;
import finalforeach.cosmicreach.savelib.crbin.ICRBinSerializable;
import meteordevelopment.meteorclient.gui.utils.StarscriptTextBoxRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.starscript.Script;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Macro implements ISerializable<Macro> {
    public final Settings settings = new Settings();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public Setting<String> name = sgGeneral.add(new StringSetting.Builder()
        .name("name")
        .description("The name of the macro.")
        .build()
    );

    public Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder()
        .name("messages")
        .description("The messages for the macro to send.")
        .onChanged(v -> dirty = true)
        .renderer(StarscriptTextBoxRenderer.class)
        .build()
    );

    public Setting<Keybind> keybind = sgGeneral.add(new KeybindSetting.Builder()
        .name("keybind")
        .description("The bind to run the macro.")
        .build()
    );

    private final List<Script> scripts = new ArrayList<>(1);
    private boolean dirty;

    public Macro() {}
    public Macro(ICRBinSerializable tag) {
        fromTag((DataTagManifest) tag);
    }

    public boolean onAction(boolean isKey, int value, int modifiers) {
        if (!keybind.get().matches(isKey, value, modifiers) /* || mc.currentScreen != null*/) return false;
        return onAction();
    }

    public boolean onAction() {
            if (dirty) {
                scripts.clear();

                for (String message : messages.get()) {
                    Script script = MeteorStarscript.compile(message);
                    if (script != null) scripts.add(script);
                }

                dirty = false;
            }

            for (Script script : scripts) {
                String message = MeteorStarscript.run(script);

                if (message != null) {
                    ChatUtils.sendPlayerMsg(message);
                }
            }

            return true;
    }

    @Override
    public DataTagManifest toTag() {
        DataTagManifest tag = new DataTagManifest();
        tag.addTag(new DataTag<>("settings", new DataTagManifestAttribute(settings.toTag())));
        return tag;
    }

    @Override
    public Macro fromTag(DataTagManifest tag) {
        if (tag.hasTag("settings")) {
            settings.fromTag(tag.getTag("settings").getTagAsType(DataTagManifest.class).getValue());
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Macro macro = (Macro) o;
        return Objects.equals(macro.name.get(), this.name.get());
    }
}
