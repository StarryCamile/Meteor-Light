/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.hud.elements;

import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.utils.misc.MeteorIdentifier;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.report.ChatReportScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import qwq.wumie.gui.screens.FakeFlux;
import qwq.wumie.systems.modules.inject.combat.NameTagInject;
import qwq.wumie.utils.render.BezierCurve;
import qwq.wumie.utils.render.MSAAFramebuffer;
import qwq.wumie.utils.render.RenderUtil;

import java.util.ArrayList;
import java.util.List;

public class TargetHud extends HudElement {
    public static final HudElementInfo<TargetHud> INFO = new HudElementInfo<>(Hud.GROUP, "target-hud", "Displays information about your combat target.", TargetHud::new);

    public TargetHud() {
        super(INFO);
    }

    private List<PlayerEntity> targets = new ArrayList<>();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .defaultValue(Mode.Meteor)
        .build()
    );

    private final Setting<SettingColor> healthColor = sgGeneral.add(new ColorSetting.Builder()
        .name("health-color")
        .description("The color on the left of the health gradient.")
        .defaultValue(new SettingColor(255, 15, 15))
        .build()
    );

    private final Setting<StackMode> stackModeSetting = sgGeneral.add(new EnumSetting.Builder<StackMode>()
        .name("stack-mode")
        .defaultValue(StackMode.Down)
        .build()
    );


    public enum StackMode {
        Up,
        Down
    }

    public enum Mode {
        Meteor,
        DaBian,
        LOL
    }

    private final BezierCurve be = new BezierCurve(.35, .1, .25, 1);
    private double percent = 0,lastPercent = percent,offsetY = 0,lastOffsetY = offsetY,healWidth,lastHealWidth,removedHeal;

    @Override
    public void tick(HudRenderer renderer) {
        if (lastHealWidth != healWidth) {
            removedHeal = Math.max(0,healWidth-lastHealWidth);
        }

        lastPercent = percent;
        lastOffsetY = offsetY;
        lastHealWidth = healWidth;
        setSize(100,100);
        this.targets = Modules.get().get(KillAura.class).getTargets();
        super.tick(renderer);
    }

    @Override
    public void render(HudRenderer renderer) {
        renderer.post(() -> {
            double boxX = this.x;
            double boxY = this.y;
            double offsetY = 0;

            if (!this.targets.isEmpty()) percent = be.get(false, 12);
            else percent = be.get(true, 12);

            if (!targets.isEmpty()) {
                if (mode.get().equals(Mode.Meteor)) {
                    offsetY = RenderUtil.smoothTrans(画原版(renderer, boxX, boxY, offsetY, targets.get(0)), lastOffsetY);
                    return;
                }
            }

            for (PlayerEntity player : targets) {
                assert player != null;
                switch (mode.get()) {
                    case DaBian -> {
                       offsetY = RenderUtil.smoothTrans(画答辩(renderer,boxX,boxY,offsetY,player),lastOffsetY);
                    }
                    case Meteor -> {

                    }
                    case LOL -> {
                        offsetY = RenderUtil.smoothTrans(画sfz(renderer,boxX,boxY,offsetY,player),lastOffsetY);
                    }
                }
            }
        });
        super.render(renderer);
    }

    private double 画sfz(HudRenderer renderer,double boxX,double boxY,double offsetY,PlayerEntity player) {
        Identifier sfzId = new MeteorIdentifier("textures/screen/sfzbg.png");
        return offsetY;
    }

    private double 画原版(final HudRenderer renderer,final double boxX,final double boxY,double offsetY,final PlayerEntity player) {
        final double finalOffsetY = offsetY;
        MSAAFramebuffer.use(() -> {
            double yPos = boxY + finalOffsetY;
            double aniX = getWindowWidth() - (getWindowWidth() - boxX)*RenderUtil.smoothTrans(percent,lastPercent);
            GL.enableBlend();
            //画背景
            gameRender.drawRoundRect(aniX,yPos,250,80,5,new Color(82,82,82, 150));
            // 画头
            //gameRender.drawHead(player,aniX+5,yPos+5,30,30);
            drawHead(gameRender.getSkinTexture(player),renderer,MathHelper.floor(aniX+5), MathHelper.floor(yPos+7),60);
            //gameRender.context.drawTexture(gameRender.getSkinTexture(player), MathHelper.floor(aniX+5), MathHelper.floor(yPos+7),8f,8f,60,60,60,60);
            // 画名字
            TextRenderer font = TextRenderer.get();
            double scale = 1.5;
            double nameX = aniX + 80;
            double nameY = boxY + 17;
            font.render(player.getName().getString(),nameX,nameY, PlayerUtils.getPlayerColor(player,Color.WHITE),true,scale);
            // 画血量
            gameRender.drawRoundRect(aniX+80,boxY + 55,150,12,5,new Color().a(170));
            float absorption = player.getAbsorptionAmount();
            int health = Math.round(player.getHealth() + absorption);
            double healthPercent = health / (player.getMaxHealth() + absorption);
            double healthWidth = 150 * healthPercent;
            this.healWidth = healthWidth;
            // 画减少的血量
            gameRender.drawRoundRect(aniX+40+healthWidth,boxY + 55,removedHeal,12,6,Color.YELLOW);
            if (removedHeal > 0) {
                removedHeal = smoothMove(removedHeal,0);
            }

            gameRender.drawRoundRect(aniX + 80,boxY+55,healWidth,12,5,PlayerUtils.getPlayerColor(player,new Color(10,250,10,210)));
            GL.disableBlend();
        });

        if (stackModeSetting.get().equals(StackMode.Down)) {
            offsetY += 40 + 2;
        }
        if (stackModeSetting.get().equals(StackMode.Up)) {
            offsetY -= 40 + 2;
        }
        return offsetY;
    }

    private double 画答辩(HudRenderer renderer,double boxX,double boxY,double offsetY,PlayerEntity player) {
            TextRenderer font = TextRenderer.get();
            double bgY = boxY + offsetY;
            double nameWidth = gameRender.getStringWidth(player.getName().getString(), 1);
            double nameHeight = gameRender.getStringHeight(false, 1);
            double bgWidth = 5 + 60 + 80 + nameWidth;
            double bgHeight = 5 + 60 + 5;
            float absorption = player.getAbsorptionAmount();
            int health = Math.round(player.getHealth() + absorption);
            double healthPercent = health / (player.getMaxHealth() + absorption);
            double hpWidth = ((bgWidth - (5 + 60 + 5)) - 5) * healthPercent;

            gameRender.drawRect(boxX, bgY, bgWidth, bgHeight, new Color(0, 0, 0, 153));
            gameRender.drawEntity(player,(int) boxX+35,(int) (bgY+bgHeight -5),31);
            String pName;
            if (player.getDisplayName().getString().startsWith("§")) {
                if (player.getDisplayName().getString().charAt(1) == '1' ||
                        player.getDisplayName().getString().charAt(1) == '2' ||
                        player.getDisplayName().getString().charAt(1) == '3' ||
                        player.getDisplayName().getString().charAt(1) == '4' ||
                        player.getDisplayName().getString().charAt(1) == '5' ||
                        player.getDisplayName().getString().charAt(1) == '6' ||
                        player.getDisplayName().getString().charAt(1) == '7' ||
                        player.getDisplayName().getString().charAt(1) == '8' ||
                        player.getDisplayName().getString().charAt(1) == '9' ||
                        player.getDisplayName().getString().charAt(1) == '0' ||
                        player.getDisplayName().getString().charAt(1) == 'a' ||
                        player.getDisplayName().getString().charAt(1) == 'b' ||
                        player.getDisplayName().getString().charAt(1) == 'c' ||
                        player.getDisplayName().getString().charAt(1) == 'd' ||
                        player.getDisplayName().getString().charAt(1) == 'e' ||
                        player.getDisplayName().getString().charAt(1) == 'f'
                ) {
                    pName = player.getDisplayName().getString().replace("§" + player.getDisplayName().getString().charAt(1), "");
                } else {
                    pName = player.getDisplayName().getString().replace("§", "");
                }
            } else {
                pName = player.getDisplayName().getString();
            }
            font.render(pName, boxX + 70, bgY + 5, Color.WHITE);
            font.render(health + "HP", boxX + 70, bgY + 6 + nameHeight, Color.WHITE, 0.7);

            gameRender.drawRect(boxX + 5 + 65 - 1, bgY + bgHeight - 5 - 10 - 1, bgWidth - 70 - 5, 11, new Color(0, 0, 0, healthColor.get().a));
            gameRender.drawRect(boxX + 5 + 65, bgY + bgHeight - 5 - 10, hpWidth, 10, healthColor.get());

            if (stackModeSetting.get().equals(StackMode.Down)) {
                offsetY += bgHeight + 5;
            }
            if (stackModeSetting.get().equals(StackMode.Up)) {
                offsetY -= bgHeight + 5;
            }

        return offsetY;
    }
}
