/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixins;

import finalforeach.cosmicreach.chat.Chat;
import finalforeach.cosmicreach.gamestates.ChatMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.lang.reflect.Field;

@Mixin(ChatMenu.class)
public interface AccessorChatMenu {
    @Accessor("inputText")
    void setInputText(String inputText);

    @Accessor("ChatMenu$myChat")
    static Chat getMyChat() {
        throw new AssertionError();
    }
}
