package qwq.wumie.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.UpdateEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.commands.Command;
import qwq.wumie.systems.commands.arguments.ClientPosArgumentType;
import meteordevelopment.meteorclient.commands.arguments.PlayerArgumentType;
import qwq.wumie.systems.modules.combat.InfiniteAura;
import qwq.wumie.utils.pathfinding.pathfinding.CustomPathFinder;
import qwq.wumie.utils.time.TickTimer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import qwq.wumie.utils.pathfinding.player.PathFind;

import java.util.ArrayList;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class InfTpCommand extends Command {
    private CustomPathFinder.Vec3 to = null;
    private ArrayList<CustomPathFinder.Vec3> path = new ArrayList<>();
    TickTimer timer = new TickTimer();

    public InfTpCommand() {
        super("infTp", "infinite tp", "itp","inf");
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("player").then(argument("player", PlayerArgumentType.create()).executes(context -> {
            PlayerEntity player = PlayerArgumentType.get(context);
            CustomPathFinder.Vec3 topPlayer = new CustomPathFinder.Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());
            to = new CustomPathFinder.Vec3(player.getX(),player.getY(),player.getZ());
            path = PathFind.computePath(topPlayer,to,true);
            if (path.isEmpty()) {
                path = PathFind.computePath(topPlayer,to);
            }
            for (CustomPathFinder.Vec3 pathElm : path) {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
            }
            mc.player.updatePosition(to.x,to.y,to.z);
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("pos").then(argument("pos", ClientPosArgumentType.pos()).executes(ctx -> {
            Vec3d pos = ClientPosArgumentType.getPos(ctx, "pos");
            to = new CustomPathFinder.Vec3(pos.x,pos.y,pos.z);
            CustomPathFinder.Vec3 topPlayer = new CustomPathFinder.Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());
            path = PathFind.computePath(topPlayer,to,true);
            if (path.isEmpty()) {
                path = PathFind.computePath(topPlayer,to);
            }
            for (CustomPathFinder.Vec3 pathElm : path) {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
            }
            mc.player.updatePosition(to.x,to.y,to.z);
            return SINGLE_SUCCESS;
        })));
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (path != null) {
            if (timer.hasTimePassed(15)) {
                path = null;
                timer.reset();
            }
            timer.update();
        }
    }

    @EventHandler
    public void render(Render3DEvent event) {
        if (path != null) {
            CustomPathFinder.Vec3 lastPoint = null;

            for (CustomPathFinder.Vec3 pos : path) {
                switch (InfiniteAura.getPathMode()) {
                    case Box -> {
                        if (pos != null)
                            drawPath(pos, event);
                    }
                    case Line -> {
                        if (lastPoint != null && pos != null) {
                            int current = event.renderer.lines.vec3(pos.x,pos.y,pos.z).color(InfiniteAura.getPathColor()).next();
                            int last = event.renderer.lines.vec3(lastPoint.x,lastPoint.y,lastPoint.z).color(InfiniteAura.getPathColor()).next();
                            event.renderer.lines.line(last,current);
                        }

                        lastPoint = pos;
                    }
                }
            }
        }
    }

    public void drawPath(CustomPathFinder.Vec3 vec, Render3DEvent r) {
        r.rpl.vecBox(vec,mc.player.getBoundingBox(mc.player.getPose()), InfiniteAura.getPathColor(), InfiniteAura.getPathColor(), ShapeMode.Lines, 0);
    }
}
