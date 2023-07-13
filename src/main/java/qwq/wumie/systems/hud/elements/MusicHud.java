package qwq.wumie.systems.hud.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.SMesh;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.MeteorIdentifier;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import qwq.wumie.systems.music.MusicManager;
import qwq.wumie.systems.music.objs.music.SongInfoObj;
import qwq.wumie.systems.music.player.PlayMusic;

import java.io.IOException;
import java.io.InputStream;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MusicHud extends HudElement {
    public static final HudElementInfo<MusicHud> INFO = new HudElementInfo<>(Hud.GROUP, "music-hud", "music.", MusicHud::new);

    private final SettingGroup sgA = settings.getDefaultGroup();
    private final SettingGroup sgKey = settings.createGroup("key");
    public static MusicHud INSTANCE;
    public String picUrl;
    public Identifier picTex;
    public MusicInfo info;

    public final Setting<Integer> volume = sgA.add(new IntSetting.Builder()
            .name("volume")
            .defaultValue(80)
            .range(0, 100)
            .sliderRange(0, 100)
            .build()
    );

    public final Setting<Integer> height = sgA.add(new IntSetting.Builder()
            .name("height")
            .defaultValue(100)
            .range(10, Utils.getWindowHeight())
            .sliderRange(10, Utils.getWindowHeight())
            .build()
    );

    public final Setting<Integer> width = sgA.add(new IntSetting.Builder()
            .name("width")
            .defaultValue(Utils.getWindowWidth())
            .range(10, Utils.getWindowWidth())
            .sliderRange(10, Utils.getWindowWidth())
            .build()
    );

    private final Setting<Boolean> onInventory = sgA.add(new BoolSetting.Builder()
            .name("inventory")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> noChat = sgA.add(new BoolSetting.Builder()
            .name("chat")
            .defaultValue(false)
            .build()
    );
    public final Setting<Boolean> debug = sgA.add(new BoolSetting.Builder()
            .name("debug")
            .defaultValue(true)
            .build()
    );
    public final Setting<Boolean> autoPlay = sgA.add(new BoolSetting.Builder()
            .name("circular-play")
            .defaultValue(false)
            .build()
    );

    public final Setting<Boolean> randomPlay = sgA.add(new BoolSetting.Builder()
            .name("random-play")
            .defaultValue(false)
            .visible(autoPlay::get)
            .build()
    );

    public final Setting<Keybind> pauseKey = sgKey.add(new KeybindSetting.Builder()
            .name("pause-key")
            .defaultValue(Keybind.fromKey(GLFW.GLFW_KEY_END))
            .build()
    );

    public final Setting<Keybind> nextKey = sgKey.add(new KeybindSetting.Builder()
            .name("next-key")
            .defaultValue(Keybind.fromKey(GLFW.GLFW_KEY_DOWN))
            .build()
    );

    public final Setting<Keybind> lastKey = sgKey.add(new KeybindSetting.Builder()
            .name("last-key")
            .defaultValue(Keybind.fromKey(GLFW.GLFW_KEY_UP))
            .build()
    );

    public final Setting<Keybind> stopKey = sgKey.add(new KeybindSetting.Builder()
            .name("stop-key")
            .defaultValue(Keybind.fromKey(GLFW.GLFW_KEY_INSERT))
            .build()
    );

    public final Setting<SettingColor> anaColor = sgKey.add(new ColorSetting.Builder()
            .name("ana-color")
            .defaultValue(new SettingColor(255, 255, 255, 200))
            .build()
    );

    public static void loadMusic(String url) {
        if (url == null) return;

        INSTANCE.picUrl = url;
        if (Utils.canUpdate()) INSTANCE.loadPic();
    }

    public static boolean debug() {
        return INSTANCE.isActive() ? INSTANCE.debug.get() : false;
    }

    public static void setInfo(MusicInfo i) {
        INSTANCE.info = i;
    }

    public static void stop() {
        INSTANCE.info = null;
    }

    @Override
    public void tick(HudRenderer renderer) {
        setSize(width.get(), height.get());

        if (autoPlay.get()) {
            if (PlayMusic.getList().isEmpty()) return;
            if (!PlayMusic.playing) {
                if (randomPlay.get()) {
                    SongInfoObj music = PlayMusic.random();
                    if (music == null) {
                        info("歌单不存在歌曲，已关闭autoplay");
                        autoPlay.set(false);
                    } else {
                        PlayMusic.playMusic(music.getID());
                    }
                } else {
                    SongInfoObj music = PlayMusic.next();
                    if (music == null) {
                        info("歌单不存在歌曲，已关闭autoplay");
                        autoPlay.set(false);
                    } else {
                        PlayMusic.playMusic(music.getID());
                    }
                }
            }
        }
        super.tick(renderer);
    }

    public static boolean autoPlay() {
        return INSTANCE.autoPlay.get();
    }

    public static boolean randomPlay() {
        return INSTANCE.randomPlay.get();
    }

    @Override
    public void render(HudRenderer renderer) {
        if (mc.currentScreen instanceof ChatScreen) {
            if (!noChat.get()) {
                return;
            }
        }
        if (mc.currentScreen instanceof InventoryScreen) {
            if (!onInventory.get()) {
                return;
            }
        }

        renderer.post(() -> {
            if (info != null) {
                if (PlayMusic.musicProgress != 0) {
                    gameRender.drawRect(x, y, width.get() * PlayMusic.musicProgress, 2, anaColor.get());
                }
                SMesh.BMesh matrices = new SMesh.BMesh();
                matrices.setMatrix(gameRender.context.getMatrices());
                draw(matrices, info.name, x + 10 + 80, y + 10, 2);
                draw(matrices, info.author, x + 10 + 80, y + 10 + 20, 2);
                draw(matrices, PlayMusic.getInfo(), x + 10 + 80, y + 10 + 20 * 2, 2);
                draw(matrices, PlayMusic.getTimeInfo(), x + 10 + 80, y + 10 + 20 * 3, 2);
                if (picTex != null) {
                    RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                    RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                    renderer.texture(picTex, x + 10, y + 10, 75, 75, Color.WHITE);
                }
            }
        });
        super.render(renderer);
    }

    public void draw(SMesh.BMesh matrices, String message, float x, float y, double scale) {
        matrices.glPushMatrix();
        x += 0.5 * scale;
        y += 0.5 * scale;
        matrices.glScaled(scale, scale, 1);
        gameRender.context.drawText(mc.textRenderer,message,(int)(x / scale), (int) (y / scale), Color.WHITE.toAWTColor().getRGB(),false);
        matrices.glPopMatrix();
    }

    @EventHandler
    public void onKey(KeyEvent event) {
        if ((MusicManager.INSTANCE == null) && !MusicManager.loaded) return;

        if (event.action == KeyAction.Press) {
            if (event.key == MusicHud.INSTANCE.stopKey.get().getValue()) {
                PlayMusic.stopPlay();
            }
            if (event.key == MusicHud.INSTANCE.pauseKey.get().getValue()) {
                PlayMusic.pause(!PlayMusic.paused);
            }
            if (event.key == MusicHud.INSTANCE.nextKey.get().getValue()) {
                if (MusicHud.INSTANCE.randomPlay.get()) {
                    SongInfoObj music = PlayMusic.random();
                    if (music == null) {
                        info("歌单不存在歌曲");
                    } else {
                        PlayMusic.playMusic(music.getID());
                    }
                } else {
                    SongInfoObj music = PlayMusic.next();
                    if (music == null) {
                        info("歌单不存在歌曲");
                    } else {
                        PlayMusic.playMusic(music.getID());
                    }
                }
            }
            if (event.key == MusicHud.INSTANCE.lastKey.get().getValue()) {
                if (MusicHud.INSTANCE.randomPlay.get()) {
                    SongInfoObj music = PlayMusic.random();
                    if (music == null) {
                        info("歌单不存在歌曲");
                    } else {
                        PlayMusic.playMusic(music.getID());
                    }
                } else {
                    SongInfoObj music = PlayMusic.latest();
                    if (music == null) {
                        info("歌单不存在歌曲");
                    } else {
                        PlayMusic.playMusic(music.getID());
                    }
                }
            }
        }
    }

    public void loadPic() {
        try {
            new Thread(() -> {
                try {
                    if (Utils.canUpdate()) {
                        Http.Request b = Http.get(picUrl);
                        InputStream a = b.sendInputStream();
                        if (a != null) {
                            NativeImage image = NativeImage.read(a);
                            NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
                            mc.getTextureManager().registerTexture(picTex, texture);
                        }
                    }
                } catch (Exception e) {
                    error("该音乐没有图标: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            error("该音乐没有图标: " + e.getMessage());
        }
    }

    public static void debug(String message) {
        if (debug()) {
            INSTANCE.info(message);
        }
    }

    public MusicHud() {
        super(INFO);
        INSTANCE = this;
        picTex = new MeteorIdentifier("abc");
        setTitle("Music");
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    public static class MusicInfo {
        public String name;
        public String author;

        public MusicInfo(String name, String author) {
            this.name = name;
            this.author = author;
        }
    }
}
