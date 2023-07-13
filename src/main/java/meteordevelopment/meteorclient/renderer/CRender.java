package meteordevelopment.meteorclient.renderer;

import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import qwq.wumie.utils.render.notifications.Renderer2DQuad;
import net.minecraft.client.util.math.MatrixStack;

public class CRender {
    public final Renderer2D R2D;
    public final Renderer2D TR2D;
    public final Renderer3D R3D;
    public final Renderer2DQuad draw2d;
    public final Renderer2DQuad draw2dTexture;
    public Mesh mesh = new ShaderMesh(Shaders.POS_COLOR,DrawMode.Triangles, Mesh.Attrib.Vec2, Mesh.Attrib.Color);
    public static CRender INSTANCE;


    public CRender() {
        INSTANCE = this;
        R2D = new Renderer2D(false);
        TR2D = new Renderer2D(true);
        R3D = new Renderer3D();
        draw2d = new Renderer2DQuad(false);
        draw2dTexture = new Renderer2DQuad(true);
    }

    public void begin(DrawMode mode) {
        this.mesh = new ShaderMesh(Shaders.POS_COLOR,mode, Mesh.Attrib.Vec2, Mesh.Attrib.Color);
        this.mesh.begin();
    }

    public void drawRect(MatrixStack matrices,double x, double y, double width, double height, Color color) {
        begin(DrawMode.Triangles);
        mesh.quad(
                mesh.vec2(x, y).color(color).next(),
                mesh.vec2(x, y + height).color(color).next(),
                mesh.vec2(x + width, y + height).color(color).next(),
                mesh.vec2(x + width, y).color(color).next()
        );
        end(matrices);
    }

    public void drawLine(MatrixStack matrices,double x, double y, double x2, double y2, Color color) {
        begin(DrawMode.Lines);
        mesh.line(
                mesh.vec2(x, y).color(color).next(),
                mesh.vec2(x2, y2).color(color).next()
        );
        end(matrices);
    }

    public void drawRoundedRect(double x, double y, double width, double height, double radius, Color color) {
        this.drawRoundedRect(null,x, y, width, height, radius, color);
    }

    public void drawRoundedRect(MatrixStack matrices,double x, double y, double width, double height, double radius, Color color) {
        draw2d.begin();
        draw2d.quadRounded(x, y, width, height, color, radius, true);
        draw2d.end();
        draw2d.render(matrices);
    }

    public void drawRoundedRect(MatrixStack matrices,double x, double y, double width, double height, double radius, Color color, boolean roundTop) {
        draw2d.begin();
        draw2d.quadRounded(x, y, width, height, color, radius, roundTop);
        draw2d.end();
        draw2d.render(matrices);
    }

    public void drawRect(double x, double y, double width, double height, Color color) {
        this.drawRect(new MatrixStack(),x,y,width,height,color);
    }

    public void drawLine(double x, double y, double x2, double y2, Color color) {
        this.drawLine(new MatrixStack(),x,y,x2,y2,color);
    }

    public void end(MatrixStack matrices) {
        this.mesh.end();
        this.mesh.render(matrices);
    }

    public void end() {
        this.end(new MatrixStack());
    }

    public static class Immediate {
        public static void drawRect(MatrixStack matrices,double x,double y,double width,double height,Color color,boolean render3d) {
            CRender render = new CRender();
            if (render3d) Utils.unscaledProjection();
            render.drawRect(matrices,x,y,width,height,color);
            if (render3d) Utils.scaledProjection();
        }

        public static void drawRect(double x,double y,double width,double height,Color color,boolean render3d) {
            drawRect(new MatrixStack(),x,y,width,height,color,render3d);
        }

        public static void drawLine(MatrixStack matrices,double x,double y,double x2,double y2,Color color,boolean render3d) {
            CRender render = new CRender();
            if (render3d) Utils.unscaledProjection();
            render.drawLine(matrices,x,y,x2,y2,color);
            if (render3d) Utils.scaledProjection();
        }

        public static void drawLine(double x,double y,double x2,double y2,Color color,boolean render3d) {
            drawLine(new MatrixStack(),x,y,x2,y2,color,render3d);
        }
    }
}
