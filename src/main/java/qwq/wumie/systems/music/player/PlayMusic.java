package qwq.wumie.systems.music.player;

import qwq.wumie.utils.misc.RandomUtils;
import qwq.wumie.systems.hud.elements.MusicHud;
import qwq.wumie.systems.music.MusicManager;
import qwq.wumie.systems.music.decoder.Bitstream;
import qwq.wumie.systems.music.decoder.Header;
import qwq.wumie.systems.music.objs.music.SongInfoObj;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static qwq.wumie.systems.music.MusicManager.apiMusic;
import static qwq.wumie.systems.music.MusicManager.nowPlaying;

public class PlayMusic {
    /**
     * 播放列表
     */
    public static final List<SongInfoObj> playList = new CopyOnWriteArrayList<>();
    /**
     * 当前歌曲信息
     */
    public static SongInfoObj nowPlayMusic;
    public static int nowPlayMusicPos;
    public static int musicMaxTime;
    public static int musicNowTime;
    public static double musicProgress;
    public static boolean playing;
    public static boolean paused;

    /**
     * 当前歌词信息
     */
    public static LyricSave lyric;
    /**
     * 错误次数
     */
    public static int error;
    private static boolean isRun;

    /**
     * 停止歌曲逻辑
     */
    public static void stop() {
        PlayMusic.clear();
        isRun = false;
    }

    public static SongInfoObj next() {
        if (!assertMusicManager()) {
            return null;
        }

        if (playList.isEmpty()) return null;
        if (playList.size() == 1) return playList.get(0);
        if (nowPlayMusicPos == -1) return playList.get(0);
        int next = nowPlayMusicPos++;

        if (next >= playList.size()) {
            next = 0;
        }
        return playList.get(next);
    }

    public static SongInfoObj latest() {
        if (!assertMusicManager()) {
            return null;
        }

        if (playList.isEmpty()) return null;
        if (playList.size() == 1) return playList.get(0);
        if (nowPlayMusicPos == -1) return playList.get(0);
        int next = nowPlayMusicPos--;

        if (next < 0) {
            next = getSize();
        }
        return playList.get(next);
    }

    public static SongInfoObj random() {
        if (!assertMusicManager()) {
            return null;
        }

        if (playList.isEmpty()) return null;
        if (playList.size() == 1) return playList.get(0);
        if (nowPlayMusicPos == -1) return playList.get(0);
        int next = RandomUtils.nextInt(0, playList.size());
        if (!(next < playList.size())) {
            next = RandomUtils.nextInt(0, playList.size());
        }
        return playList.get(next);
    }

    public static SongInfoObj playMusic(String id) {
        if (!assertMusicManager()) {
            return null;
        }

        musicNowTime = 0;
        musicMaxTime = 0;
        if (playing || nowPlayMusic != null || paused) {
            stopPlay();
        }
        String url = apiMusic.getPlayUrl(id);
        nowPlayMusic = apiMusic.getMusic(id, true);
        if (nowPlayMusic == null) {
            playing = false;
            return null;
        }
        nowPlayMusicPos = getMusicPos(id);
        if (MusicHud.INSTANCE != null) {
            MusicHud.loadMusic(nowPlayMusic.getPicUrl());
            MusicHud.setInfo(new MusicHud.MusicInfo(nowPlayMusic.getName(), nowPlayMusic.getAuthor()));
        }
        nowPlaying.setMusic(url);
        musicMaxTime = getTime(url);
        MusicHud.debug("正在播放" + nowPlayMusic.getName());
        musicProgress = 0;
        playing = true;
        return nowPlayMusic;
    }

    public static void pause(boolean b) {
        if (!assertMusicManager()) {
            return;
        }

        paused = b;
        MusicManager.INSTANCE.info("已" + ((paused) ? "停止" : "继续") + "播放");
    }

    public static void stopPlay() {
        if (!assertMusicManager()) {
            return;
        }

        playing = false;
        nowPlayMusic = null;
        paused = false;
        musicNowTime = 0;
        musicMaxTime = 0;
        nowPlaying.closePlayer();

        if (MusicHud.INSTANCE != null) MusicHud.setInfo(null);

        MusicHud.debug("已终止音乐播放");
    }

    public static String getInfo() {
        return playing ? paused ? "Paused" : "Playing" : "Stopped";
    }

    public static String getTimeInfo() {
        int s = musicNowTime / 1000;
        int m = s / 60;
        if ((m * 60) > s) {
            m = m - 1;
        }
        int M = s - m * 60;
        int mS = musicMaxTime / 1000;
        int mM = mS / 60;
        if ((mM * 60) > mS) {
            mM = mM - 1;
        }
        int MM = mS - mM * 60;
        String ma = (M + "").length() == "1".length() ? "0" + M : M + "";
        String mA = (MM + "").length() == "1".length() ? "0" + MM : MM + "";
        return m + ":" + ma + "/" + mM + ":" + mA;
    }

    public static SongInfoObj playMusic(SongInfoObj id) {
        return playMusic(id.getID());
    }

    /**
     * 开始歌曲逻辑
     */
    public static void start() {
        Thread addT = new Thread(PlayMusic::task, "music_list");
        isRun = true;
        addT.start();
    }

    private static void task() {
        while (isRun) {
            try {
                if (playing) {
                    if (musicNowTime >= musicMaxTime) {
                        if (!paused) {
                            stopPlay();
                            if (MusicHud.INSTANCE != null) {
                                if (MusicHud.autoPlay() && !PlayMusic.getList().isEmpty()) {
                                    if (MusicHud.randomPlay()) {
                                        SongInfoObj music = PlayMusic.random();
                                        if (music == null) {
                                            MusicHud.debug("歌单不存在歌曲");
                                        } else {
                                            PlayMusic.playMusic(music.getID());
                                        }
                                    } else {
                                        SongInfoObj music = PlayMusic.next();
                                        if (music == null) {
                                            MusicHud.debug("歌单不存在歌曲");
                                        } else {
                                            PlayMusic.playMusic(music.getID());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (paused) {
                        if (!nowPlaying.isClose()) nowPlaying.closePlayer();
                    } else if (nowPlaying.isClose() && playing) {
                        nowPlaying.setMusic(apiMusic.getPlayUrl(nowPlayMusic.getID()));
                        nowPlaying.set(musicNowTime);
                    }

                    if (!paused) musicNowTime = nowPlaying.getTime();

                    if (musicNowTime != 0 && musicMaxTime != 0) musicProgress = musicNowTime / musicMaxTime;
                }
                Thread.sleep(10);
            } catch (Exception e) {
                MusicManager.INSTANCE.warning("歌曲处理出现问题");
                e.printStackTrace();
            }
        }
    }

    public static int getMusicPos(String id) {
        for (int i = 0; i < playList.size(); i++) {
            SongInfoObj music = playList.get(i);
            if (music.getID().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return -1;
    }

    public static void addMusic(String id, boolean debug) {
        if (isHave(id))
            return;

        if (debug) MusicManager.INSTANCE.info("正在解析歌曲");
        try {
            SongInfoObj info = apiMusic.getMusic(id, true);
            if (info == null) {
                String data = "无法播放歌曲%MusicID%可能该歌曲为VIP歌曲";
                if (debug) MusicManager.INSTANCE.info(data.replace("%MusicID%", id));
                return;
            }
            if (info.getLength() / 1000 > 600) {
                if (debug) MusicManager.INSTANCE.info("音乐长度过长");
                return;
            }
            playList.add(info);
            String data = "音乐列表添加%MusicName% | %MusicAuthor% | %MusicAl% | %MusicAlia%";
            data = data.replace("%MusicName%", info.getName())
                    .replace("%MusicAuthor%", info.getAuthor())
                    .replace("%MusicAl%", info.getAl())
                    .replace("%MusicAlia%", info.getAlia());

            if (debug) MusicManager.INSTANCE.info(data);

            error = 0;
        } catch (Exception e) {
            MusicManager.INSTANCE.warning("歌曲信息解析错误");
            e.printStackTrace();
        }
    }

    public static void addMusic(String id) {
        addMusic(id, true);
    }

    /**
     * 获取播放列表长度
     *
     * @return 长度
     */
    public static int getSize() {
        return playList.size();
    }

    /**
     * 获取当前播放列表
     *
     * @return 播放列表
     */
    public static List<SongInfoObj> getList() {
        return new ArrayList<>(playList);
    }

    /**
     * 清理播放列表
     */
    public static void clear() {
        playList.clear();
    }

    /**
     * 从播放列表删除
     *
     * @param index 标号
     * @return 结果
     */
    public static SongInfoObj remove(int index) {
        return playList.remove(index);
    }

    /**
     * 是否在播放列表中
     *
     * @param id 音乐ID
     * @return 结果
     */
    public static boolean isHave(String id) {
        if (nowPlayMusic != null && nowPlayMusic.getID().equalsIgnoreCase(id))
            return true;
        for (SongInfoObj item : playList) {
            if (item.getID().equalsIgnoreCase(id))
                return true;
        }
        return false;
    }

    private static int getTime(String url) {
        try {
            URL urlfile = new URL(url);
            URLConnection con = urlfile.openConnection();
            int b = con.getContentLength();
            BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
            Bitstream bt = new Bitstream(bis);
            Header h = bt.readFrame();
            int le = 6000000;
            if (h == null) {
                MusicManager.INSTANCE.warning("未知音乐类型");
            } else {
                le = (int) h.total_ms(b);
            }
            return le;
        } catch (Exception e) {
            MusicManager.INSTANCE.warning("歌曲信息解析错误");
            e.printStackTrace();
        }
        return -1;
    }

    private static boolean assertMusicManager() {
        return MusicManager.INSTANCE != null && MusicManager.INSTANCE.assertLoaded();
    }
}

