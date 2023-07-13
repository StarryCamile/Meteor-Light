package qwq.wumie.systems.modules.inject.combat;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.combat.Criticals;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import qwq.wumie.mixininterface.FPlayerMoveC2SPacket;
import qwq.wumie.systems.modules.inject.ModuleInject;

public class CritInject extends ModuleInject<Criticals> {
    public CritInject() {
        super(Criticals.class);
    }

    public boolean onPacket(Setting<Criticals.Mode> mode, PacketEvent.Send e) {
        if (mode.get().equals(Criticals.Mode.NoGround)) {
            var packet = e.packet;
            if (packet instanceof PlayerMoveC2SPacket) {
                ((FPlayerMoveC2SPacket) packet).setOnGround(false);
            }

            return false;
        }

        return true;
    }

    public boolean onAttack(Setting<Criticals.Mode> mode, LivingEntity entity) {
        if (mode.get().equals(Criticals.Mode.More) || mode.get().equals(Criticals.Mode.TpHop) || mode.get().equals(Criticals.Mode.NoGround)) {
            switch (mode.get()) {
                case TpHop -> {
                    sendCriticalPacket(0.02, false);
                    sendCriticalPacket(0.01, false);
                    mc.player.updatePosition(mc.player.getX(), mc.player.getY() + 0.01, mc.player.getZ());
                }
                case More -> {
                    sendCriticalPacket(0.00000000001, false);
                    sendCriticalPacket(0,false);
                }
            }
            return false;
        }
        return true;
    }

    private void sendCriticalPacket(double height) {
        sendCriticalPacket(height,false);
    }

    private void sendCriticalPacket(double height,boolean ground) {
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();
        PlayerMoveC2SPacket packet;
        if (height == 0) {
            packet = new PlayerMoveC2SPacket.OnGroundOnly(ground);
        } else {
            packet = new PlayerMoveC2SPacket.PositionAndOnGround(x, y + height, z, ground);
        }
        ((IPlayerMoveC2SPacket) packet).setTag(1337);

        mc.player.networkHandler.sendPacket(packet);
    }
}
