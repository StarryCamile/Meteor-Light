package qwq.wumie.systems.handle;

import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import qwq.wumie.systems.commands.commands.BaritoneCommand;
import qwq.wumie.systems.commands.commands.GhostCommand;
import qwq.wumie.systems.commands.commands.ItemCommand;
import qwq.wumie.systems.commands.commands.*;
import qwq.wumie.systems.commands.commands.Check.rip;

public class CommandHandler {
    public static void initCommand() {
        add(new SetVelocityCommand());
        add(new PayloadCommand());
        add(new ServerLocateCommand());
        add(new GhostCommand());
        add(new PacketCommand());
        add(new InfTpCommand());
        add(new CenterCommand());
        add(new PlayerCheckCommand());
        add(new TeleportCommand());
        add(new SeedCommand());
        add(new MusicCommand());
        add(new ItemCommand());
        add(new BaritoneCommand());
        add(new NotificationCommand());
        add(new CenterCommand());
        add(new TppCommand());
        add(new TerrainExport());
        add(new SetBlockCommand());
        add(new rip());
    }

    private static void add(Command c) {
        Commands.add(c);
    }
}
