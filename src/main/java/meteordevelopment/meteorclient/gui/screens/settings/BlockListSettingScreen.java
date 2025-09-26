/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.screens.settings;

import com.github.puzzle.core.registries.GenericRegistry;
import com.github.puzzle.core.registries.IRegistry;
import com.github.puzzle.game.PuzzleRegistries;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.util.Identifier;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.misc.Names;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class BlockListSettingScreen extends RegistryListSettingScreen<Block> {
    private static final IRegistry<Block> BLOCKS = new GenericRegistry<>(Identifier.of("puzzle-loader", "blocks"));

    static {
        PuzzleRegistries.BLOCKS.names().forEach((name) -> BLOCKS.store(name,(Block) PuzzleRegistries.BLOCKS.get(name)));
    }


    public BlockListSettingScreen(GuiTheme theme, Setting<List<Block>> setting) {
        super(theme, "Select Blocks", setting, setting.get(), BLOCKS);
    }

    @Override
    protected boolean includeValue(Block value) {
        Predicate<Block> filter = ((BlockListSetting) setting).filter;

        if (filter == null) return value != Block.AIR;
        return filter.test(value);
    }

    @Override
    protected WWidget getValueWidget(Block value) {
        return theme.itemWithLabel(new ItemStack(value.getDefaultBlockState().getItem()), getValueName(value));
    }

    @Override
    protected String getValueName(Block value) {
        return Names.get(value);
    }


    private static Identifier getId(Block block) {
        Set<Identifier> ids = PuzzleRegistries.BLOCKS.names();
        for (Identifier id : ids) {
            if (PuzzleRegistries.BLOCKS.get(id).getIdentifier().toString() == block.getStringId()) {
                return id;
            }
        }
        return Identifier.of("UNKNOWN");
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
