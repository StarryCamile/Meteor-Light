package qwq.wumie.version.V1192;

import meteordevelopment.meteorclient.renderer.Shader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.resource.ResourceManager;

public class GameRenderer1192 extends GameRenderer {
    public GameRenderer1192(MinecraftClient client, HeldItemRenderer heldItemRenderer, ResourceManager resourceManager, BufferBuilderStorage buffers) {
        super(client, heldItemRenderer, resourceManager, buffers);
    }

    public static ShaderProgram getPositionColorShader() {
        return getPositionColorProgram();
    }

    public static ShaderProgram getPositionTexShader() {
        return getPositionTexProgram();
    }

}
