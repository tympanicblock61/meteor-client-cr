/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc.input;

import finalforeach.cosmicreach.settings.Keybind;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class KeyBinds {
    //private static final String CATEGORY = "Meteor Client";
    public static Keybind OPEN_GUI = Keybind.fromDefaultKey("key.meteor-client.open-gui", GLFW.GLFW_KEY_RIGHT_SHIFT);
    public static Keybind OPEN_COMMANDS = Keybind.fromDefaultKey("key.meteor-client.open-commands", GLFW.GLFW_KEY_PERIOD);

    private KeyBinds() {
    }

//    public static Keybind[] apply(Keybind[] binds) {
//        // Add category
//        Map<String, Integer> categories = KeyBindingAccessor.getCategoryOrderMap();
//
//        int highest = 0;
//        for (int i : categories.values()) {
//            if (i > highest) highest = i;
//        }
//
//        categories.put(CATEGORY, highest + 1);
//
//        // Add key binding
//        KeyBinding[] newBinds = new KeyBinding[binds.length + 2];
//
//        System.arraycopy(binds, 0, newBinds, 0, binds.length);
//        newBinds[binds.length] = OPEN_GUI;
//        newBinds[binds.length + 1] = OPEN_COMMANDS;
//
//        return newBinds;
//    }
}
