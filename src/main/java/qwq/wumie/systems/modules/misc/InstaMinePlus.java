package qwq.wumie.systems.modules.misc;

import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import org.joml.Vector3d;
import qwq.wumie.utils.time.MSTimer;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import qwq.wumie.systems.modules.lemon.utils.BlockInfo;

import java.util.ArrayList;
import java.util.List;

public class InstaMinePlus extends Module {
    private final List<MBlock> mineBlocks = new ArrayList<>();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final SettingGroup sgTest = settings.createGroup("Test");

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("The delay between breaks.")
            .defaultValue(0)
            .min(0)
            .sliderMax(10000)
            .build()
    );

    private final Setting<Integer> maxBreakingBlocks = sgGeneral.add(new IntSetting.Builder()
            .name("max-breaking-blocks")
            .description("The max breaking blocks.")
            .defaultValue(0)
            .min(0)
            .sliderMax(10000)
            .build()
    );

    private final Setting<Integer> maxBreakBlocks = sgGeneral.add(new IntSetting.Builder()
            .name("max-blocks")
            .description("The max mine blocks.")
            .defaultValue(0)
            .min(0)
            .sliderMax(10000)
            .build()
    );

    private final Setting<Boolean> pick = sgGeneral.add(new BoolSetting.Builder()
            .name("only-pick")
            .description("Only tries to mine the block if you are holding a pickaxe.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Faces the blocks being mined server side.")
            .defaultValue(true)
            .build()
    );

    // Render

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
            .name("render")
            .description("Renders a block overlay on the block being broken.")
            .defaultValue(true)
            .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The color of the sides of the blocks being rendered.")
            .defaultValue(new SettingColor(204, 0, 0, 10))
            .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The color of the lines of the blocks being rendered.")
            .defaultValue(new SettingColor(204, 0, 0, 255))
            .build()
    );

    private final Setting<SettingColor> sideColorOut = sgRender.add(new ColorSetting.Builder()
            .name("side-color-out")
            .description("The color of the sides of the blocks being rendered.[out]")
            .defaultValue(new SettingColor(204, 0, 0, 10))
            .build()
    );

    private final Setting<SettingColor> lineColorOut = sgRender.add(new ColorSetting.Builder()
            .name("line-color-out")
            .description("The color of the lines of the blocks being rendered.[out]")
            .defaultValue(new SettingColor(204, 0, 0, 255))
            .build()
    );

    private final Setting<Boolean> fastBreak = sgRender.add(new BoolSetting.Builder()
            .name("fast-break")
            .description("break fast.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> test1 = sgRender.add(new BoolSetting.Builder()
            .name("test-breaking-det")
            .description("only break.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> test2 = sgRender.add(new BoolSetting.Builder()
            .name("test-breaking-block")
            .description("only break.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> test3 = sgRender.add(new BoolSetting.Builder()
            .name("test-no-exists-det")
            .description("only break.")
            .defaultValue(true)
            .build()
    );

    @EventHandler
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        if (mineBlocks.size() < maxBreakBlocks.get()) {
            mineBlocks.add(new MBlock(event.blockPos));
        }
    }

    private final MSTimer timer = new MSTimer();

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (shouldMine()) {
            mineBlocks.forEach((block) -> {
                block.render(event);
            });
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (timer.hasTimePassed(delay.get())) {
            timer.reset();

            if (shouldMine()) {
                mineBlocks.forEach(MBlock::tick);

                if (rotate.get()) {
                    MBlock block = mineBlocks.get(0);
                    BlockPos blockPos = block.pos;
                    Rotations.rotate(Rotations.getYaw(blockPos), Rotations.getPitch(blockPos), () -> mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, block.getDirection())));
                }


                if (fastBreak.get()) {
                    if (test2.get()) {
                        for (int i = 0; i < maxBreakingBlocks.get(); i++) {
                            if (i > getExistsBlocks().size()) continue;

                            MBlock block = getExistsBlocks().get(i);

                            MBlock topBlock = getExistsBlocks().get(0);
                            while (topBlock.shouldMine()) {
                                topBlock.breakBlock();
                            }

                            if (!block.pos.equals(topBlock.pos)) {
                                if (block.shouldMine()) {
                                    block.breakBlock();

                                    mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                                }
                            }
                        }
                    } else {

                        MBlock topBlock = getExistsBlocks().get(0);
                        while (topBlock.shouldMine()) {
                            topBlock.breakBlock();
                        }

                        getExistsBlocks().forEach((block) -> {
                            if (!block.pos.equals(topBlock.pos)) {
                                if (block.shouldMine()) {
                                    block.breakBlock();

                                    mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                                }
                            }
                        });
                    }
                }

                if (test2.get()) {
                    for (int i = 0; i < maxBreakingBlocks.get(); i++) {
                        if (i > getExistsBlocks().size()) continue;

                        MBlock block = getExistsBlocks().get(i);

                        if (block.shouldMine()) {
                            block.breakBlock();

                            mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                        }
                    }
                } else {
                    mineBlocks.forEach((block) -> {
                        if (block.shouldMine()) {
                            block.breakBlock();

                            mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                        }
                    });
                }
            }
        }
    }

    public boolean shouldMine() {
        return !mineBlocks.isEmpty();
    }

    public List<MBlock> getExistsBlocks() {
        mineBlocks.forEach(MBlock::tick);

        if (test3.get()) return mineBlocks;

        List<MBlock> blocks = new ArrayList<>();
        for (MBlock block : mineBlocks) {
            if (block.existsBlock) {
                blocks.add(block);
            }
        }
        return blocks;
    }

    @Override
    public void onActivate() {
        timer.reset();
        mineBlocks.clear();
        super.onActivate();
    }

    public InstaMinePlus() {
        super(Categories.Player, "insta-mine+", "Attempts to instantly mine blocks.");
    }

    public class MBlock {
        public BlockPos pos;
        public Block block;

        public boolean breaking;
        public boolean existsBlock;

        public MBlock(BlockPos pos) {
            this.pos = pos;
            this.breaking = false;
            this.block = BlockInfo.getBlock(pos);
        }

        public boolean shouldMine() {
            if (test1.get() && !existsBlock) return false;
            if (pos.getY() == -65) return false;
            if (!BlockUtils.canBreak(pos)) return false;
            return !pick.get() || (mc.player.getMainHandStack().getItem() == Items.DIAMOND_PICKAXE || mc.player.getMainHandStack().getItem() == Items.NETHERITE_PICKAXE);
        }

        public void tick() {
            BlockState block = BlockInfo.getBlockState(pos);
            this.block = block.getBlock();

            existsBlock = !block.isAir() && !(block.getBlock() instanceof FluidBlock);
        }

        public void breakBlock() {
            if (!breaking) breaking = true;

            Direction direction = Direction.getFacing(pos.getX(), pos.getY(), pos.getZ());
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, direction));
        }

        public Direction getDirection() {
            return Direction.getFacing(pos.getX(), pos.getY(), pos.getZ());
        }

        public void render(Render3DEvent event) {
            if (!render.get()) return;

            if (shouldMine()) {
                event.renderer.box(pos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
            }
            event.rpl.vecBox(new Vector3d(pos.getX(), pos.getY(), pos.getZ()), new Vector3d(0.25, 0.25, 0.25), sideColorOut.get(), lineColorOut.get(), shapeMode.get(), 0);
        }
    }
}
