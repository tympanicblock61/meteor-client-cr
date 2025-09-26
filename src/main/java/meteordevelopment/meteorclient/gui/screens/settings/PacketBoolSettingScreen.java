/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.screens.settings;

import finalforeach.cosmicreach.networking.GamePacket;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.PacketListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.network.PacketUtils;

import java.util.Set;
import java.util.function.Predicate;

public class PacketBoolSettingScreen extends RegistryListSettingScreen<Class<? extends GamePacket>> {
    public PacketBoolSettingScreen(GuiTheme theme, Setting<Set<Class<? extends GamePacket>>> setting) {
        super(theme, "Select Packets", setting, setting.get(), PacketUtils.REGISTRY);
    }

    @Override
    protected boolean includeValue(Class<? extends GamePacket> value) {
        Predicate<Class<? extends GamePacket>> filter = ((PacketListSetting) setting).filter;

        if (filter == null) return true;
        return filter.test(value);
    }

    @Override
    protected WWidget getValueWidget(Class<? extends GamePacket> value) {
        return theme.label(getValueName(value));
    }

    @Override
    protected String getValueName(Class<? extends GamePacket> value) {
        return PacketUtils.getName(value);
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }
}
