package qwq.wumie.systems.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import qwq.wumie.systems.notification.NotificationManager;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class NotificationCommand extends Command {
    public NotificationCommand() {
        super("notification", "devTest");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("title", StringArgumentType.string()).then(argument("text",StringArgumentType.string()).executes(context -> {
            NotificationManager notificationManager = NotificationManager.INSTANCE;
            if  (notificationManager != null && mc.world != null) {
                notificationManager.info(StringArgumentType.getString(context,"title"),StringArgumentType.getString(context,"text"));
            }
            return SINGLE_SUCCESS;
        })));
    }
}
