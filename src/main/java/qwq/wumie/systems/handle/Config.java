package qwq.wumie.systems.handle;

import meteordevelopment.meteorclient.MeteorClient;
import qwq.wumie.systems.websocket.server.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private static final File cfg = new File(MeteorClient.FOLDER,"launch_cfg.txt");
    public static boolean enableViaVersion;

    public static void load() {
        if (!cfg.exists()) {
            try {
                createConfig();
            } catch (FileNotFoundException ignored) {
                enableViaVersion = true;
                return;
            }
        }

        List<String> lines = new ArrayList<>(FileUtils.read(cfg));
        Map<String,String> values = new HashMap<>();
        for (String s : lines) {
            String[] a = s.split(":");
            if (a.length == 2) {
                String k = a[0];
                String v = a[1];
                values.put(k.toLowerCase(),v);
            }
        }

        if (values.containsKey("viaversion")) {
            enableViaVersion = values.get("viaversion").equalsIgnoreCase("true");
        }
    }

    private static void createConfig() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(cfg);
        pw.println("ViaVersion:true");
        pw.close();
    }
}
