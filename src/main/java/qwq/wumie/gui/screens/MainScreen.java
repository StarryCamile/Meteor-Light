/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.WindowResizedEvent;
import meteordevelopment.meteorclient.renderer.Framebuffer;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.PostProcessRenderer;
import meteordevelopment.meteorclient.renderer.Shader;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Blur;
import meteordevelopment.meteorclient.utils.misc.MeteorIdentifier;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.listeners.ConsumerListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import qwq.wumie.utils.render.BlurUtils;
import qwq.wumie.version.GameRenderer;

import java.util.Optional;

public class MainScreen extends Screen {
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
    private final RotatingCubeMapRenderer backgroundRenderer;
    private long backgroundFadeStart;
    boolean doBackgroundFade = true;
    public static final String blurTaskName = "MainScreen";

    public MainScreen() {
        super(Text.translatable("narrator.screen.title"));
        this.backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
    }

    public boolean shouldPause() {
        return false;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public static MeteorIdentifier IDSINGLEPLAYER, IDMULITPLAYER,IDEXIT,IDLANGUAGE,IDSETTINGS,IDLOGO;

    static {
        IDSINGLEPLAYER = new MeteorIdentifier("textures/mainmenu/singleplayer.png");
        IDMULITPLAYER = new MeteorIdentifier("textures/mainmenu/mulitplayer.png");
        IDSETTINGS = new MeteorIdentifier("textures/mainmenu/settings.png");
        IDLOGO = new MeteorIdentifier("textures/mainmenu/logo.png");
        IDLANGUAGE = new MeteorIdentifier("textures/mainmenu/language.png");
    }

    @Override
    public void renderBackground(DrawContext c) {
        // bar start
        Color bgColor = new Color(117,117,125,190);
        Color lineColor = new Color(152,151,150,220);
        //left
        int leftBarWidth = 110;
        int leftBarHeight = this.height;
        int left_part1Y = 80;
        int left_part2Y = 270;
        //right
        int rightBarX = width - 110;
        int rightBarWidth = 110;
        int rightBarHeight = this.height;
        int right_part1Y = 270;

        c.fill(0,0,leftBarWidth,leftBarHeight,bgColor.toAWTColor().getRGB());
        c.fill(rightBarX,0,rightBarWidth,rightBarHeight,bgColor.toAWTColor().getRGB());
        c.fill(0,left_part1Y,leftBarWidth,2,lineColor.toAWTColor().getRGB());
        c.fill(0,left_part2Y,leftBarWidth,2,lineColor.toAWTColor().getRGB());
        c.fill(rightBarX,right_part1Y,rightBarWidth,2,lineColor.toAWTColor().getRGB());
        // bar end
        super.renderBackground(c);
    }

    protected void init() {
        BlurUtils.addBlurTask(blurTaskName);
     /*   //Logo
        addDrawableChild(new ImageRender( 11,35,87,24,IDLOGO,false));

        //Buttons
        // OldTitleMenu Button
        addDrawableChild(new ButtonWidget(width-100,height-25,95,20,Text.literal("OldTitleMenu"),button -> {
            client.setScreen(new TitleScreen());
        }));

        int leftBarButtonX = 30;
        addDrawableChild(new ImageButton(leftBarButtonX,100,50,50,IDSINGLEPLAYER,button -> {
            this.client.setScreen(new SelectWorldScreen(this));
        },false));
        addDrawableChild(new ImageButton(leftBarButtonX,200,50,50,IDMULITPLAYER,button -> {
            Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            this.client.setScreen(screen);
        },false));
        addDrawableChild(new ImageButton(leftBarButtonX,315,50,50,IDSETTINGS,button -> {
            this.client.setScreen(new OptionsScreen(this, this.client.options));
        },false));
        addDrawableChild(new ImageButton(leftBarButtonX,415,50,50,IDLANGUAGE,button -> {
            this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager()));
        },false));
        addDrawableChild(new ImageButton(leftBarButtonX,this.height-80,50,50,IDEXIT,button -> {
            this.client.scheduleStop();
        },false));*/
    }

    @Override
    public void render(DrawContext c, int mouseX, int mouseY, float delta) {
        if (this.backgroundFadeStart == 0L && doBackgroundFade) {
            this.backgroundFadeStart = Util.getMeasuringTimeMs();
        }

        float f = this.doBackgroundFade ? (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0F : 1.0F;
        this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0F, 1.0F));
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
        c.drawTexture( PANORAMA_OVERLAY, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        BlurUtils.renderBlur(blurTaskName);
        super.render(c, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        BlurUtils.removeBlurTask(blurTaskName);
        super.close();
    }
}
