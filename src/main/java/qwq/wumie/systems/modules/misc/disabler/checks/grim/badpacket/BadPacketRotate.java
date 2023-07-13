package qwq.wumie.systems.modules.misc.disabler.checks.grim.badpacket;

import net.minecraft.network.packet.Packet;
import qwq.wumie.mixininterface.FPlayerMoveC2SPacket;
import qwq.wumie.systems.modules.misc.Disabler;
import qwq.wumie.systems.modules.misc.disabler.Check;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class BadPacketRotate extends Check {
    public BadPacketRotate() {
        super("badpacket_rotate");
    }

    @Override
    public void onPacketSend(Packet packet, Disabler.Mode mode, boolean badpacketEnable, boolean combatEnable, boolean movementEnable) {
        if (mode.equals(Disabler.Mode.GrimAC) && badpacketEnable) {
            if (packet instanceof PlayerMoveC2SPacket p) {
                if (Math.min(p.getYaw(mc.player.getYaw()), 180) < 180) {
                    ((FPlayerMoveC2SPacket) p).setYaw(-180);
                }
                if (Math.min(p.getPitch(mc.player.getPitch()), 90) < 90) {
                    ((FPlayerMoveC2SPacket) p).setYaw(-90);
                    return;
                }
                if (Math.min(p.getYaw(mc.player.getYaw()), 180) == 180) {
                    ((FPlayerMoveC2SPacket) p).setYaw(180);
                }
                if (Math.min(p.getPitch(mc.player.getPitch()), 90) == 90) {
                    ((FPlayerMoveC2SPacket) p).setYaw(90);
                    return;
                }
            }
        }
        super.onPacketSend(packet, mode, badpacketEnable, combatEnable, movementEnable);
    }
}
