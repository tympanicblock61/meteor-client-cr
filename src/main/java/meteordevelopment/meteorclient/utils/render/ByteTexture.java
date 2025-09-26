/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.render;

//import com.mojang.blaze3d.systems.RenderSystem;
//import net.minecraft.client.texture.AbstractTexture;

import java.nio.ByteBuffer;


import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.BufferUtils;

public class ByteTexture extends Texture {
    public ByteTexture(int width, int height, byte[] data, Format format, Filter filterMin, Filter filterMag) {
        super(createPixmap(width, height, data, format));
        setFilter(filterMin.toGdx(), filterMag.toGdx());
    }

    public ByteTexture(int width, int height, ByteBuffer buffer, Format format, Filter filterMin, Filter filterMag) {
        super(createPixmap(width, height, buffer, format));
        setFilter(filterMin.toGdx(), filterMag.toGdx());
    }

    private static Pixmap createPixmap(int width, int height, byte[] data, Format format) {
        ByteBuffer buffer = BufferUtils.newUnsafeByteBuffer(data.length);
        buffer.put(data).flip();
        return createPixmap(width, height, buffer, format);
    }

    private static Pixmap createPixmap(int width, int height, ByteBuffer buffer, Format format) {
        Pixmap pixmap = new Pixmap(width, height, format);
        ByteBuffer pixmapPixels = pixmap.getPixels();
        pixmapPixels.clear();
        pixmapPixels.put(buffer);
        pixmapPixels.flip();
        return pixmap;
    }

    public enum Filter {
        Nearest(TextureFilter.Nearest),
        Linear(TextureFilter.Linear);

        private final TextureFilter gdxFilter;

        Filter(TextureFilter gdxFilter) {
            this.gdxFilter = gdxFilter;
        }

        public TextureFilter toGdx() {
            return gdxFilter;
        }
    }
}
