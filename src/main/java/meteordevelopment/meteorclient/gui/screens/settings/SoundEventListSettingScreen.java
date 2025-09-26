/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.screens.settings;

import de.pottgames.tuningfork.SoundBuffer;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SoundEventListSetting;
import java.util.List;

public class SoundEventListSettingScreen extends RegistryListSettingScreen<SoundBuffer> {
    public SoundEventListSettingScreen(GuiTheme theme, Setting<List<SoundBuffer>> setting) {
        super(theme, "Select Sounds", setting, setting.get(), SoundEventListSetting.SOUNDS);
    }

    @Override
    protected WWidget getValueWidget(SoundBuffer value) {
        return theme.label(getValueName(value));
    }

    @Override
    protected String getValueName(SoundBuffer value) {
        return SoundEventListSetting.SOUNDS_R.get(value).toPath();
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
