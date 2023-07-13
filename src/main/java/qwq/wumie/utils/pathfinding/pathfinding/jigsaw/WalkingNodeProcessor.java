package qwq.wumie.utils.pathfinding.pathfinding.jigsaw;

import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;

public class WalkingNodeProcessor extends NodeProcessor {
	
	@Override
	public Node createNode(BlockPos pos) {
		return new Node(isWalkable(Utils.getBlockState(pos.down()),pos.down()) && isPassable(Utils.getBlockState(pos),pos) && isPassable(Utils.getBlockState(pos.up()),pos.up()), pos).setId(pos.hashCode());
	}
	
	@Override
	public boolean isPassable(BlockState blockState,BlockPos pos) {
		return blockState.isAir() || (blockState.getBlock() instanceof PlantBlock && !(blockState.getBlock() instanceof LilyPadBlock)) || blockState.getBlock() instanceof VineBlock
				|| blockState.getFluidState().isOf(Fluids.WATER)|| blockState.getBlock() instanceof AbstractRedstoneGateBlock ||
				blockState.getBlock() instanceof SignBlock || blockState.getBlock() instanceof WallSignBlock || blockState.getBlock() instanceof LadderBlock;
	}
	
	@Override
	public boolean isWalkable(BlockState blockState,BlockPos pos) {
		MinecraftClient mc = MinecraftClient.getInstance();
		return !blockState.isAir() && (blockState.getBlock() instanceof LilyPadBlock || blockState.getBlock() instanceof LadderBlock || blockState.isFullCube(mc.world,pos));
	}
	
}
