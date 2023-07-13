/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.addons;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import qwq.wumie.systems.handle.MainHandler;

import java.util.ArrayList;
import java.util.List;

public class AddonManager {
    public static final List<MeteorAddon> ADDONS = new ArrayList<>();

    public static void init() {
        // Meteor pseudo addon
        {
            MeteorClient.ADDON = new MeteorAddon() {
                @Override
                public void onInitialize() {}

                @Override
                public String getPackage() {
                    return "meteordevelopment.meteorclient";
                }

                @Override
                public String getWebsite() {
                    return "https://meteorclient.com";
                }

                @Override
                public GithubRepo getRepo() {
                    return new GithubRepo("MeteorDevelopment", "meteor-client");
                }

                @Override
                public String getCommit() {
                    String commit = MeteorClient.MOD_META.getCustomValue(MeteorClient.MOD_ID + ":commit").getAsString();
                    return commit.isEmpty() ? null : commit;
                }
            };

            MainHandler.ADDON = new MeteorAddon() {
                @Override
                public void onInitialize() {}

                @Override
                public String getPackage() {
                    return "qwq.wumie";
                }

                @Override
                public String getWebsite() {
                    return "https://null.ptr";
                }

                @Override
                public GithubRepo getRepo() {
                    return new GithubRepo("ImWuMie", "meteor-light");
                }

                @Override
                public String getCommit() {
                    return "github.com/ImWuMie";
                }
            };

            ModMetadata metadata = FabricLoader.getInstance().getModContainer(MeteorClient.MOD_ID).get().getMetadata();

            MainHandler.ADDON.name = "meteor-light";
            MainHandler.ADDON.authors = new String[] {"WuMie"};
            MainHandler.ADDON.color.set(new Color(10,10,255,255));

            MeteorClient.ADDON.name = metadata.getName();
            MeteorClient.ADDON.authors = new String[metadata.getAuthors().size()];
            if (metadata.containsCustomValue(MeteorClient.MOD_ID + ":color")) {
                MeteorClient.ADDON.color.parse(metadata.getCustomValue(MeteorClient.MOD_ID + ":color").getAsString());
            }

            int i = 0;
            for (Person author : metadata.getAuthors()) {
                MeteorClient.ADDON.authors[i++] = author.getName();
            }
        }

        // Addons
        for (EntrypointContainer<MeteorAddon> entrypoint : FabricLoader.getInstance().getEntrypointContainers("meteor", MeteorAddon.class)) {
            ModMetadata metadata = entrypoint.getProvider().getMetadata();
            MeteorAddon addon = entrypoint.getEntrypoint();

            addon.name = metadata.getName();

            if (metadata.getAuthors().isEmpty()) throw new RuntimeException("Addon %s requires at least 1 author to be defined in it's fabric.mod.json. See https://fabricmc.net/wiki/documentation:fabric_mod_json_spec".formatted(addon.name));
            addon.authors = new String[metadata.getAuthors().size()];

            if (metadata.containsCustomValue(MeteorClient.MOD_ID + ":color")) {
                addon.color.parse(metadata.getCustomValue(MeteorClient.MOD_ID + ":color").getAsString());
            }

            int i = 0;
            for (Person author : metadata.getAuthors()) {
                addon.authors[i++] = author.getName();
            }

            ADDONS.add(addon);
        }
    }
}
