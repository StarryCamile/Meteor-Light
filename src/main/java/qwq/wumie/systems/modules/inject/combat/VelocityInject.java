package qwq.wumie.systems.modules.inject.combat;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.movement.Velocity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import qwq.wumie.systems.modules.inject.ModuleInject;

public class VelocityInject extends ModuleInject<Velocity> {
    private final Setting<Mode> mode = getDefaultGroup().add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("The mode for antiknockbacking")
            .defaultValue(Mode.Vanilla)
            .build()
    );

    public enum Mode {
        Vanilla,
        Cancel
    }

    public boolean onPacket(PacketEvent.Receive e,Packet<?> packet) {
        if (packet instanceof EntityVelocityUpdateS2CPacket p) {
            if (mode.get().equals(Mode.Cancel)) {
                e.cancel();
                return false;
            }
        }

        return true;
    }

    public VelocityInject() {
        super(Velocity.class);

        module.knockback.visible = () -> mode.get().equals(Mode.Vanilla);
        module.knockbackHorizontal.visible = () -> mode.get().equals(Mode.Vanilla) && module.knockback.get();
        module.knockbackVertical.visible = () -> mode.get().equals(Mode.Vanilla) && module.knockback.get();
        module.explosions.visible = () -> mode.get().equals(Mode.Vanilla);
        module.explosionsHorizontal.visible = () -> mode.get().equals(Mode.Vanilla) && module.explosions.get();
        module.explosionsVertical.visible = () -> mode.get().equals(Mode.Vanilla) && module.explosions.get();
        module.liquids.visible = () -> mode.get().equals(Mode.Vanilla);
        module.liquidsHorizontal.visible = () -> mode.get().equals(Mode.Vanilla) && module.liquids.get();
        module.liquidsVertical.visible = () -> mode.get().equals(Mode.Vanilla) && module.liquids.get();
        module.entityPush.visible = () -> mode.get().equals(Mode.Vanilla);
        module.entityPushAmount.visible = () -> mode.get().equals(Mode.Vanilla) && module.entityPush.get();
        module.blocks.visible = () -> mode.get().equals(Mode.Vanilla);
        module.sinking.visible = () -> mode.get().equals(Mode.Vanilla);
    }
}
