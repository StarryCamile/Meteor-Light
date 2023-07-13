package qwq.wumie.utils.pathfinding.pathfinding.jigsaw;

import java.util.ArrayList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public abstract class PathfinderController {
	
	protected static MinecraftClient mc = MinecraftClient.getInstance();
	
	protected int positionIndex;
	protected ArrayList<Vec3d> positions = new ArrayList<Vec3d>();
	
	public PathfinderController(PathfinderResult result) {
		positions.clear();
		for(Node node : result.getPath()) {
			positions.add(new Vec3d(node.getBlockpos().getX(), node.getBlockpos().getY(), node.getBlockpos().getZ()));
		}
		positionIndex = 0;
	}
	
	public abstract void update();
	
	public void update(PathfinderResult result) {
		positions.clear();
		for(Node node : result.getPath()) {
			positions.add(new Vec3d(node.getBlockpos().getX(), node.getBlockpos().getY(), node.getBlockpos().getZ()));
		}
		update();
	}
	
	protected abstract void onFinish();
	
	protected abstract void onIncrement();
	
	public int getPositionIndex() {
		return positionIndex;
	}
	
	public ArrayList<Vec3d> getPositions() {
		return positions;
	}

}
