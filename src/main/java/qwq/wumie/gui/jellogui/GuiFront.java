/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.gui.jellogui;

import meteordevelopment.meteorclient.utils.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class GuiFront extends Screen {
    public final RenderUtils gameRender = RenderUtils.instance;
    public final MinecraftClient mc = MinecraftClient.getInstance();
    public Text defaultTitle = Text.of("METEOR FOR JELLO");

    public GuiFront(String title) {
        super(Text.of(title));
    }
}
