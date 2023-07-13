package qwq.wumie.systems.modules.lemon.utils;

import net.minecraft.util.math.BlockPos;

public class HoleUtils {

    public static Hole getHole(BlockPos pos) {
        return getHole(pos, true, true, true, 3);
    }

    public static Hole getHole(BlockPos pos, int depth) {
        return getHole(pos, true, true, true, depth);
    }

    public static Hole getHole(BlockPos pos, boolean s, boolean d, boolean q, int depth) {
        if (!isHole(pos, depth)) {
            return new Hole(pos, HoleType.NotHole);
        }

        if (!isBlock(pos.west()) || !isBlock(pos.north())) {
            return new Hole(pos, HoleType.NotHole);
        }

        boolean x = isHole(pos.east(), depth) && isBlock(pos.east().north()) && isBlock(pos.east(2));
        boolean z = isHole(pos.south(), depth) && isBlock(pos.south().west()) && isBlock(pos.south(2));

        // Single
        if (s && !x && !z && isBlock(pos.east()) && isBlock(pos.south())) {
            return new Hole(pos, HoleType.Single);
        }

        // Quad
        if (q && x && z && isHole(pos.south().east(), depth) && isBlock(pos.east().east().south()) && isBlock(pos.south().south().east())) {
            return new Hole(pos, HoleType.Quad);
        }

        if (!d) {
            return new Hole(pos, HoleType.NotHole);
        }

        // DoubleX
        if (x && !z && isBlock(pos.south()) && isBlock(pos.south().east())) {
            return new Hole(pos, HoleType.DoubleX);
        }

        // DoubleZ
        if (z && !x && isBlock(pos.east()) && isBlock(pos.south().east())) {
            return new Hole(pos, HoleType.DoubleZ);
        }


        return new Hole(pos, HoleType.NotHole);
    }

    static boolean isBlock(BlockPos pos) {
        return LemonUtils.collidable(pos);
    }

    static boolean isHole(BlockPos pos, int depth) {
        if (!isBlock(pos.down())) {
            return false;
        }

        for (int i = 0; i < depth; i++) {
            if (isBlock(pos.up(i))) {
                return false;
            }
        }
        return true;
    }
}
