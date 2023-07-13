/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.modules.misc;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.gui.utils.StarscriptTextBoxRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.starscript.Script;
import meteordevelopment.starscript.compiler.Compiler;
import meteordevelopment.starscript.compiler.Parser;
import meteordevelopment.starscript.utils.StarscriptError;

import java.util.List;

public class NewChatBot extends Module {
    public NewChatBot() {
        super(Categories.Misc, "new-chat-bot", "description");
    }

    private Bot defaultBot = new Bot(new Message(null,null),null,this);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode").defaultValue(Mode.Annoy).build()
    );

    private final Setting<List<String>> annoyPlayer = sgGeneral.add(new StringListSetting.Builder()
        .name("annoy-player")
        .description("annoy target players.")
        .visible(() -> mode.get().equals(Mode.Annoy))
        .defaultValue(List.of("MuXi"))
        .build()
    );

    private final Setting<Boolean> txtFilters = sgGeneral.add(new BoolSetting.Builder()
            .name("txt-filters").defaultValue(true).visible(() -> mode.get().equals(Mode.Annoy)).build()
    );

    private final Setting<FilterMode> filterMode = sgGeneral.add(new EnumSetting.Builder<FilterMode>()
            .name("filter-mode").visible(txtFilters::get).defaultValue(FilterMode.Cancel).build()
    );

    private final Setting<List<String>> filters = sgGeneral.add(new StringListSetting.Builder()
            .name("filters").defaultValue(List.of("sb","§")).visible(() -> mode.get().equals(Mode.Annoy)).build()
    );

    private final Setting<Boolean> annoyPrefix = sgGeneral.add(new BoolSetting.Builder()
        .name("annoy-prefix").defaultValue(true).visible(() -> mode.get().equals(Mode.Annoy)).build()
    );

    private final Setting<String> annoyPrefixText = sgGeneral.add(new StringSetting.Builder()
        .name("annoy-prefix-text").defaultValue("!player!:").visible(() -> mode.get().equals(Mode.Annoy) && annoyPrefix.get()).renderer(StarscriptTextBoxRenderer.class).build()
    );

    private final Setting<Boolean> annoySuffix = sgGeneral.add(new BoolSetting.Builder()
        .name("annoy-suffix").defaultValue(true).visible(() -> mode.get().equals(Mode.Annoy)).build()
    );

    private final Setting<String> annoySuffixText = sgGeneral.add(new StringSetting.Builder()
        .name("annoy-suffix-text").defaultValue(" -{time}").visible(() -> mode.get().equals(Mode.Annoy) && annoySuffix.get()).renderer(StarscriptTextBoxRenderer.class).build()
    );

    private final Setting<List<String>> acceptPlayers = sgGeneral.add(new StringListSetting.Builder()
        .name("accept-players")
        .description("accept.")
        .visible(() -> mode.get().equals(Mode.Bot))
        .defaultValue(List.of("ImWuMie","MuXi"))
        .build()
    );

    private final Setting<String> prefix = sgGeneral.add(new StringSetting.Builder()
        .name("prefix").defaultValue("-").build()
    );

    private final Setting<String> splitChar = sgGeneral.add(new StringSetting.Builder()
        .name("split-char").defaultValue(":").build()
    );

    private final Setting<Boolean> hasEmptyChar = sgGeneral.add(new BoolSetting.Builder()
        .name("has-empty-char").defaultValue(true).build()
    );

    private static Script compile(String script) {
        if (script == null) return null;
        Parser.Result result = Parser.parse(script);
        if (result.hasErrors()) {
            MeteorStarscript.printChatError(result.errors.get(0));
            return null;
        }
        return Compiler.compile(result);
    }

    @EventHandler
    public void onMessage(ReceiveMessageEvent e) {
        String message = e.getMessage().getString();
        if (splitChar.get().isEmpty()) return;
        if (!message.contains(splitChar.get())) return;
        String sender = message.split(splitChar.get())[0];
        String receive = hasEmptyChar.get() ? message.replace(sender+splitChar.get()+" ","") : message.replace(sender+splitChar.get(),"");
        switch (mode.get()) {
            case Annoy -> {
                if (isAnnoyPlayer(sender)) {
                    String text = receive;
                    if (txtFilters.get() && IgnoreMessage(text)) {
                        switch (filterMode.get()) {
                            case Cancel -> {
                                return;
                            }
                            case Replace -> {
                                text = toMessage(text);
                            }
                        }
                    }

                    if (annoyPrefix.get()) {
                        String prefix = annoyPrefixText.get();
                        while (prefix.contains("!player!")) {
                          prefix = prefix.replace("!player!",getSender(sender));
                        }
                        Script script = compile(prefix);
                        if (script == null) info("An error occurred");
                        try {
                            prefix = MeteorStarscript.ss.run(script).toString();
                        } catch (StarscriptError xe) {
                            MeteorStarscript.printChatError(xe);
                            info("An error occurred");
                        }
                        text = prefix + text;
                    }

                    if (annoySuffix.get()) {
                        String suffix = annoySuffixText.get();
                        while (suffix.contains("!player!")) {
                            suffix = suffix.replace("!player!",getSender(sender));
                        }
                        Script script = compile(suffix);
                        if (script == null) info("An error occurred");
                        try {
                            suffix = MeteorStarscript.ss.run(script).toString();
                        } catch (StarscriptError xe) {
                            MeteorStarscript.printChatError(xe);
                            info("An error occurred");
                        }
                        text = text+suffix;
                    }
                    sendChatMessage(text);
                }
            }
            case Bot -> {
                if (isAcceptPlayer(sender)) {
                    if (receive.startsWith(prefix.get())) {
                            Message text = new Message(receive,prefix.get());
                            defaultBot.setMessage(text);
                            defaultBot.setSender(getSender(sender));
                            defaultBot.run();
                        }
                    }
                }
            }
        }

    private boolean isAcceptPlayer(String playerName) {
        for (String s : acceptPlayers.get()) {
            if (playerName.contains(s)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnnoyPlayer(String playerName) {
        for (String s : annoyPlayer.get()) {
            if (playerName.contains(s)) {
                return true;
            }
        }
        return false;
    }

    private String getSender(String playerName) {
        for (String s : annoyPlayer.get()) {
            if (playerName.contains(s)) {
                return s;
            }
        }
        return "";
    }

    private boolean isCommand(String[] args, String enter) {
        String[] en = enter.split(" ");
        return args[0].equals(en[0]);
    }

    public enum Mode {
        Annoy,
        Bot
    }

    public enum FilterMode {
        Replace,
        Cancel
    }

    public boolean IgnoreMessage(String message) {
        List<String> filters = this.filters.get();
        for (String filter : filters) {
            if (message.contains(filter)) {
                return true;
            }
        }
        return false;
    }

    public String toMessage(String message) {
        String out = message;
        List<String> filters = this.filters.get();
        for (String filter : filters) {
            if (filter.isEmpty()) continue;
            while (out.contains(filter)) {
                char[] size = filter.toCharArray();
                StringBuilder builder = new StringBuilder();
                for (char c : size) {
                    builder.append("*");
                }
                out = out.replace(filter,builder.toString());
            }
        }
        return out;
    }

    static class Bot {
        private Message message;
        private String sender;
        private final NewChatBot base;

        public Bot(Message message,String sender,NewChatBot base) {
            this.message = message;
            this.sender = sender;
            this.base = base;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public void run() {
            if (message != null && !message.getMessage().isEmpty() && sender != null && !sender.isEmpty()) {
                String text = message.getMessage();

                // Tpa Command only in Server
                if (text.startsWith(message.getPrefix() + "tpa")) {
                    boolean error = false;
                    String errorMessage = "error";
                    String player = text.substring((message.getPrefix() + "tpa ").length());
                    if (player.equals(" ")) {
                        errorMessage = "[error] Player name cannot be empty.";
                        error = true;
                    }
                    if (player.isEmpty()) {
                        errorMessage = "[error] Player name cannot be empty.";
                        error = true;
                    }

                    if (error) {
                        base.sendChatMessage(errorMessage);
                    } else {
                        base.sendChatMessage("/tpa "+player);
                    }
                }

                if (text.startsWith(message.getPrefix() + "say")) {
                    String txt = text.substring((message.getPrefix() + "say ").length());
                    base.sendChatMessage(txt);
                }

                if (text.startsWith(message.getPrefix() + "goto ")) {
                    String coordString = text.substring((message.getPrefix() + "goto ").length());

                    boolean error = false;
                    String errorMessage = "error";
                    if (coordString.equals(" ")) {
                        errorMessage = "[error] Coord cannot be empty.";
                        error = true;
                    }
                    if (coordString.isEmpty()) {
                        errorMessage = "[error] Coord cannot be empty.";
                        error = true;
                    }

                    if (error) {
                        base.sendChatMessage(errorMessage);
                    } else {
                        String[] coords = coordString.split(" ");
                        double x = Double.parseDouble(coords[0]), y = Double.parseDouble(coords[1]), z = Double.parseDouble(coords[2]);
                        final IBaritone b = BaritoneAPI.getProvider().getPrimaryBaritone();
                        b.getCommandManager().execute("goto "+x+" "+y+" "+z);
                    }
                }
            }
        }
    }

    static class Message {
        private String message;
        private String prefix;

        public Message(String message, String prefix) {
            this.message = message;
            this.prefix = prefix;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getMessage() {
            return message;
        }

        public String getPrefix() {
            return prefix;
        }
    }
}
