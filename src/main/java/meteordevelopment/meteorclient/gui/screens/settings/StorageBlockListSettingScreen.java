/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.screens.settings;

import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.blockentities.BlockEntityFurnace;
import finalforeach.cosmicreach.blockentities.BlockEntityItemContainer;
import finalforeach.cosmicreach.items.Item;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.util.Identifier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.StorageBlockListSetting;
//import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.item.Item;
//import net.minecraft.item.Items;

import java.util.List;
import java.util.Map;

public class StorageBlockListSettingScreen extends RegistryListSettingScreen<Identifier> {
    private static final Map<Identifier, BlockEntityTypeInfo> BLOCK_ENTITY_TYPE_INFO_MAP = new Object2ObjectOpenHashMap<>();
    private static final BlockEntityTypeInfo UNKNOWN = new BlockEntityTypeInfo(Item.getItem("base:air"), "Unknown");

    static {
        // Map of storage blocks
        // TODO check if item is correct
        BLOCK_ENTITY_TYPE_INFO_MAP.put(Identifier.of(BlockEntityFurnace.BLOCK_ENTITY_ID), new BlockEntityTypeInfo(Item.getItem(BlockEntityFurnace.BLOCK_ENTITY_ID), "Furnace"));
        //BLOCK_ENTITY_TYPE_INFO_MAP.put(Identifier.of(BlockEntityItemContainer.BLOCK_ENTITY_ID), new BlockEntityTypeInfo(Item.getItem(Item), "Furnace"));
    }

    public StorageBlockListSettingScreen(GuiTheme theme, Setting<List<Identifier>> setting) {
        super(theme, "Select Storage Blocks", setting, setting.get(), StorageBlockListSetting.REGISTRY);
    }

    @Override
    protected WWidget getValueWidget(Identifier value) {
        Item item = BLOCK_ENTITY_TYPE_INFO_MAP.getOrDefault(value, UNKNOWN).item();
        return theme.itemWithLabel(new ItemStack(item), getValueName(value));
    }

    @Override
    protected String getValueName(Identifier value) {
        return BLOCK_ENTITY_TYPE_INFO_MAP.getOrDefault(value, UNKNOWN).name();
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    private record BlockEntityTypeInfo(Item item, String name) {}
}
