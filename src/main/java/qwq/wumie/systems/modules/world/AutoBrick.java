/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package qwq.wumie.systems.modules.world;

import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3d;
import qwq.wumie.utils.pathfinding.player.PathFind;
import qwq.wumie.utils.time.MSTimer;

public class AutoBrick extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgBuySign = settings.createGroup("BuySign");
    private final SettingGroup sgSellSign = settings.createGroup("SellSign");

    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Item> defaultitem = sgGeneral.add(new ItemSetting.Builder()
            .name("Item")
            .defaultValue(Items.CACTUS)
            .build()
    );

    private final Setting<Integer> allDelay = sgGeneral.add(new IntSetting.Builder()
            .name("all-delay")
            .description("The delay of send all.(ms)")
            .defaultValue(1000)
            .build()
    );

    private final Setting<Integer> commandDelay = sgGeneral.add(new IntSetting.Builder()
            .name("command-delay")
            .description("The delay of send command.(ms)")
            .defaultValue(5000)
            .build()
    );

    private final Setting<String> startRes = sgGeneral.add(new StringSetting.Builder()
            .name("StartCommand")
            .description("res tp xxx & warp xxx")
            .defaultValue("res tp ")
            .build()
    );

    private final Setting<String> endRes = sgGeneral.add(new StringSetting.Builder()
            .name("EndCommand")
            .description("res tp xxx && warp xxx")
            .defaultValue("warp ")
            .build()
    );

    private final Setting<Vector3d> buySignPos = sgBuySign.add(new Vector3dSetting.Builder()
            .name("buy-sign-pos")
            .description("use the pos to click.")
            .defaultValue(191, 98, 10)
            .build()
    );

    private final Setting<Vector3d> buyTpPos = sgBuySign.add(new Vector3dSetting.Builder()
            .name("buy-tp-pos")
            .description("Tp the pos to click the buy sign.")
            .defaultValue(11, 45, 14)
            .build()
    );

    private final Setting<Vector3d> sellSignPos = sgSellSign.add(new Vector3dSetting.Builder()
            .name("sell-sign-pos")
            .description("use the pos to click.")
            .defaultValue(191, 98, 10)
            .build()
    );

    private final Setting<Vector3d> sellTpPos = sgSellSign.add(new Vector3dSetting.Builder()
            .name("sell-tp-pos")
            .description("Tp the pos to click the sell sign.")
            .defaultValue(11, 45, 14)
            .build()
    );

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

    private BlockPos buySign;
    private BlockPos sellSign;
    private boolean inBuyShop, clicked, allSend;
    private final MSTimer timer = new MSTimer(), commandTimer = new MSTimer();

    @Override
    public void onActivate() {
        startTpRes();
        timer.reset();
        commandTimer.reset();
        //start();
        super.onActivate();
    }

    @EventHandler
    private void onDamageBlock(StartBreakingBlockEvent e) {
    }

    @EventHandler
    private void onRender(Render3DEvent e) {
        buySign = BlockPos.ofFloored(buySignPos.get().x, buySignPos.get().y, buySignPos.get().z);
        sellSign = BlockPos.ofFloored(sellSignPos.get().x, sellSignPos.get().y, sellSignPos.get().z);

        if (inBuyShop) e.renderer.box(buySign, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        else e.renderer.box(sellSign, sideColor.get().r(0), lineColor.get(), shapeMode.get(), 0);
    }

    @Override
    public void onDeactivate() {
        inBuyShop = false;

        super.onDeactivate();
    }

    @EventHandler
    private void onTick(TickEvent.Pre e) {
        if (buySign == null) return;
        if (sellSign == null) return;

        if (inBuyShop) {
            teleportToBuy();
            clickBuySign();
            tickAll();
            if (allSend && commandTimer.hasTimePassed(commandDelay.get()) && mc.player.getMainHandStack().getItem().equals(defaultitem.get())) {
                endTpRes();
            }
        } else {
            teleportToSell();
            clickSellSign();
            tickAll();
            if (allSend && commandTimer.hasTimePassed(commandDelay.get())) {
                startTpRes();
            }
        }

        //tickAll();
    }

    @EventHandler
    private void onReceiveMessage(ReceiveMessageEvent e) {
        String message = e.getMessage().getString();

        if (message.contains("这个商店已缺货")) {
            switchToNearbySign();
        }
    }

    private void teleportToBuy() {
        if (!(distanceTo(buyTpPos.get()) < 1) && distanceTo(buyTpPos.get()) < 20) {
            PathFind.teleportTo(buyTpPos.get());
        }
    }

    private void clickBuySign() {
        if (!(distanceTo(buySign) > 6) && !clicked) {
            mc.interactionManager.attackBlock(buySign, Direction.getFacing(buySign.getX(), buySign.getY(), buySign.getZ()));
            Rotations.rotate(Rotations.getYaw(buySign), Rotations.getPitch(buySign));
            clicked = true;
            allSend = false;
        }
    }

    private void teleportToSell() {
        if (!(distanceTo(sellTpPos.get()) < 1) && distanceTo(sellTpPos.get()) < 20) {
            PathFind.teleportTo(sellTpPos.get());
        }
    }

    private void clickSellSign() {
        if (!(distanceTo(sellSign) > 6) && !clicked) {
            mc.interactionManager.attackBlock(sellSign, Direction.getFacing(sellSign.getX(), sellSign.getY(), sellSign.getZ()));
            Rotations.rotate(Rotations.getYaw(sellSign), Rotations.getPitch(sellSign));
            clicked = true;
            allSend = false;
        }
    }

    private void switchToNearbySign() {
    }

    void setBuySign(double x, double y, double z) {
        buySignPos.set(new Vector3d(x, y, z));
    }

    void setBuySign(BlockPos pos) {
        setBuySign(pos.getX(), pos.getY(), pos.getZ());
    }

    public float distanceTo(BlockPos pos) {
        float f = (float) (mc.player.getX() - pos.getX());
        float g = (float) (mc.player.getY() - pos.getY());
        float h = (float) (mc.player.getZ() - pos.getZ());
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    public float distanceTo(Vector3d pos) {
        float f = (float) (mc.player.getX() - pos.x());
        float g = (float) (mc.player.getY() - pos.y());
        float h = (float) (mc.player.getZ() - pos.z());
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    private void tickAll() {
        if (!allSend && clicked && timer.hasTimePassed(allDelay.get())) {

            sendChatMessage("all");
            allSend = true;

            clicked = false;
            timer.reset();
        }
    }

    void startTpRes() {
        if (mc.player != null) {
            inBuyShop = true;
            allSend = false;
            sendChatMessage("/" + startRes.get());
        }

        commandTimer.reset();
    }

    void endTpRes() {
        if (mc.player != null) {
            inBuyShop = false;
            allSend = false;
            sendChatMessage("/" + endRes.get());
        }

        commandTimer.reset();
    }

    public AutoBrick() {
        super(Categories.World, "auto-brick", "some server auto brick");
    }
}
