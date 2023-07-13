package qwq.wumie.systems.modules.misc.disabler;

import net.minecraft.network.packet.Packet;
import qwq.wumie.systems.modules.misc.Disabler;
import net.minecraft.client.MinecraftClient;

public class Check {
    public String name;
    public boolean canceled;
    public boolean cancelRecEd;
    public static final MinecraftClient mc = MinecraftClient.getInstance();

    public Check(String name) {
        this.name = name;
    }

    public void cancel() {
        canceled = true;
    }
    public void cancelRec() {
        cancelRecEd = true;
    }

    public void onInit() {}
    public void onPacketSend(Packet packet, Disabler.Mode mode, boolean badpacketEnable, boolean combatEnable, boolean movementEnable) {}
    public void onPacketRec(Packet packet, Disabler.Mode mode,boolean badpacketEnable,boolean combatEnable,boolean movementEnable) {}
}
