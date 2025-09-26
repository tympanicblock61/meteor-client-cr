/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils;

//import com.mojang.blaze3d.systems.ProjectionType;
//import com.mojang.blaze3d.systems.RenderSystem;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.github.puzzle.game.resources.PuzzleGameAssetLoader;
import com.github.puzzle.util.Vec3i;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.gamestates.*;
import finalforeach.cosmicreach.items.Item;
import finalforeach.cosmicreach.settings.ControlSettings;
import finalforeach.cosmicreach.settings.Keybind;
import it.unimi.dsi.fastutil.objects.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.entities.player.PlayerEntity;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.settings.GraphicsSettings;
import finalforeach.cosmicreach.world.Chunk;
import finalforeach.cosmicreach.world.Zone;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
//import meteordevelopment.meteorclient.mixin.*;
import meteordevelopment.meteorclient.mixininterface.IMinecraftClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.misc.Names;
import meteordevelopment.meteorclient.utils.player.EChestMemory;
import meteordevelopment.meteorclient.utils.render.PeekScreen;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockEntityIterator;
import meteordevelopment.meteorclient.utils.world.ChunkIterator;
import meteordevelopment.orbit.EventHandler;
//import net.minecraft.block.Block;
//import net.minecraft.block.Blocks;
//import net.minecraft.block.ShulkerBoxBlock;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.client.gui.screen.TitleScreen;
//import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
//import net.minecraft.client.gui.screen.world.SelectWorldScreen;
//import net.minecraft.client.resource.ResourceReloadLogger;
//import net.minecraft.component.ComponentMap;
//import net.minecraft.component.DataComponentTypes;
//import net.minecraft.component.type.ItemEnchantmentsComponent;
//import net.minecraft.component.type.NbtComponent;
//import net.minecraft.enchantment.Enchantment;
//import net.minecraft.enchantment.EnchantmentHelper;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.effect.StatusEffect;
//import net.minecraft.item.*;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtList;
//import net.minecraft.registry.RegistryKey;
//import net.minecraft.registry.entry.RegistryEntry;
//import net.minecraft.util.DyeColor;
//import net.minecraft.util.collection.DefaultedList;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.Vec3d;
//import net.minecraft.world.chunk.Chunk;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Range;
//import org.joml.Matrix4f;
//import org.joml.Vector3d;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.MeteorClient.client;
import static org.lwjgl.glfw.GLFW.*;

public class Utils {
    public static final Pattern FILE_NAME_INVALID_CHARS_PATTERN = Pattern.compile("[\\s\\\\/:*?\"<>|]");
    public static final Color WHITE = new Color(255, 255, 255);

    private static final Random random = new Random();
    public static boolean firstTimeTitleScreen = true;
    public static boolean isReleasingTrident;
    public static boolean rendering3D = true;
    public static double frameTime;
    public static GameState screenToOpen;

    private Utils() {
    }

    @PreInit
    public static void init() {
        MeteorClient.EVENT_BUS.subscribe(Utils.class);
    }

    @EventHandler
    private static void onTick(TickEvent.Post event) {
        if (screenToOpen != null) {
            GameState.switchToGameState(screenToOpen);
            //mc.setScreen(screenToOpen);
            screenToOpen = null;
        }
    }

    public static Vector3 getPlayerSpeed() {
        if (client.getLocalPlayer() == null) return Vector3.Zero;

        Player player = client.getLocalPlayer();
        PlayerEntity playerE = (PlayerEntity) player.getEntity();


        float tX = playerE.position.x - playerE.lastPosition.x;
        float tY = playerE.position.y - playerE.lastPosition.y;
        float tZ = playerE.position.z - playerE.lastPosition.z;

        Timer timer = Modules.get().get(Timer.class);
        if (timer.isActive()) {
            tX *= timer.getMultiplier();
            tY *= timer.getMultiplier();
            tZ *= timer.getMultiplier();
        }

        tX *= 20;
        tY *= 20;
        tZ *= 20;

        return new Vector3(tX, tY, tZ);
    }

    public static String getWorldTime() {
        if (GameSingletons.world == null) return "00:00";

        Zone playerZone = InGame.getLocalPlayer().getZone();
        float currentTimeSeconds = playerZone.getCurrentWorldTick() * 0.05F;
        float totalDaySeconds = 1920.0F;

        float dayProgress = (currentTimeSeconds % totalDaySeconds) / totalDaySeconds;
        int hours = (int) (dayProgress * 24);
        int minutes = (int) ((dayProgress * 1440) % 60);

        return String.format("%02d:%02d", hours, minutes);

//        int ticks = (int) (mc.world.getTimeOfDay() % 24000);
//        ticks += 6000;
//        if (ticks > 24000) ticks -= 24000;
//
//        return String.format("%02d:%02d", ticks / 1000, (int) (ticks % 1000 / 1000.0 * 60));
    }

    public static Iterable<Chunk> chunks(boolean onlyWithLoadedNeighbours) {
        return () -> new ChunkIterator(onlyWithLoadedNeighbours);
    }

    public static Iterable<Chunk> chunks() {
        return chunks(false);
    }

    public static Iterable<BlockEntity> blockEntities() {
        return BlockEntityIterator::new;
    }

    public static int getRenderDistance() {
        return GraphicsSettings.renderDistanceInChunks.getValue();
    }

    public static int getWindowWidth() {
        return Gdx.graphics.getBackBufferWidth();
    }

    public static int getWindowHeight() {
        return Gdx.graphics.getBackBufferHeight();
    }

    //TODO fix
//    public static void unscaledProjection() {
//        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), 0, 1000, 21000), ProjectionType.ORTHOGRAPHIC);
//        rendering3D = false;
//    }
//
//    public static void scaledProjection() {
//        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, (float) (mc.getWindow().getFramebufferWidth() / mc.getWindow().getScaleFactor()), (float) (mc.getWindow().getFramebufferHeight() / mc.getWindow().getScaleFactor()), 0, 1000, 21000), ProjectionType.PERSPECTIVE);
//        rendering3D = true;
//    }

    public static Vector3 vec3d(BlockPosition pos) {
        return new Vector3(pos.localX, pos.localY, pos.localZ);
    }

    public static boolean searchTextDefault(String text, String filter, boolean caseSensitive) {
        return searchInWords(text, filter) > 0 || searchLevenshteinDefault(text, filter, caseSensitive) < text.length() / 2;
    }

    public static int searchLevenshteinDefault(String text, String filter, boolean caseSensitive) {
        return levenshteinDistance(caseSensitive ? filter : filter.toLowerCase(Locale.ROOT), caseSensitive ? text : text.toLowerCase(Locale.ROOT), 1, 8, 8);
    }

    public static int searchInWords(String text, String filter) {
        if (filter.isEmpty()) return 1;

        int wordsFound = 0;
        text = text.toLowerCase(Locale.ROOT);
        String[] words = filter.toLowerCase(Locale.ROOT).split(" ");

        for (String word : words) {
            if (!text.contains(word)) return 0;
            wordsFound += StringUtils.countMatches(text, word);
        }

        return wordsFound;
    }

    public static int levenshteinDistance(String from, String to, int insCost, int subCost, int delCost) {
        int textLength = from.length();
        int filterLength = to.length();

        if (textLength == 0) return filterLength * insCost;
        if (filterLength == 0) return textLength * delCost;

        // Populate matrix
        int[][] d = new int[textLength + 1][filterLength + 1];

        for (int i = 0; i <= textLength; i++) {
            d[i][0] = i * delCost;
        }

        for (int j = 0; j <= filterLength; j++) {
            d[0][j] = j * insCost;
        }

        // Find best route
        for (int i = 1; i <= textLength; i++) {
            for (int j = 1; j <= filterLength; j++) {
                int sCost = d[i - 1][j - 1] + (from.charAt(i - 1) == to.charAt(j - 1) ? 0 : subCost);
                int dCost = d[i - 1][j] + delCost;
                int iCost = d[i][j - 1] + insCost;
                d[i][j] = Math.min(Math.min(dCost, iCost), sCost);
            }
        }

        return d[textLength][filterLength];
    }

    public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dX = x2 - x1;
        double dY = y2 - y1;
        double dZ = z2 - z1;
        return dX * dX + dY * dY + dZ * dZ;
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dX = x2 - x1;
        double dY = y2 - y1;
        double dZ = z2 - z1;
        return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    }

    public static String getFileWorldName() {
        return FILE_NAME_INVALID_CHARS_PATTERN.matcher(getWorldName()).replaceAll("_");
    }

    public static String getWorldName() {
        return GameSingletons.world.getDisplayName();
        // Singleplayer
//        if (mc.isInSingleplayer()) {
//            if (mc.world == null) return "";
//
//            File folder = ((MinecraftServerAccessor) mc.getServer()).getSession().getWorldDirectory(mc.world.getRegistryKey()).toFile();
//            if (folder.toPath().relativize(mc.runDirectory.toPath()).getNameCount() != 2) {
//                folder = folder.getParentFile();
//            }
//            return folder.getName();
//        }

        // Multiplayer
//        if (mc.getCurrentServerEntry() != null) {
//            return mc.getCurrentServerEntry().isRealm() ? "realms" : mc.getCurrentServerEntry().address;
//        }

        //return "";
    }

    public static String nameToTitle(String name) {
        return Arrays.stream(name.split("-")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    public static String titleToName(String title) {
        return title.replace(" ", "-").toLowerCase(Locale.ROOT);
    }

    public static String getKeyName(int key) {
        return switch (key) {
            case GLFW_KEY_UNKNOWN -> "Unknown";
            case GLFW_KEY_ESCAPE -> "Esc";
            case GLFW_KEY_GRAVE_ACCENT -> "Grave Accent";
            case GLFW_KEY_WORLD_1 -> "World 1";
            case GLFW_KEY_WORLD_2 -> "World 2";
            case GLFW_KEY_PRINT_SCREEN -> "Print Screen";
            case GLFW_KEY_PAUSE -> "Pause";
            case GLFW_KEY_INSERT -> "Insert";
            case GLFW_KEY_DELETE -> "Delete";
            case GLFW_KEY_HOME -> "Home";
            case GLFW_KEY_PAGE_UP -> "Page Up";
            case GLFW_KEY_PAGE_DOWN -> "Page Down";
            case GLFW_KEY_END -> "End";
            case GLFW_KEY_TAB -> "Tab";
            case GLFW_KEY_LEFT_CONTROL -> "Left Control";
            case GLFW_KEY_RIGHT_CONTROL -> "Right Control";
            case GLFW_KEY_LEFT_ALT -> "Left Alt";
            case GLFW_KEY_RIGHT_ALT -> "Right Alt";
            case GLFW_KEY_LEFT_SHIFT -> "Left Shift";
            case GLFW_KEY_RIGHT_SHIFT -> "Right Shift";
            case GLFW_KEY_UP -> "Arrow Up";
            case GLFW_KEY_DOWN -> "Arrow Down";
            case GLFW_KEY_LEFT -> "Arrow Left";
            case GLFW_KEY_RIGHT -> "Arrow Right";
            case GLFW_KEY_APOSTROPHE -> "Apostrophe";
            case GLFW_KEY_BACKSPACE -> "Backspace";
            case GLFW_KEY_CAPS_LOCK -> "Caps Lock";
            case GLFW_KEY_MENU -> "Menu";
            case GLFW_KEY_LEFT_SUPER -> "Left Super";
            case GLFW_KEY_RIGHT_SUPER -> "Right Super";
            case GLFW_KEY_ENTER -> "Enter";
            case GLFW_KEY_KP_ENTER -> "Numpad Enter";
            case GLFW_KEY_NUM_LOCK -> "Num Lock";
            case GLFW_KEY_SPACE -> "Space";
            case GLFW_KEY_F1 -> "F1";
            case GLFW_KEY_F2 -> "F2";
            case GLFW_KEY_F3 -> "F3";
            case GLFW_KEY_F4 -> "F4";
            case GLFW_KEY_F5 -> "F5";
            case GLFW_KEY_F6 -> "F6";
            case GLFW_KEY_F7 -> "F7";
            case GLFW_KEY_F8 -> "F8";
            case GLFW_KEY_F9 -> "F9";
            case GLFW_KEY_F10 -> "F10";
            case GLFW_KEY_F11 -> "F11";
            case GLFW_KEY_F12 -> "F12";
            case GLFW_KEY_F13 -> "F13";
            case GLFW_KEY_F14 -> "F14";
            case GLFW_KEY_F15 -> "F15";
            case GLFW_KEY_F16 -> "F16";
            case GLFW_KEY_F17 -> "F17";
            case GLFW_KEY_F18 -> "F18";
            case GLFW_KEY_F19 -> "F19";
            case GLFW_KEY_F20 -> "F20";
            case GLFW_KEY_F21 -> "F21";
            case GLFW_KEY_F22 -> "F22";
            case GLFW_KEY_F23 -> "F23";
            case GLFW_KEY_F24 -> "F24";
            case GLFW_KEY_F25 -> "F25";
            default -> {
                String keyName = glfwGetKeyName(key, 0);
                yield keyName == null ? "Unknown" : StringUtils.capitalize(keyName);
            }
        };
    }

    public static String getButtonName(int button) {
        return switch (button) {
            case -1 -> "Unknown";
            case 0 -> "Mouse Left";
            case 1 -> "Mouse Right";
            case 2 -> "Mouse Middle";
            default -> "Mouse " + button;
        };
    }

    public static byte[] readBytes(InputStream in) {
        try {
            return in.readAllBytes();
        } catch (IOException e) {
            MeteorClient.LOG.error("Error reading from stream.", e);
            return new byte[0];
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static boolean canUpdate() {
        return client.getLocalPlayer() != null && GameSingletons.world != null;
    }

    public static boolean canOpenGui() {
        if (canUpdate()) return GameState.currentGameState instanceof InGame;//mc.currentScreen == null;

        return GameState.currentGameState instanceof MainMenu || GameState.currentGameState instanceof NetworkMenu || GameState.currentGameState instanceof WorldSelectionMenu;
    }

    public static boolean canCloseGui() {
        return GameState.currentGameState instanceof TabScreen;
    }

    public static int random(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static double random(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    public static void attack() {
        if (GameState.currentGameState instanceof InGame && canUpdate()) {
            if (ControlSettings.keyAttackBreak.isMouseButton()) {
                sendMouseButton(0, 0, ControlSettings.keyAttackBreak.getValue());
            } else {
                sendKeyPress(ControlSettings.keyAttackBreak.getValue());
            }
        }
    }

    public static void sendMouseButton(int screenX, int screenY, int mouse) {
        Gdx.input.setCursorPosition(screenX, screenY);
        InputProcessor currentProcessor = Gdx.input.getInputProcessor();
        currentProcessor.touchDown(screenX, screenY, 0, mouse);
        currentProcessor.touchUp(screenX, screenY, 0, mouse);
    }

    public static void sendKeyPress(int key) {
        InputProcessor currentProcessor = Gdx.input.getInputProcessor();
        currentProcessor.keyDown(key);
        currentProcessor.keyUp(key);
        if (Keybind.isPrintableChar((char) key)) {
            currentProcessor.keyTyped((char) key);
        }
    }

    public static void rightClick() {
        if (GameState.currentGameState instanceof InGame && canUpdate()) {
            sendMouseButton(0, 0, Input.Buttons.RIGHT);
        }
        // TODO not sure about this
        //((IMinecraftClient) mc).meteor$rightClick();
    }

    public static Color lerp(Color first, Color second, @Range(from = 0, to = 1) float v) {
        return new Color(
            (int) (first.r * (1 - v) + second.r * v),
            (int) (first.g * (1 - v) + second.g * v),
            (int) (first.b * (1 - v) + second.b * v)
        );
    }

    public static boolean isLoading() {
        return client.getLocalPlayer() != null && client.getLocalPlayer().isLoading() || client.getLocalPlayer() == null && GameSingletons.world == null;
    }

    public static int parsePort(String full) {
        if (full == null || full.isBlank() || !full.contains(":")) return -1;

        int port;

        try {
            port = Integer.parseInt(full.substring(full.lastIndexOf(':') + 1, full.length() - 1));
        } catch (NumberFormatException ignored) {
            port = -1;
        }

        return port;
    }

    public static String parseAddress(String full) {
        if (full == null || full.isBlank() || !full.contains(":")) return full;
        return full.substring(0, full.lastIndexOf(':'));
    }

    public static boolean resolveAddress(String address) {
        if (address == null || address.isBlank()) return false;

        int port = parsePort(address);
        if (port == -1) port = 25565;
        else address = parseAddress(address);

        return resolveAddress(address, port);
    }

    public static boolean resolveAddress(String address, int port) {
        if (port <= 0 || port > 65535 || address == null || address.isBlank()) return false;
        InetSocketAddress socketAddress = new InetSocketAddress(address, port);
        return !socketAddress.isUnresolved();
    }

    public static Vector3 set(Vector3 vec, Vec3i v) {
        vec.x = v.x();
        vec.y = v.y();
        vec.z = v.z();

        return vec;
    }

    public static Vector3 set(Vector3 vec, Entity entity, float tickDelta) {
        vec.x = MathUtils.lerp(tickDelta, entity.lastPosition.x, entity.position.x);
        vec.y = MathUtils.lerp(tickDelta, entity.lastPosition.y, entity.position.y);
        vec.z = MathUtils.lerp(tickDelta, entity.lastPosition.z, entity.position.z);
        return vec;
    }

    // Filters

    public static boolean nameFilter(String text, char character) {
        return (character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z') || (character >= '0' && character <= '9') || character == '_' || character == '-' || character == '.' || character == ' ';
    }

    public static boolean ipFilter(String text, char character) {
        if (text.contains(":") && character == ':') return false;
        return (character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z') || (character >= '0' && character <= '9') || character == '.' || character == '-';
    }
}
