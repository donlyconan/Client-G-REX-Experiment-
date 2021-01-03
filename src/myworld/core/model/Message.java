package myworld.core.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Message implements Serializable {
    static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public static String friend_name;
    public static String urlavatar;

    @SerializedName("id")
    private int message_id = 0;
    @SerializedName("sender_id")
    private int sender_id = 0;
    @SerializedName("receiver_id")
    private int receiver_id = 0;
    @SerializedName("content")
    private String content;
    @SerializedName("url_file")
    private String urlfile;
    @SerializedName("create_at")
    private String date;
    @SerializedName("seen")
    private boolean seen = true;

    public Message() {
    }

    public Message(int sender_id, int receiver_id, String content, String urlfile, String date) {
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.content = content;
        this.urlfile = urlfile;
        this.date = date;
    }

    public static Message make(int sender_id, int receiver_id, String content){
        return new Message(sender_id, receiver_id, content,
                 null, null);
    }

    @Override
    public String toString() {
        return "Message{" +
                "message_id=" + message_id +
                ", sender_id=" + sender_id +
                ", receiver_id=" + receiver_id +
                ", content='" + content + '\'' +
                ", url_file='" + urlfile + '\'' +
                ", date=" + date +
                ", seen=" + seen +
                '}';
    }

    public void setDateNow(){
        date = FORMAT.format(Calendar.getInstance().getTime());
    }

    public boolean availability(){
        return (content != null && content.length() > 1) ||
                urlfile != null ;
    }

    public String toJsonString(){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }

    public static String getFriend_name() {
        return friend_name;
    }


    public static void setFriend_name(String friend_name) {
        Message.friend_name = friend_name;
    }

    public static String getUrlavatar() {
        return urlavatar;
    }

    public static void setUrlavatar(String urlavatar) {
        Message.urlavatar = urlavatar;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public Date getDate() throws ParseException {
        return FORMAT.parse(date);
    }

    public String getUrlfile() {
        return urlfile;
    }

    public void setUrlfile(String urlfile) {
        this.urlfile = urlfile;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
