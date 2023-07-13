/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.utils.world.seeds;

import kaptainwutax.seedcrackerX.api.SeedCrackerAPI;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

public class SeedCrackerEP implements SeedCrackerAPI {
    @Override
    public void pushWorldSeed(long seed) {
        Seeds.get().setSeed(String.format("%d", seed));
        ChatUtils.infoPrefix("Seed", "Added seed from SeedCrackerX");
    }
}
