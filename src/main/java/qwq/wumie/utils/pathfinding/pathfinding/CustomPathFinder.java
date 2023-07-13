/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.utils.pathfinding.pathfinding;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;

import static qwq.wumie.systems.modules.lemon.utils.BlockInfo.*;

public class CustomPathFinder {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private final Vec3 startVec3;
    private final Vec3 endVec3;
    private ArrayList<Vec3> path = new ArrayList<>();
    private final ArrayList<Hub> hubs = new ArrayList<>();
    private final ArrayList<Hub> hubsToWork = new ArrayList<>();
    private static final Vec3[] flatCardinalDirections = new Vec3[]{new Vec3(1.0, 0.0, 0.0), new Vec3(-1.0, 0.0, 0.0), new Vec3(0.0, 0.0, 1.0), new Vec3(0.0, 0.0, -1.0)};

    public Vec3 floor(Vec3 v) {
        return new Vec3(Math.floor(v.x), Math.floor(v.y), Math.floor(v.z));
    }

    public CustomPathFinder(Vec3 startVec3, Vec3 endVec3) {
        this.startVec3 = startVec3.addVector(0.0, 0.0, 0.0).floor();
        this.endVec3 = endVec3.addVector(0.0, 0.0, 0.0).floor();
    }

    public ArrayList<Vec3> getPath() {
        return this.path;
    }

    public void compute() {
        this.compute(1000, 4);
    }

    public void compute(int loops, int depth) {
        this.path.clear();
        this.hubsToWork.clear();
        ArrayList<Vec3> initPath = new ArrayList<>();
        initPath.add(this.startVec3);
        this.hubsToWork.add(new Hub(this.startVec3, null, initPath, this.startVec3.squareDistanceTo(this.endVec3), 0.0, 0.0));
        int i = 0;
        loop:
        while (i < loops) {
            this.hubsToWork.sort(new CompareHub());
            int j = 0;
            if (this.hubsToWork.size() == 0) break;
            for (Hub hub : new ArrayList<>(this.hubsToWork)) {
                Vec3 loc2;
                if (++j > depth) break;
                this.hubsToWork.remove(hub);
                this.hubs.add(hub);
                int n = flatCardinalDirections.length;
                int n2 = 0;
                while (n2 < n) {
                    Vec3 direction = flatCardinalDirections[n2];
                    Vec3 loc = hub.getLoc().add(direction).floor();
                    if (CustomPathFinder.checkPositionValidity(loc) && this.addHub(hub, loc, 0.0)) break loop;
                    ++n2;
                }
                Vec3 loc1 = hub.getLoc().addVector(0.0, 1.0, 0.0).floor();
                if (CustomPathFinder.checkPositionValidity(loc1) && this.addHub(hub, loc1, 0.0) || CustomPathFinder.checkPositionValidity(loc2 = hub.getLoc().addVector(0.0, -1.0, 0.0).floor()) && this.addHub(hub, loc2, 0.0)) break loop;
            }
            ++i;
        }

        this.hubs.sort(new CompareHub());
        this.path = this.hubs.get(0).getPath();
    }

    public static boolean checkPositionValidity(Vec3 loc) {
        return CustomPathFinder.checkPositionValidity((int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
    }

    public static boolean checkPositionValidity(BlockPos loc) {
        return CustomPathFinder.checkPositionValidity((int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
    }

    public static boolean checkPositionValidity(int x, int y, int z) {
        BlockPos block1 = new BlockPos(x, y, z);
        BlockPos block2 = new BlockPos(x, y + 1, z);
        BlockPos block3 = new BlockPos(x, y - 1, z);
        return
            !CustomPathFinder.isBlockSolid(block1) &&
            !CustomPathFinder.isBlockSolid(block2) &&
             CustomPathFinder.isSafeToWalkOn(block3);
    }

    private static boolean isBlockSolid(BlockPos block) {
        BlockState blockState = getBlockState(block);
        Block b = getBlock(block);

        return (
                ((blockState.isSolidBlock(mc.world, block) && blockState.isFullCube(mc.world,block))) || b instanceof SlabBlock || b instanceof StairsBlock || b instanceof CactusBlock || b instanceof ChestBlock || b instanceof EnderChestBlock || b instanceof SkullBlock || b instanceof PaneBlock ||
                        b instanceof FenceBlock || b instanceof WallBlock || b instanceof GlassBlock || b instanceof PistonBlock || b instanceof PistonExtensionBlock || b instanceof PistonHeadBlock || b instanceof StainedGlassBlock ||
                        b instanceof TrapdoorBlock ||
                        // 1.14+
                        b instanceof BambooBlock || b instanceof BellBlock ||
                        b instanceof CakeBlock || b instanceof RedstoneBlock ||
                        b instanceof LeavesBlock
                );
    }

    private static boolean isSafeToWalkOn(BlockPos block) {
        Block b = getBlock(block);
        return (
            !(b instanceof FenceBlock) && !(b instanceof WallBlock));
    }

    public Hub isHubExisting(Vec3 loc) {
        for (Hub hub : this.hubs) {
            if (hub.getLoc().getX() != loc.getX() || hub.getLoc().getY() != loc.getY() || hub.getLoc().getZ() != loc.getZ())
                continue;
            return hub;
        }
        for (Hub hub : this.hubsToWork) {
            if (hub.getLoc().getX() != loc.getX() || hub.getLoc().getY() != loc.getY() || hub.getLoc().getZ() != loc.getZ())
                continue;
            return hub;
        }
        return null;
    }

    public boolean addHub(Hub parent, Vec3 loc, double cost) {
        Hub existingHub = this.isHubExisting(loc);
        double totalCost = cost;
        if (parent != null) {
            totalCost += parent.getTotalCost();
        }
        if (existingHub == null) {
            double minDistanceSquared = 9.0;
            if (loc.getX() == this.endVec3.getX() && loc.getY() == this.endVec3.getY() && loc.getZ() == this.endVec3.getZ() || loc.squareDistanceTo(this.endVec3) <= minDistanceSquared) {
                this.path.clear();
                this.path = parent.getPath();
                this.path.add(loc);
                return true;
            }
            ArrayList<Vec3> path = new ArrayList<>(parent.getPath());
            path.add(loc);
            this.hubsToWork.add(new Hub(loc, parent, path, loc.squareDistanceTo(this.endVec3), cost, totalCost));
        } else if (existingHub.getCost() > cost) {
            ArrayList<Vec3> path = new ArrayList<>(parent.getPath());
            path.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(path);
            existingHub.setSquareDistanceToFromTarget(loc.squareDistanceTo(this.endVec3));
            existingHub.setCost(cost);
            existingHub.setTotalCost(totalCost);
        }
        return false;
    }

    public static class CompareHub implements Comparator<Hub> {
        @Override
        public int compare(Hub o1, Hub o2) {
            return (int) (o1.getSquareDistanceToFromTarget() + o1.getTotalCost() - (o2.getSquareDistanceToFromTarget() + o2.getTotalCost()));
        }
    }

    private static class Hub {
        private Vec3 loc;
        private Hub parent;
        private ArrayList<Vec3> path;
        private double squareDistanceToFromTarget;
        private double cost;
        private double totalCost;

        public Hub(Vec3 loc, Hub parent, ArrayList<Vec3> path, double squareDistanceToFromTarget, double cost, double totalCost) {
            this.loc = loc;
            this.parent = parent;
            this.path = path;
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
            this.cost = cost;
            this.totalCost = totalCost;
        }

        public Vec3 getLoc() {
            return this.loc;
        }

        public Hub getParent() {
            return this.parent;
        }

        public ArrayList<Vec3> getPath() {
            return this.path;
        }

        public double getSquareDistanceToFromTarget() {
            return this.squareDistanceToFromTarget;
        }

        public double getCost() {
            return this.cost;
        }

        public void setLoc(Vec3 loc) {
            this.loc = loc;
        }

        public void setParent(Hub parent) {
            this.parent = parent;
        }

        public void setPath(ArrayList<Vec3> path) {
            this.path = path;
        }

        public void setSquareDistanceToFromTarget(double squareDistanceToFromTarget) {
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public double getTotalCost() {
            return this.totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }
    }

    public static class Vec3 {
        public double x;
        public double y;
        public double z;

        public Vec3(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vec3(BlockPos blockPos) {
            this.x = blockPos.getX();
            this.y = blockPos.getY();
            this.z = blockPos.getZ();
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }

        public Vec3 addVector(double x, double y, double z) {
            return new Vec3(this.x + x, this.y + y, this.z + z);
        }

        public Vec3 floor() {
            return new Vec3(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z));
        }

        public double squareDistanceTo(Vec3 v) {
            return Math.pow(v.x - this.x, 2.0) + Math.pow(v.y - this.y, 2.0) + Math.pow(v.z - this.z, 2.0);
        }

        public Vec3 add(Vec3 v) {
            return this.addVector(v.getX(), v.getY(), v.getZ());
        }

        public Vec3d mc() {
            return new Vec3d(this.x, this.y, this.z);
        }

        public Vector3f mc_float() {
            return new Vector3f((float) this.x, (float) this.y, (float) this.z);
        }

        public Vec3d mc_double() {
            return new Vec3d(this.x, this.y, this.z);
        }

        public Vec3i mc_int() {
            return new Vec3i(MathHelper.floor(x), MathHelper.floor(this.y), MathHelper.floor(this.z));
        }

        public String toString() {
            return "[" + this.x + ";" + this.y + ";" + this.z + "]";
        }
    }
}
