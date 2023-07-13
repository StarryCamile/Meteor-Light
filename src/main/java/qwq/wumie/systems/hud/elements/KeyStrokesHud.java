package qwq.wumie.systems.hud.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.misc.MeteorIdentifier;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import qwq.wumie.utils.render.CircleManager;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Identifier;

public class KeyStrokesHud extends HudElement {
    public static final HudElementInfo<KeyStrokesHud> INFO = new HudElementInfo<>(Hud.GROUP, "key-strokes", "Display keys.", KeyStrokesHud::new);

    public KeyStrokesHud() {
        super(INFO);
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    public static CircleManager Wcircles = new CircleManager();
    public static CircleManager Acircles = new CircleManager();
    public static CircleManager Scircles = new CircleManager();
    public static CircleManager Dcircles = new CircleManager();
    public static CircleManager Lcircles = new CircleManager();
    public static CircleManager Rcircles = new CircleManager();

    @Override
    public void tick(HudRenderer renderer) {
        setSize(172,172);

        Wcircles.runCircles();
        Acircles.runCircles();
        Scircles.runCircles();
        Dcircles.runCircles();
        Lcircles.runCircles();
        Rcircles.runCircles();

        super.tick(renderer);
    }

    @EventHandler
    private void onKey(KeyEvent e) {
        double keyStrokeX = this.x;
        double keyStrokeY = this.y;
        if (e.action == KeyAction.Press) {
            if (e.key == mc.options.forwardKey.getDefaultKey().getCode()) {
                Wcircles.addCircle(85 / 2f, 284 / 2f, 26, 5, mc.options.forwardKey.getDefaultKey().getCode());
            }
            if (e.key == mc.options.leftKey.getDefaultKey().getCode()) {
                Acircles.addCircle(34 / 2f, 334 / 2f, 26, 5, mc.options.leftKey.getDefaultKey().getCode());
            }
            if (e.key == mc.options.backKey.getDefaultKey().getCode()) {
                Scircles.addCircle(85 / 2f, 334 / 2f, 26, 5, mc.options.backKey.getDefaultKey().getCode());
            }
            if (e.key == mc.options.rightKey.getDefaultKey().getCode()) {
                Dcircles.addCircle(136 / 2f, 334 / 2f, 26, 5, mc.options.rightKey.getDefaultKey().getCode());
            }
            if (e.key == mc.options.attackKey.getDefaultKey().getCode()) {
                Lcircles.addCircle(47 / 2f, 386 / 2f, 35, 5, mc.options.attackKey.getDefaultKey().getCode());
            }
            if (e.key == mc.options.useKey.getDefaultKey().getCode()) {
                Rcircles.addCircle(124 / 2f, 386 / 2f, 35, 5, mc.options.useKey.getDefaultKey().getCode());
            }
        }
    }

    @Override
    public void render(HudRenderer renderer) {
        double keyStrokeX = this.x;
        double keyStrokeY = this.y;
        Identifier image = new MeteorIdentifier("textures/jello/keystrokes.png");
        renderer.post(() -> {
            renderer.texture(image,x,y,172,172, Color.WHITE);

            RenderSystem.enableBlend();
            //Stencil.write(false);
            renderer.quad(keyStrokeX + 26.5f - 1, keyStrokeY, keyStrokeX + 35 + 15.5f - 1, keyStrokeY + 25 - 1, new Color(0xb2000000));
            //Stencil.erase(true);

            //Stencil.write(false);
            renderer.quad(keyStrokeX, keyStrokeY + 26.5f - 1, keyStrokeX + 25 - 1, keyStrokeY + 30 + 5 + 15.5f - 1, new Color(0xb2000000));
            //Stencil.erase(true);
            RenderSystem.enableBlend();
            Acircles.drawCircles(renderer);
            //Stencil.dispose();

            //Stencil.write(false);
            renderer.quad(keyStrokeX + 51 / 2f, keyStrokeY + 26.5f - 1, keyStrokeX + 25 + 51 / 2f - 1, keyStrokeY + 30 + 5 + 15.5f - 1, new Color(0xb2000000));
           // Stencil.erase(true);
            RenderSystem.enableBlend();
            Scircles.drawCircles(renderer);
            //Stencil.dispose();

          //  Stencil.write(false);
            renderer.quad(keyStrokeX + 51 / 2f + 51 / 2f, keyStrokeY + 26.5f - 1, keyStrokeX + 25 + 51 / 2f + 51 / 2f - 1, keyStrokeY + 30 + 5 + 15.5f - 1, new Color(0xb2000000));
         //   Stencil.erase(true);
            RenderSystem.enableBlend();
            Dcircles.drawCircles(renderer);
         //   Stencil.dispose();

         //   Stencil.write(false);
            renderer.quad(keyStrokeX, keyStrokeY + 26.5f + 51 / 2f - 1, keyStrokeX + 74 / 2f, keyStrokeY + 26.5f + 51 / 2f + 24 - 1, new Color(0xb2000000));
         //   Stencil.erase(true);
            RenderSystem.enableBlend();
            Lcircles.drawCircles(renderer);
          //  Stencil.dispose();

         //   Stencil.write(false);
            renderer.quad(keyStrokeX + 77 / 2f, keyStrokeY + 26.5f + 51 / 2f - 1, keyStrokeX + 74 / 2f + 76 / 2f, keyStrokeY + 26.5f + 51 / 2f + 24 - 1, new Color(0xb2000000));
          //  Stencil.erase(true);
            RenderSystem.enableBlend();
            Rcircles.drawCircles(renderer);
          //  Stencil.dispose();

        });
        super.render(renderer);
    }
}
