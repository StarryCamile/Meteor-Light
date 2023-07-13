package qwq.wumie.utils.pathfinding.pathfinding.jigsaw;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.ArrayList;

public class BBRayTrace {
    private static MinecraftClient mc = MinecraftClient.getInstance();
    private int highestHitBlockHeight;
    private BlockPos highestBlock = null;
    private boolean hitBlock;
    private ArrayList<BlockPos> hitBlocks = new ArrayList<BlockPos>();

    public BBRayTrace(Vec3d pos1, Vec3d pos2, int checks, double bbSize) {
        Box bb = mc.player.getBoundingBox();
        bb.expand(bbSize, 0, bbSize);
        double xDist = pos2.x - pos1.x;
        double yDist = pos2.y - pos1.y;
        double zDist = pos2.z - pos1.z;
        for(int i = 0; i < checks; i++) {
            bb = bb.offset((zDist / checks) * i, ((yDist / checks) * i) + 0.05, (xDist / checks) * i);
            for(VoxelShape shape : mc.world.getCollisions(mc.player, bb)) {
                BlockPos pos = BlockPos.ofFloored(shape.getBoundingBox().getCenter());
                if(!hitBlocks.contains(pos)) {
                    hitBlocks.add(pos);
                }
            }
        }
        if(hitBlocks.isEmpty()) {
            hitBlock = false;
            return;
        }
        hitBlock = true;
        int maxHeight = -1000;
        for(BlockPos pos : hitBlocks) {
            if(pos.getY() > maxHeight) {
                maxHeight = pos.getY();
                highestBlock = pos;
            }
        }
    }

    public boolean didHitBlock() {
        return hitBlock;
    }

    public ArrayList<BlockPos> getHitBlocks() {
        return hitBlocks;
    }

    public int getHighestHitBlockHeight() {
        return highestHitBlockHeight;
    }

    public BlockPos getHighestBlock() {
        return highestBlock;
    }
}
