/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.hud.elements.custom;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import qwq.wumie.utils.render.RenderUtil;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;

import static com.mojang.blaze3d.systems.RenderSystem.recordRenderCall;

public class CustomQuad extends HudElement {
    public static final HudElementInfo<CustomQuad> INFO = new HudElementInfo<>(Hud.GROUP, "custom-quad", "render quad.", CustomQuad::new);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> customXY = sgGeneral.add(new BoolSetting.Builder()
        .name("custom-xy")
        .defaultValue(false)
        .build()
    );

    public final Setting<Double> quadX = sgGeneral.add(new DoubleSetting.Builder()
        .name("x")
        .sliderMin(-scaled.width)
        .sliderMax(scaled.width)
        .defaultValue(scaled.width / 2)
        .visible(customXY::get)
        .build()
    );

    public final Setting<Double> quadY = sgGeneral.add(new DoubleSetting.Builder()
        .name("y")
        .sliderMin(-scaled.height)
        .sliderMax(scaled.height)
        .defaultValue(scaled.height / 2)
        .visible(customXY::get)
        .build()
    );

    public final Setting<Double> quadWidth = sgGeneral.add(new DoubleSetting.Builder()
        .name("width")
        .min(1.0)
        .defaultValue(100.0)
        .build()
    );

    public final Setting<Double> quadHeight = sgGeneral.add(new DoubleSetting.Builder()
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

    private final Setting<SettingColor> bottomColor = sgGeneral.add(new ColorSetting.Builder()
            .name("color1")
            .defaultValue(new SettingColor(255,255,255,255))
            .build()
    );

    public final Setting<Boolean> test = sgGeneral.add(new BoolSetting.Builder()
        .name("test")
        .defaultValue(false)
        .build()
    );

    public CustomQuad() {
        super(INFO);
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    @Override
    public void tick(HudRenderer renderer) {
        if (quadHeight.get() == 1 || quadWidth.get() == 1) {
            setSize(20.0,20.0);
        } else {
            setSize(quadWidth.get(),quadHeight.get());
        }
        super.tick(renderer);
    }

    @EventHandler
    public void onGameLeft(GameLeftEvent  e) {
        test.set(false);
    }

    @Override
    public void render(HudRenderer renderer) {
        renderer.post(() -> {
            double x;
            double y;
            if (test.get()) {
                x = this.x;
                y = this.y;
                recordRenderCall(() -> {
                    RenderUtil.R2DUtils.drawGradientRect((float) x, (float) y, quadWidth.get().floatValue(), quadHeight.get().floatValue(), color.get().toAWTColor().getRGB(), bottomColor.get().toAWTColor().getRGB());
                });
            } else {
                if (customXY.get()) {
                    x = quadX.get();
                    y = quadY.get();
                } else {
                    x = this.x;
                    y = this.y;
                }

                gameRender.drawRect(x, y, quadWidth.get(), quadHeight.get(), color.get());
            }
        });
        super.render(renderer);
    }
}
