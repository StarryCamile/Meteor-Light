package qwq.wumie.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.misc.MeteorIdentifier;
import meteordevelopment.meteorclient.utils.player.TitleScreenCredits;
import net.minecraft.client.gui.DrawContext;
import qwq.wumie.utils.render.BezierCurve;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import qwq.wumie.gui.LGuiRender;
import qwq.wumie.utils.render.MSAAFramebuffer;
import qwq.wumie.utils.render.RenderUtil;

public class FakeFlux extends LGuiRender {
    private BezierCurve aniCur = new BezierCurve(.35, .1, .25, 1);
    private boolean loaded;

    private final double buttonHeight = 30 / 2;
    private final double buttonWidth = 185 / 2;
    private final double buttonOffset = 6.5 / 2;
    private final Color[] colors = new Color[]{
            new Color(0, 26, 26, 170), // Rect Color
            new Color(27, 52, 53, 170), // Outline Color
            new Color(255, 255, 240, 170) // Outline Hover Color
    };

    private final String centerButton = "Alt Manager";

    private final String[] buttons = new String[]{
            "Singleplayer",
            "Multiplayer",
            "Alt Manager",
            "Options",
            "Languages",
            "Quit Game"
    };

    private final MeteorIdentifier logo = new MeteorIdentifier("textures/screen/logo.png");
    private final MeteorIdentifier bg = new MeteorIdentifier("textures/screen/background.png");

    private final boolean meshRender = true;

    private int ticks = 0;

    private double percent = 0,lastPercent = percent;

    public FakeFlux() {
    }

    @Override
    protected void init() {
        ticks = 0;
        super.init();
    }

    @Override
    public void tick() {
        lastPercent = percent;

        if (hov) {
            ticks = 0;
        }

        if (ticks >= 5000) {
            if (!hov) {
                if (loaded) {
                    loaded = false;
                }
            }
        } else {
            ticks++;
        }
        super.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            loaded = !loaded;

            if (isMouseHoveringRect(0,0,5,5,mouseX,mouseY)) {
                mc.setScreen(new NewJelloScreen());
            }
        }

        double centerA = (this.width / 2);
        double centerB = this.height / 2;
        double centerX = centerA - (buttonWidth / 2);
        double centerY = centerB - (buttonHeight / 2);

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            double y = (-((buttonHeight * 2) + (buttonOffset * 2)) + centerY) * percent;
            for (String name : buttons) {
                boolean hovered = isMouseHoveringRect(centerX, y, buttonWidth, buttonHeight, mouseX, mouseY);
                if (hovered) {
                    switch (name) {
                        case "Singleplayer" -> {
                            minecraft.setScreen(new SelectWorldScreen(this));
                        }
                        case "Multiplayer" -> {
                            if (!this.minecraft.options.skipMultiplayerWarning) {
                                this.minecraft.options.skipMultiplayerWarning = true;
                                this.minecraft.options.write();
                            }

                            Screen screen = new MultiplayerScreen(this);
                            this.minecraft.setScreen(screen);
                        }
                        case "Alt Manager" -> {
                            this.mc.setScreen(GuiThemes.get().accountsScreen());
                        }
                        case "Options" -> {
                            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
                        }
                        case "Languages" -> {
                            this.minecraft.setScreen(new LanguageOptionsScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()));
                        }
                        case "Quit Game" -> {
                            this.minecraft.stop();
                        }
                        default -> {
                            System.out.println(name + " Button Clicked");
                        }
                    }
                }
                y += (buttonHeight + buttonOffset);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean hov;

    public double textWidth(String s) {
        TextRenderer text = TextRenderer.get();
        return text.getWidth(s,fontScale);
    }

    public double textHeight() {
        TextRenderer text = TextRenderer.get();
        return text.getHeight(false,fontScale);
    }

    public void text(String s,double x,double y,Color color) {
        TextRenderer text = TextRenderer.get();
        text.render(s,x,y,color,fontScale);
    }

    public void centerText(String s,double x,double y,Color color) {
        TextRenderer text = TextRenderer.get();
        double fX = text.getWidth(s,fontScale);
        double sX = x - (fX / 2);
        text.render(s,sX,y,color,fontScale);
    }

    double fontScale = 0.6;

    @Override
    public void draw(DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        loaded = true;
        double centerA = (this.width / 2);
        double centerB = this.height / 2;
        double centerX = centerA - (buttonWidth / 2);
        double centerY = centerB - (buttonHeight / 2);

        double Y = (-((buttonHeight * 2) + (buttonOffset * 2)) + centerY);
        for (String name : buttons) {
            boolean hovered = isMouseHoveringRect(centerX, Y, buttonWidth, buttonHeight+buttonOffset, mouseX, mouseY);
            this.hov = hovered;
            if (hovered) {
                if (!loaded) {
                    loaded = true;
                }
            }
            Y += (buttonHeight + buttonOffset);
        }

        // bg render
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        drawContext.drawTexture(bg, 0, 0, 0, 0, width, height, width, height);

        if (this.loaded) percent = aniCur.get(false, 12);
        else percent = aniCur.get(true, 12);

        if (percent != 0) {
            // Logo Render
            // calc pos
            double logoBox = 50;
            double logoX = centerA - (logoBox / 2);
            double logoY = 40;

            logoY = ((-((buttonHeight * 2) + (buttonOffset * 2)) + centerY) -80) * RenderUtil.smoothTrans(lastPercent,percent);

            // Draw using mesh
            renderer.texture(logo,logoX, logoY, logoBox, logoBox, Color.WHITE);

            // MC RenderSystem
            if (!meshRender) {
                RenderSystem.setShaderTexture(0, logo);
                RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                drawContext.drawTexture(logo,(int) logoX, (int) logoY, 0, 0, (int) logoBox, (int) logoBox,  (int)logoBox,  (int)logoBox);
            }

            MSAAFramebuffer.use(() -> {
                // Render Buttons
                double y = (-((buttonHeight * 2) + (buttonOffset * 2)) + centerY) * RenderUtil.smoothTrans(lastPercent, percent);
                for (String name : buttons) {
                    // render quad box
                    double buttonRoundRadius = 3;
                    renderer.R2D.begin();
                    boolean hovered = isMouseHoveringRect(centerX, y, buttonWidth, buttonHeight, mouseX, mouseY);
                    renderer.R2D.quadRoundedOutline(centerX, y, buttonWidth, buttonHeight, hovered ? colors[2] : colors[1], buttonRoundRadius - 0.1, 0.5);
                    renderer.R2D.quadRounded(centerX + 0.5, y + 0.5, buttonWidth - 0.5*2, buttonHeight - 0.5*2, buttonRoundRadius, colors[0]);
                    renderer.R2D.render(null);

                    Color fontColor = Color.WHITE;
                    double fY = y + (buttonHeight / 2) - (textHeight() / 2);
                    centerText(name, centerA, fY, fontColor);
                    y += (buttonHeight + buttonOffset);
                }
            });
        }

        // render info text
        String copyright = "Copyright Mojang AB. Do not distribute!";
        String versionInfo = "Minecraft " + SharedConstants.getGameVersion().getName();
        if (this.mc.isDemo()) {
            versionInfo = versionInfo + " Demo";
        } else {
            versionInfo = versionInfo + ("release".equalsIgnoreCase(this.mc.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
        }
        if (MinecraftClient.getModStatus().isModded()) {
            versionInfo = versionInfo + I18n.translate("menu.modded");
        }

        String updateInfo = "(Latest)";
        String clientInfo = MeteorClient.clientName + " " + MeteorClient.clientVersion;
        double fontHeight = textHeight();
        // render copyright --- left
        double textX = 0.2;
        double textOffset = (fontHeight + 0.5);
        double textY = this.height - textOffset + 1;
        text(copyright, (float) textX, (float) textY, Color.WHITE);
        textY = textY - textOffset;
        text(versionInfo, (float) textX, (float) textY, Color.WHITE);
        textY = textY - textOffset;
        text(clientInfo, (float) textX, (float) textY, Color.WHITE);
        double tempWidth = textWidth(clientInfo)+0.5;
        text(updateInfo,tempWidth,textY,Color.GREEN);

        // render ciu -- right
        String clientDevInfo = MeteorClient.clientName + " is developed by The Meteor Team & WuMie";
        String userInfo = "Welcome, " + mc.getSession().getUsername();

        textX = this.width - textWidth(clientDevInfo);
        textY = this.height - textOffset + 1;
        text(clientDevInfo, (float) textX, (float) textY, Color.WHITE);
        textX = this.width - textWidth(userInfo);
        textY = textY - textOffset;
        text( userInfo, (float) textX, (float) textY, Color.WHITE);

        if (Config.get().titleScreenCredits.get()) TitleScreenCredits.render(drawContext);
    }
}
