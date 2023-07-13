/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.sound;

import net.minecraft.client.MinecraftClient;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class MeteorSoundPlay {
    private File file;

    public MeteorSoundPlay(File sound) {
        this.file = sound;
    }

    public void play() {
        if (MinecraftClient.getInstance().world == null) return;
        (new Thread(this::playSound)).start();
    }

    public final void playSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }
}
