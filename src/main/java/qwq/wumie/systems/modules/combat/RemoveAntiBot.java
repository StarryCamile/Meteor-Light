/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.modules.combat;

import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;

public class RemoveAntiBot extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgHelmet = settings.createGroup("Helment");
    private final SettingGroup sgChestplate = settings.createGroup("Chestplate");
    private final SettingGroup sgLeggings = settings.createGroup("Leggings");
    private final SettingGroup sgBoots = settings.createGroup("Boots");

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("The mode on how RemoveAntiBot will function.")
        .defaultValue(Mode.NoColorArmor)
        .build()
    );

    private final Setting<Boolean> debug = sgHelmet.add(new BoolSetting.Builder()
        .name("debug")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> friend = sgHelmet.add(new BoolSetting.Builder()
        .name("addBotFriend")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> allowNetheriteHelmet = sgHelmet.add(new BoolSetting.Builder()
        .name("allowNetheriteHelmet")
        .defaultValue(true)
            .visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowDiamondHelmet = sgHelmet.add(new BoolSetting.Builder()
        .name("AllowDiamondHelmet")
        .defaultValue(true)
        .visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowGoldenHelmet = sgHelmet.add(new BoolSetting.Builder()
        .name("allowGoldenHelmet")
        .defaultValue(true)            .visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowIronHelmet = sgHelmet.add(new BoolSetting.Builder()
        .name("allowIronHelmet")
        .defaultValue(true)            .visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowChainHelmet = sgHelmet.add(new BoolSetting.Builder()
        .name("allowChainHelmet")            .visible(() -> mode.get().equals(Mode.UnusualArmor))
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> allowLeatherHelmet = sgHelmet.add(new BoolSetting.Builder()
        .name("allowLeatherHelmet")            .visible(() -> mode.get().equals(Mode.UnusualArmor))
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> allowNoHelmet = sgHelmet.add(new BoolSetting.Builder()
        .name("allowNoHelmet")
        .defaultValue(true)            .visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );

    private final Setting<Boolean> allowNetheriteChestplate = sgChestplate.add(new BoolSetting.Builder()
        .name("allowNetheriteChestplate")
        .defaultValue(true)            .visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowDiamondChestplate = sgChestplate.add(new BoolSetting.Builder()
        .name("AllowDiamondChestplate")
        .defaultValue(true)            .visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowGoldenChestplate = sgChestplate.add(new BoolSetting.Builder()
        .name("allowGoldenChestplate")
        .defaultValue(true).visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowIronChestplate = sgChestplate.add(new BoolSetting.Builder()
        .name("allowIronChestplate")
        .defaultValue(true)            .visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowChainChestplate = sgChestplate.add(new BoolSetting.Builder()
        .name("allowChainChestplate")
        .defaultValue(true).visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowLeatherChestplate = sgChestplate.add(new BoolSetting.Builder()
        .name("allowLeatherChestplate")
        .defaultValue(true).visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowNoChestplate = sgChestplate.add(new BoolSetting.Builder()
        .name("allowNoChestplate").visible(() -> mode.get().equals(Mode.UnusualArmor))
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> allowNetheriteLeggings = sgLeggings.add(new BoolSetting.Builder()
        .name("allowNetheriteLeggings")
        .defaultValue(true).visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowDiamondLeggings = sgLeggings.add(new BoolSetting.Builder()
        .name("allowDiamondLeggings")
        .defaultValue(true).visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowGoldenLeggings = sgLeggings.add(new BoolSetting.Builder()
        .name("allowGoldenLeggings").visible(() -> mode.get().equals(Mode.UnusualArmor))
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> allowIronLeggings = sgLeggings.add(new BoolSetting.Builder()
        .name("allowIronLeggings")
        .defaultValue(true).visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowChainLeggings = sgLeggings.add(new BoolSetting.Builder()
        .name("allowChainLeggings")
        .defaultValue(true).visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowLeatherLeggings = sgLeggings.add(new BoolSetting.Builder()
        .name("allowLeatherLeggings").visible(() -> mode.get().equals(Mode.UnusualArmor))
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> allowNoLeggings = sgLeggings.add(new BoolSetting.Builder()
        .name("allowNoLeggings").visible(() -> mode.get().equals(Mode.UnusualArmor))
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> allowNetheriteBoots = sgBoots.add(new BoolSetting.Builder()
        .name("allowNetheriteBoots")
        .defaultValue(true).visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowDiamondBoots = sgBoots.add(new BoolSetting.Builder()
        .name("allowDiamondBoots").visible(() -> mode.get().equals(Mode.UnusualArmor))
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> allowGoldenBoots = sgBoots.add(new BoolSetting.Builder()
        .name("allowGoldenBoots").visible(() -> mode.get().equals(Mode.UnusualArmor))
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> allowIronBoots = sgBoots.add(new BoolSetting.Builder()
        .name("allowIronBoots")
        .defaultValue(true).visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowChainBoots = sgBoots.add(new BoolSetting.Builder()
        .name("allowChainBoots").visible(() -> mode.get().equals(Mode.UnusualArmor))
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> allowLeatherBoots = sgBoots.add(new BoolSetting.Builder()
        .name("allowLeatherBoots")
        .defaultValue(true).visible(() -> mode.get().equals(Mode.UnusualArmor))
        .build()
    );
    private final Setting<Boolean> allowNoBoots = sgBoots.add(new BoolSetting.Builder()
        .name("allowNoBoots").visible(() -> mode.get().equals(Mode.UnusualArmor))
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> removeNoColorLeatherArmor = sgBoots.add(new BoolSetting.Builder()
        .name("removeNoColorLeatherArmor")
        .defaultValue(true)
        .build()
    );

    public void update() {
        if (mode.get().equals(Mode.NoColorArmor)) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                boolean isBot = false;
                DefaultedList<ItemStack> armorInventory = player.getInventory().armor;
                for (ItemStack armor : armorInventory) {
                    if (armor == null || armor.getItem() == null) continue;
                    ArmorItem itemArmor;
                    Item[] leatherarmor = new Item[]{
                        Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS
                    };
                    try {
                        itemArmor = (ArmorItem) armor.getItem();
                    } catch (Exception e) {
                        return;
                    }
                    for (Item armorItem : leatherarmor) {
                        if (itemArmor == armorItem) {
                            if (!armor.hasNbt()) isBot = true;
                        }
                    }
                }
                if (isBot) {
                    if (serverBots.contains(player)) return;
                    serverBots.add(player);
                    if (friend.get()) {
                        Friends.get().add(new Friend(player));
                    }
                    debug("检测到 " + player.getEntityName() + " 为Bot,已添加白名单");
                }
            }
        }
        if (mode.get().equals(Mode.UnusualArmor)) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                boolean isBot = false;
                DefaultedList<ItemStack> armorInventory = player.getInventory().armor;
                ItemStack helmet = armorInventory.get(3);
                ItemStack chestPlate = armorInventory.get(2);
                ItemStack leggings = armorInventory.get(1);
                ItemStack boots = armorInventory.get(0);
                if (
                    //NoArmor
                        ((boots==null|| boots.getItem() ==null)&&!allowNoBoots.get())
                        ||((leggings==null|| leggings.getItem() ==null)&&!allowNoLeggings.get())
                        ||((chestPlate==null|| chestPlate.getItem() ==null)&&!allowNoChestplate.get())
                        ||((helmet==null|| helmet.getItem() ==null)&&!allowNoHelmet.get())
//-----------------------------------1145141919810-------------------------------------------------------
                        //Netherite
                        ||((helmet!=null&& helmet.getItem() !=null&& helmet.getItem() instanceof ArmorItem)&&(helmet.getItem()) == Items.NETHERITE_HELMET&&!allowNetheriteHelmet.get())
                        ||((chestPlate!=null&& chestPlate.getItem() !=null&& chestPlate.getItem() instanceof ArmorItem)&&(chestPlate.getItem()) == Items.NETHERITE_CHESTPLATE&&!allowNetheriteChestplate.get())
                        ||((leggings!=null&& leggings.getItem() !=null&& leggings.getItem() instanceof ArmorItem)&&(leggings.getItem()) == Items.NETHERITE_LEGGINGS&&!allowNetheriteLeggings.get())
                        ||((boots!=null&& boots.getItem() !=null&& boots.getItem() instanceof ArmorItem)&&(boots.getItem()) == Items.NETHERITE_BOOTS&&!allowNetheriteBoots.get())
//-----------------------------------1145141919810--------------------------------------------------------
                         //Diamond
                        ||((helmet!=null&& helmet.getItem() !=null&& helmet.getItem() instanceof ArmorItem)&&(helmet.getItem()) == Items.DIAMOND_HELMET&&!allowDiamondHelmet.get())
                        ||((chestPlate!=null&& chestPlate.getItem() !=null&& chestPlate.getItem() instanceof ArmorItem)&&(chestPlate.getItem()) == Items.DIAMOND_CHESTPLATE&&!allowDiamondChestplate.get())
                        ||((leggings!=null&& leggings.getItem() !=null&& leggings.getItem() instanceof ArmorItem)&&(leggings.getItem()) == Items.DIAMOND_LEGGINGS&&!allowDiamondLeggings.get())
                        ||((boots!=null&& boots.getItem() !=null&& boots.getItem() instanceof ArmorItem)&&(boots.getItem()) == Items.DIAMOND_BOOTS&&!allowDiamondBoots.get())
//-----------------------------------1145141919810--------------------------------------------------------
                        //Golden
                        ||((helmet!=null&& helmet.getItem() !=null&& helmet.getItem() instanceof ArmorItem)&&(helmet.getItem()) == Items.GOLDEN_HELMET&&!allowGoldenHelmet.get())
                        ||((chestPlate!=null&& chestPlate.getItem() !=null&& chestPlate.getItem() instanceof ArmorItem)&&(chestPlate.getItem()) == Items.GOLDEN_CHESTPLATE&&!allowGoldenChestplate.get())
                        ||((leggings!=null&& leggings.getItem() !=null&& leggings.getItem() instanceof ArmorItem)&&(leggings.getItem()) == Items.GOLDEN_LEGGINGS&&!allowGoldenLeggings.get())
                        ||((boots!=null&& boots.getItem() !=null&& boots.getItem() instanceof ArmorItem)&&(boots.getItem()) == Items.GOLDEN_BOOTS&&!allowGoldenBoots.get())
//-----------------------------------1145141919810--------------------------------------------------------
                        //Chain
                        ||((helmet!=null&& helmet.getItem() !=null&& helmet.getItem() instanceof ArmorItem)&&(helmet.getItem()) == Items.CHAINMAIL_HELMET&&!allowChainHelmet.get())
                        ||((chestPlate!=null&& chestPlate.getItem() !=null&& chestPlate.getItem() instanceof ArmorItem)&&(chestPlate.getItem()) == Items.CHAINMAIL_CHESTPLATE&&!allowChainChestplate.get())
                        ||((leggings!=null&& leggings.getItem() !=null&& leggings.getItem() instanceof ArmorItem)&&(leggings.getItem()) == Items.CHAINMAIL_LEGGINGS&&!allowChainLeggings.get())
                        ||((boots!=null&& boots.getItem() !=null&& boots.getItem() instanceof ArmorItem)&&(boots.getItem()) == Items.CHAINMAIL_BOOTS&&!allowChainBoots.get())
//-----------------------------------1145141919810--------------------------------------------------------
                        //Leather
                        ||((helmet!=null&& helmet.getItem() !=null&& helmet.getItem() instanceof ArmorItem)&&(helmet.getItem()) == Items.LEATHER_HELMET&&!allowLeatherHelmet.get())
                        ||((chestPlate!=null&& chestPlate.getItem() !=null&& chestPlate.getItem() instanceof ArmorItem)&&(chestPlate.getItem()) == Items.LEATHER_CHESTPLATE&&!allowLeatherChestplate.get())
                        ||((leggings!=null&& leggings.getItem() !=null&& leggings.getItem() instanceof ArmorItem)&&(leggings.getItem()) == Items.LEATHER_LEGGINGS&&!allowLeatherLeggings.get())
                        ||((boots!=null&& boots.getItem() !=null&& boots.getItem() instanceof ArmorItem)&&(boots.getItem()) == Items.LEATHER_BOOTS&&!allowLeatherBoots.get())
//-----------------------------------1145141919810--------------------------------------------------------
                            //Iron
                            ||((helmet!=null&& helmet.getItem() !=null&& helmet.getItem() instanceof ArmorItem)&&(helmet.getItem()) == Items.IRON_HELMET&&!allowIronHelmet.get())
                            ||((chestPlate!=null&& chestPlate.getItem() !=null&& chestPlate.getItem() instanceof ArmorItem)&&(chestPlate.getItem()) == Items.IRON_CHESTPLATE&&!allowIronChestplate.get())
                            ||((leggings!=null&& leggings.getItem() !=null&& leggings.getItem() instanceof ArmorItem)&&(leggings.getItem()) == Items.IRON_LEGGINGS&&!allowIronLeggings.get())
                            ||((boots!=null&& boots.getItem() !=null&& boots.getItem() instanceof ArmorItem)&&(boots.getItem()) == Items.IRON_BOOTS&&!allowIronBoots.get())
//-----------------------------------1145141919810--------------------------------------------------------
                            //LeatherNoColor
                            ||((((helmet!=null&& helmet.getItem() !=null&& helmet.getItem() instanceof ArmorItem)&&(helmet.getItem()) == Items.LEATHER_HELMET&&!helmet.hasNbt())
                            ||((chestPlate!=null&& chestPlate.getItem() !=null&& chestPlate.getItem() instanceof ArmorItem)&&(chestPlate.getItem()) == Items.LEATHER_CHESTPLATE&&!chestPlate.hasNbt())
                            ||((leggings!=null&& leggings.getItem() !=null&& leggings.getItem() instanceof ArmorItem)&&(leggings.getItem()) == Items.LEATHER_LEGGINGS&&!leggings.hasNbt())
                            ||((boots!=null&& boots.getItem() !=null&& boots.getItem() instanceof ArmorItem)&&(boots.getItem()) == Items.LEATHER_BOOTS&&!boots.hasNbt())
                                )&&removeNoColorLeatherArmor.get())
                ) {
                    isBot = true;
                }
                if (isBot) {
                    if (serverBots.contains(player)) return;
                    serverBots.add(player);
                    if (friend.get()) {
                        Friends.get().add(new Friend(player));
                    }
                    debug("检测到 " + player.getEntityName() + " 为Bot,已添加白名单");
                }
            }
        }
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
            update();
        }

        private void debug(String info) {
        if (debug.get()) {
            info(info);
            }
        }

        @EventHandler
        public void Left(GameLeftEvent event) {
        serverBots.clear();
        }

        @EventHandler
        public void Join(GameJoinedEvent event) {
        serverBots.clear();
        }

    @Override
    public void onActivate() {
        serverBots.clear();
    }

    @Override
    public void onDeactivate() {
        serverBots.clear();
    }

    @EventHandler
        public void EntityRemove(EntityRemovedEvent event) {
            Entity entity = event.entity;
            if (entity instanceof PlayerEntity player) {
                for (PlayerEntity p : serverBots) {
                    if (player == p) {
                        serverBots.remove(player);
                        if (friend.get()) {
                            Friends.get().remove(new Friend(player));
                        }
                        debug(player.getEntityName() +" 已消失,从白名单删除");
                    }
                }
            }
        }


    public ArrayList<PlayerEntity> serverBots = new ArrayList<>();

    public enum Mode {
        NoColorArmor,
        UnusualArmor
    }

    public RemoveAntiBot() {
        super(Categories.Combat,"equals-anti-bot","Prevents KillAura from attacking AntiCheat bots.");
    }
}
