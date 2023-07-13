package qwq.wumie.utils.render.notifications;

import meteordevelopment.meteorclient.renderer.Mesh;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.render.color.Color;

import static org.lwjgl.system.MemoryUtil.memPutInt;

public class Renderer2DQuad extends Renderer2D {
    public Renderer2DQuad(boolean texture) {
        super(texture);
    }
}
