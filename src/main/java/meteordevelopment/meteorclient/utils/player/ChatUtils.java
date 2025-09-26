/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.player;

import com.badlogic.gdx.math.Vector3;
import com.github.puzzle.util.MutablePair;
import finalforeach.cosmicreach.ClientSingletons;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.accounts.AccountOffline;
import finalforeach.cosmicreach.chat.Chat;
import finalforeach.cosmicreach.gamestates.ChatMenu;
import finalforeach.cosmicreach.networking.client.ChatSender;
import meteordevelopment.meteorclient.mixins.AccessorChatMenu;
import meteordevelopment.meteorclient.utils.PostInit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

//import static meteordevelopment.meteorclient.MeteorClient.client;
//import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ChatUtils {
    private static final AccountOffline MeteorAccount = new AccountOffline();
    private static final List<MutablePair<String, Supplier<String>>> customPrefixes = new ArrayList<>();
    private static String forcedPrefixClassName;
    private static String PREFIX;

    static {
        MeteorAccount.setDisplayName("Meteor");
    }

    private ChatUtils() {
    }

    @PostInit
    public static void init() {
        PREFIX = "["+"Meteor"+"] ";
    }

    public static String getMeteorPrefix() {
        return PREFIX;
    }

    /**
     * Registers a custom prefix to be used when calling from a class in the specified package. When null is returned from the supplier the default Meteor prefix is used.
     */
    public static void registerCustomPrefix(String packageName, Supplier<String> supplier) {
        for (MutablePair<String, Supplier<String>> pair : customPrefixes) {
            if (pair.getLeft().equals(packageName)) {
                pair.b = supplier;
                return;
            }
        }

        customPrefixes.add(new MutablePair<>(packageName, supplier));
    }

    /**
     * The package name must match exactly to the one provided through {@link #registerCustomPrefix(String, Supplier)}.
     */
    public static void unregisterCustomPrefix(String packageName) {
        customPrefixes.removeIf(pair -> pair.getLeft().equals(packageName));
    }

    public static void forceNextPrefixClass(Class<?> klass) {
        forcedPrefixClassName = klass.getName();
    }

    // Player

    /**
     * Sends the message as if the user typed it into chat.
     */
    public static void sendPlayerMsg(String message) {
        ChatSender.sendMessageOrCommand(Chat.MAIN_CLIENT_CHAT, ClientSingletons.ACCOUNT, message);
        AccessorChatMenu.getMyChat().addMessage(ClientSingletons.ACCOUNT, message);

        //mc.inGameHud.getChatHud().addToMessageHistory(message);
        //if (message.startsWith("/")) mc.player.networkHandler.sendChatCommand(message.substring(1));
        //else mc.player.networkHandler.sendChatMessage(message);
    }

    // Default

    public static void info(String message) {
        sendMsg(null, message);
    }

    public static void infoPrefix(String prefix, String message) {
        sendMsg(prefix, message);
    }

    // Warning

    public static void warning(String message, Object... args) {
        sendMsg(null, message);
    }

    public static void warningPrefix(String prefix, String message) {
        sendMsg(prefix, message);
    }

    // Error

    public static void error(String message) {
        sendMsg(null, message);
    }

    public static void errorPrefix(String prefix, String message) {
        sendMsg(prefix, message);
    }

    // Misc

    public static void sendMsg(@Nullable String prefixTitle, String msg) {
        if (GameSingletons.world == null) return;

        StringBuilder message = new StringBuilder(msg);
        message.append(getPrefix());
        if (prefixTitle != null) message.append(getCustomPrefix(prefixTitle));
        message.append(msg);

//        if (!Config.get().deleteChatFeedback.get()) id = 0;

//        ((IChatHud) mc.inGameHud.getChatHud()).meteor$add(message, id);


        AccessorChatMenu.getMyChat().addMessage(MeteorAccount, message.toString());
    }

    private static String getCustomPrefix(String prefixTitle) {
        String prefix = "";
//        prefix.setStyle(prefix.getStyle().withFormatting(Formatting.GRAY));

        prefix+=("[");

//        MutableText moduleTitle = Text.literal(prefixTitle);
//        moduleTitle.setStyle(moduleTitle.getStyle().withFormatting(prefixColor));
        prefix+=(prefixTitle);

        prefix+=("] ");

        return prefix;
    }

    private static String getPrefix() {
        if (customPrefixes.isEmpty()) {
            forcedPrefixClassName = null;
            return PREFIX;
        }

        boolean foundChatUtils = false;
        String className = null;

        if (forcedPrefixClassName != null) {
            className = forcedPrefixClassName;
            forcedPrefixClassName = null;
        } else {
            for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
                if (foundChatUtils) {
                    if (!element.getClassName().equals(ChatUtils.class.getName())) {
                        className = element.getClassName();
                        break;
                    }
                } else {
                    if (element.getClassName().equals(ChatUtils.class.getName())) foundChatUtils = true;
                }
            }
        }

        if (className == null) return PREFIX;

        for (MutablePair<String, Supplier<String>> pair : customPrefixes) {
            if (className.startsWith(pair.getLeft())) {
                String prefix = pair.getRight().get();
                return prefix != null ? prefix : PREFIX;
            }
        }

        return PREFIX;
    }



    public static String formatCoords(Vector3 pos) {
        return String.format("%.0f, %.0f, %.0f", pos.x, pos.y, pos.z);
    }
}
