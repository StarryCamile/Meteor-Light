/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.renderer.text;

import meteordevelopment.meteorclient.renderer.*;
import meteordevelopment.meteorclient.renderer.text.Font;
import meteordevelopment.meteorclient.renderer.text.FontFace;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.BufferUtils;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class TTFFontRender {
    public static final Color SHADOW_COLOR = new Color(60, 60, 60, 180);

    private final Mesh mesh = new ShaderMesh(Shaders.TEXT, DrawMode.Triangles, Mesh.Attrib.Vec2, Mesh.Attrib.Vec2, Mesh.Attrib.Color);

    public final FontFace fontFace;

    private final Font[] fonts;
    private Font font;

    private boolean building;
    private boolean scaleOnly;
    private double scale = 1;

    public TTFFontRender(FontFace fontFace) {
        this.fontFace = fontFace;

        byte[] bytes = Utils.readBytes(fontFace.toStream());
        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length).put(bytes);

        fonts = new Font[5];
        for (int i = 0; i < fonts.length; i++) {
            ((Buffer) buffer).flip();
            fonts[i] = new Font(buffer, (int) Math.round(18 * ((i * 0.5) + 1)));
        }
    }


    public void setAlpha(double a) {
        mesh.alpha = a;
    }

    public void begin() {
        begin(1, false, false);
    }

    public void begin(double scale, boolean scaleOnly, boolean big) {
        if (building) throw new RuntimeException("TTFFontRender.begin() called twice");

        if (!scaleOnly) mesh.begin();

        if (big) {
            this.font = fonts[fonts.length - 1];
        }
        else {
            double scaleA = Math.floor(scale * 10) / 10;

            int scaleI;
            if (scaleA >= 3) scaleI = 5;
            else if (scaleA >= 2.5) scaleI = 4;
            else if (scaleA >= 2) scaleI = 3;
            else if (scaleA >= 1.5) scaleI = 2;
            else scaleI = 1;

            font = fonts[scaleI - 1];
        }

        this.building = true;
        this.scaleOnly = scaleOnly;

        double fontScale = font.getHeight() / 18.0;
        this.scale = 1 + (scale - fontScale) / fontScale;
    }

    public double getWidth(String text, int length, boolean shadow) {
        if (text.isEmpty()) return 0;

        Font font = building ? this.font : fonts[0];
        return (font.getWidth(text, length) + (shadow ? 1 : 0)) * scale + (shadow ? 1 : 0);
    }

    public double getWidth(String text, boolean shadow) {
            return getWidth(text,text.length(),shadow);
    }

    public double getWidth(String text, int length, boolean shadow,double fontScale) {
        double textWidth;
        Font font = building ? this.font : fonts[0];
        begin(fontScale,false,true);
        textWidth = getWidth(text,length,shadow);
        end();
        return textWidth;
    }

    public double getWidth(String text, boolean shadow,double fontScale) {
        return getWidth(text,text.length(),shadow,fontScale);
    }

    public double getHeight(boolean shadow) {
        Font font = building ? this.font : fonts[0];
        return (font.getHeight() + 1 + (shadow ? 1 : 0)) * scale;
    }

    public double getHeight(boolean shadow,double fontScale) {
        double textHeight;
        begin(fontScale,false,true);
        textHeight = getHeight(shadow);
        end();
        return textHeight;
    }

    public double render(String text, double x, double y, Color color, boolean shadow) {
        boolean wasBuilding = building;
        if (!wasBuilding) begin();

        double width;
        if (shadow) {
            int preShadowA = SHADOW_COLOR.a;
            SHADOW_COLOR.a = (int) (color.a / 255.0 * preShadowA);

            width = font.render(mesh, text, x + 1, y + 1, SHADOW_COLOR, scale);
            font.render(mesh, text, x, y, color, scale);

            SHADOW_COLOR.a = preShadowA;
        }
        else {
            width = font.render(mesh, text, x, y, color, scale);
        }

        if (!wasBuilding) end();
        return width;
    }

    public double render(String text, double x, double y, Color color, double fontScale) {
        boolean wasBuilding = building;
        if (!wasBuilding) begin(fontScale,false,false);

        double width;
        width = font.render(mesh, text, x, y, color, scale);

        if (!wasBuilding) end();
        return width;
    }

    public double render(String text, double x, double y, Color color, boolean shadow,double fontScale) {
        boolean wasBuilding = building;
        if (!wasBuilding) begin(fontScale,false,false);

        double width;
        if (shadow) {
            int preShadowA = SHADOW_COLOR.a;
            SHADOW_COLOR.a = (int) (color.a / 255.0 * preShadowA);

            width = font.render(mesh, text, x + 1, y + 1, SHADOW_COLOR, scale);
            font.render(mesh, text, x, y, color, scale);

            SHADOW_COLOR.a = preShadowA;
        }
        else {
            width = font.render(mesh, text, x, y, color, scale);
        }

        if (!wasBuilding) end();
        return width;
    }


    public boolean isBuilding() {
        return building;
    }

    public void end() {
        end(null);
    }

    public void end(MatrixStack matrices) {
        if (!building) throw new RuntimeException("TTFFontRender.end() called without calling begin()");

        if (!scaleOnly) {
            mesh.end();

            GL.bindTexture(font.texture.getGlId());
            mesh.render(matrices);
        }

        building = false;
        scale = 1;
    }
}
