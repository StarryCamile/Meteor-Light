/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.renderer.text;

import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.util.math.MatrixStack;

public interface TextRenderer {
    static TextRenderer get() {
        return Config.get().customFont.get() ? Fonts.RENDERER : VanillaTextRenderer.INSTANCE;
    }

    void setAlpha(double a);

    void begin(double scale, boolean scaleOnly, boolean big);
    default void begin(double scale) { begin(scale, false, false); }
    default void begin() { begin(1, false, false); }

    default void beginBig() { begin(1, false, true); }

    double getWidth(String text, int length, boolean shadow, double fontScale);
    double getWidth(String text, int length, boolean shadow);
    default double getWidth(String text, boolean shadow) { return getWidth(text, text.length(), shadow); }
    default double getWidth(String text, boolean shadow,double fontScale) { return getWidth(text, text.length(), shadow,fontScale); }
    default double getWidth(String text) { return getWidth(text, text.length(), false); }
    default double getWidth(String text,double fontScale) { return getWidth(text, text.length(), false,fontScale); }

    double getHeight(boolean shadow);
    double getHeight(boolean shadow,double fontScale);
    default double getHeight() { return getHeight(false); }

    double render(String text, double x, double y, Color color, boolean shadow);
    double render(String text, double x, double y, Color color, boolean shadow,double fontScale);
    default double render(String text, double x, double y, Color color) { return render(text, x, y, color, false); }
    default double render(String text, double x, double y,Color color ,double fontScale) { return render(text, x, y, color, false,fontScale); }

    boolean isBuilding();

    default void end() { end(null); }
    void end(MatrixStack matrices);
}
