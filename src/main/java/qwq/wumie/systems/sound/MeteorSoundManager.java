/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.sound;

import meteordevelopment.meteorclient.MeteorClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MeteorSoundManager {
    public MeteorSoundPlay enableSound;
    public MeteorSoundPlay disableSound;

    public MeteorSoundManager() {
        File enableSoundFile = new File(MeteorClient.SOUNDS_FOLODER,"enable.wav");
        File disableSoundFile = new File(MeteorClient.SOUNDS_FOLODER,"disable.wav");
        File enableSoundFolder = new File(enableSoundFile.getParent());
        File disableSoundFolder = new File(disableSoundFile.getParent());

        if (!enableSoundFile.exists()) {
            unpackFile(enableSoundFile, "enable");
        }
        if (!disableSoundFile.exists()) {
            unpackFile(disableSoundFile,"disable");
        }
        if (!enableSoundFolder.exists()) {
            enableSoundFolder.mkdir();
        }
        if (!disableSoundFolder.exists()) {
            disableSoundFolder.mkdir();
        }
        enableSound = new MeteorSoundPlay(enableSoundFile);
        disableSound = new MeteorSoundPlay(disableSoundFile);
    }

    public void unpackFile(File file,String name) {
        try {
            InputStream is = MeteorSoundManager.class.getResourceAsStream("/assets/meteor-client/sounds/"+name+".wav");
            file.createNewFile();
            OutputStream os = new FileOutputStream(file);
            int index;
            byte[] bytes = new byte[12800];
            while ((index = is.read(bytes)) != -1) {
                os.write(bytes, 0, index);
            }
            os.flush();
            os.close();
            is.close();
        } catch (Exception ex) {
            System.out.print("Error unpackFile: " + name +".wav");
            ex.printStackTrace();
        }
    }

}
