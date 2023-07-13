/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.mixin;

import meteordevelopment.meteorclient.gui.GuiThemes;
import qwq.wumie.gui.screens.FakeFlux;
import qwq.wumie.gui.screens.NewJelloScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    public TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "tick",at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (GuiThemes.get() !=  null) {
            client.setScreen(new FakeFlux());
        }
    }

    @Inject(method = "init",at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        if (GuiThemes.get() !=  null) {
            client.setScreen(new FakeFlux());
        }
       /* this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, this.height / 4 + 48 + 72 + 12+55, 98, 20, Text.literal("TitleScreen1"), (button) -> {
            this.client.setScreen(new JelloScreen());
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, this.height / 4 + 48 + 72 + 12+55+55, 98, 20, Text.literal("TitleScreen2"), (button) -> {
            this.client.setScreen(new MainScreen());
        }));*/
    }
/*
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawStringWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V", ordinal = 0))
    private void onRenderIdkDude(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (Utils.firstTimeTitleScreen) {
            Utils.firstTimeTitleScreen = false;
            MeteorClient.LOG.info("Checking latest version of Meteor Client || Skip");
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (Config.get().titleScreenCredits.get()) TitleScreenCredits.render(matrices);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> info) {
        if (Config.get().titleScreenCredits.get() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (TitleScreenCredits.onClicked(mouseX, mouseY)) info.setReturnValue(true);
        }
    }*/
}
