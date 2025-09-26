/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient;

import com.badlogic.gdx.Gdx;
import com.github.puzzle.core.loader.launch.provider.mod.entrypoint.impls.ClientModInitializer;
import com.github.puzzle.core.loader.provider.mod.ModContainer;
import com.github.puzzle.core.loader.util.ModLocator;
import finalforeach.cosmicreach.ClientSingletons;
import finalforeach.cosmicreach.gamestates.ChatMenu;
import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.io.SaveLocation;
import finalforeach.cosmicreach.util.Identifier;
import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.hud.screens.HudEditorScreen;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.DiscordPresence;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.meteorclient.utils.ReflectInit;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Version;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.misc.input.KeyBinds;
import meteordevelopment.meteorclient.utils.network.OnlinePlayers;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import meteordevelopment.orbit.IEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;

public class MeteorClient implements ClientModInitializer {
    public static final String MOD_ID = "meteor-client";
    public static final ModContainer MOD_META;
    public static final String NAME;
    public static final Version VERSION;
    public static final String BUILD_NUMBER;

    public static MeteorClient INSTANCE;
    public static MeteorAddon ADDON;

    public static ClientSingletons client;
    public static final IEventBus EVENT_BUS = new EventBus();
    public static final File FOLDER = Gdx.files.absolute(SaveLocation.getSaveFolderLocation() + "/"+MOD_ID).file();
    public static final Logger LOG;

    static {
        MOD_META = ModLocator.locatedMods.get(MOD_ID);
        NAME = MOD_META.NAME;
        LOG = LoggerFactory.getLogger(NAME);

        String versionString = MOD_META.VERSION.toString();
        if (versionString.contains("-")) versionString = versionString.split("-")[0];

        // When building and running through IntelliJ and not Gradle it doesn't replace the version so just use a dummy
        if (versionString.equals("${version}")) versionString = "0.0.0";

        VERSION = new Version(versionString);
        BUILD_NUMBER = MOD_META.INFO.Metadata.get(MeteorClient.MOD_ID + ":build_number").asString();
    }

    @Override
    public void onInit() {
        if (INSTANCE == null) {
            INSTANCE = this;
            return;
        }

        LOG.info("Initializing {}", NAME);

        // Global client accessor
        client = ClientSingletons.get();

        // Pre-load
        if (!FOLDER.exists()) {
            FOLDER.getParentFile().mkdirs();
            FOLDER.mkdir();
            Systems.addPreLoadTask(() -> Modules.get().get(DiscordPresence.class).toggle());
        }

        // Register addons
        AddonManager.init();

        // Register event handlers
        AddonManager.ADDONS.forEach(addon -> {
            try {
                EVENT_BUS.registerLambdaFactory(addon.getPackage(), (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
            } catch (AbstractMethodError e) {
                throw new RuntimeException("Addon \"%s\" is too old and cannot be ran.".formatted(addon.name), e);
            }
        });

        // TODO remove
        Reflections.initReflections();

        // Register init classes
        ReflectInit.registerPackages();

        // Pre init
        ReflectInit.init(PreInit.class);

        // Register module categories
        Categories.init();

        // Load systems
        Systems.init();

        // Subscribe after systems are loaded
        EVENT_BUS.subscribe(this);

        // Initialise addons
        AddonManager.ADDONS.forEach(MeteorAddon::onInitialize);

        // Sort modules after addons have added their own
        Modules.get().sortModules();

        // Load configs
        Systems.load();

        // Post init
        ReflectInit.init(PostInit.class);

        // Save on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            OnlinePlayers.leave();
            Systems.save();
            GuiThemes.save();
        }));
    }

    @EventHandler
    private void onTick(TickEvent.Post event) throws Exception {
        if ((GameState.currentGameState == null || GameState.currentGameState instanceof InGame) && KeyBinds.OPEN_COMMANDS.isJustPressed()) {
            ChatMenu menu = new ChatMenu(true);
            Reflections.ChatMenu$inputText.set(menu, ".");
            GameState.currentGameState.switchAwayTo(menu);
        }
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        if (event.action == KeyAction.Press && KeyBinds.OPEN_GUI.getValue() == event.key) {
            toggleGui();
        }
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (event.action == KeyAction.Press && KeyBinds.OPEN_GUI.getValue() == event.button) {
            toggleGui();
        }
    }

    private void toggleGui() {
        if (Utils.canOpenGui()) Tabs.get().getFirst().openScreen(GuiThemes.get());
    }

    // Hide HUD

    private boolean wasWidgetScreen, wasHudHiddenRoot;

    @EventHandler(priority = EventPriority.LOWEST)
    private void onOpenScreen(OpenScreenEvent event) {
        if (event.screen instanceof WidgetScreen) {
            //if (!wasWidgetScreen) wasHudHiddenRoot = mc.options.hudHidden;
            if (GuiThemes.get().hideHUD() || wasHudHiddenRoot) {
                // Always show the MC HUD in the HUD editor screen since people like
                // to align some items with the hotbar or chat
                //mc.options.hudHidden = !(event.screen instanceof HudEditorScreen);
            }
        } else {
            //if (wasWidgetScreen) mc.options.hudHidden = wasHudHiddenRoot;
            //wasHudHiddenRoot = mc.options.hudHidden;
        }

        wasWidgetScreen = event.screen instanceof WidgetScreen;
    }

    public static Identifier identifier(String path) {
        return Identifier.of(MeteorClient.MOD_ID, path);
    }
}
