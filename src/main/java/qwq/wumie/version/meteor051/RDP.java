package qwq.wumie.version.meteor051;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import qwq.wumie.renderer.text.TTFFontRender;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.utils.misc.MeteorIdentifier;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class RDP {
    public RenderSystem renderSystem = new RenderSystem();
    public Renderer2D render2d = Renderer2D.COLOR;
    public Renderer3D render3d = new Renderer3D();
    public MatrixStack matrices = RenderSystem.getModelViewStack();
    public DrawContext context;

    public boolean isMouseHoveringRect(float x, float y, int width, int height, int mouseX, int mouseY){
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    public boolean isMouseHoveringRect(float x, float y, int width, int height, double mouseX, double mouseY){
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    public Identifier getSkinByName(String name) {
        if (mc.getNetworkHandler() == null) return null;
        for (String part : name.split("(§.)|[^\\w]")) {
            if (part.isBlank()) continue;
            PlayerListEntry p = mc.getNetworkHandler().getPlayerListEntry(part);
            if (p != null) {
                return p.getSkinTexture();
            }
        }
        return null;
    }

    public Color getColorForString(String code, int alpha) {
        if (code.equals("0")) {
            return new Color(0, 0, 0, alpha);
        }
        if (code.equals("1")) {
            return new Color(0, 0, 170, alpha);
        }
        if (code.equals("2")) {
            return new Color(0, 170, 0, alpha);
        }
        if (code.equals("3")) {
            return new Color(0, 170, 170, alpha);
        }
        if (code.equals("4")) {
            return new Color(170, 0, 0, alpha);
        }
        if (code.equals("5")) {
            return new Color(170, 0, 170, alpha);
        }
        if (code.equals("6")) {
            return new Color(255, 170, 0, alpha);
        }
        if (code.equals("7")) {
            return new Color(170, 170, 170, alpha);
        }
        if (code.equals("8")) {
            return new Color(85, 85, 85, alpha);
        }
        if (code.equals("9")) {
            return new Color(85, 85, 255, alpha);
        }
        if (code.equals("a")) {
            return new Color(85, 255, 85, alpha);
        }
        if (code.equals("b")) {
            return new Color(85, 255, 255, alpha);
        }
        if (code.equals("c")) {
            return new Color(255, 85, 85, alpha);
        }
        if (code.equals("d")) {
            return new Color(255, 85, 255, alpha);
        }
        if (code.equals("e")) {
            return new Color(255, 255, 85, alpha);
        }
        return new Color(255, 255, 255, alpha);
    }

    public void drawHead(PlayerEntity entity, double x, double y, double width, double height) {
        Identifier headTexture = getSkinTexture(entity);

        GL.bindTexture(headTexture);
        GL.textureParam(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL.textureParam(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        render2d.texQuad(x,y,width,height,Color.WHITE);
    }

    public void drawImage(MeteorIdentifier image, double x, double y, double width, double height) {
        GL.bindTexture(image);
        render2d.texQuad(x,y,width,height,Color.WHITE);
    }

    public Identifier getSkinTexture(PlayerEntity entity) {
        try {
            return mc.getNetworkHandler().getPlayerListEntry(entity.getUuid()).getSkinTexture();
        } catch (Exception e) {
            try {
                return ((AbstractClientPlayerEntity) entity).getSkinTexture();
            } catch (Exception e1) {
                DefaultSkinHelper.getTexture();
            }
        }

        return DefaultSkinHelper.getTexture();
    }

    public double getStringWidth(String text,double fontScale) {
        double object;
        Fonts.RENDERER.begin(fontScale,false,true);
        object = Fonts.RENDERER.getWidth(text,false);
        Fonts.RENDERER.end();
        return object;
    }

    public double getStringWidth(String text) {
        return TextRenderer.get().getWidth(text);
    }

    public double getStringWidth(String text,boolean shadow,double fontScale) {
        double object;
        Fonts.RENDERER.begin(fontScale,false,false);
        object = Fonts.RENDERER.getWidth(text,shadow);
        Fonts.RENDERER.end();
        return object;
    }

    public double getStringWidth(TTFFontRender font, String text, boolean shadow) {
        double object;
        object = font.getWidth(text,shadow);
        return object;
    }

    public double getStringWidth(TTFFontRender font, String text, boolean shadow,double fontScale) {
        double object;
        object = font.getWidth(text,shadow,fontScale);
        return object;
    }

    public double getStringHeight(boolean shadow,double fontScale) {
        double object;
        Fonts.RENDERER.begin(fontScale,false,false);
        object = Fonts.RENDERER.getHeight(shadow);
        Fonts.RENDERER.end();
        return object;
    }

    public double getStringHeight(TTFFontRender font,boolean shadow) {
        double object;
        object = font.getHeight(shadow);
        return object;
    }

    public double getStringHeight(TTFFontRender font,boolean shadow,double fontScale) {
        double object;
        object = font.getHeight(shadow,fontScale);
        return object;
    }

    public double getStringHeight(boolean shadow) {
        return TextRenderer.get().getHeight(shadow);
    }

    public void drawRect(double x, double y, double width, double height, Color color) {
        Renderer2D.COLOR.begin();
        Renderer2D.COLOR.quad(x, y, width, height, color);
        Renderer2D.COLOR.render(null);
    }

    public void drawRoundRect(double x, double y, double width, double height,double radius, Color color) {
        Renderer2D.COLOR.begin();
        Renderer2D.COLOR.quadRounded(x, y, width, height,radius, color);
        Renderer2D.COLOR.render(null);
    }

    public void drawRoundRectNoTop(double x, double y, double width, double height,double radius, Color color) {
        Renderer2D.COLOR.begin();
        Renderer2D.COLOR.quadRounded(x, y, width, height, color,radius,false);
        Renderer2D.COLOR.render(null);
    }

    public void drawEntity(LivingEntity entity, int x, int y, int size) {
        float yaw = entity.getYaw();
        float pitch = entity.getPitch();
        InventoryScreen.drawEntity(context,x,y,size,-yaw,-pitch,entity);
    }

    public void drawRect(double x, double y, double width, double height, int color) {
        Renderer2D.COLOR.begin();
        Renderer2D.COLOR.quad(x, y, width, height, new Color(color));
        Renderer2D.COLOR.render(null);
    }

    public void drawLine(double x1, double y1, double x2, double y2, Color color) {
        Renderer2D.COLOR.begin();
        Renderer2D.COLOR.line(x1, y1, x2, y2, color);
        Renderer2D.COLOR.render(null);
    }

    public void drawLine(double x1, double y1, double x2, double y2, int color) {
        Renderer2D.COLOR.begin();
        Renderer2D.COLOR.line(x1, y1, x2, y2, new Color(color));
        Renderer2D.COLOR.render(null);
    }

    public double getAnimationState(double animation, double finalState, double speed) {
        float add = (float) (0.01 * speed);
        if (animation < finalState) {
            if (animation + add < finalState)
                animation += add;
            else
                animation = finalState;
        } else {
            if (animation - add > finalState)
                animation -= add;
            else
                animation = finalState;
        }
        return animation;
    }

    public double getAnimationState2(double animation, double finalState, double speed) {
        float add = (float) (0.01 * speed);
        if (animation < finalState) {
            animation = finalState;
        } else {
            if (animation - add > finalState)
                animation -= add;
            else
                animation = finalState;
        }
        return animation;
    }
}
