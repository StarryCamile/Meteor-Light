package qwq.wumie.systems.websocket.server;

public class User {
    String qq;
    int level;

    public User(String qq, int level) {
        this.qq = qq;
        this.level = level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
