package qwq.wumie.systems.modules.misc;

import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import qwq.wumie.mixininterface.FPlayerMoveC2SPacket;
import qwq.wumie.systems.modules.misc.disabler.Check;
import qwq.wumie.utils.time.MSTimer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import qwq.wumie.systems.modules.misc.disabler.checks.grim.badpacket.*;

import java.util.ArrayList;
import java.util.List;

public class Disabler extends Module {
    private final SettingGroup sgDefault = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgDefault.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .defaultValue(Mode.GrimAC)
            .build()
    );

    private final Setting<Boolean> grimCombat = sgDefault.add(new BoolSetting.Builder()
            .name("grim-combat")
            .defaultValue(true)
            .visible(mode.get().equals(Mode.GrimAC))
            .build()
    );

    private final Setting<Boolean> grimMovement = sgDefault.add(new BoolSetting.Builder()
            .name("grim-movement")
            .defaultValue(true)
            .visible(mode.get().equals(Mode.GrimAC))
            .build()
    );

    private final Setting<Boolean> grimBadPacket = sgDefault.add(new BoolSetting.Builder()
            .name("grim-bad-packet")
            .defaultValue(true)
            .visible(mode.get().equals(Mode.GrimAC))
            .build()
    );

    private final List<Check> grimChecks = new ArrayList<>();
    public boolean inCombat;
    MSTimer timer = new MSTimer();

    public Disabler() {
        super(Categories.Misc, "disabler", "bypass some anti cheat");
        addCheck(grimChecks, new BadPacketRotate());
        addCheck(grimChecks, new BadPacketSlot());
        addCheck(grimChecks, new BadPacketSprint());
        addCheck(grimChecks, new BadPacketMessage());
        addCheck(grimChecks, new BadPacketSneak());
    }

    public static void addCheck(List<Check> list, Check check) {
        list.add(check);
    }

    @Override
    public void onActivate() {
        for (Check check : grimChecks) {
            check.onInit();
        }
        timer.reset();
        super.onActivate();
    }

    @EventHandler
    public void onAttack(AttackEntityEvent event) {
        inCombat = true;
    }

    @EventHandler
    public void onPacket(PacketEvent.Send event) {
        for (Check check : grimChecks) {
            check.onPacketSend(event.packet, mode.get(), grimBadPacket.get(), grimCombat.get(), grimMovement.get());
            if (check.canceled) {
                event.cancel();
                check.canceled = false;
            }
        }
        if (event.packet instanceof PlayerMoveC2SPacket packet) {
            switch (mode.get()) {
                case GrimAC -> {
                    if (inCombat) {
                        if (grimCombat.get()) {
                            ((FPlayerMoveC2SPacket) packet).setPitch(mc.player.getPitch());
                            ((FPlayerMoveC2SPacket) packet).setYaw(mc.player.getYaw());
                            inCombat = false;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPacket(PacketEvent.Receive event) {
        for (Check check : grimChecks) {
            check.onPacketRec(event.packet, mode.get(), grimBadPacket.get(), grimCombat.get(), grimMovement.get());
            if (check.cancelRecEd) {
                event.cancel();
                check.cancelRecEd = false;
            }
        }
    }

    public enum Mode {
        GrimAC
    }
}
