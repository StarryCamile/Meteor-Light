/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.hud.elements.custom;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class CustomLine extends HudElement {
    public static final HudElementInfo<CustomLine> INFO = new HudElementInfo<>(Hud.GROUP, "custom-line", "render line.", CustomLine::new);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> customXY = sgGeneral.add(new BoolSetting.Builder()
        .name("custom-xy")
        .defaultValue(false)
        .build()
    );

    public final Setting<Double> lineX = sgGeneral.add(new DoubleSetting.Builder()
        .name("x")
        .sliderMin(-scaled.width)
        .sliderMax(scaled.width)
        .defaultValue(scaled.width / 2)
        .visible(customXY::get)
        .build()
    );

    public final Setting<Double> lineY = sgGeneral.add(new DoubleSetting.Builder()
        .name("y")
        .sliderMin(-scaled.height)
        .sliderMax(scaled.height)
        .defaultValue(scaled.height / 2)
        .visible(customXY::get)
        .build()
    );

    public final Setting<Double> lineWidth = sgGeneral.add(new DoubleSetting.Builder()
        .name("width")
        .min(1.0)
        .defaultValue(100.0)
        .build()
    );

    public final Setting<Double> lineHeight = sgGeneral.add(new DoubleSetting.Builder()
        .name("height")
        .min(1.0)
        .defaultValue(100.0)
        .build()
    );

    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color")
        .defaultValue(new SettingColor(255,255,255,255))
        .build()
    );

    public CustomLine() {
        super(INFO);
    }

    @Override
    public void tick(HudRenderer renderer) {
        if (lineHeight.get() == 1 || lineWidth.get() == 1) {
            setSize(20.0,20.0);
        } else {
            setSize(lineWidth.get(),lineHeight.get());
        }
        super.tick(renderer);
    }

    @Override
    public void render(HudRenderer renderer) {
        renderer.post(() -> {
            double x;
            double y;
            if (customXY.get()) {
                x = lineX.get();
                y= lineY.get();
            } else {
                x = this.x;
                y = this.y;
            }

            gameRender.drawLine(x,y,lineWidth.get(),lineHeight.get(),color.get());

        });
        super.render(renderer);
    }
}
