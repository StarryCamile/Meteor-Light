package qwq.wumie.systems.modules.misc;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.CrystalAura;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class FuckedDetector extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Lines)
            .build()
    );

    private final Setting<SettingColor> sideColor = sgGeneral.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The side color.")
            .defaultValue(new SettingColor(255, 255, 255, 75))
            .build()
    );

    private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The line color.")
            .defaultValue(new SettingColor(255, 255, 255, 255))
            .build()
    );

    private final Setting<Double> damageThreshold = sgGeneral.add(new DoubleSetting.Builder()
            .name("damage-threshold")
            .description("The threshold for CA damage before FuckedDetector begins rendering.")
            .defaultValue(6.0)
            .min(0)
            .sliderMax(40)
            .build()
    );

    public FuckedDetector() {
        super(Categories.Misc, "fucked-detector", "Checks if the CA target is not burrowed, and isn't surrounded.");
    }

    private boolean isTargetFucked = false;
    private PlayerEntity target = null;

    @Override
    public void onActivate() {
        isTargetFucked = false;
        target = null;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        CrystalAura caura = Modules.get().get(CrystalAura.class);
        if(caura.isActive()) {
            target = caura.bestTarget;
            if(target != null) {
                isTargetFucked = !isSurrounded(target) && !isBurrowed(target) && caura.bestTargetDamage >= damageThreshold.get();
            }
        }
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if(isTargetFucked) {
            BlockPos tbp = target.getBlockPos();
            event.renderer.box(tbp, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        }
    }

    private boolean isSurrounded(LivingEntity target){
        assert mc.world != null;
        return !mc.world.getBlockState(target.getBlockPos().add(1, 0, 0)).isAir()
                && !mc.world.getBlockState(target.getBlockPos().add(-1, 0, 0)).isAir()
                && !mc.world.getBlockState(target.getBlockPos().add(0, 0, 1)).isAir() &&
                !mc.world.getBlockState(target.getBlockPos().add(0, 0, -1)).isAir();
    }

    private boolean isBurrowed(LivingEntity target) {
        assert mc.world != null;
        return !mc.world.getBlockState(target.getBlockPos()).isAir();
    }
}
