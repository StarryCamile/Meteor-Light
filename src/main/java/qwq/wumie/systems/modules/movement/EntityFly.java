package qwq.wumie.systems.modules.movement;


import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class EntityFly extends Module {
    Entity lastRide = null;
    Keybind down = Keybind.fromKey(GLFW.GLFW_KEY_LEFT_ALT);

    public EntityFly() {
        super(Categories.Movement,"EntityFly", "boatfly but yes");
    }

    @EventHandler
    public void onTick(TickEvent.Pre e) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        Entity vehicle = mc.player.getVehicle();
        if (vehicle == null) return;
        lastRide = vehicle;
        vehicle.setNoGravity(true);
        if (vehicle instanceof MobEntity) {
            ((MobEntity) vehicle).setAiDisabled(true);
        }
        Vec3d entityPos = vehicle.getPos();
        GameOptions go = mc.options;
        float y = mc.player.getYaw();
        int mx = 0, my = 0, mz = 0;
        if (go.jumpKey.isPressed())
            my++;
        if (go.backKey.isPressed())
            mz++;
        if (go.leftKey.isPressed())
            mx--;
        if (go.rightKey.isPressed())
            mx++;
        if (down.isPressed())
            my--;
        if (go.forwardKey.isPressed())
            mz--;
        double ts = 1;
        double s = Math.sin(Math.toRadians(y));
        double c = Math.cos(Math.toRadians(y));
        double nx = ts * mz * s;
        double nz = ts * mz * -c;
        double ny = ts * my;
        nx += ts * mx * -c;
        nz += ts * mx * -s;
        Vec3d nv3 = new Vec3d(nx, ny, nz);
        entityPos = entityPos.add(nv3.multiply(0.4));
        vehicle.updatePosition(entityPos.x, entityPos.y, entityPos.z);
        vehicle.setVelocity(0, 0, 0);
        vehicle.setYaw(mc.player.getYaw());
        VehicleMoveC2SPacket p = new VehicleMoveC2SPacket(vehicle);
        mc.getNetworkHandler().sendPacket(p);
    }

    @Override
    public void onDeactivate() {
        if (lastRide != null) {
            lastRide.setNoGravity(false);
        }
    }
}
