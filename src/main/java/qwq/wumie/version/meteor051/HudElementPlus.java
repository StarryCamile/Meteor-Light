package qwq.wumie.version.meteor051;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.SMesh;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import qwq.wumie.utils.ExChat;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import qwq.wumie.utils.render.ScreenUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import qwq.wumie.systems.handle.MainHandler;

public class HudElementPlus extends ExChat {
    public final MinecraftClient mc = MeteorClient.mc;
    public final RenderUtils gameRender = RenderUtils.instance;
    public MatrixStack matrices = gameRender.matrices;
    public ScreenUtil scaled = new ScreenUtil(mc);
    public SMesh.BMesh dMesh = MainHandler.dMesh;

    public void drawScaledImage(HudRenderer renderer , Identifier image, double x, double y, double width, double height, double scale) {
        matrices.push();
        x += 0.5 * scale;
        y += 0.5 * scale;
        matrices.scale((float) scale, (float) scale, 1);
        renderer.texture(image, x / scale, y / scale, width, height, Color.WHITE);
        matrices.pop();
    }

    public void drawHead(Identifier renderSkin,HudRenderer renderer, double x, double y, int size) {
        PlayerSkinDrawer.draw(renderer.drawContext, renderSkin, MathHelper.floor(x), MathHelper.floor(y), size, false, false);
    }

    public double smoothMove(double start, double end) {
        double speed = (end - start) * 0.1;

        if (speed > 0) {
            speed = Math.max(0.1, speed);
            speed = Math.min(end - start, speed);
        } else if (speed < 0) {
            speed = Math.min(-0.1, speed);
            speed = Math.max(end - start, speed);
        }
        return start + speed;
    }
}
