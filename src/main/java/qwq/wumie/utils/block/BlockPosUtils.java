package qwq.wumie.utils.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class BlockPosUtils {
    public static BlockPos createPos(Position pos) {
        return BlockPos.ofFloored(pos);
    }

    public static BlockPos createPos(Vec3i pos) {
        return new BlockPos(pos.getX(), pos.getY(), pos.getX());
    }
}
