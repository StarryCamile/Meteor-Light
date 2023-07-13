package qwq.wumie.systems.websocket.server;

public class GMessage {
    String post_type;
    String message_type;
    String time;
    String self_id;
    String sub_type;
    String message;
    String raw_message;
    String font;
    String group_id;
    Sender sender;
    String user_id;
    String message_id;
    String anonymous;
    String message_seq;

    public String getPost_type() {
        return post_type;
    }

    public String getMessage_type() {
        return message_type;
    }

    public String getTime() {
        return time;
    }

    public String getSelf_id() {
        return self_id;
    }

    public String getSub_type() {
        return sub_type;
    }

    public String getMessage() {
        return message;
    }

    public String getRaw_message() {
        return raw_message;
    }

    public String getFont() {
        return font;
    }

    public String getGroup_id() {
        return group_id;
    }

    public Sender getSender() {
        return sender;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public Object getAnonymous() {
        return anonymous;
    }

    public String getMessage_seq() {
        return message_seq;
    }

     public static class Sender {
        String age;
        String area;
        String card;
        String level;
        String nickname;
        String role;
        String sex;
        String title;
        String user_id;

        public String getAge() {
            return age;
        }

        public String getArea() {
            return area;
        }

        public String getCard() {
            return card;
        }

        public String getLevel() {
            return level;
        }

        public String getNickname() {
            return nickname;
        }

        public String getRole() {
            return role;
        }

        public String getSex() {
            return sex;
        }

        public String getTitle() {
            return title;
        }

        public String getUser_id() {
            return user_id;
        }
    }
}
