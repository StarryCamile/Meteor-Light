package qwq.wumie.utils.render;

import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.List;

public class CircleManager {

	public final List<Circle> circles = new ArrayList<Circle>();
	
	public void addCircle(double x, double y, double rad, double speed, int key) {
		circles.add(new Circle(x, y, rad, speed, key));
	}
	
	public void runCircle(Circle c){
		//if(c.progress < 2){
		
		
		c.lastProgress = c.progress;
		if(c.progress > c.topRadius * 0.67 && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(),c.keyCode))
			return;
		
		c.progress += (c.topRadius-c.progress)/(c.speed) + 0.01;
		if(c.progress >= c.topRadius){
			c.complete = true;
		}
		//}
	}
	
	public void runCircles(){
		List<Circle> completes = new ArrayList<>();
		for(Circle c : circles){
			if(c.complete) {
				completes.add(c);
			} else {
			runCircle(c);
			}
		}
		synchronized(circles){
			circles.removeAll(completes);
		}
	}
	
	public void drawCircles(HudRenderer renderer){
		for(Circle c : circles){
			if(!c.complete)
			drawCircle(c,renderer);
		}
	}
	
	public void drawCircle(Circle c,HudRenderer renderer){
    	float progress = (float) (c.progress * MinecraftClient.getInstance().getTickDelta() + (c.lastProgress * (1.0f - MinecraftClient.getInstance().getTickDelta())));
    	if(!c.complete) {

	    }
	}
	
}

