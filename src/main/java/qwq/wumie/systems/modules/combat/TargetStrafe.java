package qwq.wumie.systems.modules.combat;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.systems.modules.movement.Flight;
import meteordevelopment.meteorclient.systems.modules.movement.speed.Speed;
import qwq.wumie.utils.player.MeteorRotation;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;

import static java.lang.Math.*;
import static net.minecraft.util.math.MathHelper.sqrt;

public class TargetStrafe extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Double> radius = sgGeneral.add(new DoubleSetting.Builder()
            .name("radius")
            .sliderRange(0, 10)
            .min(0.0)
            .defaultValue(3)
            .build()
    );

    public final Setting<Integer> test = sgGeneral.add(new IntSetting.Builder()
            .name("void-test")
            .sliderRange(-64, 320)
            .min(-64)
            .build()
    );

    private final Setting<Boolean> holdSpace = sgGeneral.add(new BoolSetting.Builder()
            .name("hold-space")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> onlySpeed = sgGeneral.add(new BoolSetting.Builder()
            .name("only-speed")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> onlyFly = sgGeneral.add(new BoolSetting.Builder()
            .name("only-fly")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
            .name("render")
            .defaultValue(true)
            .build()
    );

    private final Setting<SettingColor> renderColor = sgRender.add(new ColorSetting.Builder()
            .name("render-color")
            .visible(render::get)
            .defaultValue(new SettingColor(204, 0, 0, 10))
            .build()
    );

    public TargetStrafe() {
        super(Categories.Movement, "target-strafe", "Strafe around your target");
    }

    private int direction = -1;
    public Entity target;
    private final KillAura aura = Modules.get().get(KillAura.class);

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        target = aura.isActive() ? aura.getDirTarget() == null ? null : aura.getTarget() : null;
    }

    @EventHandler
    public void onRender(Render3DEvent event) {
        if (shouldRender()) {
            event.rpl.drawTargetStrafeCircle(target,radius.get(),event,renderColor.get());
        }
    }

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (canStrafe(target)) {
            var yaw = getRotationFromEyeHasPrev(target).yaw;
            var aroundVoid = false;
            for (int x = -1; x < 0; x++) {
                for (int z = -1; z < 0; z++) {
                    if (isVoid(x, z)) aroundVoid = true;
                }
            }

            if (mc.player.horizontalCollision || aroundVoid) direction *= -1;

            var targetStrafe = ((mc.player.forwardSpeed != 0F) ? mc.player.sidewaysSpeed * direction : direction);
            if (!PlayerUtils.isBlockUnder()) targetStrafe = 0f;
            var rotAssist = 45 / mc.player.distanceTo(target);
            var moveAssist = (double) (45f / getStrafeDistance(target));

            var mathStrafe = 0f;

            if (targetStrafe > 0) {
                if ((target.getBoundingBox().minY > mc.player.getBoundingBox().maxY || target.getBoundingBox().maxY < mc.player.getBoundingBox().minY) && mc.player.distanceTo(
                        target
                ) < radius.get()
            ) yaw += -rotAssist;
                mathStrafe += -moveAssist;
            } else if (targetStrafe < 0) {
                if ((target.getBoundingBox().minY > mc.player.getBoundingBox().maxY ||target.getBoundingBox().maxY < mc.player.getBoundingBox().minY) && mc.player.distanceTo(
                        target
                ) < radius.get()
            ) yaw += rotAssist;
                mathStrafe += moveAssist;
            }

            var doSomeMath = new double[]{
                    cos(Math.toRadians((yaw + 90f + mathStrafe))),
                    sin(Math.toRadians((yaw + 90f + mathStrafe)))
            };
            var moveSpeed = sqrt((float) (pow(event.movement.x,2.0) + pow(event.movement.z,2.0)));

            var asLast = new double[]{
                    moveSpeed * doSomeMath[0],
                    moveSpeed * doSomeMath[1]
            };

            event.movement.add(asLast[0],0,asLast[1]);
        }
    }

    public MeteorRotation getRotationFromEyeHasPrev(Entity target) {
        final double x = (target.prevX + (target.getX() - target.prevX));
        final double y = (target.prevY + (target.getY() - target.prevY));
        final double z = (target.prevZ + (target.getZ() - target.prevZ));
        return getRotationFromEyeHasPrev(x, y, z);
    }

    public MeteorRotation getRotationFromEyeHasPrev(double x, double y, double z) {
        double xDiff = x - (mc.player.prevX + (mc.player.getX() - mc.player.prevX));
        double yDiff = y - ((mc.player.prevY + (mc.player.getY() - mc.player.prevY)) + (mc.player.getBoundingBox().maxY - mc.player.getBoundingBox().minY));
        double zDiff = z - (mc.player.prevZ + (mc.player.getZ() - mc.player.prevZ));
        final double dist = sqrt((float) (xDiff * xDiff + zDiff * zDiff));
        return new MeteorRotation((float) (Math.atan2(zDiff, xDiff) * 180D / Math.PI) - 90F, (float) -(Math.atan2(yDiff, dist) * 180D / Math.PI));
    }

    private boolean isVoid(int xPos, int zPos) {
        if ((mc.player.getY() < test.get())) return true;

        var off = 0;
        while (off < mc.player.getY() + 2) {
            var bb = mc.player.getBoundingBox().offset(xPos, -off, zPos);
            if (mc.world.getEntityCollisions(mc.player, bb).isEmpty()) {
                off += 2;
                continue;
            }
            return false;
        }
        return true;
    }

    private float getStrafeDistance(Entity target) {
        return (float) Math.max(mc.player.distanceTo(target) - radius.get(), mc.player.distanceTo(
                target
        ) - (mc.player.distanceTo(target) - radius.get() / (radius.get() * 2)));
    }

    private boolean canStrafe(Entity target) {
        return target != null && (!holdSpace.get() || mc.options.jumpKey.isPressed()) && ((!onlySpeed.get() || Modules.get().get(Speed.class).isActive()) || (onlyFly.get() && Modules.get().get(Flight.class).isActive()));
    }

    private boolean shouldRender() {
        return target != null && render.get();
    }
}
