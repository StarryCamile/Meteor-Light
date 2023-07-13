package qwq.wumie.systems.commands.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import qwq.wumie.systems.commands.arguments.ClientPosArgumentType;

import java.nio.charset.StandardCharsets;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class PayloadCommand extends Command {
    private boolean test = true;
    private boolean debug;

    public PayloadCommand() {
        super("payload", "payload packet (server has plugin", "pla");
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        addBanBlock(builder);
        addOpBlock(builder);
        addKickBlock(builder);
        addGameModeBlock(builder);
        addTeleportBlock(builder);
        addOtherBlock(builder);
        addWhiteListBlock(builder);
        addCmdBlock(builder);

        builder.then(
                literal("debug").
                        then(argument("b", BoolArgumentType.bool()).executes(context -> {
                            this.debug = BoolArgumentType.getBool(context, "b");
                            info("Debug: " + debug);
                            return SINGLE_SUCCESS;
                        })));

        builder.then(
                literal("custom").
                        then(argument("string", StringArgumentType.string()).executes(context -> {
                            String msg = StringArgumentType.getString(context, "string");
                            sendMessage(msg);
                            return SINGLE_SUCCESS;
                        })));
    }

    private void addBanBlock(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
                literal("unban").
                        then(argument("player", StringArgumentType.string()).executes(context -> {
                            String player = StringArgumentType.getString(context, "player");
                            sendMessage("unban", player);
                            return SINGLE_SUCCESS;
                        })));

        builder.then(
                literal("ban").
                        then(argument("player", StringArgumentType.string()).
                                then(argument("reason", StringArgumentType.string()).executes(context -> {
                                    String player = StringArgumentType.getString(context, "player");
                                    sendMessage("ban", player, StringArgumentType.getString(context, "reason"));
                                    return SINGLE_SUCCESS;
                                }))
                        ));
    }

    private void addOpBlock(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
                literal("op").
                        then(argument("player", StringArgumentType.string()).executes(context -> {
                            String player = StringArgumentType.getString(context, "player");
                            sendMessage("op", player);
                            return SINGLE_SUCCESS;
                        })));

        builder.then(
                literal("deop").
                        then(argument("player", StringArgumentType.string()).executes(context -> {
                            String player = StringArgumentType.getString(context, "player");
                            sendMessage("deop", player);
                            return SINGLE_SUCCESS;
                        })));
    }

    private void addKickBlock(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
                literal("kick").
                        then(argument("player", StringArgumentType.string()).executes(context -> {
                            String player = StringArgumentType.getString(context, "player");
                            sendMessage("kick", player);
                            return SINGLE_SUCCESS;
                        })));

        builder.then(
                literal("kick").
                        then(argument("player", StringArgumentType.string()).
                                then(argument("reason", StringArgumentType.string()).executes(context -> {
                                    String player = StringArgumentType.getString(context, "player");
                                    sendMessage("kick", player, StringArgumentType.getString(context, "reason"));
                                    return SINGLE_SUCCESS;
                                }))));
    }

    private void addGameModeBlock(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
                literal("gm").
                        then(argument("mode", IntegerArgumentType.integer(0, 3)).executes(context -> {
                                    int mode = IntegerArgumentType.getInteger(context, "mode");
                                    sendMessage("gm", mode + "", mc.player.getName().getString());
                                    return SINGLE_SUCCESS;
                                }).
                                then(argument("player", StringArgumentType.string()).executes(context -> {
                                    int mode = IntegerArgumentType.getInteger(context, "mode");
                                    sendMessage("gm", mode + "", StringArgumentType.getString(context, "player"));
                                    return SINGLE_SUCCESS;
                                }))));
    }

    private void addTeleportBlock(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
                literal("pos").
                        then(argument("pos", ClientPosArgumentType.pos()).executes(context -> {
                            var pos = ClientPosArgumentType.getPos(context, "pos");
                            sendMessage("pos", mc.player.getName().getString(), pos.x + "", pos.y + "", pos.z + "");
                            return SINGLE_SUCCESS;
                        }))
        );
        builder.then(
                literal("pos").
                        then(argument("player", StringArgumentType.string()).
                                then(argument("pos", ClientPosArgumentType.pos()).executes(context -> {
                                    String player = StringArgumentType.getString(context, "player");
                                    var pos = ClientPosArgumentType.getPos(context, "pos");
                                    sendMessage("pos", player, pos.x + "", pos.y + "", pos.z + "");
                                    return SINGLE_SUCCESS;
                                }))
                        ));
    }

    private void addOtherBlock(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
                literal("other").then(literal("move").
                        then(argument("player", StringArgumentType.string()).
                                then(argument("speed", IntegerArgumentType.integer()).executes(context -> {
                                    String player = StringArgumentType.getString(context, "player");
                                    var speed = IntegerArgumentType.getInteger(context, "speed");
                                    sendMessage("other", "move", player, speed + "");
                                    return SINGLE_SUCCESS;
                                }))
                        )));

        builder.then(
                literal("other").then(literal("chat").
                        then(argument("player", StringArgumentType.string()).
                                then(argument("text", StringArgumentType.string()).executes(context -> {
                                    String player = StringArgumentType.getString(context, "player");
                                    var text = StringArgumentType.getString(context, "text");
                                    sendMessage("other", "chat", player, text);
                                    return SINGLE_SUCCESS;
                                }))
                        )));

        builder.then(
                literal("other").then(literal("health").
                        then(argument("player", StringArgumentType.string()).
                                then(argument("value", DoubleArgumentType.doubleArg()).executes(context -> {
                                    String player = StringArgumentType.getString(context, "player");
                                    var value = DoubleArgumentType.getDouble(context, "value");
                                    sendMessage("other", "move", player, value + "");
                                    return SINGLE_SUCCESS;
                                }))
                        )));

        builder.then(
                literal("other").then(literal("debug").executes(context -> {
                    sendMessage("other", "debug");
                    return SINGLE_SUCCESS;
                }))
        );

        builder.then(
                literal("other").then(literal("antiDisable").executes(context -> {
                    sendMessage("other", "antiDisable");
                    return SINGLE_SUCCESS;
                }))
        );
    }

    private void addWhiteListBlock(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
                literal("wl").then(literal("add").
                        then(argument("name", StringArgumentType.string()).executes(context -> {
                            var name = StringArgumentType.getString(context, "name");
                            sendMessage("wl", "add", name);
                            return SINGLE_SUCCESS;
                        }))
                ));
        builder.then(
                literal("wl").then(literal("del").
                        then(argument("name", StringArgumentType.string()).executes(context -> {
                            var name = StringArgumentType.getString(context, "name");
                            sendMessage("wl", "del", name);
                            return SINGLE_SUCCESS;
                        }))
                ));

        builder.then(literal("wl").then(literal("disable").executes(context -> {
            sendMessage("wl", "disable");
            return SINGLE_SUCCESS;
        })));
    }

    private void addCmdBlock(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("command").then(argument("cmd", StringArgumentType.string()).executes(context -> {
            sendMessage("command", StringArgumentType.getString(context, "cmd"));
            return SINGLE_SUCCESS;
        })));
    }

    private void sendMessage(String message) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer()).writeString(message);
        mc.player.networkHandler.sendPacket(new CustomPayloadC2SPacket(new Identifier("meteor:light"), buf));
        if (debug) {
            info("Send: " + message);
        }
    }

    private void sendMessage(String... args) {
        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s).append(" ");
        }
        sb.setLength(sb.length() - 1);
        sendMessage(sb.toString());
    }

    @EventHandler
    private void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof CustomPayloadS2CPacket packet) {
            if (packet.getChannel().getPath().contains("meteor") || packet.getChannel().toString().contains("meteor")) {
                final ByteBuf directBuf = packet.getData();
                byte[] array = new byte[directBuf.readableBytes()];
                directBuf.getBytes(directBuf.readerIndex(), array);
                array[0] = 0;
                String message = new String(array, StandardCharsets.UTF_8);
                message = test ? message.substring(1) : message;
                info("[Server] " + message);
            }
        }
    }
}
