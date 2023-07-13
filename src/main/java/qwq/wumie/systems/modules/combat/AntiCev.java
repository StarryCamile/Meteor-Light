package qwq.wumie.systems.modules.combat;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.math.BlockPos;

public class AntiCev extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Automatically rotates you towards the blocks.")
        .defaultValue(true)
        .build()
    );

    private boolean ceved = false;

    public AntiCev() {
        super(Categories.Combat, "anti-cev", "Protects you from various cev breaker.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        BlockPos top = mc.player.getBlockPos().up(2);
        if (mc.world.getBlockState(top).getBlock() == Blocks.OBSIDIAN) {
            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof EndCrystalEntity crystal && crystal.getBlockPos().equals(top.up())) {
                    mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(crystal, mc.player.isSneaking()));
                    ceved = true;
                } else if (ceved) {
                    BlockUtils.place(top.up(), InvUtils.findInHotbar(Items.OBSIDIAN), rotate.get(), 50, true);
                    ceved = false;
                }
            }
        }
    }
}
