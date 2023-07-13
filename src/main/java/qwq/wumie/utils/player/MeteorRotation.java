/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.utils.player;

import org.joml.Vector3d;

public class MeteorRotation {
    public double yaw;
    public double pitch;
    public MeteorRotation(double yaw,double pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public static class VecRotation {
        public Vector3d vec3;
        public MeteorRotation meteorRotation;
        public VecRotation(Vector3d vec, MeteorRotation rotation) {
            this.vec3 = vec;
            this.meteorRotation = rotation;
        }

        public MeteorRotation getRotation() {
            return meteorRotation;
        }
    }
}
