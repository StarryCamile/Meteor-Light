package qwq.wumie.systems.modules.movement;

import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.HoleESP;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import qwq.wumie.utils.pathfinding.pathfinding.CustomPathFinder;
import meteordevelopment.meteorclient.utils.render.color.Color;
import qwq.wumie.utils.time.MSTimer;
import qwq.wumie.utils.time.TickTimer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import qwq.wumie.systems.modules.combat.MeteorAntiBot;
import qwq.wumie.utils.pathfinding.player.PathFind;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.minecraft.util.math.MathHelper.abs;

public class EPlayerTeleport extends Module {
    public EPlayerTeleport() {
        super(Categories.Player, "e-player-tp", "inf tp");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .defaultValue(60)
            .sliderRange(0, 1145141919)
            .range(0, 1145141919)
            .build()
    );

    public final Setting<Integer> tpDelay = sgGeneral.add(new IntSetting.Builder()
            .name("tp-delay")
            .defaultValue(60)
            .sliderRange(-1, 1145141919)
            .range(-1, 1145141919)
            .build()
    );

    private final Setting<Integer> Targets = sgGeneral.add(new IntSetting.Builder()
            .name("MaxTargets")
            .description("teleport max targets")
            .min(1)
            .max(50)
            .defaultValue(1)
            .build()
    );

    private final Setting<Integer> upBlock = sgGeneral.add(new IntSetting.Builder()
            .name("up-block")
            .description("teleport up block")
            .sliderRange(0,10)
            .defaultValue(3)
            .build()
    );

    private final Setting<Boolean> bindTp = sgGeneral.add(new BoolSetting.Builder()
            .name("bind-tp")
            .defaultValue(false)
            .build()
    );

    public final Setting<Keybind> tpKey = sgGeneral.add(new KeybindSetting.Builder()
            .name("tp-key")
            .visible(bindTp::get)
            .defaultValue(Keybind.fromKey(GLFW.GLFW_KEY_INSERT))
            .build()
    );

    private final Setting<SortPriority> priority = sgGeneral.add(new EnumSetting.Builder<SortPriority>()
            .name("priority")
            .description("How to filter targets within range.")
            .defaultValue(SortPriority.LowestHealth)
            .build()
    );

    private MSTimer timer = new MSTimer();
    private final List<Entity> targets = new CopyOnWriteArrayList<>();

    private CustomPathFinder.Vec3 to = null;
    private ArrayList<CustomPathFinder.Vec3> path = new ArrayList<>();
    TickTimer timerA = new TickTimer();

    @Override
    public void onActivate() {
        timer.reset();
        timerA.reset();
        super.onActivate();
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        if (event.action == KeyAction.Press && event.key == tpKey.get().getValue()) {
            TargetUtils.getList(targets, this::entityCheck, priority.get(), Targets.get());
            new Thread(() -> {
                for (Entity entity : targets) {
                    try {
                        stageOne();
                        stageTwo(entity);
                        stageThree(entity);
                        if (tpDelay.get() != -1) {
                            Thread.sleep(tpDelay.get());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private HoleESP.Hole targetHole;

    private int holeID = 0;

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        int delayTick = delay.get();
        if (timer.hasTimePassed(delayTick)) {
            TargetUtils.getList(targets, this::entityCheck, priority.get(), Targets.get());

            new Thread(() -> {
                for (Entity entity : targets) {
                    try {
                        stageOne();
                        stageTwo(entity);
                        stageThree(entity);
                        if (tpDelay.get() != -1) {
                            Thread.sleep(tpDelay.get());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            timer.reset();
        }
    }

    public void stageTwo(CustomPathFinder.Vec3 player) {
        CustomPathFinder.Vec3 topPlayer = new CustomPathFinder.Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        to = new CustomPathFinder.Vec3(player.getX(), player.getY(), player.getZ());
        path = PathFind.computePath(topPlayer, to);
        for (CustomPathFinder.Vec3 pathElm : path) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
        }
    }

    public void stageOne(double blocks) {
        ClientPlayerEntity player = mc.player;
        assert player != null;

        if (player.hasVehicle()) {
            Entity vehicle = player.getVehicle();
            vehicle.setPosition(vehicle.getX(), vehicle.getY() + blocks, vehicle.getZ());
        }
        player.setPosition(player.getX(), player.getY() + blocks, player.getZ());
    }

    public BlockPos canTeleport() {
        BlockPos pos = mc.player.getBlockPos().add(0, 2, 0);
        for (int y = 0; y < 10; y++) {
            BlockPos b1 = pos.add(0, y, 0);
            if (CustomPathFinder.checkPositionValidity(b1)) {
                return b1;
            }
            BlockPos b3 = pos.add(0, -y, 0);
            if (CustomPathFinder.checkPositionValidity(b3)) {
                if (b3.getY() < -64) {
                    return null;

                }
                return b3;
            }
        }

        return null;
    }

    public void stageThree(CustomPathFinder.Vec3 player) {
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY(), player.getZ(), mc.player.isOnGround()));
        mc.player.updatePosition(player.getX(), player.getY(), player.getZ());
        to = null;
        path = null;
    }

    private boolean targetInHole(Entity target, BlockPos hole) {
        BlockPos pPos = target.getBlockPos();

        if (hole.getX() == pPos.getX()) {
            if (hole.getY() == pPos.getY()) {
                return hole.getZ() == pPos.getZ();
            }
        }

        return false;
    }

    private boolean targetInTargetHole(Entity target) {
        BlockPos pPos = target.getBlockPos();

        if (targetHole.blockPos.getX() == pPos.getX()) {
            if (targetHole.blockPos.getY() == pPos.getY()) {
                return targetHole.blockPos.getZ() == pPos.getZ();
            }
        }

        return false;
    }

    private boolean inTargetHole() {
        BlockPos pPos = mc.player.getBlockPos();

        if (targetHole.blockPos.getX() == pPos.getX()) {
            if (targetHole.blockPos.getY() == pPos.getY()) {
                return targetHole.blockPos.getZ() == pPos.getZ();
            }
        }

        return false;
    }

    public void stageOne() {
        ClientPlayerEntity player = mc.player;
        assert player != null;

        double blocks = upBlock.get();
        if (player.hasVehicle()) {
            Entity vehicle = player.getVehicle();
            vehicle.setPosition(vehicle.getX(), vehicle.getY() + blocks, vehicle.getZ());
        }
        player.setPosition(player.getX(), player.getY() + blocks, player.getZ());
    }

    public void stageTwo(Entity player) {
        CustomPathFinder.Vec3 topPlayer = new CustomPathFinder.Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        to = new CustomPathFinder.Vec3(player.getX(), player.getY(), player.getZ());
        path = PathFind.computePath(topPlayer, to);
        for (CustomPathFinder.Vec3 pathElm : path) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
            mc.player.updatePosition(pathElm.getX(), pathElm.getY(), pathElm.getZ());
        }
    }

    public void stageThree(Entity player) {
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY(), player.getZ(), player.isOnGround()));
        mc.player.setPos(player.getX(), player.getY(), player.getZ());
        to = null;
        path = null;
    }

    @EventHandler
    public void render(Render3DEvent e) {
        if (path != null) {
            for (CustomPathFinder.Vec3 pos : path) {
                if (pos != null)
                    drawPath(pos, e);
            }
        }
    }

    public void drawPath(CustomPathFinder.Vec3 vec, Render3DEvent r) {
        r.rpl.vecBox(vec, mc.player.getBoundingBox(mc.player.getPose()), new Color(255, 255, 255, 200), new Color(255, 255, 255, 200), ShapeMode.Lines, 0);
    }

    private boolean entityCheck(Entity entity) {
        if (entity.equals(mc.player) || entity.equals(mc.cameraEntity)) return false;
        if ((entity instanceof LivingEntity && ((LivingEntity) entity).isDead()) || !entity.isAlive()) return false;
        if (entity instanceof PlayerEntity player) {
            if (((PlayerEntity) entity).isCreative()) return false;
            if (player.getDisplayName().getString().contains("NPC")) return false;
            if (!Friends.get().shouldAttack((PlayerEntity) entity)) return false;
            if (Modules.get().get(MeteorAntiBot.class).isBot(player)) return false;
        }
        return (entity instanceof PlayerEntity);
    }
}
