package qwq.wumie.systems.modules.player;

import it.unimi.dsi.fastutil.chars.Char2CharArrayMap;
import it.unimi.dsi.fastutil.chars.Char2CharMap;
import kotlin.reflect.KVariance;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.utils.StarscriptTextBoxRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.starscript.Script;
import meteordevelopment.starscript.compiler.Compiler;
import meteordevelopment.starscript.compiler.Parser;
import meteordevelopment.starscript.utils.StarscriptError;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

import java.util.List;
import java.util.Random;

public class Welcomer extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgWelcome = settings.createGroup("Welcome");
    private final SettingGroup sgGoodbye = settings.createGroup("Goodbye");

    private final Setting<FriendMode> friendsMode = sgGeneral.add(new EnumSetting.Builder<FriendMode>()
        .name("friends-mode")
        .description("How friends are greeted.")
        .defaultValue(FriendMode.Both)
        .build()
    );

    private final Setting<Boolean> smallCaps = sgGeneral.add(new BoolSetting.Builder()
        .name("small-caps")
        .description("Sends all messages with small caps.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> randomMsg = sgGeneral.add(new BoolSetting.Builder()
        .name("random")
        .description("Sends random messages every kill or pop.")
        .defaultValue(true)
        .build()
    );

    // Welcome

    private final Setting<Boolean> welcome = sgWelcome.add(new BoolSetting.Builder()
        .name("welcome")
        .description("Sends messages in the chat when a player joins.")
        .defaultValue(true)
        .build()
    );

    private final Setting<String> welcomeString = sgWelcome.add(new StringSetting.Builder()
        .name("welcome-message")
        .description("The message to send when a player joins.")
        .defaultValue("welcome !player!")
        .visible(() -> !randomMsg.get() && welcome.get())
        .renderer(StarscriptTextBoxRenderer.class)
        .build()
    );

    private final Setting<List<String>> welcomeMessages = sgWelcome.add(new StringListSetting.Builder()
        .name("welcome-messages")
        .description("The random messages to send when a player joins.")
        .defaultValue(List.of("welcome !player!", "evening !player!", "hello !player!"))
        .visible(() -> randomMsg.get() && welcome.get())
        .renderer(StarscriptTextBoxRenderer.class)
        .build()
    );

    private final Setting<Integer> welcomeDelay = sgWelcome.add(new IntSetting.Builder()
        .name("welcome-delay")
        .description("How long to wait in ticks before sending another welcome message.")
        .defaultValue(20)
        .min(0)
        .visible(welcome::get)
        .build()
    );

    private final Char2CharMap SMALL_CAPS = new Char2CharArrayMap();

    private Random random;
    private int welcomeTimer;
    private int leaveTimer;

    public Welcomer() {
        super(Categories.Player, "welcomer", "Sends a chat message when a player joins or leaves.");

        String[] a = "abcdefghijklmnopqrstuvwxyz".split("");
        String[] b = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴩqʀꜱᴛᴜᴠᴡxyᴢ".split("");
        for (int i = 0; i < a.length; i++) SMALL_CAPS.put(a[i].charAt(0), b[i].charAt(0));
    }

    @Override
    public void onActivate() {
        random = new Random();
        welcomeTimer = 0;
        leaveTimer = 0;
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof PlayerListS2CPacket packet && mc != null && mc.world != null && mc.player != null) {
            for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
                if (entry.profile() != null && entry.profile().getName() != null && isFriend(entry.profile().getName())) {
                    if (packet.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER) && welcome.get() && (welcomeTimer >= welcomeDelay.get() || welcomeDelay.get() == 0)) {
                        sendMsg(apply(entry.profile().getName(), randomMsg.get() ? welcomeMessages.get() : List.of(welcomeString.get())));
                        welcomeTimer = 0;
                    }
                }
            }
        }
    }

    @EventHandler
    private void onPostTick(TickEvent.Post event) {
        welcomeTimer++;
        leaveTimer++;
    }

    // Messaging

    private void sendMsg(String string) {
        if (string != null) {
            StringBuilder builder = new StringBuilder();

            if (smallCaps.get()) {
                for (char ch : string.toCharArray()) {
                    if (SMALL_CAPS.containsKey(ch)) builder.append(SMALL_CAPS.get(ch));
                    else builder.append(ch);
                }
            }

            sendChatMessage(smallCaps.get() && !builder.isEmpty() ? builder.toString() : string);
        }
    }

    // Utils

    private static Script compile(String script) {
        if (script == null) return null;
        Parser.Result result = Parser.parse(script);
        if (result.hasErrors()) {
            MeteorStarscript.printChatError(result.errors.get(0));
            return null;
        }
        return Compiler.compile(result);
    }

    private String apply(String player, List<String> strings) {
        Script script = compile((strings.get(random.nextInt(strings.size())).replace("!player!", player)));
        if (script == null) return "An error occurred";
        try {
            var section = MeteorStarscript.ss.run(script);
            return section.text;
        } catch (StarscriptError e) {
            MeteorStarscript.printChatError(e);
            return "An error occurred";
        }
    }

    private boolean isFriend(String name) {
        boolean friended = false;

        for (Friend friend : Friends.get()) {
            if (friend.name.contains(name)) {
                friended = true;
                break;
            }
        }

        if (friendsMode.get() == FriendMode.Only && !friended || friendsMode.get() == FriendMode.Ignore && friended) return false;
        else if (friendsMode.get() == FriendMode.Both) return true;
        return true;
    }

    // Constants

    public enum FriendMode {
        Ignore,
        Only,
        Both
    }
}
