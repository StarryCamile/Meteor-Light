package qwq.wumie.mixininterface;

public interface FPlayerMoveC2SPacket {
    void setX(double x);
    void setY(double y);
    void setZ(double z);
    void setYaw(float y);
    void setPitch(float p);
    void setOnGround(boolean ground);
}
