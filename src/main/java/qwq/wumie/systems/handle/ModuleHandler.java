package qwq.wumie.systems.handle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import qwq.wumie.systems.modules.combat.TNTAura;
import meteordevelopment.meteorclient.systems.modules.misc.AutoMountBypassDupe;
import qwq.wumie.systems.modules.inject.Injects;
import qwq.wumie.systems.modules.lemon.LemonModule;
import qwq.wumie.systems.modules.movement.EntityFly;
import qwq.wumie.systems.modules.render.NewChunks;
import qwq.wumie.systems.modules.crash.*;
import qwq.wumie.systems.modules.misc.*;
import qwq.wumie.systems.modules.movement.*;
import qwq.wumie.systems.modules.player.*;
import qwq.wumie.systems.modules.render.search.search.Search;
import qwq.wumie.systems.modules.world.*;
import qwq.wumie.systems.modules.render.*;
import qwq.wumie.systems.modules.combat.*;

public class ModuleHandler {
    private static Modules mods;
    private static final Injects mI = new Injects();

    public static void loadInjects() {
        mI.init();
    }

    public static void postInjects() {
        mI.postInit();
    }

    public static void initModules(Modules m) {
        mods = m;

        initCombat();
        initCrash();
        initMisc();
        initMovement();
        initPlayer();
        initRender();
        initWorld();
        LemonModule.INSTANCE.onInit(m);
    }

    private static void add(Module m) {
        if (mods != null) {
            mods.add(m);
        }
    }

    private static void initCombat() {
        add(new AntiCrystalPhase());
        add(new ShieldBypass());
        add(new AntiCrystal());
        add(new ArrowDmg());
        add(new AutoPot());
        add(new AutoSoup());
        add(new BowBomb());
        add(new InfiniteAura());
        //add(new TNTAura());
        add(new MeteorAntiBot());
        add(new AutoGriffer());
        add(new PacketHoleFill());
    }

    private static void initMovement() {
        add(new AntiLagBack());
        add(new AntiPistonPush());
        add(new StepPlus());
        add(new PacketFly());
        add(new Jetpack());
        add(new Phase());
        add(new EPlayerTeleport());
        add(new BedrockWalk());
        add(new SleepWalker());
        add(new MoonGravity());
        add(new EntityFly());
    }

    private static void initRender() {
        add(new BobView());
        //add(new BetterAdvancement());  bugs
        add(new StorageViewer());
        //add(new ESPPlus()); idk
        add(new BurrowESP());
        add(new Rendering());
        add(new Search()); // TODO: Added
        add(new CalcScreen());
        //add(new MotionBlur()); TODO: Removed
        add(new NewChunks());
    }

    private static void initPlayer() {
        add(new AutoLogin());
        add(new AutoCraft());
        add(new PacketRightClick());
        add(new OldHitting());
        add(new Freeze());
        add(new Welcomer());
        add(new InstaMine());
        //add(new AutoEz());
        add(new AutoInteract());
        add(new InstaMinePlus());
        add(new ChorusPredict());
        add(new HoleBot());
        add(new DeathAnimations());
        add(new GamemodeNotifier());
        add(new ItemRelease());
        add(new NoSwing());
        add(new PortalGodMode());
        add(new AutoGriffer());
        add(new ColorSigns());
    }

    private static void initMisc() {
        add(new VillagerRoller());
        add(new Disabler());
        add(new PlayerCheck());
        add(new AntiCrash());
        add(new NewChatBot());
        add(new AutoCalc());
        add(new NotebotI());
        add(new PayloadBackDoor());
        add(new 测你码());
        add(new Socket());
        add(new SuperSpammer());
        add(new ChatEncryption());
        add(new NoBlockTrace());
        add(new NoCollision());
        add(new AutoMountBypassDupe());
        add(new PacketLogger());
        add(new PingSpoof());
        add(new Placeholders());
        add(new ChatBot());
        //add(new MultiTask());
        add(new AntiBookKick());
        //add(new AutoCommand());
    }

    private static void initWorld() {
        add(new OreSim());
        add(new AutoBrick());
        add(new SoundLocator());
    }

    private static void initCrash() {
        add(new AACCrash());
        add(new BookCrash());
        add(new CraftingCrash());
        add(new ContainerCrash());
        add(new CreativeCrash());
        add(new EntityCrash());
        add(new ErrorCrash());
        add(new InteractCrash());
        add(new LecternCrash());
        add(new MessageLagger());
        add(new MovementCrash());
        add(new PacketSpammer());
        add(new SequenceCrash());
        add(new BannerCrash());
        add(new BoatCrash());
        add(new InvalidPositionCrash());
        add(new LoginCrash());
        add(new NoComCrash());
        add(new SignCrash());
        //add(new SBCrash());
        add(new TryUseCrash());
    }
}
