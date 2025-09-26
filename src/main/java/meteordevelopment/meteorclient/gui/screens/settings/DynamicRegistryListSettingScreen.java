/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.screens.settings;

import com.github.puzzle.core.registries.IRegistry;
import com.github.puzzle.util.MutablePair;
import finalforeach.cosmicreach.util.Identifier;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.Utils;

import java.util.*;
import java.util.function.Consumer;

public abstract class DynamicRegistryListSettingScreen<E> extends WindowScreen {
    protected final Setting<?> setting;
    protected final Collection<E> collection;
    private final IRegistry<E> registryKey;
    private final Optional<IRegistry<E>> registry;

    private WTextBox filter;
    private String filterText = "";

    private WTable table;

    public DynamicRegistryListSettingScreen(GuiTheme theme, String title, Setting<?> setting, Collection<E> collection, IRegistry<E> registryKey) {
        super(theme, title);

        this.registryKey = registryKey;
        this.registry = Optional.empty(); /*Optional.ofNullable(MinecraftClient.getInstance().getNetworkHandler())
            .flatMap(networkHandler -> networkHandler.getRegistryManager().getOptional(registryKey))*/;
        this.setting = setting;
        this.collection = collection;
    }

    @Override
    public void initWidgets() {
        // Filter
        filter = add(theme.textBox("")).minWidth(400).expandX().widget();
        filter.setFocused(true);
        filter.action = () -> {
            filterText = filter.get().trim();

            table.clear();
            generateWidgets();
        };

        table = add(theme.table()).expandX().widget();

        generateWidgets();
    }

    private void generateWidgets() {
        // Left (all)
        WTable left = abc(pairs -> registry.ifPresent(registry -> {
            registry.names().stream().map(registry::get)
                .forEach(t -> {
                    if (skipValue(t) || collection.contains(t)) return;

                    int words = Utils.searchInWords(getValueName(t), filterText);
                    int diff = Utils.searchLevenshteinDefault(getValueName(t), filterText, false);
                    if (words > 0 || diff <= getValueName(t).length() / 2) pairs.add(new MutablePair<>(t, -diff));
                });
            }), true, t -> {
                addValue(t);

                E v = getAdditionalValue(t);
                if (v != null) addValue(v);
            }
        );

        if (!left.cells.isEmpty()) {
            left.add(theme.horizontalSeparator()).expandX();
            left.row();
        }

        WHorizontalList manualEntry = left.add(theme.horizontalList()).expandX().widget();
        WTextBox textBox = manualEntry.add(theme.textBox("minecraft:")).expandX().minWidth(120d).widget();
        manualEntry.add(theme.plus()).expandCellX().right().widget().action = () -> {
            String entry = textBox.get().trim();
            Identifier id = Identifier.of(entry);
            addValue((E) id);
        };

        table.add(theme.verticalSeparator()).expandWidgetY();

        // Right (selected)
        abc(pairs -> {
            for (E value : collection) {
                if (skipValue(value)) continue;

                int words = Utils.searchInWords(getValueName(value), filterText);
                int diff = Utils.searchLevenshteinDefault(getValueName(value), filterText, false);
                if (words > 0 || diff <= getValueName(value).length() / 2) pairs.add(new MutablePair<>(value, -diff));
            }
        }, false, t -> {
            removeValue(t);

            E v = getAdditionalValue(t);
            if (v != null) removeValue(v);
        });
    }

    private void addValue(E value) {
        if (!collection.contains(value)) {
            collection.add(value);

            setting.onChanged();
            table.clear();
            generateWidgets();
        }
    }

    private void removeValue(E value) {
        if (collection.remove(value)) {
            setting.onChanged();
            table.clear();
            generateWidgets();
        }
    }

    private WTable abc(Consumer<List<MutablePair<E, Integer>>> addValues, boolean isLeft, Consumer<E> buttonAction) {
        // Create
        Cell<WTable> cell = this.table.add(theme.table()).top();
        WTable table = cell.widget();

        Consumer<E> forEach = t -> {
            if (!includeValue(t)) return;

            table.add(getValueWidget(t));

            WPressable button = table.add(isLeft ? theme.plus() : theme.minus()).expandCellX().right().widget();
            button.action = () -> buttonAction.accept(t);

            table.row();
        };

        // Sort
        List<MutablePair<E, Integer>> values = new ArrayList<>();
        addValues.accept(values);
        if (!filterText.isEmpty()) values.sort(Comparator.comparingInt(value -> -value.getRight()));
        for (MutablePair<E, Integer> pair : values) forEach.accept(pair.getLeft());

        if (!table.cells.isEmpty()) cell.expandX();

        return table;
    }

    protected boolean includeValue(E value) {
        return true;
    }

    protected abstract WWidget getValueWidget(E value);

    protected abstract String getValueName(E value);

    protected boolean skipValue(E value) {
        return false;
    }

    protected E getAdditionalValue(E value) {
        return null;
    }
}
