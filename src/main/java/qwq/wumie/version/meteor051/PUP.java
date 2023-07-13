package qwq.wumie.version.meteor051;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import qwq.wumie.systems.modules.combat.MeteorAntiBot;
import qwq.wumie.systems.modules.combat.TargetStrafe;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PUP {
    public static boolean isServerBot(Entity entity) {
        if (!Modules.get().get(MeteorAntiBot.class).isActive()) return false;

        if (entity instanceof PlayerEntity player) {
            return Modules.get().get(MeteorAntiBot.class).isBot(player);
        }
        return false;
    }

    public static boolean isBlockUnder() {
        if(mc.player.getY() < Modules.get().get(TargetStrafe.class).test.get())
            return false;
        for(int off = 0; off < (int)mc.player.getY()+2; off += 2){
            Box bb = mc.player.getBoundingBox().offset(0, -off, 0);
            if(!mc.world.getEntityCollisions(mc.player, bb).isEmpty()){
                return true;
            }
        }
        return false;
    }
}
