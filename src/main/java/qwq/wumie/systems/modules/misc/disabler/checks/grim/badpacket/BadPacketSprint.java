package qwq.wumie.systems.modules.misc.disabler.checks.grim.badpacket;

import net.minecraft.network.packet.Packet;
import qwq.wumie.systems.modules.misc.Disabler;
import qwq.wumie.systems.modules.misc.disabler.Check;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class BadPacketSprint extends Check {
    public BadPacketSprint() {
        super("badpacket_sprint");
    }

    boolean last;

    @Override
    public void onInit() {
        last = mc.player.isSprinting();
        super.onInit();
    }

    @Override
    public void onPacketSend(Packet packet, Disabler.Mode mode, boolean badpacketEnable, boolean combatEnable, boolean movementEnable) {
        if (mode.equals(Disabler.Mode.GrimAC) && badpacketEnable) {
            if (packet instanceof ClientCommandC2SPacket p) {
                if (p.getMode().equals(ClientCommandC2SPacket.Mode.START_SPRINTING)) {
                    if (last) {
                        cancel();
                    } else {
                        last = true;
                    }
                } else if (p.getMode().equals(ClientCommandC2SPacket.Mode.STOP_SPRINTING)) {
                    if (last) {
                        last = false;
                    } else {
                        cancel();
                    }
                }
            }
        }
        super.onPacketSend(packet, mode, badpacketEnable, combatEnable, movementEnable);
    }
}
