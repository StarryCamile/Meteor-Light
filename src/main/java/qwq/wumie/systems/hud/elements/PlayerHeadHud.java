/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.hud.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class PlayerHeadHud  extends HudElement {
    public static final HudElementInfo<PlayerHeadHud> INFO = new HudElementInfo<>(Hud.GROUP, "player-skin-hud", "Displays skin about your targetname.", PlayerHeadHud::new);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> width = sgGeneral.add(new DoubleSetting.Builder()
            .name("width")
            .defaultValue(50)
        .build()
    );
    private final Setting<Double> height = sgGeneral.add(new DoubleSetting.Builder()
        .name("height")
        .defaultValue(50)
        .build()
    );

    private final Setting<String> name = sgGeneral.add(new StringSetting.Builder()
        .name("Name")
        .defaultValue("ImWuMie")
        .build()
    );

    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
        .name("scale")
        .defaultValue(1.0)
        .build()
    );

    public PlayerHeadHud() {
        super(INFO);
    }

    public WWidget getWidget(GuiTheme theme) {
        assert mc.player != null;
        Identifier playerskin = name.get().equalsIgnoreCase(mc.player.getName().toString()) ? RenderUtils.instance.getSkinTexture(mc.player) : RenderUtils.instance.getSkinByName(name.get());
        if (playerskin != null) {
            WTable table = theme.table();
            table.add(theme.label("Skin: "+name.get())).expandCellX().widget();
            WButton stop = table.add(theme.button("Save")).right().widget();
            stop.action = () -> download(playerskin);
            table.row();
            return table;
        }
        return null;
    }

    private void download(Identifier path) {
        try {
           InputStream is  = mc.getResourceManager().getResource(path).get().getInputStream();
            File x = new File(MeteorClient.FOLDER, "skins");
            if (!x.exists()) x.mkdirs();
            OutputStream os = new FileOutputStream(x);
            int index;
            byte[] bytes = new byte[12800];
            while ((index = is.read(bytes)) != -1) {
                os.write(bytes, 0, index);
            }
            os.flush();
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tick(HudRenderer renderer) {
        setSize(width.get(),height.get());
        super.tick(renderer);
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = this.x;
        double y = this.y;
        renderer.post(() -> {
            if (mc.player == null) return;
            Identifier playerHeadId = name.get().equalsIgnoreCase(mc.player.getName().toString()) ? RenderUtils.instance.getSkinTexture(mc.player) : RenderUtils.instance.getSkinByName(name.get());
            if (playerHeadId == null) {
                renderer.text("Unknown player",x,y,Color.WHITE,true,0.9);
            } else {
                drawScaledImage(renderer,playerHeadId, x, y, width.get(), height.get(), scale.get());
            }

        });
        super.render(renderer);
    }
}
