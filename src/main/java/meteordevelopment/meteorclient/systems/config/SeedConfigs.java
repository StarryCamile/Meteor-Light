/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.config;

import com.seedfinding.mccore.version.MCVersion;
import meteordevelopment.meteorclient.MeteorClient;
import qwq.wumie.utils.world.seeds.Seed;
import qwq.wumie.utils.world.seeds.Seeds;

import java.io.*;
import java.util.HashMap;

public class SeedConfigs {
    public static File dir = new File(MeteorClient.FOLDER,"seeds.txt");
    public static Seeds seeds = Seeds.get();

    public static HashMap<String, Seed> load() {
        if (!dir.exists()) createNewFile(dir);
        HashMap<String, Seed> seedHashMap = new HashMap<>();
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dir));
            while ((line = reader.readLine()) != null) {
                String[] stuff = line.split(":");
                String worldName = stuff[0];
                Seed seed = new Seed(Long.parseLong(stuff[1]), MCVersion.fromString(stuff[2]),stuff[3]);

                seedHashMap.put(worldName,seed);
            }
            reader.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return seedHashMap;
    }

    public static void save() {
        try {
            final PrintWriter writer = new PrintWriter(new FileWriter(dir));
            /*
            for (Map.Entry<String, Seed> seed : seeds.seeds.entrySet()) {
            writer.println(seed.getKey()+":"+seed.getValue().seed+":"+seed.getValue().version+":"+seed.getValue().ip);
            }*/
            seeds.getSeeds().forEach((name,seed) -> {
                writer.println(name+":"+seed.seed+":"+seed.version+":"+seed.ip);
            });
            writer.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createNewFile(File path,String name) {
        try {
            File f = new File(path,name);
            final PrintWriter writer = new PrintWriter(new FileWriter(f));
            writer.println("");
            writer.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createNewFile(File f) {
        try {
            final PrintWriter writer = new PrintWriter(new FileWriter(f));
            writer.println("");
            writer.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
