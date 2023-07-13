/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.modules.misc;

import com.google.common.collect.Multimap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import qwq.wumie.utils.misc.NotebotUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.Instrument;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class NotebotI extends Module {
    public NotebotI() {
        super(Categories.Misc, "note-bot-i", "new notebot !");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render", false);

    private final Setting<Boolean> tune = sgRender.add(new BoolSetting.Builder()
        .name("tune")
        .description("Tunes the noteblocks before and while playing.")
        .defaultValue(true)
        .build()
    );

    private final Setting<TuneMode> tuneMode = sgRender.add(new EnumSetting.Builder<TuneMode>()
        .name("tune-mode")
        .description("How to tune the noteblocks.")
        .defaultValue(TuneMode.Normal)
        .visible(tune::get)
        .build()
    );

    public enum TuneMode {
        Normal,
        Wait_1,
        Wait_2,
        Batch_5,
        All
    }

    private final Setting<Boolean> loop = sgRender.add(new BoolSetting.Builder()
        .name("loop")
        .description("Loop the song you're playing.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> noInstruments = sgRender.add(new BoolSetting.Builder()
        .name("no-instruments")
        .description("Ignores instruments.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> autoPlay = sgRender.add(new BoolSetting.Builder()
        .name("auto-play")
        .description("Auto plays a random song after one is finished.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .description("Whether or not to render the outline around the noteblocks.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The color of the sides of the blocks being rendered.")
        .defaultValue(new SettingColor(204, 0, 0, 10))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The color of the lines of the blocks being rendered.")
        .defaultValue(new SettingColor(204, 0, 0, 255))
        .build()
    );


    private final Setting<ShapeMode> oshapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode-1")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> osideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color-1")
        .description("The color of the sides of the blocks being rendered.")
        .defaultValue(new SettingColor(102, 0, 0, 10))
        .build()
    );

    private final Setting<SettingColor> olineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color-1")
        .description("The color of the lines of the blocks being rendered.")
        .defaultValue(new SettingColor(102, 0, 0, 255))
        .build()
    );

    // song
    public Song song;

    private Map<BlockPos, Integer> blockPitches = new HashMap<>();
    private int timer = -10;
    private int tuneDelay = 0;
    private boolean noSongsFound = true;
    private WLabel status;
    private boolean pause = false;
    private boolean isPlaying = false;

//preview
    private         boolean isPreviwe = false;
    private         Song entry;
    private         boolean playing = false;
    private         int playTick = 0;
    private         boolean previewPause = false;
    private         boolean isPreviwePlaying = false;

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WTable table = theme.table();

        // Label
        status = (table.add(theme.label(getStatus())).expandCellX().widget());

        // Pause
        WButton pause = table.add(theme.button(isPlaying || isPreviwePlaying ? "Pause" : "Resume")).right().widget();
        pause.action = () -> {
            this.pause = !this.pause;
            if (isPreviwe) {
                this.previewPause = !previewPause;
            }
            pause.set(isPlaying || isPreviwePlaying ? "Pause" : "Resume");
            status.set(getStatus());
        };

        // Stop
        WButton stop = table.add(theme.button("Stop")).right().widget();
        stop.action = () -> {
            this.song = null;
            this.entry = null;
        };

        table.row();

        noSongsFound = true;

        try {
            Files.list(MeteorClient.FOLDER.toPath().resolve("notebot")).forEach(path -> {
                if (isValidFile(path)) {
                    noSongsFound = false;
                    table.add(theme.label(getFileLabel(path))).expandCellX();
                    WButton load = table.add(theme.button("Load")).right().widget();
                    load.action = () -> {
                        if (!isActive()) toggle();
                       this.song = NotebotUtils.parse(path);
                       playing = false;
                       playTick = 0;
                       this.entry = null;
                        status.set(getStatus());
                    };
                    WButton preview = table.add(theme.button("Preview")).right().widget();
                    preview.action = () -> {
                        if (!isActive()) toggle();
                        this.entry = NotebotUtils.parse(path);
                        playing = true;
                        isPlaying = false;
                        playTick = 0;
                        status.set(getStatus());
                    };
                    table.row();
                }
            });
        } catch (IOException e) {
            table.add(theme.label("Missing meteor-client/notebot folder.")).expandCellX();
            table.row();
        }

        if (noSongsFound) {
            table.add(theme.label("No songs found.")).expandCellX();
            table.row();

            WButton guide = table.add(theme.button("Guide")).expandX().widget();
            guide.action = () -> Util.getOperatingSystem().open("https://github.com/MeteorDevelopment/meteor-client/wiki/Notebot-Guide");
        }

        return table;
    }

    public String getStatus() {
        if (!this.isActive()) return "Module disabled.";
        if (song == null) return "No song loaded.";
        else return String.format("Playing song. " + song.filename);
    }

    private String getFileLabel(Path file) {
        return file
            .getFileName()
            .toString()
            .replace(".txt", "")
            .replace(".nbs", "");
    }

    private boolean isValidFile(Path file) {
        String extension = FilenameUtils.getExtension(file.toFile().getName());
        if (extension.equals("txt")) return true;
        else return extension.equals("nbs");
    }

    @Override
    public void onActivate() {
        if (mc.world == null)
            return;

        blockPitches.clear();

        if (!mc.interactionManager.getCurrentGameMode().isSurvivalLike()) {
            error("Not In Survival Mode!");
            toggle();
            return;
        } else if (song == null) {
            error("No Song Loaded!");
            setActive(false);
            return;
        }

        timer = -10;

        List<BlockPos> noteblocks = BlockPos.streamOutwards(BlockPos.ofFloored(mc.player.getEyePos()), 4, 4, 4)
            .filter(this::isNoteblock)
            .map(BlockPos::toImmutable)
            .toList();

        for (Note note : song.requirements) {
            for (BlockPos pos: noteblocks) {
                if (blockPitches.containsKey(pos))
                    continue;

                if (noInstruments.get()) {
                    if (!blockPitches.containsValue(note.pitch)) {
                        blockPitches.put(pos, note.pitch);
                        break;
                    }
                } else {
                    int instrument = getInstrument(pos).ordinal();
                    if (note.instrument == instrument
                        && blockPitches.entrySet().stream()
                        .filter(e -> e.getValue() == note.pitch)
                        .noneMatch(e -> getInstrument(e.getKey()).ordinal() == instrument)) {
                        blockPitches.put(pos, note.pitch);
                        break;
                    }
                }
            }
        }

        int required = noInstruments.get()
            ? (int) song.requirements.stream().mapToInt(i -> i.instrument).distinct().count() : song.requirements.size();
        if (required > blockPitches.size()) {
            warning("Mapping Error: Missing " + (required - blockPitches.size()) + " Noteblocks");
        }
        super.onActivate();
    }


    @EventHandler
    public void onRender(Render3DEvent event) {
        if (!render.get()) return;
        for (Map.Entry<BlockPos, Integer> e : blockPitches.entrySet()) {
            if (getNote(e.getKey()) == e.getValue()) {
                event.renderer.box(e.getKey(),sideColor.get(),lineColor.get(),shapeMode.get(),0);
            } else {
                event.renderer.box(e.getKey(),osideColor.get(),olineColor.get(),oshapeMode.get(),0);
            }
        }
    }

    private int getTuneMode() {
        switch (this.tuneMode.get()) {
            case Normal -> {
                return 0;
            }
            case Wait_1 -> {
                return 1;
            }
            case Wait_2 -> {
                return 2;
            }
            case Batch_5 -> {
                return 3;
            }
            case All -> {
                return 4;
            }
        }
        return 0;
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (isPreviwe) {
            if (previewPause) {
                isPreviwePlaying = false;
            } else {
                if (entry != null && playing) {
                    isPreviwePlaying = false;
                    playTick++;
                    NotebotUtils.playNote(entry.notes, playTick);
                }
            }
        }
        if (song == null) return;
        // Tune Noteblocks
        int tuneMode = getTuneMode();

        if (tune.get()) {
            for (Map.Entry<BlockPos, Integer> e : blockPitches.entrySet()) {
                int note = getNote(e.getKey());
                if (note == -1)
                    continue;

                if (note != e.getValue()) {
                    if (tuneMode <= 2) {
                        if (tuneMode >= 1) {
                            if (mc.player.age % 2 == 0 ||
                                (mc.player.age % 3 == 0 && tuneMode == 2))
                                return;
                        }

                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND,
                            new BlockHitResult(Vec3d.ofCenter(e.getKey(), 1), Direction.UP, e.getKey(), true));
                    } else if (tuneMode >= 3) {
                        if (tuneDelay < (tuneMode == 3 ? 3 : 5)) {
                            tuneDelay++;
                            return;
                        }

                        int neededNote = e.getValue() < note ? e.getValue() + 25 : e.getValue();
                        int reqTunes = Math.min(tuneMode == 3 ? 5 : 25, neededNote - note);
                        for (int i = 0; i < reqTunes; i++)
                            mc.interactionManager.interactBlock(mc.player,
                                Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(e.getKey(), 1), Direction.UP, e.getKey(), true));

                        tuneDelay = 0;
                    }

                    return;
                }
            }
        }
        if (pause) {
            isPlaying = false;

        // Loop
        if (timer - 10 > song.length) {
            if (autoPlay.get()) {
                try {
                    var files = Files.list(MeteorClient.FOLDER.toPath().resolve("notebot")).toList();
                    Path path = files.get(ThreadLocalRandom.current().nextInt(files.size())).toFile().toPath();

                    song = NotebotUtils.parse(path);

                    setActive(false);
                    setActive(true);
                    info("Now Playing: \u00a7a" + song.filename);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (loop.get()) {
                timer = -10;
            }
        }
        // Play Noteblocks
        } else {
            timer++;

            Collection<Note> curNotes = song.notes.get(timer);

            if (curNotes.isEmpty())
                return;

            isPlaying = true;

            for (Map.Entry<BlockPos, Integer> e : blockPitches.entrySet()) {
                for (Note i : curNotes) {
                    if (isNoteblock(e.getKey()) && (i.pitch == (getNote(e.getKey()))
                        && (noInstruments.get() || i.instrument == getInstrument(e.getKey()).ordinal())))
                        playBlock(e.getKey());
                }
            }
        }
    }

    public Instrument getInstrument(BlockPos pos) {
        if (!isNoteblock(pos))
            return Instrument.HARP;

        return mc.world.getBlockState(pos).get(NoteBlock.INSTRUMENT);
    }

    public int getNote(BlockPos pos) {
        if (!isNoteblock(pos))
            return -1;

        return mc.world.getBlockState(pos).get(NoteBlock.NOTE);
    }

    public boolean isNoteblock(BlockPos pos) {
        // Checks if this block is a noteblock and the noteblock can be played
        return mc.world.getBlockState(pos).getBlock() instanceof NoteBlock
            && mc.world.getBlockState(pos.up()).isAir();
    }

    public void playBlock(BlockPos pos) {
        if (!isNoteblock(pos))
            return;

        mc.interactionManager.attackBlock(pos, Direction.UP);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    public static class Song {

        public String filename;
        public String name;
        public String author;
        public String format;

        public Multimap<Integer, Note> notes;
        public Set<Note> requirements = new HashSet<>();
        public int length;

        public Song(String filename, String name, String author, String format, Multimap<Integer, Note> notes) {
            this.filename = filename;
            this.name = name;
            this.author = author;
            this.format = format;
            this.notes = notes;

            notes.values().stream().distinct().forEach(requirements::add);
            length = notes.keySet().stream().max(Comparator.naturalOrder()).orElse(0);
        }
    }

    public static class Note {

        public int pitch;
        public int instrument;

        public Note(int pitch, int instrument) {
            this.pitch = pitch;
            this.instrument = instrument;
        }

        @Override
        public int hashCode() {
            return pitch * 31 + instrument;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Note))
                return false;

            Note other = (Note) obj;
            return instrument == other.instrument && pitch == other.pitch;
        }
    }
}
