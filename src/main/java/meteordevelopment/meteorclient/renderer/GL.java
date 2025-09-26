/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.renderer;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;
import com.github.puzzle.game.resources.PuzzleGameAssetLoader;
import finalforeach.cosmicreach.util.Identifier;
import meteordevelopment.meteorclient.mixininterface.ICapabilityTracker;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GL {
    private static final FloatBuffer MAT = BufferUtils.createFloatBuffer(4 * 4);

    private static final ICapabilityTracker DEPTH = getTracker("DEPTH");
    private static final ICapabilityTracker BLEND = getTracker("BLEND");
    private static final ICapabilityTracker CULL = getTracker("CULL");
    private static final ICapabilityTracker SCISSOR = getTracker("SCISSOR");

    private static boolean depthSaved, blendSaved, cullSaved, scissorSaved;

    public static int CURRENT_IBO;
    private static int prevIbo;

    private GL() {
    }

    // Generation

    public static int genVertexArray() {
        int[] vaoHandle = new int[1];
        Gdx.gl30.glGenVertexArrays(1, vaoHandle, 0);
        return vaoHandle[0];
    }

    public static int genBuffer() {
        return Gdx.gl32.glGenBuffer();
        //return GlStateManager._glGenBuffers();
    }

    public static int genTexture() {
        return Gdx.gl32.glGenTexture();
        //return GlStateManager._genTexture();
    }

    public static int genFramebuffer() {
        return Gdx.gl32.glGenFramebuffer();
        //return GlStateManager.glGenFramebuffers();
    }

    // Deletion

    public static void deleteBuffer(int buffer) {
        Gdx.gl32.glDeleteBuffer(buffer);
        //GlStateManager._glDeleteBuffers(buffer);
    }

    public static void deleteVertexArray(int vao) {
        Gdx.gl30.glDeleteVertexArrays(1, new int[]{vao}, 0);
        //GlStateManager._glDeleteVertexArrays(vao);
    }

    public static void deleteShader(int shader) {
        Gdx.gl32.glDeleteShader(shader);
        //GlStateManager.glDeleteShader(shader);
    }

    public static void deleteTexture(int id) {
        Gdx.gl32.glDeleteTexture(id);
        //GlStateManager._deleteTexture(id);
    }

    public static void deleteFramebuffer(int fbo) {
        Gdx.gl32.glDeleteFramebuffer(fbo);
        //GlStateManager._glDeleteFramebuffers(fbo);
    }

    public static void deleteProgram(int program) {
        Gdx.gl32.glDeleteProgram(program);
        //GlStateManager.glDeleteProgram(program);
    }

    // Binding

    public static void bindVertexArray(int vao) {
        Gdx.gl30.glBindVertexArray(vao);
        //GlStateManager._glBindVertexArray(vao);
        //BufferRendererAccessor.setCurrentVertexBuffer(null);
    }

    public static void bindVertexBuffer(int vbo) {
        Gdx.gl30.glBindVertexArray(vbo);
        //GlStateManager._glBindBuffer(GL_ARRAY_BUFFER, vbo);
    }

    public static void bindIndexBuffer(int ibo) {
        if (ibo != 0) prevIbo = CURRENT_IBO;
        Gdx.gl32.glBindBuffer(GL32.GL_ELEMENT_ARRAY_BUFFER, ibo != 0 ? ibo : prevIbo);
        //GlStateManager._glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo != 0 ? ibo : prevIbo);
    }

    public static void bindFramebuffer(int fbo) {
        Gdx.gl32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, fbo);
        //GlStateManager._glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    }

    // Buffers

    public static void bufferData(int target, ByteBuffer data, int usage) {
        Gdx.gl32.glBufferData(target, data.remaining(), data, usage);
        //GlStateManager._glBufferData(target, data, usage);
    }

    public static void drawElements(int mode, int first, int type) {
        Gdx.gl32.glDrawElements(mode, first, type, 0);
        //GlStateManager._drawElements(mode, first, type, 0);
    }

    // Vertex attributes

    public static void enableVertexAttribute(int i) {
        Gdx.gl32.glEnableVertexAttribArray(i);
        //GlStateManager._enableVertexAttribArray(i);
    }

    public static void vertexAttribute(int index, int size, int type, boolean normalized, int stride, long pointer) {
        Gdx.gl32.glVertexAttribPointer(index, size, type, normalized, stride, (int) pointer);
        //GlStateManager._vertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    // Shaders

    public static int createShader(int type) {
        return Gdx.gl32.glCreateShader(type);
        //return GlStateManager.glCreateShader(type);
    }

    public static void shaderSource(int shader, String source) {
        Gdx.gl32.glShaderSource(shader, source);
        //GlStateManager.glShaderSource(shader, source);
    }

    public static String compileShader(int shader) {
        Gdx.gl32.glCompileShader(shader);
        //GlStateManager.glCompileShader(shader);


        if (shaderStatus(shader) == GL32.GL_FALSE) {
            return Gdx.gl32.glGetShaderInfoLog(shader).substring(0, 512);
        }
//        if (GlStateManager.glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
//            return GlStateManager.glGetShaderInfoLog(shader, 512);
//        }

        return null;
    }

    public static int createProgram() {
        return Gdx.gl32.glCreateProgram();
        //return GlStateManager.glCreateProgram();
    }

    public static String linkProgram(int program, int vertShader, int fragShader) {
        Gdx.gl32.glAttachShader(program, vertShader);
        //GlStateManager.glAttachShader(program, vertShader);
        Gdx.gl32.glAttachShader(program, fragShader);
        //GlStateManager.glAttachShader(program, fragShader);
        Gdx.gl32.glLinkProgram(program);
        //GlStateManager.glLinkProgram(program);

        if (programStatus(program) == GL32.GL_FALSE) {
            return Gdx.gl32.glGetShaderInfoLog(program).substring(0,512);
        }
        //if (GlStateManager.glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
        //    return GlStateManager.glGetProgramInfoLog(program, 512);
        //}

        return null;
    }

    public static void useProgram(int program) {
        Gdx.gl32.glUseProgram(program);
        //GlStateManager._glUseProgram(program);
    }

    public static void viewport(int x, int y, int width, int height) {
        Gdx.gl32.glViewport(x, y, width, height);
        //GlStateManager._viewport(x, y, width, height);
    }

    // Uniforms

    public static int getUniformLocation(int program, String name) {
        return Gdx.gl32.glGetUniformLocation(program, name);
        //return GlStateManager._glGetUniformLocation(program, name);
    }

    public static void uniformInt(int location, int v) {
        Gdx.gl32.glUniform1i(location, v);
        //GlStateManager._glUniform1i(location, v);
    }

    public static void uniformFloat(int location, float v) {
        Gdx.gl32.glUniform1f(location, v);
        //glUniform1f(location, v);
    }

    public static void uniformFloat2(int location, float v1, float v2) {
        Gdx.gl32.glUniform2f(location, v1, v2);
        //glUniform2f(location, v1, v2);
    }

    public static void uniformFloat3(int location, float v1, float v2, float v3) {
        Gdx.gl32.glUniform3f(location, v1, v2, v3);
        //glUniform3f(location, v1, v2, v3);
    }

    public static void uniformFloat4(int location, float v1, float v2, float v3, float v4) {
        Gdx.gl32.glUniform4f(location, v1, v2, v3, v4);
        //glUniform4f(location, v1, v2, v3, v4);
    }

    public static void uniformFloat3Array(int location, float[] v) {
        Gdx.gl32.glUniform3fv(location, v.length / 3, v, 0);
        //glUniform3fv(location, v);
    }

    // TODO not so sure about this
    public static void uniformMatrix(int location, Matrix4 v) {
        MAT.put(v.val).flip();
        Gdx.gl32.glUniformMatrix4fv(location, 1, false, MAT);
        //v.get(MAT);
        //GlStateManager._glUniformMatrix4(location, false, MAT);
    }

    // Textures

    public static void pixelStore(int name, int param) {
        Gdx.gl32.glPixelStorei(name, param);
        //GlStateManager._pixelStore(name, param);
    }

    public static void textureParam(int target, int name, int param) {
        Gdx.gl32.glTexParameteri(target, name, param);
        //GlStateManager._texParameter(target, name, param);
    }

    public static void textureImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        Gdx.gl32.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
        //glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }

    public static void defaultPixelStore() {
        pixelStore(GL11.GL_UNPACK_SWAP_BYTES, GL32.GL_FALSE);
        pixelStore(GL11.GL_UNPACK_LSB_FIRST, GL32.GL_FALSE);
        pixelStore(GL11.GL_UNPACK_ROW_LENGTH, 0);
        pixelStore(GL30.GL_UNPACK_IMAGE_HEIGHT, 0);
        pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
        pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        pixelStore(GL30.GL_UNPACK_SKIP_IMAGES, 0);
        pixelStore(GL11.GL_UNPACK_ALIGNMENT, 4);
    }

    public static void generateMipmap(int target) {
        Gdx.gl32.glGenerateMipmap(target);
        //glGenerateMipmap(target);
    }

    // Framebuffers

    public static void framebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
        Gdx.gl32.glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
        //GlStateManager._glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
    }

    public static void clear(int mask) {
        Gdx.gl32.glClearColor(0, 0, 0, 1);
        Gdx.gl32.glClear(mask);
        //GlStateManager._clearColor(0, 0, 0, 1);
        //GlStateManager._clear(mask);
    }

    // State

    public static void saveState() {
        depthSaved = DEPTH.meteor$get();
        blendSaved = BLEND.meteor$get();
        cullSaved = CULL.meteor$get();
        scissorSaved = SCISSOR.meteor$get();
    }

    public static void restoreState() {
        DEPTH.meteor$set(depthSaved);
        BLEND.meteor$set(blendSaved);
        CULL.meteor$set(cullSaved);
        SCISSOR.meteor$set(scissorSaved);

        disableLineSmooth();
    }

    public static void enableDepth() {
        DEPTH.meteor$set(true);
        //GlStateManager._enableDepthTest();
    }
    public static void disableDepth() {
        DEPTH.meteor$set(false);
        //GlStateManager._disableDepthTest();
    }

    public static void enableBlend() {
        BLEND.meteor$set(true);
        Gdx.gl32.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        //GlStateManager._enableBlend();
        //GlStateManager._blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
    public static void disableBlend() {
        BLEND.meteor$set(false);
        //GlStateManager._disableBlend();
    }

    public static void enableCull() {
        CULL.meteor$set(true);
        //GlStateManager._enableCull();
    }
    public static void disableCull() {
        CULL.meteor$set(false);
        //GlStateManager._disableCull();
    }

    public static void enableScissorTest() {
        SCISSOR.meteor$set(true);
        //GlStateManager._enableScissorTest();
    }
    public static void disableScissorTest() {
        SCISSOR.meteor$set(false);
        //GlStateManager._disableScissorTest();
    }

    public static void enableLineSmooth() {
        Gdx.gl32.glEnable(GL11.GL_LINE_SMOOTH);
        Gdx.gl32.glLineWidth(1);
        //glEnable(GL_LINE_SMOOTH);
        //glLineWidth(1);
    }
    public static void disableLineSmooth() {
        Gdx.gl32.glDisable(GL11.GL_LINE_SMOOTH);
        //glDisable(GL_LINE_SMOOTH);
    }

    public static void bindTexture(Identifier id) {
        Texture texture = PuzzleGameAssetLoader.LOADER.getResource(id, Texture.class);
        bindTexture(texture.getTextureObjectHandle(), 0);

        //AbstractTexture texture = mc.getTextureManager().getTexture(id);
        //bindTexture(texture.getGlId(), 0);
    }

    public static void bindTexture(int i, int slot) {
        Gdx.gl32.glActiveTexture(GL32.GL_TEXTURE0+slot);
        Gdx.gl32.glBindTexture(GL32.GL_TEXTURE0+slot, i);
        //GlStateManager._activeTexture(GL_TEXTURE0 + slot);
        //GlStateManager._bindTexture(i);
    }
    public static void bindTexture(int i) {
        bindTexture(i, 0);
    }

    public static void resetTextureSlot() {
        Gdx.gl32.glActiveTexture(GL32.GL_TEXTURE0);
        //GlStateManager._activeTexture(GL_TEXTURE0);
    }

    private static ICapabilityTracker getTracker(String fieldName) {
        try {
            if (fieldName.equals("BLEND") || fieldName.equals("DEPTH") || fieldName.equals("CULL") || fieldName.equals("SCISSOR")) {
                return new ICapabilityTracker() {
                    private boolean isEnabled = false;
                    @Override
                    public boolean meteor$get() {
                        return isEnabled;
                    }
                    @Override
                    public void meteor$set(boolean state) {
                        GL32 gl = Gdx.gl32;
                        int type = switch (fieldName) {
                            case "BLEND" -> GL32.GL_BLEND;
                            case "DEPTH" -> GL32.GL_DEPTH_TEST;
                            case "CULL" -> GL32.GL_CULL_FACE;
                            case "SCISSOR" -> GL32.GL_SCISSOR_TEST;
                            default -> throw new IllegalStateException("Unexpected value: " + fieldName);
                        };
                        if (state) {
                            gl.glEnable(type);
                            isEnabled = true;
                        } else {
                            gl.glDisable(type);
                            isEnabled = false;
                        }
                    }
                };
            }
            throw new IllegalStateException("Unknown field name: " + fieldName);
        } catch (Exception e) {
            throw new IllegalStateException("Could not handle GL state for '" + fieldName + "'", e);
        }
    }

    public static int shaderStatus(int shader) {
        IntBuffer statusBuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        Gdx.gl32.glGetShaderiv(shader, GL32.GL_COMPILE_STATUS, statusBuffer);
        return statusBuffer.get(0);
    }

    public static int programStatus(int program) {
        IntBuffer statusBuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        Gdx.gl32.glGetProgramiv(program, GL32.GL_LINK_STATUS, statusBuffer);
        return statusBuffer.get(0);
    }
}
