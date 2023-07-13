package qwq.wumie.systems.music;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import qwq.wumie.systems.hud.elements.MusicHud;
import qwq.wumie.systems.music.objs.CookieObj;
import qwq.wumie.systems.music.objs.SearchMusicObj;
import qwq.wumie.systems.music.player.APIMain;
import qwq.wumie.systems.music.player.APlayer;
import qwq.wumie.systems.music.player.PlayMusic;
import qwq.wumie.utils.ExChat;
import meteordevelopment.orbit.EventHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class MusicManager extends ExChat {
    public static File folder = new File(MeteorClient.FOLDER,"music");
    public static MusicManager INSTANCE;
    public static APlayer nowPlaying;
    public static CookieObj cookie;
    public static APIMain apiMusic;
    private static File cookieFile;
    public static final Gson gson = new Gson();
    public static boolean loaded;

    public MusicManager() {
        INSTANCE = this;
    }

    public static APIMain getMusicApi() {
        return apiMusic;
    }

    public void start() throws IOException {
        setTitle("Music");
        MeteorClient.EVENT_BUS.subscribe(this);
        nowPlaying = new APlayer();
        if (!folder.exists()) folder.mkdirs();

        if (cookieFile == null) cookieFile = new File(folder, "cookie.json");

        if (!cookieFile.exists()) cookieFile.createNewFile();
        cookie = new CookieObj();
        loadCookie();
        apiMusic = new APIMain();
        PlayMusic.start();

        loaded = true;

        if (MusicHud.INSTANCE == null) MusicHud.INSTANCE = new MusicHud();
    }

    public void loadCookie() throws IOException {
        InputStreamReader reader;
        BufferedReader bf;
        reader = new InputStreamReader(Files.newInputStream(cookieFile.toPath()), StandardCharsets.UTF_8);
        bf = new BufferedReader(reader);
        cookie = new Gson().fromJson(bf, CookieObj.class);
        bf.close();
        reader.close();
        if (cookie == null) {
            cookie = new CookieObj();
            saveCookie();
        }
    }

    public void stop() {
        if (!assertLoaded()) {
            return;
        }

        PlayMusic.stop();
        nowPlaying.closePlayer();
    }

    public static void saveCookie() {
        try {
            String data = new GsonBuilder().setPrettyPrinting().create().toJson(cookie);
            FileOutputStream out = new FileOutputStream(cookieFile);
            OutputStreamWriter write = new OutputStreamWriter(
                    out, StandardCharsets.UTF_8);
            write.write(data);
            write.close();
        } catch (Exception e) {
            INSTANCE.warning("Cookie文件保存错误");
            e.printStackTrace();
        }
    }

    public static List<SearchMusicObj> searchMusic(String... names) {
        return apiMusic.search(names,true).resData;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (!loaded) {
            return;
        }

        nowPlaying.tick();
    }

    public static int getVolume() {
        return MusicHud.INSTANCE.volume.get();
    }

    public static class Song {
        public String url;
        public String id;

        public Song(String url, String id) {
            this.url = url;
            this.id = id;
        }
    }

    public boolean assertLoaded() {
        if (!loaded) error("Music disabled.");

        return loaded;
    }
}
