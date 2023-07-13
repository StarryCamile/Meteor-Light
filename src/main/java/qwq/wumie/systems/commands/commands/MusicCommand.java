package qwq.wumie.systems.commands.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.command.CommandSource;
import qwq.wumie.systems.music.MusicManager;
import qwq.wumie.systems.music.objs.SearchMusicObj;
import qwq.wumie.systems.music.objs.music.SongInfoObj;
import qwq.wumie.systems.music.player.PlayMusic;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class MusicCommand extends Command {
    public List<SearchMusicObj> currentSearch = new ArrayList<>();
    public boolean canChoose;
    public static String phone;
    public static String code;
    public boolean debug;

    public MusicCommand() {
        super("music", "*** now playing ***", "music");
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("get").then(argument("name", StringArgumentType.string()).executes(context -> {
            String name = StringArgumentType.getString(context, "name");
            this.currentSearch = MusicManager.searchMusic(name);
            if (currentSearch == null) {
                info("未找到名为" + name + "的歌曲");
                return SINGLE_SUCCESS;
            }
            if (currentSearch.isEmpty()) {
                info("未找到名为" + name + "的歌曲");
            } else {
                info("----------------Search----------------(" + currentSearch.size() + ")");
                for (int i = 0; i < currentSearch.size(); i++) {
                    SearchMusicObj music = currentSearch.get(i);

                    String debugString = "";
                    if (debug) {
                        debugString = " | " + MusicManager.apiMusic.getPlayUrl(music.id);
                    }
                    info(i + ": " + music.name + " |by: " + music.author + debugString);
                }
                info("----------------Search------------------------");
                canChoose = true;
            }
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("download").then(argument("id", IntegerArgumentType.integer(0, 29)).executes(context -> {
            if (canChoose) {
                int num = IntegerArgumentType.getInteger(context, "id");
                if (num > currentSearch.size()) {
                    info(num + "超出索引范围");
                    return SINGLE_SUCCESS;
                } else {
                    if (currentSearch.get(num) != null) {
                        SearchMusicObj music = currentSearch.get(num);

                        if (MusicManager.apiMusic.getPlayUrl(music.id) == null) {
                            info("该歌曲" + music.name + "没有解析到链接，无法进行下载");
                        } else {
                            String url = MusicManager.apiMusic.getPlayUrl(music.id);

                            new Thread(() -> {
                                File folder = new File(MusicManager.folder, "downloads");
                                if (!folder.exists()) folder.mkdirs();

                                File f = new File(folder, music.name + "-" + music.author + ".mp3");
                                downloadFile(url, f);
                                info("已下载完成");
                            }).start();


                            if (debug) {
                                info("Debug: Url-> " + url);
                            }
                        }
                    }
                }
            } else {
                info("你还未搜索歌曲，使用music get进行搜索");
                return SINGLE_SUCCESS;
            }
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("select").then(argument("id", IntegerArgumentType.integer(0, 29)).executes(context -> {
            if (canChoose) {
                int num = IntegerArgumentType.getInteger(context, "id");
                if (num > currentSearch.size()) {
                    info(num + "超出索引范围");
                    return SINGLE_SUCCESS;
                } else {
                    if (currentSearch.get(num) != null) {
                        SearchMusicObj music = currentSearch.get(num);

                        if (MusicManager.apiMusic.getPlayUrl(music.id) == null) {
                            info("该歌曲" + music.name + "没有解析到链接，无法进行播放");
                        } else {
                            SongInfoObj m = PlayMusic.playMusic(music.id);
                            if (debug && m != null) {
                                info("Debug: Url-> " + m.getUrl());
                            }
                        }
                    }
                }
            } else {
                info("你还未搜索歌曲，使用music get进行搜索");
                return SINGLE_SUCCESS;
            }
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("play").then(argument("id", IntegerArgumentType.integer(0, 29)).executes(context -> {
            if (PlayMusic.getSize() > 0) {
                int num = IntegerArgumentType.getInteger(context, "id");
                if (num > PlayMusic.getSize()) {
                    info(num + "超出索引范围");
                    return SINGLE_SUCCESS;
                } else {
                    if (PlayMusic.playList.get(num) != null) {
                        SongInfoObj music = PlayMusic.playList.get(num);
                        PlayMusic.playMusic(music.getID());
                    }
                }
            } else {
                info("你还未搜索歌曲，使用music get进行搜索");
                return SINGLE_SUCCESS;
            }
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("add").then(argument("id", IntegerArgumentType.integer(0, 29)).executes(context -> {
            if (canChoose) {
                int num = IntegerArgumentType.getInteger(context, "id");
                if (num > currentSearch.size()) {
                    info(num + "超出索引范围");
                    return SINGLE_SUCCESS;
                }
                if (currentSearch.get(num) != null) {
                    SearchMusicObj music = currentSearch.get(num);
                    PlayMusic.addMusic(music.id);
                }
            } else {
                info("你还未搜索歌曲，使用music get进行搜索");
                return SINGLE_SUCCESS;
            }

            return SINGLE_SUCCESS;
        })));

        builder.then(literal("addAll").executes(context -> {
            if (canChoose) {
                currentSearch.forEach((music) -> {
                    PlayMusic.addMusic(music.id, false);
                });
            } else {
                info("你还未搜索歌曲，使用music get进行搜索");
                return SINGLE_SUCCESS;
            }
            info("已把搜索的所有歌曲添加到播放列表");
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("del").then(argument("id", IntegerArgumentType.integer(0)).executes(context -> {
            if (PlayMusic.getSize() > 0) {
                int num = IntegerArgumentType.getInteger(context, "id");
                if (num > PlayMusic.getSize()) {
                    info(num + "超出索引范围");
                    return SINGLE_SUCCESS;
                } else {
                    if (PlayMusic.playList.get(num) != null) {
                        SongInfoObj music = PlayMusic.playList.get(num);
                        PlayMusic.playList.remove(music);
                        info("已移除" + music.getName());
                    }
                }
            } else {
                info("你还未搜索歌曲，使用music get进行搜索");
                return SINGLE_SUCCESS;
            }

            return SINGLE_SUCCESS;
        })));

        builder.then(literal("login").then(argument("phone", StringArgumentType.string()).executes(context -> {
            phone = StringArgumentType.getString(context, "phone");

            String sb = "*".repeat(Math.max(0, phone.length() - 2));
            info("设置手机号为" + phone.substring(0, 2) + sb);

            MusicManager.apiMusic.sendCode();
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("captcha").then(argument("code", StringArgumentType.string()).executes(context -> {
            code = StringArgumentType.getString(context, "code");

            info("设置验证码为" + code);

            MusicManager.apiMusic.login(code);
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("debug").then(argument("enable", BoolArgumentType.bool()).executes(context -> {
            debug = BoolArgumentType.getBool(context, "enable");

            info("设置Debug为" + debug);
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("stop").executes(context -> {
            PlayMusic.stopPlay();
            info("已停止播放");
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("pause").then(argument("pa", BoolArgumentType.bool()).executes(context -> {
            PlayMusic.pause(BoolArgumentType.getBool(context, "pa"));
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("last").executes(context -> {
            SongInfoObj music = PlayMusic.latest();
            if (music == null) {
                info("歌单不存在歌曲");
            } else {
                PlayMusic.playMusic(music.getID());
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("next").executes(context -> {
            SongInfoObj music = PlayMusic.next();
            if (music == null) {
                info("歌单不存在歌曲");
            } else {
                PlayMusic.playMusic(music.getID());
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("random").executes(context -> {
            SongInfoObj music = PlayMusic.random();
            if (music == null) {
                info("歌单不存在歌曲");
            } else {
                PlayMusic.playMusic(music.getID());
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("playlist").executes(context -> {
            info("-------------PlayList-----------(" + PlayMusic.getSize() + ")");
            for (int i = 0; i < PlayMusic.getSize(); i++) {
                SongInfoObj song = PlayMusic.getList().get(i);

                info(i + ": " + song.getName() + "-> | by: " + song.getAuthor());
            }
            info("-------------PlayList----------------");
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("clear").executes(context -> {
            PlayMusic.playList.clear();
            info("已清空播放列表");
            return SINGLE_SUCCESS;
        }));
    }

    public void downloadFile(String str, File out) {
        try {
            URL url = new URL(str);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            InputStream inputStream = conn.getInputStream();
            byte[] getData = readInputStream(inputStream);
            FileOutputStream fos = new FileOutputStream(out);
            fos.write(getData);
            fos.close();
            inputStream.close();
        } catch (Exception e) {
            InputStream inputStream;
            try {
                inputStream = Http.get(str).sendInputStream();
            } catch (Exception e1) {
                inputStream = Http.post(str).sendInputStream();
            }
            try {
                if (inputStream != null) {
                    byte[] getData = readInputStream(inputStream);
                    FileOutputStream fos = new FileOutputStream(out);
                    fos.write(getData);
                    fos.close();
                    inputStream.close();
                }
            } catch (IOException ex) {
                try {
                    Files.copy(inputStream,out.toPath());
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            }
        }
    }


    private byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4 * 1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}
