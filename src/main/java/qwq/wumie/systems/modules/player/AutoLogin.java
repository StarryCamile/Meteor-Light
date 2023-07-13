/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package qwq.wumie.systems.modules.player;

import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class AutoLogin extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> register = sgGeneral.add(new BoolSetting.Builder()
        .name("AutoRegister")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> join = sgGeneral.add(new BoolSetting.Builder()
        .name("onJoin")
        .description("Auto")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> login = sgGeneral.add(new BoolSetting.Builder()
        .name("Login")
        .description("AutoLogin")
        .defaultValue(true)
        .build()
    );

    private final Setting<String> password = sgGeneral.add(new StringSetting.Builder()
        .name("Password")
        .defaultValue("")
        .build()
    );

    private final Setting<String> loginmessage = sgGeneral.add(new StringSetting.Builder()
        .name("LoginMessage")
        .defaultValue("/login")
        .build()
    );

    private final Setting<String> regmessage = sgGeneral.add(new StringSetting.Builder()
        .name("RegisterMessage")
        .defaultValue("/register")
        .build()
    );

    private final Setting<Integer> tryNumber = sgGeneral.add(new IntSetting.Builder()
        .name("number-of-attempts")
        .min(1)
        .defaultValue(1)
        .build()
    );

    int flag = 0;
    int flag1 = 0;

    @EventHandler
    public void onReceiveMessage(ReceiveMessageEvent event) {
        String message = event.getMessage().toString();
            if (message.contains(regmessage.get())) {
                if (register.get()) {
                    if (flag <= tryNumber.get()) {
                        flag+=1;
                        sendChatMessage("/register " + password.get() + " " + password.get());
                    }
                }
            }

        if (message.contains(loginmessage.get())) {
            if (login.get()) {
                if (flag1 <= tryNumber.get()) {
                    flag1 += 1;
                    sendChatMessage("/login " + password.get());
                }
            }
        }
    }

    @EventHandler
    private void onLeft(GameLeftEvent e) {
        flag1 = 0;
        flag =0;
    }

    @Override
    public void onDeactivate() {
        flag = 0;
        flag1 = 0;
        super.onDeactivate();
    }

    @EventHandler
    private void onJoin(GameJoinedEvent e) {
        if (join.get()) {
            if (login.get()) {
               sendChatMessage("/login " + password.get());
            }
            if (register.get()) {
              sendChatMessage("/register " + password.get() + " " + password.get());
            }
        }
    }

    public AutoLogin() {
        super(Categories.Player,"AutoLogin","");
    }
}
