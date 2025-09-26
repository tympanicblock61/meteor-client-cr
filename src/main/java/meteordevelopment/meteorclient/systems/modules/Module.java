/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules;

import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.BooleanDataAttribute;
import com.github.puzzle.game.items.data.attributes.DataTagManifestAttribute;
import com.github.puzzle.game.items.data.attributes.StringDataAttribute;
import finalforeach.cosmicreach.ClientSingletons;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtElement;
//import net.minecraft.text.Text;
//import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Module implements ISerializable<Module>, Comparable<Module> {
    protected final ClientSingletons client;

    public final Category category;
    public final String name;
    public final String title;
    public final String description;
    public final String[] aliases;
    public final Color color;

    public final MeteorAddon addon;
    public final Settings settings = new Settings();

    private boolean active;

    public boolean serialize = true;
    public boolean runInMainMenu = false;
    public boolean autoSubscribe = true;

    public final Keybind keybind = Keybind.none();
    public boolean toggleOnBindRelease = false;
    public boolean chatFeedback = true;
    public boolean favorite = false;

    public Module(Category category, String name, String description, String... aliases) {
        if (name.contains(" ")) MeteorClient.LOG.warn("Module '{}' contains invalid characters in its name making it incompatible with Meteor Client commands.", name);

        this.client = ClientSingletons.get();
        this.category = category;
        this.name = name;
        this.title = Utils.nameToTitle(name);
        this.description = description;
        this.aliases = aliases;
        this.color = Color.fromHsv(Utils.random(0.0, 360.0), 0.35, 1);

        String classname = this.getClass().getName();
        for (MeteorAddon addon : AddonManager.ADDONS) {
            if (classname.startsWith(addon.getPackage())) {
                this.addon = addon;
                return;
            }
        }

        this.addon = null;
    }

    public Module(Category category, String name, String desc) {
        this(category, name, desc, new String[0]);
    }

    public WWidget getWidget(GuiTheme theme) {
        return null;
    }

    public void onActivate() {}
    public void onDeactivate() {}

    public void toggle() {
        if (!active) {
            active = true;
            Modules.get().addActive(this);

            settings.onActivated();

            if (runInMainMenu || Utils.canUpdate()) {
                if (autoSubscribe) MeteorClient.EVENT_BUS.subscribe(this);
                onActivate();
            }
        }
        else {
            if (runInMainMenu || Utils.canUpdate()) {
                if (autoSubscribe) MeteorClient.EVENT_BUS.unsubscribe(this);
                onDeactivate();
            }

            active = false;
            Modules.get().removeActive(this);
        }
    }

    public void sendToggledMsg() {
        if (Config.get().chatFeedback.get() && chatFeedback) {
            ChatUtils.forceNextPrefixClass(getClass());
            ChatUtils.sendMsg(""+this.hashCode(), String.format("Toggled (highlight)%s(default) %s(default).", title, isActive() ? "on" : "off"));
        }
    }

    public void info(String message) {
        ChatUtils.forceNextPrefixClass(getClass());
        ChatUtils.sendMsg(title, message);
    }

    public void warning(String message) {
        ChatUtils.forceNextPrefixClass(getClass());
        ChatUtils.warningPrefix(title, message);
    }

    public void error(String message) {
        ChatUtils.forceNextPrefixClass(getClass());
        ChatUtils.errorPrefix(title, message);
    }

    public boolean isActive() {
        return active;
    }

    public String getInfoString() {
        return null;
    }

    @Override
    public DataTagManifest toTag() {
        if (!serialize) return null;
        DataTagManifest tag = new DataTagManifest();

        tag.addTag(new DataTag<>("name", new StringDataAttribute(name)));
        tag.addTag(new DataTag<>("keybind", new DataTagManifestAttribute(keybind.toTag())));
        tag.addTag(new DataTag<>("toggleOnKeyRelease", new BooleanDataAttribute(toggleOnBindRelease)));
        tag.addTag(new DataTag<>("chatFeedback", new BooleanDataAttribute(chatFeedback)));
        tag.addTag(new DataTag<>("favorite", new BooleanDataAttribute(favorite)));
        tag.addTag(new DataTag<>("settings", new DataTagManifestAttribute(settings.toTag())));
        tag.addTag(new DataTag<>("active", new BooleanDataAttribute(active)));

        return tag;
    }

    @Override
    public Module fromTag(DataTagManifest tag) {
        // General
        keybind.fromTag(tag.getTag("keybind").getTagAsType(DataTagManifest.class).getValue());
        toggleOnBindRelease = tag.getTag("toggleOnKeyRelease").getTagAsType(Boolean.TYPE).getValue();
        chatFeedback = !tag.hasTag("chatFeedback") || tag.getTag("chatFeedback").getTagAsType(Boolean.TYPE).getValue();
        favorite = tag.getTag("favorite").getTagAsType(Boolean.TYPE).getValue();

        // Settings
        DataTagManifest settingsTag = tag.getTag("settings").getTagAsType(DataTagManifest.class).getValue();
        settings.fromTag(settingsTag);

        boolean active = tag.getTag("active").getTagAsType(Boolean.TYPE).getValue();
        if (active != isActive()) toggle();

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Module module = (Module) o;
        return Objects.equals(name, module.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(@NotNull Module o) {
        return name.compareTo(o.name);
    }
}
