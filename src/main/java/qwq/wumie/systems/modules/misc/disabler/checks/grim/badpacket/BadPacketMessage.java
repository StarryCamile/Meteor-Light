package qwq.wumie.systems.modules.misc.disabler.checks.grim.badpacket;

import net.minecraft.network.packet.Packet;
import qwq.wumie.systems.modules.misc.Disabler;
import qwq.wumie.systems.modules.misc.disabler.Check;

public class BadPacketMessage extends Check {
    public BadPacketMessage() {
        super("badpacket_message");
    }

    @Override
    public void onPacketSend(Packet packet, Disabler.Mode mode, boolean badpacketEnable, boolean combatEnable, boolean movementEnable) {
        if (mode.equals(Disabler.Mode.GrimAC) && badpacketEnable) {

        }
        super.onPacketSend(packet, mode, badpacketEnable, combatEnable, movementEnable);
    }
}
