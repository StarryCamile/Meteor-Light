/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package qwq.wumie.systems.modules.combat;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Breadcrumbs;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import qwq.wumie.systems.modules.lemon.utils.BlockInfo;
import qwq.wumie.utils.pathfinding.pathfinding.CustomPathFinder;
import qwq.wumie.utils.pathfinding.pathfinding.jigsaw.Node;
import qwq.wumie.utils.pathfinding.player.PathFind;
import qwq.wumie.utils.time.MSTimer;
import qwq.wumie.utils.time.Timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class InfiniteAura extends Module {
    public InfiniteAura() {
        super(Categories.Combat, "Infinite-aura", "The original infinite reach :)");
    }

    // no clip mode
    private final SettingGroup sgDelay = settings.createGroup("Delay");
    private final List<Entity> targets = new CopyOnWriteArrayList<>();
    private ArrayList<CustomPathFinder.Vec3> path = new ArrayList<>();
    private ArrayList<CustomPathFinder.Vec3>[] test = new ArrayList[50];
    private final Timer cps = new Timer();
    public static Timer timer = new Timer();
    private int findCount = 0;

    // plus mode
    boolean attack = false;
    double x;
    double y;
    double z;
    double xPreEn;
    double yPreEn;
    double zPreEn;
    double xPre;
    double yPre;
    double zPre;
    int stage = 0;

    ArrayList<Vec3d> positions = new ArrayList<Vec3d>();
    ArrayList<Vec3d> positionsBack = new ArrayList<Vec3d>();
    ArrayList<Node> triedPaths = new ArrayList<Node>();

    public static final double maxXZTP = 9.8;
    public static final int maxYTP = 9;

    private MSTimer backTimer = new MSTimer();
    private boolean backed;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("mode")
            .defaultValue(Mode.Vanilla)
            .build()
    );


    private final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
            .name("entities")
            .description("Entities to attack.")
            .onlyAttackable()
            .build()
    );

    private final Setting<Integer> Targets = sgGeneral.add(new IntSetting.Builder()
            .name("MaxTargets")
            .description("attack max targets")
            .min(1)
            .max(50)
            .defaultValue(1)
            .build()
    );

    private final Setting<Double> Range = sgGeneral.add(new DoubleSetting.Builder()
            .name("Range")
            .description("teleport Range")
            .range(6, 250)
            .sliderRange(6, 250)
            .defaultValue(150)
            .build()
    );

    private final Setting<Boolean> ground = sgDelay.add(new BoolSetting.Builder()
            .name("ground")
            .description("Teleport the ground status.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> smartDelay = sgDelay.add(new BoolSetting.Builder()
            .name("smart-delay")
            .description("Uses the vanilla cooldown to attack entities.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> backDelay = sgDelay.add(new IntSetting.Builder()
            .name("back-delay")
            .description("How fast you hit the entity then back in ticks.")
            .defaultValue(0)
            .min(0)
            .sliderMax(10000)
            .build()
    );

    private final Setting<Boolean> antiLag = sgGeneral.add(new BoolSetting.Builder()
            .name("anti-lag")
            .description("Find ways anti lag.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> hitDelay = sgDelay.add(new IntSetting.Builder()
            .name("hit-delay")
            .description("How fast you hit the entity in ticks.")
            .defaultValue(0)
            .min(0)
            .sliderMax(60)
            .visible(() -> !smartDelay.get())
            .build()
    );

    private final Setting<Boolean> randomDelayEnabled = sgDelay.add(new BoolSetting.Builder()
            .name("random-delay-enabled")
            .description("Adds a random delay between hits to attempt to bypass anti-cheats.")
            .defaultValue(false)
            .visible(() -> !smartDelay.get())
            .build()
    );

    private final Setting<Integer> randomDelayMax = sgDelay.add(new IntSetting.Builder()
            .name("random-delay-max")
            .description("The maximum value for random delay.")
            .defaultValue(4)
            .min(0)
            .sliderMax(20)
            .visible(() -> randomDelayEnabled.get() && !smartDelay.get())
            .build()
    );

    private final Setting<Integer> switchDelay = sgDelay.add(new IntSetting.Builder()
            .name("switch-delay")
            .description("How many ticks to wait before hitting an entity after switching hotbar slots.")
            .defaultValue(0)
            .min(0)
            .build()
    );

    private final Setting<Integer> maxFindCount = sgDelay.add(new IntSetting.Builder()
            .name("max-find-count")
            .description("idk")
            .sliderRange(100, 1145141919)
            .defaultValue(24600)
            .min(0)
            .build()
    );

    private final Setting<Boolean> drawpath = sgGeneral.add(new BoolSetting.Builder()
            .name("path")
            .description("render path")
            .defaultValue(false)
            .build()
    );

    private final Setting<PathDrawMode> pathMode = sgGeneral.add(new EnumSetting.Builder<PathDrawMode>()
            .name("path-mode")
            .description("Rendering path mode")
            .defaultValue(PathDrawMode.Line)
            .build()
    );

    private final Setting<SettingColor> pathColor = sgGeneral.add(new ColorSetting.Builder()
            .name("path-color")
            .description("The color of the path render.")
            .defaultValue(new SettingColor(200, 200, 200))
            .build()
    );

    public enum PathDrawMode {
        Line,
        Box
    }

    private final Setting<Boolean> drawEsp = sgGeneral.add(new BoolSetting.Builder()
            .name("esp")
            .description("render path")
            .defaultValue(false)
            .build()
    );

    private final Setting<SortPriority> priority = sgGeneral.add(new EnumSetting.Builder<SortPriority>()
            .name("priority")
            .description("How to filter targets within range.")
            .defaultValue(SortPriority.LowestHealth)
            .build()
    );

    private final Setting<Boolean> nametagged = sgGeneral.add(new BoolSetting.Builder()
            .name("nametagged")
            .description("Whether or not to attack mobs with a name tag.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> babies = sgGeneral.add(new BoolSetting.Builder()
            .name("babies")
            .description("Whether or not to attack baby variants of the entity.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> ignorePassive = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-passive")
            .description("Only attacks angry piglins and enderman.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> packetmove = sgGeneral.add(new BoolSetting.Builder()
            .name("packet-move")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
    );

    @Override
    public void onActivate() {
        targets.clear();
        if (mode.get().equals(Mode.Plus)) {
            this.stage = 0;
            this.x = 0;
            this.y = 0;
            this.z = 0;
            this.xPreEn = 0;
            this.yPreEn = 0;
            this.zPreEn = 0;
            this.attack = false;
            return;
        }
        findCount = 0;
        timer.reset();
        backTimer.reset();
        super.onActivate();
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (event.packet instanceof UpdateSelectedSlotC2SPacket) {
            switchTimer = switchDelay.get();
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }


    @Override
    public void onDeactivate() {
        hitDelayTimer = 0;
        super.onDeactivate();
    }

    private int hitDelayTimer, switchTimer;

    private boolean delayCheck() {
        if (switchTimer > 0) {
            switchTimer--;
            return false;
        }

        if (smartDelay.get()) return mc.player.getAttackCooldownProgress(0.5f) >= 1;

        if (hitDelayTimer > 0) {
            hitDelayTimer--;
            return false;
        } else {
            hitDelayTimer = hitDelay.get();
            if (randomDelayEnabled.get()) hitDelayTimer += Math.round(Math.random() * randomDelayMax.get());
            return true;
        }
    }

    @EventHandler
    public void onUpdate(TickEvent.Pre e) {
        TargetUtils.getList(targets, this::entityCheck, priority.get(), Targets.get());
        if (delayCheck()) {
            switch (mode.get()) {
              /*  case Plus -> {
                    if (targets.size() > 0) {
                        for (int i = 0; i < (targets.size() > Targets.get() ? Targets.get() : targets.size()); i++) {
                            Entity en = targets.get(i);
                            positions.clear();
                            positionsBack.clear();
                            triedPaths.clear();

                            TeleportResult result = Utils.pathFinderTeleportTo(mc.player.getPos(), en.getPos());
                            triedPaths = result.triedPaths;

                            if(!result.foundPath) {
                                return;
                            }

                            ArrayList<CustomPathFinder.Vec3> paths = new ArrayList<>();
                            result.positions.forEach((p) -> {
                                paths.add(new CustomPathFinder.Vec3(p.x,p.y,p.z));
                            });

                            test[i] = paths;

                            attackEntity(en);
                            positions = result.positions;

                            TeleportResult resultBack = Utils.pathFinderTeleportBack(positions);

                            positionsBack = resultBack.positionsBack;

                            cps.reset();
                        }
                    }
                }*/
                case Vanilla,Plus -> {
                    if (targets.size() > 0) {
                        test = new ArrayList[100];
                        for (int i = 0; i < (targets.size() > Targets.get() ? Targets.get() : targets.size()); i++) {
                            Entity T = targets.get(i);
                            findCount = 0;
                            CustomPathFinder.Vec3 topFrom = new CustomPathFinder.Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());
                            CustomPathFinder.Vec3 to = new CustomPathFinder.Vec3(T.getX(), T.getY(), T.getZ());
                            if (backed) {
                            path = PathFind.computePath(topFrom,to,antiLag.get());
                            if (antiLag.get() && path.isEmpty()) {
                                path = computePath(topFrom, to);
                            }
                            test[i] = path;
                                for (CustomPathFinder.Vec3 pathElm : path) {
                                    if (packetmove.get()) {
                                        sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pathElm.getX(), pathElm.getY(), pathElm.getZ(), ground.get()));
                                    } else {
                                        mc.player.updatePosition(pathElm.getX(), pathElm.getY(), pathElm.getZ());
                                        mc.player.setOnGround(ground.get());
                                    }
                                }
                            }
                            mc.player.swingHand(Hand.MAIN_HAND);
                            attackEntity(T);
                            backed = false;
                            Collections.reverse(path);
                            if (backTimer.hasTimePassed(backDelay.get())) {
                                for (CustomPathFinder.Vec3 pathElmi : path) {
                                    if (packetmove.get()) {
                                        sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pathElmi.getX(), pathElmi.getY(), pathElmi.getZ(), ground.get()));
                                    } else {
                                        mc.player.updatePosition(pathElmi.getX(), pathElmi.getY(), pathElmi.getZ());
                                        mc.player.setOnGround(ground.get());
                                    }
                                }
                                backed = true;

                                backTimer.reset();
                            }
                        }
                        cps.reset();
                    }
                }
            }
        }
    }

    public static PathDrawMode getPathMode() {
        InfiniteAura mod = Modules.get().get(InfiniteAura.class);
        return mod != null ? mod.pathMode.get() : PathDrawMode.Line;
    }

    public static Color getPathColor() {
        InfiniteAura mod = Modules.get().get(InfiniteAura.class);
        return mod != null ? mod.pathColor.get() : new Color(200,200,200);
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        try {

            if (!this.targets.isEmpty() && drawEsp.get() && this.targets.size() > 0) {
                for (int i = 0; i < (this.targets.size() > Targets.get() ? Targets.get() : this.targets.size()); ++i) {
                    event.renderer.box(targets.get(i).getBoundingBox(), new Color(255, 255, 255, 30), Color.WHITE, ShapeMode.Both, 0);
                }
            }
            switch (mode.get()) {
                case Vanilla, Plus -> {
                    if (!this.path.isEmpty() && drawpath.get()) {
                        ArrayList<CustomPathFinder.Vec3>[] test = this.test.clone();
                        for (int i = 0; i < targets.size(); i++) {
                            try {
                                if (test != null && test[i] != null) {
                                    CustomPathFinder.Vec3 lastPoint = null;

                                    for (CustomPathFinder.Vec3 pos : test[i]) {
                                        switch (pathMode.get()) {
                                            case Box -> {
                                                if (pos != null)
                                                    drawPath(pos, event);
                                            }
                                            case Line -> {
                                                if (lastPoint != null && pos != null) {
                                                    int current = event.renderer.lines.vec3(pos.x,pos.y,pos.z).color(pathColor.get()).next();
                                                    int last = event.renderer.lines.vec3(lastPoint.x,lastPoint.y,lastPoint.z).color(pathColor.get()).next();
                                                    event.renderer.lines.line(last,current);
                                                }

                                                lastPoint = pos;
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                        if (this.cps.check(1000.0f)) {
                            this.test = new ArrayList[50];
                            this.path.clear();
                        }
                    }
                }
            }

        } catch (Exception e) {e.printStackTrace();}
    }

    public void drawPath(CustomPathFinder.Vec3 vec, Render3DEvent r) {
        r.rpl.vecBox(vec, mc.player.getBoundingBox(mc.player.getPose()), InfiniteAura.getPathColor(), InfiniteAura.getPathColor(), ShapeMode.Lines, 0);
    }

    public void drawPathTo(Vec3d vec, Render3DEvent r) {
        r.rpl.vecBox(vec, mc.player.getBoundingBox(mc.player.getPose()), InfiniteAura.getPathColor(), InfiniteAura.getPathColor(), ShapeMode.Lines, 0);
    }

    public void drawPathBack(Vec3d vec, Render3DEvent r) {
        r.rpl.vecBox(vec, mc.player.getBoundingBox(mc.player.getPose()), InfiniteAura.getPathColor().a(100), InfiniteAura.getPathColor().a(100), ShapeMode.Lines, 0);
    }

    private ArrayList<CustomPathFinder.Vec3> computePath(CustomPathFinder.Vec3 topFrom, CustomPathFinder.Vec3 to) {
        if (!canPassThrow(BlockPos.ofFloored(topFrom.mc()))) {
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
            if (findCount >= maxFindCount.get()) {
                continue;
            }
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
                                if (CustomPathFinder.checkPositionValidity(x, y, z)) {
                                    findCount++;
                                } else {
                                    findCount++;
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

    private boolean canPassThrow(BlockPos pos) {
        Block block = BlockInfo.getBlock(new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
        // IBlock materialBlock = (IBlock) block;
        // return materialBlock.getMaterial() == Material.AIR || materialBlock.getMaterial() == Material.PLANT || block == Blocks.VINE || block == Blocks.LADDER || block == Blocks.WATER || block == Blocks.ACACIA_WALL_SIGN || block == Blocks.OAK_WALL_SIGN || block == Blocks.BIRCH_WALL_SIGN || block == Blocks.CRIMSON_WALL_SIGN || block == Blocks.JUNGLE_WALL_SIGN || block == Blocks.MANGROVE_WALL_SIGN || block == Blocks.SPRUCE_WALL_SIGN || block == Blocks.WARPED_WALL_SIGN || block == Blocks.DARK_OAK_WALL_SIGN;
        return block instanceof AirBlock || block instanceof PlantBlock || block instanceof VineBlock || block instanceof LadderBlock || block instanceof FluidBlock || block instanceof SignBlock;
    }

    private boolean entityCheck(Entity entity) {
        if (entity.equals(mc.player) || entity.equals(mc.cameraEntity)) return false;
        if ((entity instanceof LivingEntity && ((LivingEntity) entity).isDead()) || !entity.isAlive()) return false;
        if (PlayerUtils.distanceTo(entity) > Range.get()) return false;
        if (!entities.get().contains(entity.getType())) return false;
        if (!nametagged.get() && entity.hasCustomName()) return false;
        if (ignorePassive.get()) {
            if (entity instanceof EndermanEntity enderman && !enderman.isAngry()) return false;
            if (entity instanceof Tameable tameable
                    && tameable.getOwnerUuid() != null
                    && tameable.getOwnerUuid().equals(mc.player.getUuid())) return false;
            if (entity instanceof MobEntity mob && !mob.isAttacking() && !(entity instanceof PhantomEntity))
                return false; // Phantoms don't seem to set the attacking property
        }
        if (entity instanceof PlayerEntity player) {
            if (((PlayerEntity) entity).isCreative()) return false;
            if (!Friends.get().shouldAttack((PlayerEntity) entity)) return false;
            if (Modules.get().get(MeteorAntiBot.class).isBot(player)) return false;
        }
        return !(entity instanceof AnimalEntity) || babies.get() || !((AnimalEntity) entity).isBaby();
    }

    public enum PathFindM {
        PathFind1,
        PathFind2
    }

    public enum Mode {
        Plus,
        Vanilla
    }


}
