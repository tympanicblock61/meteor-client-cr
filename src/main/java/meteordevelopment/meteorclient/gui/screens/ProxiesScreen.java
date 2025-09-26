/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.screens;

import finalforeach.cosmicreach.gamestates.GameState;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.proxies.Proxies;
import meteordevelopment.meteorclient.systems.proxies.Proxy;
import meteordevelopment.meteorclient.utils.misc.DataTagUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ProxiesScreen extends WindowScreen {
    private final List<WCheckbox> checkboxes = new ArrayList<>();

    public ProxiesScreen(GuiTheme theme) {
        super(theme, "Proxies");
    }

    @Override
    public void initWidgets() {
        WTable table = add(theme.table()).expandX().minWidth(400).widget();
        initTable(table);

        add(theme.horizontalSeparator()).expandX();

        WHorizontalList l = add(theme.horizontalList()).expandX().widget();

        // New
        WButton newBtn = l.add(theme.button("New")).expandX().widget();
        newBtn.action = () -> GameState.switchToGameState(new EditProxyScreen(theme, null, this::reload));

        // Import
        PointerBuffer filters = BufferUtils.createPointerBuffer(1);

        ByteBuffer txtFilter = MemoryUtil.memASCII("*.txt");

        filters.put(txtFilter);
        filters.rewind();

        WButton importBtn = l.add(theme.button("Import")).expandX().widget();
        importBtn.action = () -> {
            String selectedFile = TinyFileDialogs.tinyfd_openFileDialog("Import Proxies", null, filters, null, false);
            if (selectedFile != null) {
                File file = new File(selectedFile);
                GameState.switchToGameState(new ProxiesImportScreen(theme, file));
            }
        };
    }

    private void initTable(WTable table) {
        table.clear();
        if (Proxies.get().isEmpty()) return;

        for (Proxy proxy : Proxies.get()) {
            WCheckbox enabled = table.add(theme.checkbox(proxy.enabled.get())).widget();
            checkboxes.add(enabled);
            enabled.action = () -> {
                boolean checked = enabled.checked;
                Proxies.get().setEnabled(proxy, checked);

                for (WCheckbox checkbox : checkboxes) checkbox.checked = false;
                enabled.checked = checked;
            };

            WLabel name = table.add(theme.label(proxy.name.get())).widget();
            name.color = theme.textColor();

            WLabel type = table.add(theme.label("(" + proxy.type.get() + ")")).widget();
            type.color = theme.textSecondaryColor();

            WHorizontalList ipList = table.add(theme.horizontalList()).expandCellX().widget();
            ipList.spacing = 0;

            ipList.add(theme.label(proxy.address.get()));
            ipList.add(theme.label(":")).widget().color = theme.textSecondaryColor();
            ipList.add(theme.label(Integer.toString(proxy.port.get())));

            WButton edit = table.add(theme.button(GuiRenderer.EDIT)).widget();
            edit.action = () -> GameState.switchToGameState(new EditProxyScreen(theme, proxy, this::reload));

            WMinus remove = table.add(theme.minus()).widget();
            remove.action = () -> {
                Proxies.get().remove(proxy);
                reload();
            };

            table.row();
        }
    }

    @Override
    public boolean toClipboard() {
        return DataTagUtils.toClipboard(Proxies.get());
    }

    @Override
    public boolean fromClipboard() {
        return DataTagUtils.fromClipboard(Proxies.get());
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    protected static class EditProxyScreen extends EditSystemScreen<Proxy> {
        public EditProxyScreen(GuiTheme theme, Proxy value, Runnable reload) {
            super(theme, value, reload);
        }

        @Override
        public Proxy create() {
            return new Proxy.Builder().build();
        }

        @Override
        public boolean save() {
            return value.resolveAddress() && (!isNew || Proxies.get().add(value));
        }

        @Override
        public Settings getSettings() {
            return value.settings;
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
}
