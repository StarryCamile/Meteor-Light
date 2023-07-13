package qwq.wumie.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

import java.awt.Color;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {
    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static void resetColor() {
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    public static void enableRender2D() {
            GL11.glEnable(3042);
            GL11.glDisable(2884);
            GL11.glDisable(3553);
            GL11.glEnable(2848);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.0F);
    }

    public static void disableRender2D() {
            GL11.glDisable(3042);
            GL11.glEnable(2884);
            GL11.glEnable(3553);
            GL11.glDisable(2848);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glShadeModel(7424);
            RenderSystem.disableBlend();
            enableTexture();
    }

    public static void enableTexture() {
        GL11C.glEnable(GL11C.GL_TEXTURE_2D);
    }

    public static void disableTexture() {
        GL11C.glDisable(GL11C.GL_TEXTURE_2D);
    }

    public static void color(int color) {
        float f = (float) (color >> 24 & 255) / 255.0f;
        float f1 = (float) (color >> 16 & 255) / 255.0f;
        float f2 = (float) (color >> 8 & 255) / 255.0f;
        float f3 = (float) (color & 255) / 255.0f;
        GL11.glColor4f(f1, f2, f3, f);
    }

    public static void setColor(int colorHex) {
        float alpha = (float) (colorHex >> 24 & 255) / 255.0F;
        float red = (float) (colorHex >> 16 & 255) / 255.0F;
        float green = (float) (colorHex >> 8 & 255) / 255.0F;
        float blue = (float) (colorHex & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static float smoothTrans(double current, double last){
        return (float) (current * mc.getTickDelta() + (last * (1.0f - mc.getTickDelta())));
    }

    public static void drawBorderedRect(float x, float y, float width, float height, float borderWidth, Color rectColor, Color borderColor) {
        drawBorderedRect(x, y, width, height, borderWidth, rectColor.getRGB(), borderColor.getRGB());
    }

    public static void drawBorderedRect(Number x, Number y, Number width, Number height, Number borderWidth, Color rectColor, Color borderColor) {
        drawBorderedRect((float) x.doubleValue(), (float) y.doubleValue(), (float) width.doubleValue(), (float) height.doubleValue(), (float) borderWidth.doubleValue(), rectColor.getRGB(), borderColor.getRGB());
    }

    public static void drawBorder(float x, float y, float width, float height, float borderWidth, int borderColor) {
        drawRect(x + borderWidth, y + borderWidth, width - borderWidth * 2.0F, borderWidth, borderColor);
        drawRect(x, y + borderWidth, borderWidth, height - borderWidth, borderColor);
        drawRect(x + width - borderWidth, y + borderWidth, borderWidth, height - borderWidth, borderColor);
        drawRect(x + borderWidth, y + height - borderWidth, width - borderWidth * 2.0F, borderWidth, borderColor);
    }

    public static void drawRect(float x, float y, float width, float height, Color color) {
        drawRect(x, y, width, height, color.getRGB());
    }

    public static void drawRect(float x, float y, float width, float height, int color) {
            enableRender2D();
            setColor(color);
            GL11.glBegin(7);
            GL11.glVertex2d(x, y);
            GL11.glVertex2d(x + width, y);
            GL11.glVertex2d(x + width, y + height);
            GL11.glVertex2d(x, y + height);
            GL11.glEnd();
            disableRender2D();
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float edgeRadius, int color, float borderWidth, int borderColor) {
            if (color == 16777215) color = Color.WHITE.hashCode();
            if (borderColor == 16777215) borderColor = Color.WHITE.hashCode();

            if (edgeRadius < 0.0F) {
                edgeRadius = 0.0F;
            }

            if (edgeRadius > width / 2.0F) {
                edgeRadius = width / 2.0F;
            }

            if (edgeRadius > height / 2.0F) {
                edgeRadius = height / 2.0F;
            }

            drawRect(x + edgeRadius, y + edgeRadius, width - edgeRadius * 2.0F, height - edgeRadius * 2.0F, color);
            drawRect(x + edgeRadius, y, width - edgeRadius * 2.0F, edgeRadius, color);
            drawRect(x + edgeRadius, y + height - edgeRadius, width - edgeRadius * 2.0F, edgeRadius, color);
            drawRect(x, y + edgeRadius, edgeRadius, height - edgeRadius * 2.0F, color);
            drawRect(x + width - edgeRadius, y + edgeRadius, edgeRadius, height - edgeRadius * 2.0F, color);
            enableRender2D();
            RenderUtil.color(color);
            GL11.glBegin(6);
            float centerX = x + edgeRadius;
            float centerY = y + edgeRadius;
            GL11.glVertex2d((double) centerX, (double) centerY);
            int vertices = (int) Math.min(Math.max(edgeRadius, 10.0F), 90.0F);

            int i;
            double angleRadians;
            for (i = 0; i < vertices + 1; ++i) {
                angleRadians = 6.283185307179586D * (double) (i + 180) / (double) (vertices * 4);
                GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
            }

            GL11.glEnd();
            GL11.glBegin(6);
            centerX = x + width - edgeRadius;
            centerY = y + edgeRadius;
            GL11.glVertex2d((double) centerX, (double) centerY);
            vertices = (int) Math.min(Math.max(edgeRadius, 10.0F), 90.0F);

            for (i = 0; i < vertices + 1; ++i) {
                angleRadians = 6.283185307179586D * (double) (i + 90) / (double) (vertices * 4);
                GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
            }

            GL11.glEnd();
            GL11.glBegin(6);
            centerX = x + edgeRadius;
            centerY = y + height - edgeRadius;
            GL11.glVertex2d((double) centerX, (double) centerY);
            vertices = (int) Math.min(Math.max(edgeRadius, 10.0F), 90.0F);

            for (i = 0; i < vertices + 1; ++i) {
                angleRadians = 6.283185307179586D * (double) (i + 270) / (double) (vertices * 4);
                GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
            }

            GL11.glEnd();
            GL11.glBegin(6);
            centerX = x + width - edgeRadius;
            centerY = y + height - edgeRadius;
            GL11.glVertex2d((double) centerX, (double) centerY);
            vertices = (int) Math.min(Math.max(edgeRadius, 10.0F), 90.0F);

            for (i = 0; i < vertices + 1; ++i) {
                angleRadians = 6.283185307179586D * (double) i / (double) (vertices * 4);
                GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
            }

            GL11.glEnd();
            RenderUtil.color(borderColor);
            GL11.glLineWidth(borderWidth);
            GL11.glBegin(3);
            centerX = x + edgeRadius;
            centerY = y + edgeRadius;
            vertices = (int) Math.min(Math.max(edgeRadius, 10.0F), 90.0F);

            for (i = vertices; i >= 0; --i) {
                angleRadians = 6.283185307179586D * (double) (i + 180) / (double) (vertices * 4);
                GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
            }

            GL11.glVertex2d((double) (x + edgeRadius), (double) y);
            GL11.glVertex2d((double) (x + width - edgeRadius), (double) y);
            centerX = x + width - edgeRadius;
            centerY = y + edgeRadius;

            for (i = vertices; i >= 0; --i) {
                angleRadians = 6.283185307179586D * (double) (i + 90) / (double) (vertices * 4);
                GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
            }

            GL11.glVertex2d((double) (x + width), (double) (y + edgeRadius));
            GL11.glVertex2d((double) (x + width), (double) (y + height - edgeRadius));
            centerX = x + width - edgeRadius;
            centerY = y + height - edgeRadius;

            for (i = vertices; i >= 0; --i) {
                angleRadians = 6.283185307179586D * (double) i / (double) (vertices * 4);
                GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
            }

            GL11.glVertex2d((double) (x + width - edgeRadius), (double) (y + height));
            GL11.glVertex2d((double) (x + edgeRadius), (double) (y + height));
            centerX = x + edgeRadius;
            centerY = y + height - edgeRadius;

            for (i = vertices; i >= 0; --i) {
                angleRadians = 6.283185307179586D * (double) (i + 270) / (double) (vertices * 4);
                GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
            }

            GL11.glVertex2d((double) x, (double) (y + height - edgeRadius));
            GL11.glVertex2d((double) x, (double) (y + edgeRadius));
            GL11.glEnd();
            disableRender2D();
    }

    public static void fastRoundedRect(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, float radius) {
            float z = 0;
            if (paramXStart > paramXEnd) {
                z = paramXStart;
                paramXStart = paramXEnd;
                paramXEnd = z;
            }

            if (paramYStart > paramYEnd) {
                z = paramYStart;
                paramYStart = paramYEnd;
                paramYEnd = z;
            }

            double x1 = (double) (paramXStart + radius);
            double y1 = (double) (paramYStart + radius);
            double x2 = (double) (paramXEnd - radius);
            double y2 = (double) (paramYEnd - radius);

            glEnable(GL_LINE_SMOOTH);
            glLineWidth(1);

            glBegin(GL_POLYGON);

            double degree = Math.PI / 180;
            for (double i = 0; i <= 90; i += 1)
                glVertex2d(x2 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
            for (double i = 90; i <= 180; i += 1)
                glVertex2d(x2 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
            for (double i = 180; i <= 270; i += 1)
                glVertex2d(x1 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
            for (double i = 270; i <= 360; i += 1)
                glVertex2d(x1 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
            glEnd();
            glDisable(GL_LINE_SMOOTH);
    }

    public static void quickDrawRect(final float x, final float y, final float x2, final float y2) {
        glBegin(GL_QUADS);

        glVertex2d(x2, y);
        glVertex2d(x, y);
        glVertex2d(x, y2);
        glVertex2d(x2, y2);

        glEnd();
    }

    public static void start() {
        enable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        disable(GL11.GL_TEXTURE_2D);
        disable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL_ALPHA_TEST);
        RenderSystem.disableDepthTest();
    }

    public static void rect(final double x, final double y, final double width, final double height, final boolean filled, final Color color) {
        start();
        if (color != null)
            color(color);
        begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINES);

        {
            vertex(x, y);
            vertex(x + width, y);
            vertex(x + width, y + height);
            vertex(x, y + height);
            if (!filled) {
                vertex(x, y);
                vertex(x, y + height);
                vertex(x + width, y);
                vertex(x + width, y + height);
            }
        }
        end();
        stop();
    }

    public static void rect(final double x, final double y, final double width, final double height, final boolean filled) {
        rect(x, y, width, height, filled, null);
    }

    public static void rect(final double x, final double y, final double width, final double height, final Color color) {
        rect(x, y, width, height, true, color);
    }

    public static void rect(final double x, final double y, final double width, final double height) {
        rect(x, y, width, height, true, null);
    }

    public static void rectCentered(double x, double y, final double width, final double height, final boolean filled, final Color color) {
        x -= width / 2;
        y -= height / 2;
        rect(x, y, width, height, filled, color);
    }

    public static void rectCentered(double x, double y, final double width, final double height, final boolean filled) {
        x -= width / 2;
        y -= height / 2;
        rect(x, y, width, height, filled, null);
    }

    public static void rectCentered(double x, double y, final double width, final double height, final Color color) {
        x -= width / 2;
        y -= height / 2;
        rect(x, y, width, height, true, color);
    }

    public static void rectCentered(double x, double y, final double width, final double height) {
        x -= width / 2;
        y -= height / 2;
        rect(x, y, width, height, true, null);
    }

    public static void roundedRect(final double x, final double y, double width, double height, final double edgeRadius, final Color color) {
        final double halfRadius = edgeRadius / 2;
        width -= halfRadius;
        height -= halfRadius;

        float sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        begin(GL11.GL_TRIANGLE_FAN);

        {
            for (double i = 180; i <= 270; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + (sideLength * Math.sin(angle)) + sideLength);
            }
            vertex(x + sideLength, y + sideLength);
        }

        end();
        stop();

        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_TRIANGLE_FAN);

        {
            for (double i = 0; i <= 90; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + width + (sideLength * Math.cos(angle)), y + height + (sideLength * Math.sin(angle)));
            }
            vertex(x + width, y + height);
        }

        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();

        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_TRIANGLE_FAN);

        {
            for (double i = 270; i <= 360; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + width + (sideLength * Math.cos(angle)), y + (sideLength * Math.sin(angle)) + sideLength);
            }
            vertex(x + width, y + sideLength);
        }

        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();

        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_TRIANGLE_FAN);

        {
            for (double i = 90; i <= 180; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + height + (sideLength * Math.sin(angle)));
            }
            vertex(x + sideLength, y + height);
        }

        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();

        // Main block
        rect(x + halfRadius, y + halfRadius, width - halfRadius, height - halfRadius, color);

        // Horizontal bars
        rect(x, y + halfRadius, edgeRadius / 2, height - halfRadius, color);
        rect(x + width, y + halfRadius, edgeRadius / 2, height - halfRadius, color);

        // Vertical bars
        rect(x + halfRadius, y, width - halfRadius, halfRadius, color);
        rect(x + halfRadius, y + height, width - halfRadius, halfRadius, color);
    }

    public static void stop() {
        GL11.glEnable(GL_ALPHA_TEST);
        RenderSystem.enableDepthTest();
        enable(GL11.GL_CULL_FACE);
        enable(GL11.GL_TEXTURE_2D);
        disable(GL11.GL_BLEND);
        color(Color.white);
    }

    public static void enable(final int glTarget) {
        GL11.glEnable(glTarget);
    }

    public static void disable(final int glTarget) {
        GL11.glDisable(glTarget);
    }

    public static void startSmooth() {
        enable(GL11.GL_POLYGON_SMOOTH);
        enable(GL11.GL_LINE_SMOOTH);
        enable(GL11.GL_POINT_SMOOTH);
    }

    public static void endSmooth() {
        disable(GL11.GL_POINT_SMOOTH);
        disable(GL11.GL_LINE_SMOOTH);
        disable(GL11.GL_POLYGON_SMOOTH);
    }

    public static void begin(final int glMode) {
        GL11.glBegin(glMode);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void vertex(final double x, final double y) {
        GL11.glVertex2d(x, y);
    }

    public static void translate(final double x, final double y) {
        GL11.glTranslated(x, y, 0);
    }

    public static void scale(final double x, final double y) {
        GL11.glScaled(x, y, 1);
    }

    public static void polygon(final double x, final double y, double sideLength, final double amountOfSides, final boolean filled, final Color color) {
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        if (!filled) GL11.glLineWidth(2);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINE_STRIP);
        {
            for (double i = 0; i <= amountOfSides / 4; i++) {
                final double angle = i * 4 * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + (sideLength * Math.sin(angle)) + sideLength);
            }
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }

    public static void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final boolean filled) {
        polygon(x, y, sideLength, amountOfSides, filled, null);
    }

    public static void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final Color color) {
        polygon(x, y, sideLength, amountOfSides, true, color);
    }

    public static void polygon(final double x, final double y, final double sideLength, final int amountOfSides) {
        polygon(x, y, sideLength, amountOfSides, true, null);
    }

    public static void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final boolean filled, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, filled, color);
    }

    public static void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final boolean filled) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, filled, null);
    }

    public static void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, true, color);
    }

    public static void polygonCentered(double x, double y, final double sideLength, final int amountOfSides) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, true, null);
    }

    public static void circle(final double x, final double y, final double radius, final boolean filled, final Color color) {
        polygon(x, y, radius, 360, filled, color);
    }

    public static void circle(final double x, final double y, final double radius, final boolean filled) {
        polygon(x, y, radius, 360, filled);
    }

    public static void circle(final double x, final double y, final double radius, final Color color) {
        polygon(x, y, radius, 360, color);
    }

    public static void circle(final double x, final double y, final double radius) {
        polygon(x, y, radius, 360);
    }

    public static void circleCentered(double x, double y, final double radius, final boolean filled, final Color color) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, 360, filled, color);
    }

    public static void circleCentered(double x, double y, final double radius, final boolean filled) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, 360, filled);
    }

    public static void circleCentered(double x, double y, final double radius, final boolean filled, final int sides) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, sides, filled);
    }

    public static void circleCentered(double x, double y, final double radius, final Color color) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, 360, color);
    }

    public static void circleCentered(double x, double y, final double radius) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, 360);
    }

    public static void scissor(double x, double y, double width, double height) {
        final double scale = mc.getWindow().getScaleFactor();

        y = mc.getWindow().getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public static void drawFullCircle(int cx, double cy, double r, int segments, float lineWidth, int part, int c) {
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        r *= 2.0D;
        cx *= 2;
        cy *= 2;
        float f = (c >> 24 & 0xFF) / 255.0F;
        float f2 = (c >> 16 & 0xFF) / 255.0F;
        float f3 = (c >> 8 & 0xFF) / 255.0F;
        float f4 = (c & 0xFF) / 255.0F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(lineWidth);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = segments - part; i <= segments; i++) {
            double x = Math.sin(i * Math.PI / 180.0D) * r;
            double y = Math.cos(i * Math.PI / 180.0D) * r;
            GL11.glVertex2d(cx + x, cy + y);
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
    }


    public static void color(final double red, final double green, final double blue, final double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static void color(final double red, final double green, final double blue) {
        color(red, green, blue, 1);
    }

    public static void color(Color color) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }

    public static void glColor(final int red, final int green, final int blue, final int alpha) {
        GL11.glColor4f(red / 255F, green / 255F, blue / 255F, alpha / 255F);
    }

    public static void glColor(final Color color, final float alpha) {
        final float red = color.getRed() / 255F;
        final float green = color.getGreen() / 255F;
        final float blue = color.getBlue() / 255F;

        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void glColor(final Color color) {
        final float red = color.getRed() / 255F;
        final float green = color.getGreen() / 255F;
        final float blue = color.getBlue() / 255F;
        final float alpha = color.getAlpha() / 255F;

        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void glColor(final int hex, final int alpha) {
        final float red = (hex >> 16 & 0xFF) / 255F;
        final float green = (hex >> 8 & 0xFF) / 255F;
        final float blue = (hex & 0xFF) / 255F;

        GL11.glColor4f(red, green, blue, alpha / 255F);
    }

    public static void enableSmoothLine(float width) {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(width);
    }

    public static void disableSmoothLine() {
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void glColor(final int hex, final float alpha) {
        final float red = (hex >> 16 & 0xFF) / 255F;
        final float green = (hex >> 8 & 0xFF) / 255F;
        final float blue = (hex & 0xFF) / 255F;

        GL11.glColor4f(red, green, blue, alpha);
    }

    public void color(Color color, final int alpha) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5);
    }

    public static int rainbow(int delay) {
        double rainbowState = Math.ceil((double) (System.currentTimeMillis() + (long) delay) / 20.0D);
        rainbowState %= 360.0D;
        return Color.getHSBColor((float) (rainbowState / 360.0D), 0.8F, 0.7F).brighter().getRGB();
    }

    public static Color rainbow(int delay, boolean b) {
        double rainbowState = Math.ceil((double) (System.currentTimeMillis() + (long) delay) / 20.0D);
        rainbowState %= 360.0D;
        return Color.getHSBColor((float) (rainbowState / 360.0D), 0.8F, 0.7F).brighter();
    }

    public static void doGlScissor(int x, int y, int width, int height) {
        int scaleFactor = 1;
        int k = mc.options.getGuiScale().getValue();
        if (k == 0) {
            k = 1000;
        }
        while (scaleFactor < k && mc.getWindow().getScaledWidth() / (scaleFactor + 1) >= 320 && mc.getWindow().getScaledHeight() / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor((int) (x * scaleFactor), (int) (mc.getWindow().getScaledHeight() - (y + height) * scaleFactor), (int) (width * scaleFactor), (int) (height * scaleFactor));
    }

    public static void doGlScissor(float x, float y, float width, float height) {
        doGlScissor((int) x,(int) y,(int) width,(int) height);
    }

    public static void drawRoundedRect(float x, float y, float x2, float y2, final float round, final int color) {
        x += (float) (round / 2.0f + 0.5);
        y += (float) (round / 2.0f + 0.5);
        x2 -= (float) (round / 2.0f + 0.5);
        y2 -= (float) (round / 2.0f + 0.5);
        drawRect((int) x, (int) y, (int) x2, (int) y2, color);
        circle(x2 - round / 2.0f, y + round / 2.0f, round, color);
        circle(x + round / 2.0f, y2 - round / 2.0f, round, color);
        circle(x + round / 2.0f, y + round / 2.0f, round, color);
        circle(x2 - round / 2.0f, y2 - round / 2.0f, round, color);
        drawRect((int) (x - round / 2.0f - 0.5f), (int) (y + round / 2.0f), (int) x2, (int) (y2 - round / 2.0f), color);
        drawRect((int) x, (int) (y + round / 2.0f), (int) (x2 + round / 2.0f + 0.5f), (int) (y2 - round / 2.0f), color);
        drawRect((int) (x + round / 2.0f), (int) (y - round / 2.0f - 0.5f), (int) (x2 - round / 2.0f), (int) (y2 - round / 2.0f), color);
        drawRect((int) (x + round / 2.0f), (int) y, (int) (x2 - round / 2.0f), (int) (y2 + round / 2.0f + 0.5f), color);
    }

    public static void circle(final float x, final float y, final float radius, final Color fill) {
        arc(x, y, 0.0f, 360.0f, radius, fill);
    }

    public static void circle(final float x, final float y, final float radius, final int fill) {
        arc(x, y, 0.0f, 360.0f, radius, fill);
    }

    public static void arc(final float x, final float y, final float start, final float end, final float radius, final int color) {
        arcEllipse(x, y, start, end, radius, radius, color);
    }

    public static void arc(final float x, final float y, final float start, final float end, final float radius, final Color color) {
        arcEllipse(x, y, start, end, radius, radius, color);
    }

    public static void arcEllipse(final float x, final float y, float start, float end, final float w, final float h, final int color) {
        GL11.glColor3f(0.0f, 0.0f, 0.0f);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        float temp = 0.0f;
        if (start > end) {
            temp = end;
            end = start;
            start = temp;
        }
        final float var11 = (color >> 24 & 0xFF) / 255.0f;
        final float var12 = (color >> 16 & 0xFF) / 255.0f;
        final float var13 = (color >> 8 & 0xFF) / 255.0f;
        final float var14 = (color & 0xFF) / 255.0f;
        RenderSystem.enableBlend();
        disableTexture();
        RenderSystem.defaultBlendFunc();
        GL11.glColor4f(var12, var13, var14, var11);
        if (var11 > 0.5f) {
            GL11.glEnable(2848);
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            for (float i = end; i >= start; i -= 4.0f) {
                final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w * 1.001f;
                final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h * 1.001f;
                GL11.glVertex2f(x + ldx, y + ldy);
            }
            GL11.glEnd();
            GL11.glDisable(2848);
        }
        GL11.glBegin(6);
        for (float i = end; i >= start; i -= 4.0f) {
            final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w;
            final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h;
            GL11.glVertex2f(x + ldx, y + ldy);
        }
        GL11.glEnd();
        enableTexture();
        RenderSystem.disableBlend();
    }

    public static void arcEllipse(final float x, final float y, float start, float end, final float w, final float h, final Color color) {
        GL11.glColor3f(0.0f, 0.0f, 0.0f);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        float temp = 0.0f;
        if (start > end) {
            temp = end;
            end = start;
            start = temp;
        }
        RenderSystem.enableBlend();
        disableTexture();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        if (color.getAlpha() > 0.5f) {
            GL11.glEnable(2848);
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            for (float i = end; i >= start; i -= 4.0f) {
                final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w * 1.001f;
                final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h * 1.001f;
                GL11.glVertex2f(x + ldx, y + ldy);
            }
            GL11.glEnd();
            GL11.glDisable(2848);
        }
        GL11.glBegin(6);
        for (float i = end; i >= start; i -= 4.0f) {
            final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w;
            final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h;
            GL11.glVertex2f(x + ldx, y + ldy);
        }
        GL11.glEnd();
        enableTexture();
        RenderSystem.disableBlend();
    }

    public RenderUtil() {
        super();
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void drawFastRoundedRect(final int x0, final float y0, final int x1, final float y1, final float radius, final int color) {
        final int Semicircle = 18;
        final float f = 90.0f / Semicircle;
        final float f2 = (color >> 24 & 0xFF) / 255.0f;
        final float f3 = (color >> 16 & 0xFF) / 255.0f;
        final float f4 = (color >> 8 & 0xFF) / 255.0f;
        final float f5 = (color & 0xFF) / 255.0f;
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        RenderSystem.defaultBlendFunc();
        GL11.glColor4f(f3, f4, f5, f2);
        GL11.glBegin(5);
        GL11.glVertex2f(x0 + radius, y0);
        GL11.glVertex2f(x0 + radius, y1);
        GL11.glVertex2f(x1 - radius, y0);
        GL11.glVertex2f(x1 - radius, y1);
        GL11.glEnd();
        GL11.glBegin(5);
        GL11.glVertex2f(x0, y0 + radius);
        GL11.glVertex2f(x0 + radius, y0 + radius);
        GL11.glVertex2f(x0, y1 - radius);
        GL11.glVertex2f(x0 + radius, y1 - radius);
        GL11.glEnd();
        GL11.glBegin(5);
        GL11.glVertex2f(x1, y0 + radius);
        GL11.glVertex2f(x1 - radius, y0 + radius);
        GL11.glVertex2f(x1, y1 - radius);
        GL11.glVertex2f(x1 - radius, y1 - radius);
        GL11.glEnd();
        GL11.glBegin(6);
        float f6 = x1 - radius;
        float f7 = y0 + radius;
        GL11.glVertex2f(f6, f7);
        int j = 0;
        for (j = 0; j <= Semicircle; ++j) {
            final float f8 = j * f;
            GL11.glVertex2f((float) (f6 + radius * Math.cos(Math.toRadians(f8))), (float) (f7 - radius * Math.sin(Math.toRadians(f8))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f6 = x0 + radius;
        f7 = y0 + radius;
        GL11.glVertex2f(f6, f7);
        for (j = 0; j <= Semicircle; ++j) {
            final float f9 = j * f;
            GL11.glVertex2f((float) (f6 - radius * Math.cos(Math.toRadians(f9))), (float) (f7 - radius * Math.sin(Math.toRadians(f9))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f6 = x0 + radius;
        f7 = y1 - radius;
        GL11.glVertex2f(f6, f7);
        for (j = 0; j <= Semicircle; ++j) {
            final float f10 = j * f;
            GL11.glVertex2f((float) (f6 - radius * Math.cos(Math.toRadians(f10))), (float) (f7 + radius * Math.sin(Math.toRadians(f10))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f6 = x1 - radius;
        f7 = y1 - radius;
        GL11.glVertex2f(f6, f7);
        for (j = 0; j <= Semicircle; ++j) {
            final float f11 = j * f;
            GL11.glVertex2f((float) (f6 + radius * Math.cos(Math.toRadians(f11))), (float) (f7 + radius * Math.sin(Math.toRadians(f11))));
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(3042);
        enableTexture();
        RenderSystem.disableBlend();
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void drawCircle(float cx, float cy, float r, int num_segments, int c) {
        GL11.glPushMatrix();
        cx *= 2.0F;
        cy *= 2.0F;
        float f = (c >> 24 & 0xFF) / 255.0F;
        float f1 = (c >> 16 & 0xFF) / 255.0F;
        float f2 = (c >> 8 & 0xFF) / 255.0F;
        float f3 = (c & 0xFF) / 255.0F;
        float theta = (float) (6.2831852D / num_segments);
        float p = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        float x = r *= 2.0F;
        float y = 0.0F;
        enableGL2D();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(2);
        int ii = 0;
        while (ii < num_segments) {
            GL11.glVertex2f(x + cx, y + cy);
            float t = x;
            x = p * x - s * y;
            y = s * t + p * y;
            ii++;
        }
        GL11.glEnd();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        disableGL2D();
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;

        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);

        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);

        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
        GL11.glColor4d(255, 255, 255, 255);
    }

    public static void drawRectLine(float x, float y, float width, float lineWidth, int color) {
        drawRect(x, y, width, lineWidth, color);
    }

    public static void drawHorizontalLine(float x, float y, float x1, float thickness, int color) {
        drawRect2(x, y, x1, y + thickness, color);
    }

    public static void drawVerticalLine(float x, float y, float y1, float thickness, int color) {
        drawRect2(x, y, x + thickness, y1, color);
    }

    public static void drawRect2(double x, double y, double x2, double y2, int color) {
        drawRect((int) x, (int) y, (int) x2, (int) y2, color);
    }

    public static void drawHollowBox(float x, float y, float x1, float y1, float thickness, int color) {
        /* Top */
        drawHorizontalLine(x, y, x1, thickness, color);
        /* Bottom */
        drawHorizontalLine(x, y1, x1, thickness, color);
        /* Left */
        drawVerticalLine(x, y, y1, thickness, color);
        /* Right */
        drawVerticalLine(x1 - thickness, y, y1, thickness, color);
    }

    public static int getHealthColor(LivingEntity player) {
        float f = player.getHealth();
        float f1 = player.getMaxHealth();
        float f2 = Math.max(0.0F, Math.min(f, f1) / f1);
        return Color.HSBtoRGB(f2 / 3.0F, 1.0F, 1.0F) | 0xFF000000;
    }

    public static float[] getRGBAs(int rgb) {
        return new float[]{((rgb >> 16) & 255) / 255F, ((rgb >> 8) & 255) / 255F, (rgb & 255) / 255F,
                ((rgb >> 24) & 255) / 255F};
    }

    public static int getRainbow(int speed, int offset) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        hue /= speed;
        return Color.getHSBColor(hue, 0.75f, 1f).getRGB();

    }

    public static double getEntityRenderX(final Entity entity) {
        return entity.lastRenderX + (entity.getX() - entity.lastRenderX) * mc.getTickDelta() - mc.gameRenderer.getCamera().getPos().x;
    }

    public static double getEntityRenderY(final Entity entity) {
        return entity.lastRenderY + (entity.getY() - entity.lastRenderY) * mc.getTickDelta() - mc.gameRenderer.getCamera().getPos().y;
    }

    public static double getEntityRenderZ(final Entity entity) {
        return entity.lastRenderZ + (entity.getZ() - entity.lastRenderZ) * mc.getTickDelta() - mc.gameRenderer.getCamera().getPos().z;
    }

    public static void drawblock(double a, double a2, double a3, int a4, int a5, float a6) {
        float a7 = (float) (a4 >> 24 & 255) / 255.0f;
        float a8 = (float) (a4 >> 16 & 255) / 255.0f;
        float a9 = (float) (a4 >> 8 & 255) / 255.0f;
        float a10 = (float) (a4 & 255) / 255.0f;
        float a11 = (float) (a5 >> 24 & 255) / 255.0f;
        float a12 = (float) (a5 >> 16 & 255) / 255.0f;
        float a13 = (float) (a5 >> 8 & 255) / 255.0f;
        float a14 = (float) (a5 & 255) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable((int) 3042);
        GL11.glBlendFunc((int) 770, (int) 771);
        GL11.glDisable((int) 3553);
        GL11.glEnable((int) 2848);
        GL11.glDisable((int) 2929);
        GL11.glDepthMask((boolean) false);
        GL11.glColor4f((float) a8, (float) a9, (float) a10, (float) a7);
        R3DUtils.drawOutlinedBoundingBox(new Box(a, a2, a3, a + 1.0, a2 + 1.0, a3 + 1.0));
        GL11.glLineWidth((float) a6);
        GL11.glColor4f((float) a12, (float) a13, (float) a14, (float) a11);
        R3DUtils.drawOutlinedBoundingBox(new Box(a, a2, a3, a + 1.0, a2 + 1.0, a3 + 1.0));
        GL11.glDisable((int) 2848);
        GL11.glEnable((int) 3553);
        GL11.glEnable((int) 2929);
        GL11.glDepthMask((boolean) true);
        GL11.glDisable((int) 3042);
        GL11.glPopMatrix();
    }

    public static void pre3D() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    }

    public static int width() {
        return mc.getWindow().getScaledWidth();
    }

    public static int height() {
        return mc.getWindow().getScaledHeight();
    }

    public static double interpolation(final double newPos, final double oldPos) {
        return oldPos + (newPos - oldPos) * mc.getTickDelta();
    }

    public static class R3DUtils {
        public static void startDrawing() {
            GL11.glEnable((int) 3042);
            GL11.glEnable((int) 3042);
            GL11.glBlendFunc((int) 770, (int) 771);
            GL11.glEnable((int) 2848);
            GL11.glDisable((int) 3553);
            GL11.glDisable((int) 2929);
        }

        public static void stopDrawing() {
            GL11.glDisable((int) 3042);
            GL11.glEnable((int) 3553);
            GL11.glDisable((int) 2848);
            GL11.glDisable((int) 3042);
            GL11.glEnable((int) 2929);
        }

        public static void drawOutlinedBox(Box box) {
            if (box == null) {
                return;
            }
            GL11.glBegin((int) 3);
            GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
            GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
            GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
            GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
            GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
            GL11.glEnd();
            GL11.glBegin((int) 3);
            GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
            GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
            GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
            GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
            GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
            GL11.glEnd();
            GL11.glBegin((int) 1);
            GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
            GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
            GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
            GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
            GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
            GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
            GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
            GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
            GL11.glEnd();
        }


        public static void drawFilledBox(Box mask) {
            GL11.glBegin((int) 4);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glEnd();
            GL11.glBegin((int) 4);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
            GL11.glEnd();
            GL11.glBegin((int) 4);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
            GL11.glEnd();
            GL11.glBegin((int) 4);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
            GL11.glEnd();
            GL11.glBegin((int) 4);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
            GL11.glEnd();
            GL11.glBegin((int) 4);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
            GL11.glEnd();
        }

        public static void drawOutlinedBoundingBox(Box aabb) {
            GL11.glBegin((int) 3);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.minY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.minY, (double) aabb.maxZ);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.maxZ);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.minZ);
            GL11.glEnd();
            GL11.glBegin((int) 3);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.maxY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.maxY, (double) aabb.maxZ);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.maxZ);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.minZ);
            GL11.glEnd();
            GL11.glBegin((int) 1);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.minY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.maxY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.minY, (double) aabb.maxZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.maxY, (double) aabb.maxZ);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.maxZ);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.maxZ);
            GL11.glEnd();
        }
    }

    public static class R2DUtils {
        public static void enableGL2D() {
            GL11.glDisable((int) 2929);
            GL11.glEnable((int) 3042);
            GL11.glDisable((int) 3553);
            GL11.glBlendFunc((int) 770, (int) 771);
            GL11.glDepthMask((boolean) true);
            GL11.glEnable((int) 2848);
            GL11.glHint((int) 3154, (int) 4354);
            GL11.glHint((int) 3155, (int) 4354);
        }

        public static void disableGL2D() {
            GL11.glEnable((int) 3553);
            GL11.glDisable((int) 3042);
            GL11.glEnable((int) 2929);
            GL11.glDisable((int) 2848);
            GL11.glHint((int) 3154, (int) 4352);
            GL11.glHint((int) 3155, (int) 4352);
        }

        public static void drawRoundedRect(float x, float y, float x1, float y1, int borderC, int insideC) {
            R2DUtils.enableGL2D();
            GL11.glScalef((float) 0.5f, (float) 0.5f, (float) 0.5f);
            R2DUtils.drawVLine(x *= 2.0f, (y *= 2.0f) + 1.0f, (y1 *= 2.0f) - 2.0f, borderC);
            R2DUtils.drawVLine((x1 *= 2.0f) - 1.0f, y + 1.0f, y1 - 2.0f, borderC);
            R2DUtils.drawHLine(x + 2.0f, x1 - 3.0f, y, borderC);
            R2DUtils.drawHLine(x + 2.0f, x1 - 3.0f, y1 - 1.0f, borderC);
            R2DUtils.drawHLine(x + 1.0f, x + 1.0f, y + 1.0f, borderC);
            R2DUtils.drawHLine(x1 - 2.0f, x1 - 2.0f, y + 1.0f, borderC);
            R2DUtils.drawHLine(x1 - 2.0f, x1 - 2.0f, y1 - 2.0f, borderC);
            R2DUtils.drawHLine(x + 1.0f, x + 1.0f, y1 - 2.0f, borderC);
            R2DUtils.drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
            GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
            R2DUtils.disableGL2D();
        }

        public static void drawRect(double x2, double y2, double x1, double y1, int color) {
            R2DUtils.enableGL2D();
            R2DUtils.glColor(color);
            R2DUtils.drawRect(x2, y2, x1, y1);
            R2DUtils.disableGL2D();
        }

        private static void drawRect(double x2, double y2, double x1, double y1) {
            GL11.glBegin((int) 7);
            GL11.glVertex2d((double) x2, (double) y1);
            GL11.glVertex2d((double) x1, (double) y1);
            GL11.glVertex2d((double) x1, (double) y2);
            GL11.glVertex2d((double) x2, (double) y2);
            GL11.glEnd();
        }

        public static void glColor(int hex) {
            float alpha = (float) (hex >> 24 & 255) / 255.0f;
            float red = (float) (hex >> 16 & 255) / 255.0f;
            float green = (float) (hex >> 8 & 255) / 255.0f;
            float blue = (float) (hex & 255) / 255.0f;
            GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
        }

        private static void glColor(final Color color, final float alpha) {
            final float red = color.getRed() / 255F;
            final float green = color.getGreen() / 255F;
            final float blue = color.getBlue() / 255F;

            GL11.glColor4f(red, green, blue, alpha);
        }

        public static void glColor(final int red, final int green, final int blue, final int alpha) {
            GL11.glColor4f(red / 255F, green / 255F, blue / 255F, alpha / 255F);
        }

        public static void glColor(final Color color) {
            final float red = color.getRed() / 255F;
            final float green = color.getGreen() / 255F;
            final float blue = color.getBlue() / 255F;
            final float alpha = color.getAlpha() / 255F;

            GL11.glColor4f(red, green, blue, alpha);
        }

        public static void glColor(final Color color, final int alpha) {
            glColor(color, alpha/255F);
        }

        public static void glColor(final int hex, final int alpha) {
            final float red = (hex >> 16 & 0xFF) / 255F;
            final float green = (hex >> 8 & 0xFF) / 255F;
            final float blue = (hex & 0xFF) / 255F;

            GL11.glColor4f(red, green, blue, alpha / 255F);
        }

        public static void glColor(final int hex, final float alpha) {
            final float red = (hex >> 16 & 0xFF) / 255F;
            final float green = (hex >> 8 & 0xFF) / 255F;
            final float blue = (hex & 0xFF) / 255F;

            GL11.glColor4f(red, green, blue, alpha);
        }

        public static void drawRect(float x, float y, float x1, float y1, int color) {
            R2DUtils.enableGL2D();
            glColor(color);
            R2DUtils.drawRect(x, y, x1, y1);
            R2DUtils.disableGL2D();
        }

        public static void drawBorderedRect(float x, float y, float x1, float y1, float width, int borderColor) {
            R2DUtils.enableGL2D();
            glColor(borderColor);
            R2DUtils.drawRect(x + width, y, x1 - width, y + width);
            R2DUtils.drawRect(x, y, x + width, y1);
            R2DUtils.drawRect(x1 - width, y, x1, y1);
            R2DUtils.drawRect(x + width, y1 - width, x1 - width, y1);
            R2DUtils.disableGL2D();
        }

        public static void drawBorderedRect(float x, float y, float x1, float y1, int insideC, int borderC) {
            R2DUtils.enableGL2D();
            GL11.glScalef((float) 0.5f, (float) 0.5f, (float) 0.5f);
            R2DUtils.drawVLine(x *= 2.0f, y *= 2.0f, y1 *= 2.0f, borderC);
            R2DUtils.drawVLine((x1 *= 2.0f) - 1.0f, y, y1, borderC);
            R2DUtils.drawHLine(x, x1 - 1.0f, y, borderC);
            R2DUtils.drawHLine(x, x1 - 2.0f, y1 - 1.0f, borderC);
            R2DUtils.drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
            GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
            R2DUtils.disableGL2D();
        }

        public static void drawGradientRect(float x, float y, float x1, float y1, int topColor, int bottomColor) {
            R2DUtils.enableGL2D();
            GL11.glShadeModel((int) 7425);
            GL11.glBegin((int) 7);
            glColor(topColor);
            GL11.glVertex2f((float) x, (float) y1);
            GL11.glVertex2f((float) x1, (float) y1);
            glColor(bottomColor);
            GL11.glVertex2f((float) x1, (float) y);
            GL11.glVertex2f((float) x, (float) y);
            GL11.glEnd();
            GL11.glShadeModel((int) 7424);
            R2DUtils.disableGL2D();
        }

        public static void drawHLine(float x, float y, float x1, int y1) {
            if (y < x) {
                float var5 = x;
                x = y;
                y = var5;
            }
            R2DUtils.drawRect(x, x1, y + 1.0f, x1 + 1.0f, y1);
        }

        public static void drawVLine(float x, float y, float x1, int y1) {
            if (x1 < y) {
                float var5 = y;
                y = x1;
                x1 = var5;
            }
            R2DUtils.drawRect(x, y + 1.0f, x + 1.0f, x1, y1);
        }

        public static void drawHLine(float x, float y, float x1, int y1, int y2) {
            if (y < x) {
                float var5 = x;
                x = y;
                y = var5;
            }
            R2DUtils.drawGradientRect(x, x1, y + 1.0f, x1 + 1.0f, y1, y2);
        }

        public static void drawRect(float x, float y, float x1, float y1) {
            GL11.glBegin((int) 7);
            GL11.glVertex2f((float) x, (float) y1);
            GL11.glVertex2f((float) x1, (float) y1);
            GL11.glVertex2f((float) x1, (float) y);
            GL11.glVertex2f((float) x, (float) y);
            GL11.glEnd();
        }
    }

    public static int getHexRGB(final int hex) {
        return 0xFF000000 | hex;
    }

    public static void drawBorderedRect(final float x, final float y, final float x2, final float y2, final float l1,
                                        final int col1, final int col2) {
        drawRect((int) x, (int) y, (int) x2, (int) y2, (int) col2);
        final float f = (col1 >> 24 & 0xFF) / 255.0f;
        final float f2 = (col1 >> 16 & 0xFF) / 255.0f;
        final float f3 = (col1 >> 8 & 0xFF) / 255.0f;
        final float f4 = (col1 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d((double) x, (double) y);
        GL11.glVertex2d((double) x, (double) y2);
        GL11.glVertex2d((double) x2, (double) y2);
        GL11.glVertex2d((double) x2, (double) y);
        GL11.glVertex2d((double) x, (double) y);
        GL11.glVertex2d((double) x2, (double) y);
        GL11.glVertex2d((double) x, (double) y2);
        GL11.glVertex2d((double) x2, (double) y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static void pre() {
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
    }

    public static void post() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glColor3d(1.0, 1.0, 1.0);
    }

    public static void startDrawing() {
        GL11.glEnable(3042);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
    }

    public static void stopDrawing() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static Color blend(final Color color1, final Color color2, final double ratio) {
        final float r = (float) ratio;
        final float ir = 1.0f - r;
        final float[] rgb1 = new float[3];
        final float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        final Color color3 = new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir,
                rgb1[2] * r + rgb2[2] * ir);
        return color3;
    }

    public static void setupRender(final boolean start) {
        if (start) {
            RenderSystem.enableBlend();
            GL11.glEnable(2848);
            RenderSystem.disableDepthTest();
            disableTexture();
            RenderSystem.blendFunc(770, 771);
            GL11.glHint(3154, 4354);
        } else {
            RenderSystem.disableBlend();
            enableTexture();
            GL11.glDisable(2848);
            RenderSystem.enableDepthTest();
        }
        RenderSystem.depthMask(!start);
    }

    public static void layeredRect(double x1, double y1, double x2, double y2, int outline, int inline,
                                   int background) {
        R2DUtils.drawRect(x1, y1, x2, y2, outline);
        R2DUtils.drawRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, inline);
        R2DUtils.drawRect(x1 + 2, y1 + 2, x2 - 2, y2 - 2, background);
    }

    public static void glColor(float alpha, int redRGB, int greenRGB, int blueRGB) {
        float red = 0.003921569F * redRGB;
        float green = 0.003921569F * greenRGB;
        float blue = 0.003921569F * blueRGB;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void post3D() {
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glColor4f(1, 1, 1, 1);
    }

    public static void drawBorderedRect(float x, float y, float x1, float y1, float width, int borderColor) {
        R2DUtils.enableGL2D();
        glColor(borderColor);
        R2DUtils.drawRect(x + width, y, x1 - width, y + width);
        R2DUtils.drawRect(x, y, x + width, y1);
        R2DUtils.drawRect(x1 - width, y, x1, y1);
        R2DUtils.drawRect(x + width, y1 - width, x1 - width, y1);
        R2DUtils.disableGL2D();
    }

    public static void drawBorderedRect(float x, float y, float x1, float y1, int insideC, int borderC) {
        R2DUtils.enableGL2D();
        GL11.glScalef((float) 0.5f, (float) 0.5f, (float) 0.5f);
        R2DUtils.drawVLine(x *= 2.0f, y *= 2.0f, y1 *= 2.0f, borderC);
        R2DUtils.drawVLine((x1 *= 2.0f) - 1.0f, y, y1, borderC);
        R2DUtils.drawHLine(x, x1 - 1.0f, y, borderC);
        R2DUtils.drawHLine(x, x1 - 2.0f, y1 - 1.0f, borderC);
        R2DUtils.drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
        GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
        R2DUtils.disableGL2D();
    }

    public static void drawBorderedRect(double x2, double d2, double x22, double e2, float l1, int col1, int col2) {
        drawRect((float) x2, (float) d2, (float) x22, (float) e2, col2);
        float f2 = (float) (col1 >> 24 & 255) / 255.0f;
        float f22 = (float) (col1 >> 16 & 255) / 255.0f;
        float f3 = (float) (col1 >> 8 & 255) / 255.0f;
        float f4 = (float) (col1 & 255) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(f22, f3, f4, f2);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d(x2, d2);
        GL11.glVertex2d(x2, e2);
        GL11.glVertex2d(x22, e2);
        GL11.glVertex2d(x22, d2);
        GL11.glVertex2d(x2, d2);
        GL11.glVertex2d(x22, d2);
        GL11.glVertex2d(x2, e2);
        GL11.glVertex2d(x22, e2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569f * (float)c.getRed();
        float g = 0.003921569f * (float)c.getGreen();
        float b = 0.003921569f * (float)c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

    
    public static Color reAlpha(Color cIn, float alpha){
        return new Color(cIn.getRed()/255f,cIn.getGreen()/255f,cIn.getBlue()/255f,cIn.getAlpha() / 255f * alpha);
    }

    private static void quickPolygonCircle(float x, float y, float xRadius, float yRadius, int start, int end, int split) {
        for(int i = end; i >= start; i-=split) {
            glVertex2d(x + Math.sin(i * Math.PI / 180.0D) * xRadius, y + Math.cos(i * Math.PI / 180.0D) * yRadius);
        }
    }

    public static void drawCircleRect(float x, float y, float x1, float y1, float radius, int color) {
        glColor(color);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_CULL_FACE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glPushMatrix();
        glLineWidth(1F);
        glBegin(GL_POLYGON);

        float xRadius = (float) Math.min((x1 - x) * 0.5, radius);
        float yRadius = (float) Math.min((y1 - y) * 0.5, radius);
        quickPolygonCircle(x+xRadius,y+yRadius, xRadius, yRadius,180,270,4);
        quickPolygonCircle(x1-xRadius,y+yRadius, xRadius, yRadius,90,180,4);
        quickPolygonCircle(x1-xRadius,y1-yRadius, xRadius, yRadius,0,90,4);
        quickPolygonCircle(x+xRadius,y1-yRadius, xRadius, yRadius,270,360,4);

        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glDisable(GL_LINE_SMOOTH);
        glColor4f(1F, 1F, 1F, 1F);
    }

    public static void drawroundrect(double x,double y,double x2,double y2,int color) {
		drawRect((int) x,(int)  y,(int) x2,(int)  y2, color);
		drawRect((int) x, (int) y, (int) ((int) x2+1.5), (int) y-1, color);
		drawRect((int) x, (int) y2, (int) ((int) x2+1.5), (int) y2+1, color);
		drawRect((int) x2+1,(int)  y, (int) ((int) x2+0.5), (int) y-1, color);
		drawRect((int) x2+1, (int) y2, (int) ((int) x2+0.5), (int) y2+1, color);
    }

    public static void drawRoundRect(double d, double e, double g, double h, int color)
    {
        drawRect((float) (d + 1), (float) e, (float) (g - 1), (float) h, color);
        drawRect((float)d, (float)e + 1,(float) d + 1, (float)h - 1, color);
        drawRect((float)d + 1, (float)e + 1, (float) ((float)d + 0.5), (float) ((float)e + 0.5), color);
        drawRect((float)d + 1, (float)e + 1, (float) ((float)d + 0.5), (float) ((float) e + 0.5), color);
        drawRect((float)g - 1, (float)e + 1, (float) ((float)g - 0.5), (float) ((float) e + 0.5), color);
        drawRect((float)g - 1, (float)e + 1,(float) g, (float)h - 1, color);
        drawRect((float)d + 1, (float)h - 1, (float) ((float) d + 0.5), (float) ((float)h - 0.5), color);
        drawRect((float)g - 1, (float)h - 1, (float) ((float) g - 0.5), (float) ((float)h - 0.5), color);
    }

    public static void drawRectWH(final float x, final float y, final float w, final float h, final int color) {
        drawRect((int) x, (int) y, (int) (w + x), (int) (h + y), color);
    }

    public static void drawYBrect2(final float x, final float y, final float width, final float height, final float r, final int color) {
        drawSector(x + r + 0.5f, y + r + 0.5f, r, 90, 180, 1.0f, color, true, false);
        drawRect((float)0.0,(float) 0.0, (float)0.0,(float) 0.0, 0);
        drawSector(x + width - r - 0.5f, y + r + 0.5f, r, 90, 90, 1.0f, color, true, false);
        drawRect((float)0.0, (float)0.0, (float)0.0,(float) 0.0, 0);
        drawSector(x + r + 0.5f, y + height - r - 0.5f, r, 90, 270, 1.0f, color, true, false);
        drawRect((float)0.0,(float) 0.0, (float)0.0,(float) 0.0, 0);
        drawSector(x + width - r - 0.5f, y + height - r - 0.5f, r, 90, 360, 1.0f, color, true, false);
        drawRect((float)0.0,(float) 0.0, (float)0.0,(float) 0.0, 0);
        drawRect(x + r, y, x + r + width - 2.0f * r, y + height, color);
        drawRect(x, y + r, x + r, y + height - r, color);
        drawRect(x + width - r, y + r, x + width, y + height - r, color);
    }

    public static void drawSector(float x, float y, double r, final int angle, final int startAngle, final float lineWidth, final int c, final boolean isFull, final boolean isClose) {
        if (angle <= 0) {
            return;
        }
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        r *= 2.0;
        x *= 2.0f;
        y *= 2.0f;
        final float f2 = (c >> 24 & 0xFF) / 255.0f;
        final float f3 = (c >> 16 & 0xFF) / 255.0f;
        final float f4 = (c >> 8 & 0xFF) / 255.0f;
        final float f5 = (c & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glEnable(GL_ALPHA_TEST);
        GL11.glLineWidth(lineWidth);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(f3, f4, f5, f2);
        GL11.glBegin(3);
        for (int i = startAngle; i <= angle + startAngle; ++i) {
            final double x2 = Math.sin(i * 3.141592653589793 / 180.0) * r;
            final double y2 = Math.cos(i * 3.141592653589793 / 180.0) * r;
            GL11.glVertex2d((double)x + x2, (double)y + y2);
            if (isFull) {
                GL11.glVertex2d((double)x, (double)y);
            }
        }
        if (isClose && !isFull && angle < 360) {
            final double startX = Math.sin(startAngle * 3.141592653589793 / 180.0) * r;
            final double startY = Math.cos(startAngle * 3.141592653589793 / 180.0) * r;
            GL11.glVertex2d((double)x, (double)y);
            GL11.glVertex2d((double)x + startX, (double)y + startY);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }

    public static void drawCircle(float x, float y, float r, float lineWidth, boolean isFull, int color) {
      drawCircle(x, y, r, 10, lineWidth, 360, isFull, color);
    }
    
    public static void drawCircle(float cx, float cy, double r, int segments, float lineWidth, int part, boolean isFull, int c) {
  	    GL11.glScalef(0.5F, 0.5F, 0.5F);
  	    r *= 2.0D;
  	    cx *= 2.0F;
  	    cy *= 2.0F;
  	    float f2 = (c >> 24 & 0xFF) / 255.0F;
  	    float f3 = (c >> 16 & 0xFF) / 255.0F;
  	    float f4 = (c >> 8 & 0xFF) / 255.0F;
  	    float f5 = (c & 0xFF) / 255.0F;
  	    GL11.glEnable(3042);
  	    GL11.glLineWidth(lineWidth);
  	    GL11.glDisable(3553);
  	    GL11.glEnable(2848);
  	    GL11.glBlendFunc(770, 771);
  	    GL11.glColor4f(f3, f4, f5, f2);
  	    GL11.glBegin(3);
  	    for (int i = segments - part; i <= segments; i++) {
  	      double x = Math.sin(i * Math.PI / 180.0D) * r;
  	      double y = Math.cos(i * Math.PI / 180.0D) * r;
  	      GL11.glVertex2d(cx + x, cy + y);
  	      if (isFull)
  	        GL11.glVertex2d(cx, cy);
  	    } 
  	    GL11.glEnd();
  	    GL11.glDisable(2848);
  	    GL11.glEnable(3553);
  	    GL11.glDisable(3042);
  	    GL11.glScalef(2.0F, 2.0F, 2.0F);
  	  }

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

	public static Color effect(long offset, float brightness, int speed) {
		float hue = (float) (System.nanoTime() + (offset * speed)) / 1.0E10F % 1.0F;
		long color = Long.parseLong(Integer.toHexString(Integer.valueOf(Color.HSBtoRGB(hue, brightness, 1F)).intValue()), 16);
		Color c = new Color((int) color);
		return new Color(c.getRed()/255.0F, c.getGreen()/255.0F, c.getBlue()/255.0F, c.getAlpha()/255.0F);
	}

    public static void doFixTexture() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    }
}
