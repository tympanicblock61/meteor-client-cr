/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.game;

import finalforeach.cosmicreach.accounts.Account;
import meteordevelopment.meteorclient.events.Cancellable;
//import net.minecraft.client.gui.hud.MessageIndicator;
//import net.minecraft.text.Text;

public class ReceiveMessageEvent extends Cancellable {
    private static final ReceiveMessageEvent INSTANCE = new ReceiveMessageEvent();

    private String message;
    private Account indicator;
    private boolean modified;
    public int id;

    public static ReceiveMessageEvent get(String message, Account indicator, int id) {
        INSTANCE.setCancelled(false);
        INSTANCE.message = message;
        INSTANCE.indicator = indicator;
        INSTANCE.modified = false;
        INSTANCE.id = id;
        return INSTANCE;
    }

    public String getMessage() {
        return message;
    }

    public Account getIndicator() {
        return indicator;
    }

    public void setMessage(String message) {
        this.message = message;
        this.modified = true;
    }

    public void setIndicator(Account indicator) {
        this.indicator = indicator;
        this.modified = true;
    }

    public boolean isModified() {
        return modified;
    }
}
