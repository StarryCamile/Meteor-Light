package qwq.wumie.systems.modules.inject.combat;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.utils.entity.Target;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.entity.Entity;
import qwq.wumie.systems.modules.inject.ModuleInject;

public class KAInject extends ModuleInject<KillAura> {
    private final SettingGroup sgRender = createGroup("Render");
    private final SettingGroup sgRotation = createGroup("Rotation");

    // RENDER
    private final Setting<Boolean> esp = sgRender.add(new BoolSetting.Builder()
            .name("ESP")
            .defaultValue(false)
            .build()
    );

    private final Setting<ESPMode> espmode = sgRender.add(new EnumSetting.Builder<ESPMode>()
            .name("esp-mode")
            .defaultValue(ESPMode.Box)
            .visible(esp::get)
            .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The color of the sides of the blocks being rendered.")
            .defaultValue(new SettingColor(204, 0, 0, 10))
            .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The color of the lines of the blocks being rendered.")
            .defaultValue(new SettingColor(204, 0, 0, 255))
            .build()
    );

    private final Setting<Double> aimSpeed = sgRotation.add(new DoubleSetting.Builder()
            .name("aim-speed")
            .defaultValue(5.0)
            .min(0)
            .visible(() -> module.rotation.get().equals(KillAura.RotationMode.Aim))
            .build()
    );

    private final Setting<RotationTarget> rotationT = sgRotation.add(new EnumSetting.Builder<RotationTarget>()
            .name("rotation-target")
            .defaultValue(RotationTarget.Body)
            .visible(() -> module. rotation.get().equals(KillAura.RotationMode.Aim))
            .build()
    );

    private float lastRotatePitch, lastRotateYaw, rotatePitch, rotateYaw;

    public enum RotationTarget {
        Body,
        Head,
        Feet
    }

    public enum ESPMode {
        Box,
        Smooth,
        FakeSigma,
        Test
    }


    private void rotate(Entity target, Runnable callback) {
        Rotations.rotate(Rotations.getYaw(target), Rotations.getPitch(target, Target.Body), callback);
    }

    public boolean onAttack(Entity target) {

        return false;
    }

    private float tickDelta;
    private Entity dirTarget;

    public static float smoothTrans(double current, double last,float td){
        return (float) (current * td + (last * (1.0f - td)));
    }

    private double randomOffset() {
        return Math.random() * 4 - 2;
    }

    public boolean onRender(Render3DEvent e) {
        this.tickDelta = e.tickDelta;
        if (esp.get()) {
            if (espmode.get().equals(ESPMode.Box)) {
                for (int i = 0; i < (module.targets.size() > module.maxTargets.get() ? module.maxTargets.get() : this.module.targets.size()); ++i) {
                    e.renderer.box(module.targets.get(i).getBoundingBox(), sideColor.get(), lineColor.get(), shapeMode.get(), 0);
                }
            }
            if (espmode.get().equals(ESPMode.FakeSigma)) {
                for (int ii = 0; ii < (this.module.targets.size() > module.maxTargets.get() ? module.maxTargets.get() : this.module.targets.size()); ++ii) {
                    e.rpl.drawFakeSigma(e.matrices, module.targets.get(ii), sideColor.get(), lineColor.get(), shapeMode.get(), 0);
                }
            }
            if (espmode.get().equals(ESPMode.Smooth)) {
                for (int ii = 0; ii < (this.module.targets.size() > module.maxTargets.get() ? module.maxTargets.get() : this.module.targets.size()); ++ii) {
                    e.rpl.up2Dbox(module.targets.get(ii).getBoundingBox(), sideColor.get(), lineColor.get(), shapeMode.get(), (module.targets.get(ii).getBoundingBox().maxY - module.targets.get(ii).getBoundingBox().minY) / 2, 0);
                }
            }
            if (espmode.get().equals(ESPMode.Test)) {
                for (int ii = 0; ii < (this.module.targets.size() > module.maxTargets.get() ? module.maxTargets.get() : this.module.targets.size()); ++ii) {
                    e.rpl.Circle(module.targets.get(ii), lineColor.get(), shapeMode.get());
                }
            }
        }
        return false;
    }

    public boolean onTick() {
        lastRotatePitch = rotatePitch;
        lastRotateYaw = rotateYaw;
        return false;
    }

    public boolean onPostTick() {

        return false;
    }

    public boolean onPostATick() {


        return false;
    }

    public boolean onRotate(Entity target) {
        dirTarget = module.getDirTarget();
        if (module.rotation.get() == KillAura.RotationMode.Smooth) {
            rotateYaw = (float) Rotations.getYaw(dirTarget);
            rotatePitch = (float) Rotations.getPitch(dirTarget);
            Rotations.rotate(smoothTrans(lastRotateYaw, rotateYaw,tickDelta),smoothTrans(rotatePitch,lastRotatePitch,tickDelta));
        }
        if (tickDelta != 0.0) {
            if (module.rotation.get() == KillAura.RotationMode.Aim)
                Rotations.rotation(dirTarget, tickDelta, aimSpeed.get(), rotationT.get());
        }
        return false;
    }

    public KAInject() {
        super(KillAura.class);
    }
}
