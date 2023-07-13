package qwq.wumie.systems.modules.render;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class MotionBlur extends Module {
    private final SettingGroup sgVisual = settings.createGroup("Visual");

    public final Setting<Integer> blurAmount = sgVisual.add(new IntSetting.Builder()
            .name("blur-amount")
            .description("blur amount.")
            .range(0,100)
            .sliderRange(0,100)
            .defaultValue(50)
            .build()
    );

    public MotionBlur() {
        super(Categories.Render, "motion-blur", "blur~~");
    }
}
