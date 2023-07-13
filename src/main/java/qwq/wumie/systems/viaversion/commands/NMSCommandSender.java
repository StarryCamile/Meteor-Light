package qwq.wumie.systems.viaversion.commands;

import qwq.wumie.systems.viaversion.util.RemappingUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import com.viaversion.viaversion.api.command.ViaCommandSender;

import java.util.UUID;

public class NMSCommandSender implements ViaCommandSender {
    private final CommandSource source;

    public NMSCommandSender(CommandSource source) {
        this.source = source;
    }

    @Override
    public boolean hasPermission(String s) {
        // https://gaming.stackexchange.com/questions/138602/what-does-op-permission-level-do
        return source.hasPermissionLevel(3);
    }

    public static MutableText fromLegacy(String legacy) {
        return Text.Serializer.fromJson(RemappingUtil.legacyToJson(legacy));
    }

    @Override
    public void sendMessage(String s) {
    }

    @Override
    public UUID getUUID() {
            Entity entity = ((ServerCommandSource) source).getEntity();
            if (entity != null) return entity.getUuid();
        return UUID.fromString(getName());
    }

    @Override
    public String getName() {
            return ((ServerCommandSource) source).getName();
    }
}
