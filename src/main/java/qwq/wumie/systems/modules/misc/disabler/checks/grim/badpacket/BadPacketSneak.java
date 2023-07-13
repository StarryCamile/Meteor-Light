package qwq.wumie.systems.modules.misc.disabler.checks.grim.badpacket;

import net.minecraft.network.packet.Packet;
import qwq.wumie.systems.modules.misc.Disabler;
import qwq.wumie.systems.modules.misc.disabler.Check;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class BadPacketSneak extends Check {
    public BadPacketSneak() {
        super("badpacket_sneak");
    }

    boolean lastSneaking;

    @Override
    public void onInit() {
        lastSneaking = mc.player.isSneaking();
        super.onInit();
    }

    @Override
    public void onPacketSend(Packet packet, Disabler.Mode mode, boolean badpacketEnable, boolean combatEnable, boolean movementEnable) {
        if (mode.equals(Disabler.Mode.GrimAC) && badpacketEnable) {
            if (packet instanceof ClientCommandC2SPacket p) {
                if (p.getMode().equals(ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY)) {
                    if (lastSneaking) {
                        cancel();
                    } else {
                        lastSneaking = true;
                    }
                } else if (p.getMode().equals(ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY)) {
                    if (lastSneaking) {
                        lastSneaking = false;
                    } else {
                        cancel();
                    }
                }
            }
        }
        super.onPacketSend(packet, mode, badpacketEnable, combatEnable, movementEnable);
    }
}
