/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.mixin;

import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qwq.wumie.mixininterface.IScreen;
import qwq.wumie.systems.handle.Config;
import qwq.wumie.systems.handle.MainHandler;
import qwq.wumie.systems.viaversion.util.ProtocolUtils;

import java.util.concurrent.CompletableFuture;

@Mixin(value = MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    private static CompletableFuture<Void> latestProtocolSave;
    private TextFieldWidget protocolVersion;

    public MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        ButtonWidget accountsButton = ((IScreen) (MultiplayerScreen) (Object) this).getButtonWidget(Text.literal("Accounts"));
        ButtonWidget proxyButton = ((IScreen) (MultiplayerScreen) (Object) this).getButtonWidget(Text.literal("Proxies"));

        accountsButton.setX(this.width / 2 + 4 + 76 + 5 + 75);
        accountsButton.setY(this.height - 28);
        proxyButton.setX(this.width / 2 + 4 + 76 + 5 + 75);
        proxyButton.setY(this.height - 52);

        if (Config.enableViaVersion) {
            protocolVersion = new TextFieldWidget(this.textRenderer, this.width - 75 - 3, 3, 75, 20, Text.translatable("gui.protocol_version_field.name"));
            protocolVersion.setTextPredicate(ProtocolUtils::isStartOfProtocolText);
            protocolVersion.setChangedListener(this::onChangeVersionField);
            int clientSideVersion = MainHandler.config.getClientSideVersion();
            protocolVersion.setText(ProtocolUtils.getProtocolName(clientSideVersion));
            this.addDrawableChild(protocolVersion);
        }
    }


    private void onChangeVersionField(String text) {
        if (Config.enableViaVersion) {
            protocolVersion.setSuggestion(null);
            int newVersion = MainHandler.config.getClientSideVersion();

            Integer parsed = ProtocolUtils.parseProtocolId(text);
            boolean validProtocol;

            if (parsed != null) {
                newVersion = parsed;
                validProtocol = true;
            } else {
                validProtocol = false;
                String[] suggestions = ProtocolUtils.getProtocolSuggestions(text);
                if (suggestions.length == 1) {
                    protocolVersion.setSuggestion(suggestions[0].substring(text.length()));
                }
            }

            protocolVersion.setEditableColor(getProtocolTextColor(newVersion, validProtocol));

            int finalNewVersion = newVersion;
            if (latestProtocolSave == null) latestProtocolSave = CompletableFuture.completedFuture(null);
            MainHandler.config.setClientSideVersion(finalNewVersion);
            latestProtocolSave = latestProtocolSave.thenRunAsync(MainHandler.config::saveConfig, MainHandler.ASYNC_EXECUTOR);
        }
    }

    private int getProtocolTextColor(int version, boolean parsedValid) {
        if (!parsedValid) return 0xff0000; // Red
        if (version == -1 || version == -2) return 0x5555FF; // Blue
        if (!ProtocolUtils.isSupportedClientSide(version)) return 0xFFA500; // Orange
        return 0xE0E0E0; // Default
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (Config.enableViaVersion && protocolVersion != null) {
            protocolVersion.tick();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        float x = 3;
        float y = 3;

        // Meteor Portal:
        if (Config.enableViaVersion) {
            float yeee;
            float awa;

            String textmeteor = "Meteor Portal:";

            awa = textRenderer.fontHeight;
            yeee = textRenderer.getWidth(textmeteor);


            context.drawTextWithShadow(textRenderer, "Meteor Portal: ", (int) (this.width - 75 - 5 - yeee), (int) awa, Color.fromRGBA(175, 175, 175, 255));
            y += textRenderer.fontHeight + 2;
        }
    }
}
