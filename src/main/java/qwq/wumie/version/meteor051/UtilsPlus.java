package qwq.wumie.version.meteor051;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Sneak;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import qwq.wumie.systems.modules.lemon.utils.BlockInfo;
import qwq.wumie.utils.pathfinding.pathfinding.jigsaw.FlyingNodeProcessor;
import qwq.wumie.utils.pathfinding.pathfinding.jigsaw.Node;
import qwq.wumie.utils.pathfinding.pathfinding.jigsaw.NodeProcessor;
import qwq.wumie.utils.pathfinding.pathfinding.jigsaw.TeleportResult;

import java.util.ArrayList;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class UtilsPlus {

    public static BlockState getBlockState(BlockPos pos) {
        return BlockInfo.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return BlockInfo.getBlock(pos);
    }

    public static BlockPos getBlockPos(Vec3d vec) {
        return BlockPos.ofFloored(vec);
    }

    public static void sendPacket(Packet<?> packet) {
        mc.getNetworkHandler().sendPacket(packet);
    }

    public static void sendPacket(boolean goingBack, ArrayList<Vec3d> positionsBack, ArrayList<Vec3d> positions) {
        PlayerMoveC2SPacket.PositionAndOnGround playerPacket = new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true);
        //mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ofFloored(new Vec3d(x, y - 1, z)), Direction.DOWN, 0));
        sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(x, y - 1, z),Direction.DOWN,BlockPos.ofFloored(new Vec3d(x, y - 1, z)),false),0));
        sendPacket(playerPacket);
        if (goingBack) {
            positionsBack.add(new Vec3d(x, y, z));
            return;
        }
        positions.add(new Vec3d(x, y, z));
    }

    public static TeleportResult pathFinderTeleportTo(Vec3d from, Vec3d to) {
        NodeProcessor processor = new FlyingNodeProcessor();

        boolean sneaking = mc.player.isSneaking() || Modules.get().get(Sneak.class).isActive();
        ArrayList<Vec3d> positions = new ArrayList<Vec3d>();
        ArrayList<Node> triedPaths = new ArrayList<Node>();
//		System.out.println(to.toString());
        BlockPos targetBlockPos = new BlockPos(getBlockPos(to));
        BlockPos fromBlockPos = getBlockPos(from);

        BlockPos finalBlockPos = targetBlockPos;
        boolean passable = true;
        if(!processor.isPassable(getBlockState(targetBlockPos),targetBlockPos)) {
            finalBlockPos = targetBlockPos.up();
            boolean lastIsPassable;
            if(!(lastIsPassable = processor.isPassable(getBlockState(targetBlockPos.up()),targetBlockPos.up()))) {
                finalBlockPos = targetBlockPos.up(2);
                if(!lastIsPassable) {
                    passable = false;
                }
            }
        }

        processor.getPath(BlockPos.ofFloored(from.x, from.y, from.z), finalBlockPos, 2460);
        triedPaths = processor.triedPaths;
        if(processor.path.isEmpty()) {
            return new TeleportResult(positions, null, triedPaths, null, null, false);
        }
        Vec3d lastPos = null;
        if (sneaking) {
            sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
        for(Node node : processor.path) {
            BlockPos pos = node.getBlockpos();
            sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(node.getBlockpos().getX() + 0.5, node.getBlockpos().getY(), node.getBlockpos().getZ() + 0.5, true));
            positions.add((lastPos = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)));
        }
        if (sneaking) {
            sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }
        return new TeleportResult(positions, null, triedPaths, processor.path, lastPos, true);
    }

    public static TeleportResult pathFinderTeleportToA(Vec3d from, Vec3d to) {
        NodeProcessor processor = new FlyingNodeProcessor();

        boolean sneaking = mc.player.isSneaking() || Modules.get().get(Sneak.class).isActive();
        ArrayList<Vec3d> positions = new ArrayList<Vec3d>();
        ArrayList<Node> triedPaths = new ArrayList<Node>();

        BlockPos targetBlockPos = new BlockPos(getBlockPos(to));
        BlockPos fromBlockPos = getBlockPos(from);

        BlockPos finalBlockPos = targetBlockPos;
        boolean passable = true;
        if(!processor.isPassable(getBlockState(targetBlockPos),targetBlockPos)) {
            finalBlockPos = targetBlockPos.up();
            boolean lastIsPassable;
            if(!(lastIsPassable = processor.isPassable(getBlockState(targetBlockPos.up()),targetBlockPos.up()))) {
                finalBlockPos = targetBlockPos.up(2);
                if(!lastIsPassable) {
                    passable = false;
                }
            }
        }

        processor.getPath(BlockPos.ofFloored(from.x, from.y, from.z), finalBlockPos, 2460);
        triedPaths = processor.triedPaths;
        if(processor.path.isEmpty()) {
            return new TeleportResult(positions, null, triedPaths, null, null, false);
        }
        Vec3d lastPos = null;
        if (sneaking) {
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
        for(Node node : processor.path) {
            BlockPos pos = node.getBlockpos();
            sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(node.getBlockpos().getX() + 0.5, node.getBlockpos().getY(), node.getBlockpos().getZ() + 0.5, true));
            positions.add((lastPos = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)));
        }
        if (sneaking) {
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }
        return new TeleportResult(positions, null, triedPaths, processor.path, lastPos, true);
    }

    public static TeleportResult pathFinderTeleportBack(ArrayList<Vec3d> positions) {
        boolean sneaking = mc.player.isSneaking() || Modules.get().get(Sneak.class).isActive();
        ArrayList<Vec3d> positionsBack = new ArrayList<>();
        if (sneaking) {
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
        for (int i = positions.size() - 1; i > -1; i--) {
            sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(positions.get(i).x, positions.get(i).y, positions.get(i).z, true));
            positionsBack.add(positions.get(i));
        }
        if (sneaking) {
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }
        return new TeleportResult(positions, positionsBack, null, null, null, false);
    }

    static double x;
    static double y;
    static double z;
    static double xPreEn;
    static double yPreEn;
    static double zPreEn;
    static double xPre;
    static double yPre;
    static double zPre;

    public static boolean infiniteReach(double range, double maxXZTP, double maxYTP,
                                        ArrayList<Vec3d> positionsBack, ArrayList<Vec3d> positions, LivingEntity en) {

        int ind = 0;
        xPreEn = en.getX();
        yPreEn = en.getY();
        zPreEn = en.getZ();
        xPre = mc.player.getX();
        yPre = mc.player.getY();
        zPre = mc.player.getZ();
        boolean attack = true;
        boolean up = false;
        boolean tpUpOneBlock = false;

        // If something in the way
        boolean hit = false;
        boolean tpStraight = false;

        boolean sneaking = mc.player.isSneaking() || Modules.get().get(Sneak.class).isActive();

        positions.clear();
        positionsBack.clear();
        double step = maxXZTP / range;
        int steps = 0;
        for (int i = 0; i < range; i++) {
            steps++;
            if (maxXZTP * steps > range) {
                break;
            }
        }
        HitResult rayTrace = null;
        HitResult rayTrace1 = null;
        HitResult rayTraceCarpet = null;
        if ((rayTraceWide(new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ()),
                new Vec3d(en.getX(), en.getY(), en.getZ()), false, false, true))
                || (rayTrace1 = rayTracePos(
                new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ()),
                new Vec3d(en.getX(), en.getY() + mc.player.getEyeHeight(mc.player.getPose()), en.getZ()), false, false,
                true)) != null) {
            if ((rayTrace = rayTracePos(new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ()),
                    new Vec3d(en.getX(), mc.player.getY(), en.getZ()), false, false, true)) != null
                    || (rayTrace1 = rayTracePos(
                    new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()),
                            mc.player.getZ()),
                    new Vec3d(en.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), en.getZ()), false, false,
                    true)) != null) {
                HitResult trace = null;
                if (rayTrace == null) {
                    trace = rayTrace1;
                }
                if (rayTrace1 == null) {
                    trace = rayTrace;
                }
                if (trace == null) {
                    // y = mc.player.posY;
                    // yPreEn = mc.player.posY;
                } else {
                    if (trace.getPos() != null) {
                        boolean fence = false;
                        BlockPos target = BlockPos.ofFloored(trace.getPos());
                        // positions.add(BlockTools.getVec3(target));
                        up = true;
                        y = target.up().getY();
                        yPreEn = target.up().getY();
                        Block lastBlock = null;
                        Boolean found = false;
                        for (int i = 0; i < maxYTP; i++) {
                            HitResult tr = rayTracePos(
                                    new Vec3d(mc.player.getX(), target.getY() + i, mc.player.getZ()),
                                    new Vec3d(en.getX(), target.getY() + i, en.getZ()), false, false, true);
                            if (tr == null) {
                                continue;
                            }
                            if (tr.getPos() == null) {
                                continue;
                            }

                            BlockPos blockPos = BlockPos.ofFloored(tr.getPos());
                            BlockState blockState = getBlockState(blockPos);
                            Block block = blockState.getBlock();
                            if (!blockState.isAir()) {
                                lastBlock = block;
                                continue;
                            }
                            fence = lastBlock instanceof FenceBlock;
                            y = target.getY() + i;
                            yPreEn = target.getY() + i;
                            if (fence) {
                                y += 1;
                                yPreEn += 1;
                                if (i + 1 > maxYTP) {
                                    found = false;
                                    break;
                                }
                            }
                            found = true;
                            break;
                        }
                        double difX = mc.player.getX() - xPreEn;
                        double difZ = mc.player.getZ() - zPreEn;
                        double divider = step * 0;
                        if (!found) {
                            attack = false;
                            return false;
                        }
                    } else {
                        attack = false;
                        return false;
                    }
                }
            } else {
                HitResult ent = rayTracePos(
                        new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ()),
                        new Vec3d(en.getX(), en.getY(), en.getZ()), false, false, false);
                if (ent != null && ent.getType() == HitResult.Type.ENTITY) {
                    y = mc.player.getY();
                    yPreEn = mc.player.getY();
                } else {
                    y = mc.player.getY();
                    yPreEn = en.getY();
                }

            }
        }
        if (!attack) {
            return false;
        }
        if (sneaking) {
            sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
        for (int i = 0; i < steps; i++) {
            ind++;
            if (i == 1 && up) {
                x = mc.player.getX();
                y = yPreEn;
                z = mc.player.getZ();
                sendPacket(false, positionsBack, positions);
            }
            if (i != steps - 1) {
                {
                    double difX = mc.player.getX() - xPreEn;
                    double difY = mc.player.getY() - yPreEn;
                    double difZ = mc.player.getZ() - zPreEn;
                    double divider = step * i;
                    x = mc.player.getX() - difX * divider;
                    y = mc.player.getY() - difY * (up ? 1 : divider);
                    z = mc.player.getZ() - difZ * divider;
                }
                sendPacket(false, positionsBack, positions);
            } else {
                // if last teleport
                {
                    double difX = mc.player.getX() - xPreEn;
                    double difY = mc.player.getY() - yPreEn;
                    double difZ = mc.player.getZ() - zPreEn;
                    double divider = step * i;
                    x = mc.player.getX() - difX * divider;
                    y = mc.player.getY() - difY * (up ? 1 : divider);
                    z = mc.player.getZ() - difZ * divider;
                }
                sendPacket(false, positionsBack, positions);
                double xDist = x - xPreEn;
                double zDist = z - zPreEn;
                double yDist = y - en.getY();
                double dist = Math.sqrt(xDist * xDist + zDist * zDist);
                if (dist > 4) {
                    x = xPreEn;
                    y = yPreEn;
                    z = zPreEn;
                    sendPacket(false, positionsBack, positions);
                } else if (dist > 0.05 && up) {
                    x = xPreEn;
                    y = yPreEn;
                    z = zPreEn;
                    sendPacket(false, positionsBack, positions);
                }
                if (Math.abs(yDist) < maxYTP && mc.player.distanceTo(en) >= 4) {
                    x = xPreEn;
                    y = en.getY();
                    z = zPreEn;
                    sendPacket(false, positionsBack, positions);
                    attackInf(en);
                } else {
                    attack = false;
                }
            }
        }

        // Go back!
        for (int i = positions.size() - 2; i > -1; i--) {
            {
                x = positions.get(i).x;
                y = positions.get(i).y;
                z = positions.get(i).z;
            }
            sendPacket(false, positionsBack, positions);
        }
        x = mc.player.getX();
        y = mc.player.getY();
        z = mc.player.getZ();
        sendPacket(false, positionsBack, positions);
        if (!attack) {
            if (sneaking) {
                sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            }
            positions.clear();
            positionsBack.clear();
            return false;
        }
        if (sneaking) {
            sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }
        return true;
    }

    @SuppressWarnings("unused")
    public static HitResult rayTracePos(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid,
                                             boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        float[] rots = getFacePosRemote(vec32, vec31);
        float yaw = rots[0];
        double angleA = Math.toRadians(normalizeAngle(yaw));
        double angleB = Math.toRadians(normalizeAngle(yaw) + 180);
        double size = 2.1;
        double size2 = 2.1;
        Vec3d left = new Vec3d(vec31.x + Math.cos(angleA) * size, vec31.y,
                vec31.z + Math.sin(angleA) * size);
        Vec3d right = new Vec3d(vec31.x + Math.cos(angleB) * size, vec31.y,
                vec31.z + Math.sin(angleB) * size);
        Vec3d left2 = new Vec3d(vec32.x + Math.cos(angleA) * size, vec32.y,
                vec32.z + Math.sin(angleA) * size);
        Vec3d right2 = new Vec3d(vec32.x + Math.cos(angleB) * size, vec32.y,
                vec32.z + Math.sin(angleB) * size);
        Vec3d leftA = new Vec3d(vec31.x + Math.cos(angleA) * size2, vec31.y,
                vec31.z + Math.sin(angleA) * size2);
        Vec3d rightA = new Vec3d(vec31.x + Math.cos(angleB) * size2, vec31.y,
                vec31.z + Math.sin(angleB) * size2);
        Vec3d left2A = new Vec3d(vec32.x + Math.cos(angleA) * size2, vec32.y,
                vec32.z + Math.sin(angleA) * size2);
        Vec3d right2A = new Vec3d(vec32.x + Math.cos(angleB) * size2, vec32.y,
                vec32.z + Math.sin(angleB) * size2);

        HitResult trace1 = mc.world.raycast(new BlockStateRaycastContext(left,left2,(state) -> {
            return true;
        }));
        HitResult trace2 = mc.world.raycast(new BlockStateRaycastContext(vec31,vec32,(state) -> {
            return true;
        }));
        HitResult trace3 = mc.world.raycast(new BlockStateRaycastContext(right,right2,(state) -> {
            return true;
        }));


        HitResult trace4 = null;
        HitResult trace5 = null;
        if (trace2 != null || trace1 != null || trace3 != null || trace4 != null || trace5 != null) {
            if (returnLastUncollidableBlock) {
                if (trace5 != null && (!getBlockState(BlockPos.ofFloored(trace5.getPos())).isAir()
                        || trace5.getType() == HitResult.Type.ENTITY)) {
                    // positions.add(BlockTools.getVec3(trace3.getBlockPos()));
                    return trace5;
                }
                if (trace4 != null && (!getBlockState(BlockPos.ofFloored(trace4.getPos())).isAir()
                        || trace4.getType() == HitResult.Type.ENTITY)) {
                    // positions.add(BlockTools.getVec3(trace3.getBlockPos()));
                    return trace4;
                }
                if (trace3 != null && (!getBlockState(BlockPos.ofFloored(trace3.getPos())).isAir()
                        || trace3.getType() == HitResult.Type.ENTITY)) {
                    // positions.add(BlockTools.getVec3(trace3.getBlockPos()));
                    return trace3;
                }
                if (trace1 != null && (!getBlockState(BlockPos.ofFloored(trace1.getPos())).isAir()
                        || trace1.getType() == HitResult.Type.ENTITY)) {
                    // positions.add(BlockTools.getVec3(trace1.getBlockPos()));
                    return trace1;
                }
                if (trace2 != null && (!getBlockState(BlockPos.ofFloored(trace2.getPos())).isAir()
                        || trace2.getType() == HitResult.Type.ENTITY)) {
                    // positions.add(BlockTools.getVec3(trace2.getBlockPos()));
                    return trace2;
                }
            } else {
                if (trace5 != null) {
                    return trace5;
                }
                if (trace4 != null) {
                    return trace4;
                }
                if (trace3 != null) {
                    // positions.add(BlockTools.getVec3(trace3.getBlockPos()));
                    return trace3;
                }
                if (trace1 != null) {
                    // positions.add(BlockTools.getVec3(trace1.getBlockPos()));
                    return trace1;
                }
                if (trace2 != null) {
                    // positions.add(BlockTools.getVec3(trace2.getBlockPos()));
                    return trace2;
                }
            }
        }
        if (trace2 == null) {
            if (trace3 == null) {
                if (trace1 == null) {
                    if (trace5 == null) {
                        if (trace4 == null) {
                            return null;
                        }
                        return trace4;
                    }
                    return trace5;
                }
                return trace1;
            }
            return trace3;
        }
        return trace2;
    }

    public static boolean rayTraceWide(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox,
                                       boolean returnLastUncollidableBlock) {
        float yaw = getFacePosRemote(vec32, vec31)[0];
        yaw = normalizeAngle(yaw);
        yaw += 180;
        yaw = MathHelper.wrapDegrees(yaw);
        double angleA = Math.toRadians(yaw);
        double angleB = Math.toRadians(yaw + 180);
        double size = 2.1;
        double size2 = 2.1;
        Vec3d left = new Vec3d(vec31.x + Math.cos(angleA) * size, vec31.y,
                vec31.z + Math.sin(angleA) * size);
        Vec3d right = new Vec3d(vec31.x + Math.cos(angleB) * size, vec31.y,
                vec31.z + Math.sin(angleB) * size);
        Vec3d left2 = new Vec3d(vec32.x + Math.cos(angleA) * size, vec32.y,
                vec32.z + Math.sin(angleA) * size);
        Vec3d right2 = new Vec3d(vec32.x + Math.cos(angleB) * size, vec32.y,
                vec32.z + Math.sin(angleB) * size);
        Vec3d leftA = new Vec3d(vec31.x + Math.cos(angleA) * size2, vec31.y,
                vec31.z + Math.sin(angleA) * size2);
        Vec3d rightA = new Vec3d(vec31.x + Math.cos(angleB) * size2, vec31.y,
                vec31.z + Math.sin(angleB) * size2);
        Vec3d left2A = new Vec3d(vec32.x + Math.cos(angleA) * size2, vec32.y,
                vec32.z + Math.sin(angleA) * size2);
        Vec3d right2A = new Vec3d(vec32.x + Math.cos(angleB) * size2, vec32.y,
                vec32.z + Math.sin(angleB) * size2);
        // RayTraceResult trace4 = mc.world.rayTraceBlocks(leftA,
        // left2A, stopOnLiquid, ignoreBlockWithoutBoundingBox,
        // returnLastUncollidableBlock);
        HitResult trace1 = mc.world.raycast(new BlockStateRaycastContext(left,left2,(state) -> {
            return true;
        }));
        HitResult trace2 = mc.world.raycast(new BlockStateRaycastContext(vec31,vec32,(state) -> {
            return true;
        }));
        HitResult trace3 = mc.world.raycast(new BlockStateRaycastContext(right,right2,(state) -> {
            return true;
        }));
        // RayTraceResult trace5 = mc.world.rayTraceBlocks(rightA,
        // right2A, stopOnLiquid, ignoreBlockWithoutBoundingBox,
        // returnLastUncollidableBlock);
        HitResult trace4 = null;
        HitResult trace5 = null;
        if (returnLastUncollidableBlock) {
            return (trace1 != null && !getBlockState(BlockPos.ofFloored(trace1.getPos())).isAir())
                    || (trace2 != null && !getBlockState(BlockPos.ofFloored(trace2.getPos())).isAir())
                    || (trace3 != null && !getBlockState(BlockPos.ofFloored(trace3.getPos())).isAir())
                    || (trace4 != null && !getBlockState(BlockPos.ofFloored(trace4.getPos())).isAir())
                    || (trace5 != null && !getBlockState(BlockPos.ofFloored(trace5.getPos())).isAir());
        } else {
            return trace1 != null || trace2 != null || trace3 != null || trace5 != null || trace4 != null;
        }

    }

    public static void attackInf(Entity entity) {
        assert mc.interactionManager != null;
        mc.interactionManager.attackEntity(mc.player,entity);
    }

    public static float[] getFacePosRemote(Vec3d src, Vec3d dest) {
        double diffX = dest.x - src.x;
        double diffY = dest.y - (src.y);
        double diffZ = dest.z - src.z;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
        return new float[] {MathHelper.wrapDegrees(yaw),
                MathHelper.wrapDegrees(pitch) };
    }

    private final static float limitAngleChange(final float current, final float intended, final float maxChange) {
        float change = intended - current;
        if (change > maxChange)
            change = maxChange;
        else if (change < -maxChange)
            change = -maxChange;
        return current + change;
    }

    public static double normalizeAngle(double angle) {
        return (angle + 360) % 360;
    }

    public static float normalizeAngle(float angle) {
        return (angle + 360) % 360;
    }
}
