package qwq.wumie.version.meteor051;

import meteordevelopment.meteorclient.renderer.CRender;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class HudRendererPlus {
    public final Renderer2D R2D_TEX = new Renderer2D(true);
    public final Renderer2D R2D = new Renderer2D(false);

    public void roundedQuad(double x, double y, double width, double height,float radius, Color color) {
        CRender render = CRender.INSTANCE;
        render.draw2d.begin();
        render.draw2d.quadRounded(x,y,width,height,color,radius,true);
        render.draw2d.render(null);
    }

    public void texture(double x,double y,double width,double height,Color color) {
        Renderer2D.TEXTURE.begin();
        Renderer2D.TEXTURE.texQuad(x, y, width, height, color);
        Renderer2D.TEXTURE.render(null);
    }
}
