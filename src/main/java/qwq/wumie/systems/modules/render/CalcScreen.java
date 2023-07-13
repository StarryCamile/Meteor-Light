/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.modules.render;

import qwq.wumie.gui.jellogui.screens.CalcGui;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class CalcScreen extends Module {
    public CalcScreen() {
        super(Categories.Render, "Calc", "");
    }

    @Override
    public void onActivate() {
        mc.setScreen(new CalcGui());
        toggle();
        super.onActivate();
    }
}
