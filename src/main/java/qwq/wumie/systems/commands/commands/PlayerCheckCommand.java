package qwq.wumie.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import qwq.wumie.systems.modules.misc.PlayerCheck;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class PlayerCheckCommand extends Command {
    public PlayerCheckCommand() {
        super("playercheck", "check players", "playercheck");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        PlayerCheck base = Modules.get().get(PlayerCheck.class);

        builder.then(literal("players").executes(context -> {
            if (checked(base)) {
                StringBuilder message =new StringBuilder();
                message.append("Players");
                message.append("(").append(base.getPlayers().size()).append(")").append(": ");
                for (String s : base.getPlayers()) {
                    message.append(s+" ,");
                }
                info(message.toString());
            } else {
                base.check();
            }

            return SINGLE_SUCCESS;
        }));

        builder.then(literal("invisible").executes(context -> {
            if (checked(base)) {
                StringBuilder message =new StringBuilder();
                message.append("InvisiblePlayers");
                message.append("(").append(base.getInvisiblePlayers().size()).append(")").append(": ");
                for (String s : base.getInvisiblePlayers()) {
                    message.append(s+" ,");
                }
                info(message.toString());
            } else {
                base.check();
            }

            return SINGLE_SUCCESS;
        }));
    }

    private boolean checked(PlayerCheck base) {
        if (base.isActive() && base.getCheckTimes() != 0) {
            return true;
        }
        return false;
    }
}
