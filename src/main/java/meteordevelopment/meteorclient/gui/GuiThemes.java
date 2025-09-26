/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui;

import com.github.puzzle.game.items.data.DataTag;
import com.github.puzzle.game.items.data.DataTagManifest;
import com.github.puzzle.game.items.data.attributes.StringDataAttribute;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.meteorclient.utils.files.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GuiThemes {
    private static final File FOLDER = new File(MeteorClient.FOLDER, "gui");
    private static final File THEMES_FOLDER = new File(FOLDER, "themes");
    private static final File FILE = new File(FOLDER, "gui.nbt");

    private static final List<GuiTheme> themes = new ArrayList<>();
    private static GuiTheme theme;

    private GuiThemes() {
    }

    @PreInit
    public static void init() {
        add(new MeteorGuiTheme());
    }

    @PostInit
    public static void postInit() {
        if (FILE.exists()) {
            try {
                CRBinDeserializer cr = CRBinDeserializer.getNew();
                FileInputStream fis = new FileInputStream(FILE);
                FileChannel fileChannel = fis.getChannel();
                long fileSize = fileChannel.size();
                ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
                fileChannel.read(buffer);
                buffer.flip();
                cr.prepareForRead(buffer);
                DataTagManifest tag = new DataTagManifest();
                tag.read(cr);
                select(tag.getTag("currentTheme").getTagAsType(String.class).getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (theme == null) select("Meteor");
    }

    public static void add(GuiTheme theme) {
        for (Iterator<GuiTheme> it = themes.iterator(); it.hasNext();) {
            if (it.next().name.equals(theme.name)) {
                it.remove();

                MeteorClient.LOG.error("Theme with the name '{}' has already been added.", theme.name);
                break;
            }
        }

        themes.add(theme);
    }

    public static void select(String name) {
        // Find theme with the provided name
        GuiTheme theme = null;

        for (GuiTheme t : themes) {
            if (t.name.equals(name)) {
                theme = t;
                break;
            }
        }

        if (theme != null) {
            // Save current theme
            saveTheme();

            // Select new theme
            GuiThemes.theme = theme;

            // Load new theme
            try {
                File file = new File(THEMES_FOLDER, get().name + ".nbt");

                if (file.exists()) {
                    CRBinDeserializer cr = CRBinDeserializer.getNew();
                    FileInputStream fis = new FileInputStream(file);
                    FileChannel fileChannel = fis.getChannel();
                    long fileSize = fileChannel.size();
                    ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
                    fileChannel.read(buffer);
                    buffer.flip();
                    cr.prepareForRead(buffer);
                    DataTagManifest tag = new DataTagManifest();
                    tag.read(cr);
                    get().fromTag(tag);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Save global gui settings with the new theme
            saveGlobal();
        }
    }

    public static GuiTheme get() {
        return theme;
    }

    public static String[] getNames() {
        String[] names = new String[themes.size()];

        for (int i = 0; i < themes.size(); i++) {
            names[i] = themes.get(i).name;
        }

        return names;
    }

    // Saving

    private static void saveTheme() {
        if (get() != null) {
            try {
                DataTagManifest tag = get().toTag();
                THEMES_FOLDER.mkdirs();
                File file = new File(THEMES_FOLDER, get().name + ".nbt");
                CRBinSerializer cr = CRBinSerializer.getNew();
                tag.write(cr);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(cr.toBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveGlobal() {
        try {
            DataTagManifest tag = new DataTagManifest();
            tag.addTag(new DataTag<>("currentTheme", new StringDataAttribute(get().name)));

            FOLDER.mkdirs();
            CRBinSerializer cr = CRBinSerializer.getNew();
            tag.write(cr);
            FileOutputStream fos = new FileOutputStream(FILE);
            fos.write(cr.toBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        saveTheme();
        saveGlobal();
    }
}
