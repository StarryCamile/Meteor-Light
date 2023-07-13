/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.world;

import meteordevelopment.meteorclient.events.Cancellable;

public class UpdateEvent extends Cancellable {
    private static final UpdateEvent INSTANCE = new UpdateEvent();

    public float yaw,pitch;
    public double x,y,z;
    public boolean ground;

    public static UpdateEvent get(double posX,double posY,double posZ,float rotationYaw,float rotationPitch,boolean onGround) {
        INSTANCE.x = posX;
        INSTANCE.y = posY;
        INSTANCE.z = posZ;
        INSTANCE.yaw = rotationYaw;
        INSTANCE.pitch = rotationPitch;
        INSTANCE.ground = onGround;
        return INSTANCE;
    }

    public static class Pre extends Cancellable {
        private static final Pre INSTANCE = new Pre();

        public float yaw, pitch;
        public double x, y, z;
        public boolean ground;

        public static Pre get(double posX, double posY, double posZ, float rotationYaw, float rotationPitch, boolean onGround) {
            INSTANCE.x = posX;
            INSTANCE.y = posY;
            INSTANCE.z = posZ;
            INSTANCE.yaw = rotationYaw;
            INSTANCE.pitch = rotationPitch;
            INSTANCE.ground = onGround;
            return INSTANCE;
        }
    }

    public static class Post extends Cancellable {
        private static final Post INSTANCE = new Post();

        public float yaw,pitch;
        public double x,y,z;
        public boolean ground;

        public static Post get(double posX, double posY, double posZ, float rotationYaw, float rotationPitch, boolean onGround) {
            INSTANCE.x = posX;
            INSTANCE.y = posY;
            INSTANCE.z = posZ;
            INSTANCE.yaw = rotationYaw;
            INSTANCE.pitch = rotationPitch;
            INSTANCE.ground = onGround;
            return INSTANCE;
        }
    }
}
