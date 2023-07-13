/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.modules.combat;

import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.PlayerRespawnEvent;
import meteordevelopment.meteorclient.events.world.UpdateEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import qwq.wumie.utils.time.MSTimer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class MeteorAntiBot extends Module {
    private boolean inCombat = false;
    private MSTimer lastAttackTimer = new MSTimer();
    private LivingEntity target;
    private List<LivingEntity> attackedEntityList = new ArrayList<>();
    private List<LivingEntity> spawnInCombatList = new ArrayList<>();
    private List<LivingEntity> removedEntities = new ArrayList<>();
    private List<Integer> idAttackedEntityList = new ArrayList<>();
    private List<Integer> idSpawnInCombatList = new ArrayList<>();
    private List<Integer> idRemovedEntities = new ArrayList<>();


    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> useEntityID = sgGeneral.add(new BoolSetting.Builder()
        .name("use-entity-id")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> spawnInCombat = sgGeneral.add(new BoolSetting.Builder()
        .name("spawn-in-combat")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> notification = sgGeneral.add(new BoolSetting.Builder()
        .name("notification")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> spawnClear = sgGeneral.add(new BoolSetting.Builder()
            .name("spawn-clear-entities")
            .defaultValue(true)
            .build()
    );

    public boolean isBot(PlayerEntity entity) {
        if (!useEntityID.get()) {
            if (spawnInCombat.get() && spawnInCombatList.contains(entity)) {
                return true;
            }
        }

        if (useEntityID.get()) { return entityIdIsBot(entity);}
        return false;
    }

    public boolean entityIdIsBot(PlayerEntity entity) {
        return spawnInCombat.get() && idSpawnInCombatList.contains(entity.getId());
    }

    public MeteorAntiBot() {
        super(Categories.Combat,"meteor-anti-bot", "bypass some server bots");
    }

    @EventHandler
    public void onAddedEntity(EntityAddedEvent event) {
        if (event.entity instanceof PlayerEntity player) {
            if (inCombat) {
                if (notification.get()) info("Added a new bot: "+player.getName().getString());
                spawnInCombatList.add(player);
                idSpawnInCombatList.add(player.getId());
            }
        }
    }

    @EventHandler
    public void onRemoveEntity(EntityRemovedEvent event) {
        if (event.entity instanceof PlayerEntity entity) {
            removedEntities.add(entity);
            idRemovedEntities.add(entity.getId());
        }
    }

    @Override
    public void onActivate() {
        clearAll();
        super.onActivate();
    }

    private void clearAll() {
        removedEntities.clear();
        attackedEntityList.clear();
        spawnInCombatList.clear();
        idRemovedEntities.clear();
        idAttackedEntityList.clear();
        idSpawnInCombatList.clear();
    }

    @EventHandler
    private void onUpdate(UpdateEvent event) {
        if (mc.player == null) return;

        if (!lastAttackTimer.hasTimePassed(1000)) {
            inCombat = true;
            return;
        }

        if (target != null) {
            if (mc.player.distanceTo(target) > 7 || !inCombat || target.isDead()) {
                target = null;
            } else {
                inCombat = true;
            }
        }
    }

    @EventHandler
    public void onAttack(AttackEntityEvent event) {
        Entity entity = event.entity;

        if (entity instanceof LivingEntity livingTarget) {
            this.target = livingTarget;
            if (!attackedEntityList.contains(livingTarget)) {
                attackedEntityList.add(livingTarget);
            }
            if (!idAttackedEntityList.contains(livingTarget.getId())) {
                idAttackedEntityList.add(livingTarget.getId());
            }
        }
        lastAttackTimer.reset();
    }

    @EventHandler
    private void onWorld(PlayerRespawnEvent event) {
        if (spawnClear.get()) {
            onLeft(null);
        }
    }

    @EventHandler
    public void onLeft(GameLeftEvent event) {
        inCombat = false;
        target = null;
        clearAll();
    }
    @EventHandler
    public void onJoin(GameJoinedEvent event) {
        inCombat = false;
        target = null;
        clearAll();
    }

    @Override
    public String getInfoString() {
        return (inCombat) ? "InCombat" : "";
    }
}
