package meteordevelopment.meteorclient.renderer.particles;

import net.minecraft.client.gui.screen.Screen;

import java.util.Random;

public class Particle {

	public float x,y,radius,speed,ticks, opacity;

	public Particle(int width,int height, float r, float s){
		x = new Random().nextFloat()*width;
		y = new Random().nextFloat()*height;
		ticks = new Random().nextFloat()*height/2;
		radius = r;
		speed = s;
	}
}
