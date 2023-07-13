package qwq.wumie.systems.modules.misc.bedrockbreaker;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BlockPlacer {
    public static void simpleBlockPlacement(BlockPos pos, ItemConvertible item) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        InventoryManager.switchToItem(item);
        BlockHitResult hitResult = new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false);
        placeBlockWithoutInteractingBlock(minecraftClient, hitResult);
    }

    public static void pistonPlacement(BlockPos pos, Direction direction) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        double x = pos.getX();

        switch (BreakingFlowController.getWorkingMode()) {
            case CARPET_EXTRA ->//carpet accurateBlockPlacement支持
                    x = x + 2 + direction.getId() * 2;
            case VANILLA -> {//直接发包，改变服务端玩家实体视角
                PlayerEntity player = minecraftClient.player;
                float pitch = switch (direction) {
                    case UP -> 90f;
                    case DOWN -> -90f;
                    default -> 90f;
                };
                minecraftClient.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(player.getYaw(1.0f), pitch, player.isOnGround()));
            }
        }

        Vec3d vec3d = new Vec3d(x, pos.getY(), pos.getZ());

        InventoryManager.switchToItem(Blocks.PISTON);
        BlockHitResult hitResult = new BlockHitResult(vec3d, Direction.UP, pos, false);
//        minecraftClient.interactionManager.interactBlock(minecraftClient.player, minecraftClient.world, Hand.MAIN_HAND, hitResult);
        placeBlockWithoutInteractingBlock(minecraftClient, hitResult);
    }

    private static void placeBlockWithoutInteractingBlock(MinecraftClient minecraftClient, BlockHitResult hitResult) {
        ClientPlayerEntity player = minecraftClient.player;
        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);

        Method method = null;

        try {
            method = ClientPlayerInteractionManager.class.getDeclaredMethod("method_41931", ClientWorld.class, SequencedPacketCreator.class);
        } catch (NoSuchMethodException e) {
            try {
                method = ClientPlayerInteractionManager.class.getDeclaredMethod("sendSequencedPacket", ClientWorld.class, SequencedPacketCreator.class);
            } catch (NoSuchMethodException e1) {
                method = null;
            }
        }

        if (method == null) return;

        try {
            method.invoke(minecraftClient.interactionManager, minecraftClient.world, new SequencedPacketCreator() {
                @Override
                public Packet<ServerPlayPacketListener> predict(int sequence) {
                    return new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, sequence);
                }
            });
        } catch (IllegalAccessException | InvocationTargetException e) {
            return;
        }

        if (!itemStack.isEmpty() && !player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
            ItemUsageContext itemUsageContext = new ItemUsageContext(player, Hand.MAIN_HAND, hitResult);
            itemStack.useOnBlock(itemUsageContext);

        }
    }
}
