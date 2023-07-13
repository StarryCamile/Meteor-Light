package qwq.wumie.version.V1192;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;

public class BufferRenderer1192 extends BufferRenderer {
    public static void drawWithShader(BufferBuilder.BuiltBuffer buffer) {
        drawWithGlobalProgram(buffer);
    }
}
