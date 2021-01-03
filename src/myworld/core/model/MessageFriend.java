package myworld.core.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MessageFriend {
    @SerializedName("friend_id")
    private int friend_id;
    @SerializedName("hoten")
    private String friend_name;
    @SerializedName("url_avatar")
    private String url_avatar;
    @SerializedName("online")
    private boolean online = false;
    @SerializedName("current")
    private Message last_message;

    public MessageFriend() {
    }

    public MessageFriend(int friend_id, String friend_name, String url_avatar, Message last_message) {
        this.friend_id = friend_id;
        this.friend_name = friend_name;
        this.url_avatar = url_avatar;
        this.last_message = last_message;
    }

    public void setDefault(){
        Message.friend_name = this.getFriend_name();
        Message.urlavatar = this.getUrl_avatar();
    }

    @Override
    public String toString() {
        return "MessageFriend{" +
                "friend_id=" + friend_id +
                ", friend_name='" + friend_name + '\'' +
                ", url_avatar='" + url_avatar + '\'' +
                ", online=" + online +
                ", last_message=" + last_message +
                '}';
    }

    public static List<MessageFriend> readJson(JsonArray jarr){
        List<MessageFriend> mss = new ArrayList<>(jarr.size());
        for (int i = 0; i < jarr.size(); i++)
        {
            JsonObject json = jarr.get(i).getAsJsonObject();
            int id = json.get("id").getAsInt();
            int friend_id = json.get("friend_id").getAsInt();
            String url_avatar = json.get("url_avatar").getAsString();
            int sender_id = json.get("sender_id").getAsInt();
            String hoten = json.get("hoten").getAsString();
            String content = json.get("content").getAsString();
            String url_file = json.get("url_file").getAsString();
            boolean seen = true;//json.get("seen").getAsBoolean();

            Message msg = new Message(
                    0, 0, content, url_file, null
            );
            msg.setSeen(seen);
            msg.setSender_id(sender_id);

            MessageFriend ms = new MessageFriend(friend_id, hoten, url_avatar, msg);
            mss.add(ms);
        }
        return mss;
    }


    public int getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public String getUrl_avatar() {
        return url_avatar;
    }

    public void setUrl_avatar(String url_avatar) {
        this.url_avatar = url_avatar;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Message getLast_message() {
        return last_message;
    }

    public void setLast_message(Message last_message) {
        this.last_message = last_message;
    }
}
