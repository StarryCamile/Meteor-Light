package qwq.wumie.systems.modules.lemon.utils;

import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import qwq.wumie.systems.modules.lemon.combat.CevBreaker;
import qwq.wumie.systems.modules.lemon.combat.LemonAura;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CrystalUtils {
    static LemonAura BBomber = Modules.get().get(LemonAura.class);

    public static int getPlaceDelay() {
        if (isBurrowBreaking()) return BBomber.burrowBreakDelay.get();
        else if (isSurroundBreaking()) return BBomber.surroundBreakDelay.get();
        else return BBomber.placeDelay.get();
    }

    public static void attackCrystal(Entity entity) {
        // Attack
        mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking()));

        if (BBomber.renderSwing.get()) mc.player.swingHand(Hand.MAIN_HAND);
        if (!BBomber.hideSwings.get()) mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        BBomber.attacks++;

        getBreakDelay();

        if (BBomber.debug.get()) BBomber.warning("Breaking");
    }

    // Damage Ignores

    public static boolean targetJustPopped() {
        if (BBomber.targetPopInvincibility.get()) {
            return !BBomber.targetPoppedTimer.passedMillis(BBomber.targetPopInvincibilityTime.get());
        }

        return false;
    }

    public static boolean shouldIgnoreSelfPlaceDamage() {
        return (BBomber.PDamageIgnore.get() == LemonAura.DamageIgnore.Always
                || (BBomber.PDamageIgnore.get() == LemonAura.DamageIgnore.WhileSafe && (BEntityUtils.isSurrounded(mc.player, BEntityUtils.BlastResistantType.Any) || BEntityUtils.isBurrowed(mc.player, BEntityUtils.BlastResistantType.Any)))
                || (BBomber.selfPopInvincibility.get() && BBomber.selfPopIgnore.get() != LemonAura.SelfPopIgnore.Break && !BBomber.selfPoppedTimer.passedMillis(BBomber.selfPopInvincibilityTime.get())));
    }

    public static boolean shouldIgnoreSelfBreakDamage() {
        return (BBomber.BDamageIgnore.get() == LemonAura.DamageIgnore.Always
                || (BBomber.BDamageIgnore.get() == LemonAura.DamageIgnore.WhileSafe && (BEntityUtils.isSurrounded(mc.player, BEntityUtils.BlastResistantType.Any) || BEntityUtils.isBurrowed(mc.player, BEntityUtils.BlastResistantType.Any)))
                || (BBomber.selfPopInvincibility.get() && BBomber.selfPopIgnore.get() != LemonAura.SelfPopIgnore.Place && !BBomber.selfPoppedTimer.passedMillis(BBomber.selfPopInvincibilityTime.get())));
    }

    private static void getBreakDelay() {
        if (isSurroundHolding() && BBomber.surroundHoldMode.get() != LemonAura.SlowMode.Age) {
            BBomber.breakTimer = BBomber.surroundHoldDelay.get();
        } else if (BBomber.slowFacePlace.get() && BBomber.slowFPMode.get() != LemonAura.SlowMode.Age && isFacePlacing() && BBomber.bestTarget != null && BBomber.bestTarget.getY() < BBomber.placingCrystalBlockPos.getY()) {
            BBomber.breakTimer = BBomber.slowFPDelay.get();
        } else BBomber.breakTimer = BBomber.breakDelay.get();
    }

    // Face Place
    public static boolean shouldFacePlace(BlockPos crystal) {
        // Checks if the provided crystal position should face place to any target
        for (PlayerEntity target : BBomber.targets) {
            BlockPos pos = target.getBlockPos();
            if (BBomber.CevPause.get() && Modules.get().isActive(CevBreaker.class)) return false;
            if (BBomber.KAPause.get() && (Modules.get().isActive(KillAura.class))) return false;
            if (BEntityUtils.isFaceSurrounded(target, BEntityUtils.BlastResistantType.Any)) return false;
            if (BBomber.surrHoldPause.get() && isSurroundHolding()) return false;

            if (crystal.getY() == pos.getY() + 1 && Math.abs(pos.getX() - crystal.getX()) <= 1 && Math.abs(pos.getZ() - crystal.getZ()) <= 1) {
                if (EntityUtils.getTotalHealth(target) <= BBomber.facePlaceHealth.get()) return true;

                for (ItemStack itemStack : target.getArmorItems()) {
                    if (itemStack == null || itemStack.isEmpty()) {
                        if (BBomber.facePlaceArmor.get()) return true;
                    }
                    else {
                        if ((float) (itemStack.getMaxDamage() - itemStack.getDamage()) / itemStack.getMaxDamage() * 100 <= BBomber.facePlaceDurability.get()) return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isFacePlacing() {
        return (BBomber.facePlace.get() || BBomber.forceFacePlace.get().isPressed());
    }

    // Burrow Break

    public static boolean shouldBurrowBreak(BlockPos crystal) {
        BlockPos pos = BBomber.bestTarget.getBlockPos();

        if (!isBurrowBreaking()) return false;

        return ((crystal.getY() == pos.getY() - 1 || crystal.getY() == pos.getY()) && Math.abs(pos.getX() - crystal.getX()) <= 1 && Math.abs(pos.getZ() - crystal.getZ()) <= 1);
    }

    public static boolean isBurrowBreaking() {
        if (BBomber.burrowBreak.get() || BBomber.forceBurrowBreak.get().isPressed()) {
            if (BBomber.bestTarget != null && BEntityUtils.isBurrowed(BBomber.bestTarget, BEntityUtils.BlastResistantType.Mineable)) {
                switch (BBomber.burrowBWhen.get()) {
                    case BothTrapped -> {
                        return BEntityUtils.isBothTrapped(BBomber.bestTarget, BEntityUtils.BlastResistantType.Any);
                    }
                    case AnyTrapped -> {
                        return BEntityUtils.isAnyTrapped(BBomber.bestTarget, BEntityUtils.BlastResistantType.Any);
                    }
                    case TopTrapped -> {
                        return BEntityUtils.isTopTrapped(BBomber.bestTarget, BEntityUtils.BlastResistantType.Any);
                    }
                    case FaceTrapped -> {
                        return BEntityUtils.isFaceSurrounded(BBomber.bestTarget, BEntityUtils.BlastResistantType.Any);
                    }
                    case Always -> {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    // Surround Break

    // Todo : improve this
    public static boolean shouldSurroundBreak(BlockPos crystal) {
        BlockPos pos = BBomber.bestTarget.getBlockPos();

        // Checking right criteria
        if (!isSurroundBreaking()) return false;

        // Checking valid crystal position
        return
                (!BEntityUtils.isBedrock(pos.north(1))
                        && (crystal.equals(pos.north(2))
                        || (BBomber.surroundBHorse.get() && (crystal.equals(pos.north(2).west()) || crystal.equals(pos.north(2).east())))
                        || (BBomber.surroundBDiagonal.get() && (crystal.equals(pos.north().west()) || crystal.equals(pos.north().east())))
                ))

                        || (!BEntityUtils.isBedrock(pos.south(1))
                        && (crystal.equals(pos.south(2))
                        || (BBomber.surroundBHorse.get() && (crystal.equals(pos.south(2).west()) || crystal.equals(pos.south(2).east())))
                        || (BBomber.surroundBDiagonal.get() && (crystal.equals(pos.south().west()) || crystal.equals(pos.south().east())))
                ))

                        || (!BEntityUtils.isBedrock(pos.west(1))
                        && (crystal.equals(pos.west(2))
                        || (BBomber.surroundBHorse.get() && (crystal.equals(pos.west(2).north()) || crystal.equals(pos.west(2).south())))
                        || (BBomber.surroundBDiagonal.get() && (crystal.equals(pos.west().north()) || crystal.equals(pos.west().south())))
                ))

                        || (!BEntityUtils.isBedrock(pos.east(1))
                        && (crystal.equals(pos.east(2))
                        || (BBomber.surroundBHorse.get() && (crystal.equals(pos.east(2).north()) || crystal.equals(pos.east(2).south())))
                        || (BBomber.surroundBDiagonal.get() && (crystal.equals(pos.east().north()) || crystal.equals(pos.east().south())))
                ));
    }

    public static boolean isSurroundBreaking() {
        if (BBomber.surroundBreak.get() || BBomber.forceSurroundBreak.get().isPressed()) {
            if (BBomber.bestTarget != null && BEntityUtils.isSurrounded(BBomber.bestTarget, BEntityUtils.BlastResistantType.Mineable)) {
                switch (BBomber.surroundBWhen.get()) {
                    case BothTrapped -> {
                        return BEntityUtils.isBothTrapped(BBomber.bestTarget, BEntityUtils.BlastResistantType.Any);
                    }
                    case AnyTrapped -> {
                        return BEntityUtils.isAnyTrapped(BBomber.bestTarget, BEntityUtils.BlastResistantType.Any);
                    }
                    case TopTrapped -> {
                        return BEntityUtils.isTopTrapped(BBomber.bestTarget, BEntityUtils.BlastResistantType.Any);
                    }
                    case FaceTrapped -> {
                        return BEntityUtils.isFaceSurrounded(BBomber.bestTarget, BEntityUtils.BlastResistantType.Any);
                    }
                    case Always -> {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isSurroundHolding() {
        if (BBomber.surroundHold.get() && BBomber.bestTarget != null && BEntityUtils.isSurroundBroken(BBomber.bestTarget, BEntityUtils.BlastResistantType.Any)) {
            switch (BBomber.surroundHWhen.get()) {
                case BothTrapped -> {
                    return BEntityUtils.isBothTrapped(BBomber.bestTarget, BEntityUtils.BlastResistantType.Any);
                }
                case AnyTrapped -> {
                    return BEntityUtils.isAnyTrapped(BBomber.bestTarget, BEntityUtils.BlastResistantType.Any);
                }
                case TopTrapped -> {
                    return BEntityUtils.isTopTrapped(BBomber.bestTarget, BEntityUtils.BlastResistantType.Any);
                }
                case FaceTrapped -> {
                    return BEntityUtils.isFaceSurrounded(BBomber.bestTarget, BEntityUtils.BlastResistantType.Any);
                }
                case Always -> {
                    return true;
                }
            }
        }

        return false;
    }
}
