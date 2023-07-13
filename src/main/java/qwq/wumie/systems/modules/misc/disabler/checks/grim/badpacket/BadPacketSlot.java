package qwq.wumie.systems.modules.misc.disabler.checks.grim.badpacket;

import net.minecraft.network.packet.Packet;
import qwq.wumie.systems.modules.misc.Disabler;
import qwq.wumie.systems.modules.misc.disabler.Check;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

public class BadPacketSlot extends Check {
    public BadPacketSlot() {
        super("badpacket_slot");
    }

    int lastSlot;

    @Override
    public void onInit() {
        lastSlot = mc.player.getInventory().selectedSlot;
        super.onInit();
    }

    @Override
    public void onPacketSend(Packet packet, Disabler.Mode mode, boolean badpacketEnable, boolean combatEnable, boolean movementEnable) {
        if (mode.equals(Disabler.Mode.GrimAC) && badpacketEnable) {
            if (packet instanceof UpdateSelectedSlotC2SPacket p) {
                if (p.getSelectedSlot() == lastSlot) {
                    cancel();
                } else {
                    lastSlot = p.getSelectedSlot();
                }
            }
        }
        super.onPacketSend(packet, mode, badpacketEnable, combatEnable, movementEnable);
    }
}
