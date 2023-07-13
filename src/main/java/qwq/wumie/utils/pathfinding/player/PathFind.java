package qwq.wumie.utils.pathfinding.player;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import qwq.wumie.utils.pathfinding.pathfinding.CustomPathFinder;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static qwq.wumie.systems.modules.lemon.utils.BlockInfo.*;

public class PathFind {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    private static ExecutorService antiLagExec = Executors.newFixedThreadPool(5);

    public static boolean canPassThrow(BlockPos pos) {
        Block block = getBlock(new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
        return block instanceof AirBlock || block instanceof PlantBlock || block instanceof VineBlock || block instanceof LadderBlock || block instanceof FluidBlock || block instanceof SignBlock;
    }

    public static void teleportTo(Vec3d pos) {
        teleportTo(new Vector3d(pos.x,pos.y,pos.z));
    }

    public static void teleportTo(CustomPathFinder.Vec3 pos) {
        teleportTo(new Vector3d(pos.x,pos.y,pos.z));
    }

    public static void teleportTo(Vector3d pos) {
        CustomPathFinder.Vec3 topPlayer = new CustomPathFinder.Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        CustomPathFinder.Vec3 to = new CustomPathFinder.Vec3(pos.x(),pos.y(),pos.z());
        ArrayList<CustomPathFinder.Vec3> path = PathFind.computePath(topPlayer,to,true);
        for (CustomPathFinder.Vec3 pathElm : path) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
        }
        mc.player.updatePosition(to.x,to.y,to.z);
    }

    public static ArrayList<CustomPathFinder.Vec3> computePath(CustomPathFinder.Vec3 topFrom, CustomPathFinder.Vec3 to) {
        return computePath(topFrom, to, false);
    }

    public static ArrayList<CustomPathFinder.Vec3> computePath(final CustomPathFinder.Vec3 top, final CustomPathFinder.Vec3 to, final boolean antiLag) {
        if (!antiLag || antiLag) {
            return computePathLag(top, to);
        }
        ArrayList<CustomPathFinder.Vec3> paths = new ArrayList<>();
        antiLagExec.submit(() -> {
            CustomPathFinder.Vec3 topFrom = top;
            if (!canPassThrow(new BlockPos(topFrom.mc_int()))) {
                topFrom = topFrom.addVector(0, 1, 0);
            }

            CustomPathFinder pathfinder = new CustomPathFinder(topFrom, to);
            pathfinder.compute();

            int i = 0;
            CustomPathFinder.Vec3 lastLoc = null;
            CustomPathFinder.Vec3 lastDashLoc = null;
            ArrayList<CustomPathFinder.Vec3> path = new ArrayList<>();
            ArrayList<CustomPathFinder.Vec3> pathFinderPath = pathfinder.getPath();
            for (CustomPathFinder.Vec3 pathElm : pathFinderPath) {
                if (i == 0 || i == pathFinderPath.size() - 1) {
                    if (lastLoc != null) {
                        path.add(lastLoc.addVector(0.5, 0, 0.5));
                    }
                    path.add(pathElm.addVector(0.5, 0, 0.5));
                    lastDashLoc = pathElm;
                } else {
                    boolean canContinue = true;
                    double dashDistance = 5.0D;
                    if (pathElm.squareDistanceTo(lastDashLoc) > dashDistance * dashDistance) {
                        canContinue = false;
                    } else {
                        double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                        double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                        double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                        double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                        double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                        double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                        cordsLoop:
                        for (int x = (int) smallX; x <= bigX; x++) {
                            for (int y = (int) smallY; y <= bigY; y++) {
                                for (int z = (int) smallZ; z <= bigZ; z++) {
                                    if (!CustomPathFinder.checkPositionValidity(x, y, z)) {
                                        canContinue = false;
                                        break cordsLoop;
                                    }
                                }
                            }
                        }
                    }
                    if (!canContinue) {
                        path.add(lastLoc.addVector(0.5, 0, 0.5));
                        lastDashLoc = lastLoc;
                    }
                }
                lastLoc = pathElm;
                i++;

                paths.clear();
                paths.addAll(path);
            }
        });
        return paths;
    }

    private static ArrayList<CustomPathFinder.Vec3> computePathLag(CustomPathFinder.Vec3 topFrom, final CustomPathFinder.Vec3 to) {
        if (!canPassThrow(new BlockPos(topFrom.mc_int()))) {
            topFrom = topFrom.addVector(0, 1, 0);
        }

        CustomPathFinder pathfinder = new CustomPathFinder(topFrom, to);
        pathfinder.compute();

        int i = 0;
        CustomPathFinder.Vec3 lastLoc = null;
        CustomPathFinder.Vec3 lastDashLoc = null;
        ArrayList<CustomPathFinder.Vec3> path = new ArrayList<>();
        ArrayList<CustomPathFinder.Vec3> pathFinderPath = pathfinder.getPath();
        for (CustomPathFinder.Vec3 pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                double dashDistance = 5.0D;
                if (pathElm.squareDistanceTo(lastDashLoc) > dashDistance * dashDistance) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    cordsLoop:
                    for (int x = (int) smallX; x <= bigX; x++) {
                        for (int y = (int) smallY; y <= bigY; y++) {
                            for (int z = (int) smallZ; z <= bigZ; z++) {
                                if (!CustomPathFinder.checkPositionValidity(x, y, z)) {
                                    canContinue = false;
                                    break cordsLoop;
                                }
                            }
                        }
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            i++;
        }
        return path;
    }
}
