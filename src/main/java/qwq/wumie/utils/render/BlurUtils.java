package qwq.wumie.utils.render;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.WindowResizedEvent;
import meteordevelopment.meteorclient.renderer.Framebuffer;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.PostProcessRenderer;
import meteordevelopment.meteorclient.renderer.Shader;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Blur;
import meteordevelopment.orbit.listeners.ConsumerListener;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BlurUtils {
    private static List<BlurArea> blurTasks = new ArrayList<>();
    private static List<String> nameTasks = new ArrayList<>();

    public static void addBlurTask(String task) {
        BlurArea blurMain = new BlurArea(task);
        if (!blurTasks.contains(blurMain)) {
            blurTasks.add(blurMain);
            nameTasks.add(blurMain.taskName);
            //blurMain.init();
        }
    }

    public static void removeBlurTask(String task) {
        if (nameTasks.contains(task)) {
            BlurArea blurMain = getBlurArea(task);
            blurTasks.remove(blurMain);
            nameTasks.remove(task);
        }
    }

    public static void renderBlur(String task) {
        if (nameTasks.contains(task)) {
            BlurArea blurMain = getBlurArea(task);

            if (blurMain != null)
                blurMain.render();
        } else {
            addBlurTask(task);
        }
    }

    public static void blur(double x, double y, double width, double height) {

    }

    private static BlurArea getBlurArea(String task) {
        for (BlurArea blurArea : blurTasks) {
            if (blurArea.taskName.equalsIgnoreCase(task)) return blurArea;

        }
        return null;
    }

    private static class BlurArea {
        private Shader shader;
        private Framebuffer fbo1, fbo2;
        private boolean enabled;
        private long fadeEndAt;
        private final String taskName;
        private boolean building;

        public BlurArea(String taskName) {
            this.taskName = taskName;

            //init();
        }

        private void render() {
            Blur blur = Modules.get().get(Blur.class);
            // Enable / disable with fading
            boolean shouldRender = true;
            long time = System.currentTimeMillis();

            if (enabled) {
                if (!shouldRender) {
                    if (fadeEndAt == -1) fadeEndAt = System.currentTimeMillis() + blur.fadeTime.get();

                    if (time >= fadeEndAt) {
                        enabled = false;
                        fadeEndAt = -1;
                    }
                }
            } else {
                if (shouldRender) {
                    enabled = true;
                    fadeEndAt = System.currentTimeMillis() + blur.fadeTime.get();
                }
            }

            if (!enabled) return;

            // Initialize shader and framebuffer if running for the first time
            if (shader == null) {
                shader = new Shader("blur.vert", "blur.frag");
                fbo1 = new Framebuffer();
                fbo2 = new Framebuffer();
            }

            // Prepare stuff for rendering
            int sourceTexture = mc.getFramebuffer().getColorAttachment();

            shader.bind();
            shader.set("u_Size", mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
            shader.set("u_Texture", 0);

            // Update progress
            double progress = 1;

            if (time < fadeEndAt) {
                if (shouldRender) progress = 1 - (fadeEndAt - time) / blur.fadeTime.get().doubleValue();
                else progress = (fadeEndAt - time) / blur.fadeTime.get().doubleValue();
            } else {
                fadeEndAt = -1;
            }

            // Render the blur
            //
            PostProcessRenderer.beginRender();

            fbo1.bind();
            GL.bindTexture(sourceTexture);
            shader.set("u_Direction", 1.0, 0.0);
            PostProcessRenderer.render();

            /// if (blur.mode.get() == Blur.Mode.Fancy) fbo2.bind();
            //    else fbo2.unbind();
            GL.bindTexture(fbo1.texture);
            shader.set("u_Direction", 0.0, 1.0);
            PostProcessRenderer.render();

            // if (blur.mode.get() == Blur.Mode.Fancy) {
            fbo1.bind();
            GL.bindTexture(fbo2.texture);
            shader.set("u_Direction", 1.0, 0.0);
            PostProcessRenderer.render();

            fbo2.unbind();
            GL.bindTexture(fbo1.texture);
            shader.set("u_Direction", 0.0, 1.0);
            PostProcessRenderer.render();
        }

        //  PostProcessRenderer.endRender();

        public void init() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BlurArea blurArea = (BlurArea) o;

            return blurArea.taskName.equals(blurArea.taskName);
        }

        @Override
        public int hashCode() {
            return taskName.hashCode();
        }
    }

}