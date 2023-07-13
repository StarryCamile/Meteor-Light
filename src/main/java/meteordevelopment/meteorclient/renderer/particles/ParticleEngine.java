package meteordevelopment.meteorclient.renderer.particles;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.renderer.CRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParticleEngine {

	public CopyOnWriteArrayList<Particle> particles = Lists.newCopyOnWriteArrayList();
	public float lastMouseX;
	public float lastMouseY;

	public void render(MatrixStack matrixStack, float mouseX, float mouseY, int displayWidth, int displayHeight){
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        //GL11.glColor4f(1,1,1,1);
        float xOffset = displayWidth/2-mouseX;
        float yOffset = displayHeight/2-mouseY;
		for(particles.size(); particles.size() < (int)(displayWidth/19.2f); particles.add(new Particle(displayWidth, displayHeight,new Random().nextFloat()*2 + 2, new Random().nextFloat()*5 + 5)));
		List<Particle> toremove = Lists.newArrayList();
		for(Particle p : particles){
			if(p.opacity < 32){
				p.opacity += 2;
			}
			if(p.opacity > 32){
				p.opacity = 32;
			}
			Color c = new Color((int)255, (int)255, (int)255, (int)p.opacity);
			drawBorderedCircle(matrixStack,p.x + Math.sin(p.ticks/2)*50 + -xOffset/5, (p.ticks*p.speed)*p.ticks/10 + -yOffset/5, p.radius*(p.opacity/32), c.getRGB(), c.getRGB());
			p.ticks += 0.05;// +(0.005*1.777*(GLUtils.getMouseX()-lastMouseX) + 0.005*(GLUtils.getMouseY()-lastMouseY));
			if(((p.ticks*p.speed)*p.ticks/10 + -yOffset/5) > displayHeight || ((p.ticks*p.speed)*p.ticks/10 + -yOffset/5) < 0 || (p.x + Math.sin(p.ticks/2)*50 + -xOffset/5) > displayWidth|| (p.x + Math.sin(p.ticks/2)*50 + -xOffset/5) < 0){
				toremove.add(p);
			}
		}

		particles.removeAll(toremove);
        //GL11.glColor4f(1, 1, 1, 1);
        RenderSystem.disableBlend();
		lastMouseX = getMouseX(displayWidth);
		lastMouseY = getMouseY(displayHeight);
	}

    public int getMouseX(int displayWidth) {
        return (int) (MinecraftClient.getInstance().mouse.getX() * displayWidth / displayWidth);
    }

    public int getMouseY(int displayHeight) {
        return (int) (displayHeight - MinecraftClient.getInstance().mouse.getY() * displayHeight / displayHeight - 1);
    }

    public static void drawBorderedCircle(MatrixStack matrixStack,double x, double y, float radius, int outsideC, int insideC) {
        matrixStack.push();
        matrixStack.scale(0.1f,0.1f,0.1f);
        drawCircle(matrixStack,x *= 10, y *= 10, radius *= 10.0f, insideC);
        matrixStack.pop();
    }

    public static void drawCircle(MatrixStack matrices,double x, double y, float radius, int color) {
        float alpha = (float)(color >> 24 & 255) / 255.0f;
        float red = (float)(color >> 16 & 255) / 255.0f;
        float green = (float)(color >> 8 & 255) / 255.0f;
        float blue = (float)(color & 255) / 255.0f;
        int i = 0;
        while (i <= 360) {
            CRender.Immediate.drawLine(matrices,x,y,x + Math.sin((double)i * 3.141526 / 180.0) * (double)radius, (double)((double)y + Math.cos((double)i * 3.141526 / 180.0) * (double)radius),new meteordevelopment.meteorclient.utils.render.color.Color(red,green,blue,alpha),false);
            ++i;
        }
    }
}
