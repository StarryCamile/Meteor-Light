package qwq.wumie.version.meteor051;

import kaptainwutax.seedcrackerX.api.SeedCrackerAPI;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.util.ArrayList;

import static meteordevelopment.meteorclient.MeteorClient.FOLDER;

public class MeteorClientPlus {
    public static String clientName = "Meteor-Light";
    public static String clientVersion = "2.0.1";
    public static boolean disableShaders = false;
    public static boolean vanillaFont = false;
    public static boolean DEVMODE = true;
    public static MinecraftClient client;
    public static final ArrayList<SeedCrackerAPI> entrypoints = new ArrayList<>();
    public static final File SOUNDS_FOLODER = new File(FOLDER,"sounds");
}
