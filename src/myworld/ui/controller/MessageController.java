package myworld.ui.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import myworld.core.audio.SoundManager;
import myworld.core.channel.Maker;
import myworld.core.event.ChangeUserInfoListener;
import myworld.core.event.OpenListener;
import myworld.core.event.ReceiveListener;
import myworld.core.model.Message;
import myworld.core.model.MessageFriend;
import myworld.core.model.User;
import myworld.core.transfer.DataTransfer;
import myworld.core.transfer.QuickData;
import myworld.core.worker.Regex;
import myworld.debug.Debug;
import myworld.ui.ApplicationRun;
import myworld.ui.dialog.CallPhoneDialog;
import myworld.ui.item.ItemMessage;
import myworld.ui.item.ItemMessageFriend;
import res.resource.R;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageController implements Initializable, ReceiveListener, ChangeUserInfoListener, OpenListener {

    static ChangeUserInfoListener changeinfouser;

    @FXML AnchorPane frag_message;
    @FXML ListView<Message> list_message;
    @FXML TextField inp_search;
    @FXML ListView<MessageFriend> list_friend;
    @FXML Circle img_avatar;
    @FXML Label lb_username;
    @FXML Label lb_status;
    @FXML ImageView img_callphone;
    @FXML ImageView img_videocall;
    @FXML Circle img_status;
    @FXML ImageView choose_file;
    @FXML ImageView btn_send;
    @FXML TextArea txt_message;
    @FXML Button btn_callphone;
    @FXML ScrollPane scrollMessage;

    int current_frid = 0;
    ObservableList<MessageFriend> msgfriends;
    CallPhoneDialog dialog;
    Timer timer;
    SoundManager soundManager;
    boolean flag = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Debug.out("Message Controller running...");
        Maker.service().addListener(this::receive);
        ApplicationRun.listeners.add(this::open);
        changeinfouser = this::update;

        soundManager = new SoundManager();

        list_friend.setCellFactory(param -> ItemMessageFriend.createMessageFriend());

        list_message.setCellFactory(param -> new ItemMessage());

        msgfriends = list_friend.getItems();

        list_friend.setOnMouseClicked(this::ItemClick);

        //Step 2: Xu ly noi bo
        //Xu ly su kien chon file
        choose_file.setOnMouseClicked(e -> onActionOpenFile());

        if (!list_message.getItems().isEmpty())
            list_message.getFocusModel().focus(list_message.getItems().size() - 1);

        //Xu ly su kien giu tin nhan
        btn_send.setOnMouseClicked(this::onActionSend);

        btn_callphone.setOnMouseClicked(this::onActionCallPhone);

        inp_search.setOnKeyPressed(this::onActionSearch);

        timer = new Timer(20000, this::onActionCheckFriendOnline);

        scrollMessage.vvalueProperty().addListener(this::extendMessage);

        scrollMessage.prefHeightProperty().bind(list_message.heightProperty());

        scrollMessage.heightProperty().addListener((observable, oldValue, newValue) -> list_message.setPrefHeight(newValue.doubleValue()));
    }

    private void extendMessage(ObservableValue<? extends Number> observableValue, Number oldv, Number newv) {

        if(newv.doubleValue() == 0.0 && flag){
            int index = list_message.getItems().size();
            Debug.out(String.format("Extend list: from %s to %s", index, index + 10));

            try {
                getMessageOfFriend(current_frid, index, index + 10);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void onActionCheckFriendOnline(ActionEvent actionEvent) {
        try {
            checkFriendOnline();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onActionCallPhone(MouseEvent mouseEvent) {
        User user = ApplicationRun.getCurrentUser();

        try {
            //1. goi den status call phone tren server de ket noi
            DataTransfer data = DataTransfer.create(R.Cmd.RM_STT_START, DataTransfer.TYPE_JSON,
                    String.format("{\"friend_id\": %s, \"hoten\": \"%s\", \"channel_id\": %s}"
                            , current_frid, user.getHoten(), user.getMasv()), 0);
            dialog = new CallPhoneDialog();
            dialog.getBtn_chapnhan().setDisable(true);

            Maker.service().writeData(data);
            MessageFriend friend = list_friend.getItems()
                    .stream().filter(e -> e.getFriend_id() == current_frid)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)).get(0);

//            soundManager.playFromResource(SoundManager.Audio.CALLPHONE);

            EventHandler<MouseEvent> eventHandler = e -> {
                Debug.out("Event close...");
                dialog.setResult(true);
                dialog.close();
                cancleCallPhone(current_frid, user.getMasv());
            };

            dialog.getTxt_status().setText(String.format("Đang kết nối đến... %s", friend.getFriend_name()));
            dialog.register(eventHandler);
            dialog.showAndWait();

            //2.nhan su kien tra ve
            //3.bat dam thoai
        } catch (Exception e) {
            cancleCallPhone(current_frid, user.getMasv());
            e.printStackTrace();
        }
    }

    private void cancleCallPhone(int friend_id, int channel_id) {
        try {
            Maker.service().writeData(
                    DataTransfer.create(R.Cmd.RM_STT_FINISH, DataTransfer.TYPE_JSON,
                            String.format("{\"friend_id\": %s, \"channel_id\": %s}"
                                    , friend_id, channel_id), 0)
            );

            closeCallPhoneStream(ApplicationRun.getCurrentUser().getMasv() + "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onActionSearch(KeyEvent ev) {
        if (ev.getCode() == KeyCode.ENTER) {
            String text = inp_search.getText();
            ObservableList list = msgfriends.stream()
                    .filter(e -> text.contains(e.getFriend_id() + "") ||
                            e.getFriend_name().contains(text) ||
                            e.getLast_message().getContent().contains(text))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            list_friend.setItems(list);
        }

        if (ev.getCode() == KeyCode.F5)
            list_friend.setItems(msgfriends);
    }

    private void onActionSend(Event e) {
        User user = ApplicationRun.getCurrentUser();
        final Message message = Message.make(user.getMasv(), current_frid, txt_message.getText());

        Runnable runnable = () -> {
            if(message.availability()){
                try {
                    Matcher matcher = Pattern.compile(Regex.REGEX_FILE).matcher(message.getContent());
                    while (matcher.find())
                    {
                        String filename = matcher.group();
                        File fileroot = new File(filename.substring("file: ".length()));

                        Maker.fileShare().upload("/user", fileroot);

                        StringBuilder builder = new StringBuilder(txt_message.getText());
                        message.setContent(builder.replace(matcher.start()-1, matcher.start() + filename.length() + 1, "").toString());
                        message.setUrlfile(String.format("ftp://localhost/%s/%s", "user", fileroot.getName()));
                    }

                    message.setDateNow();

                    list_message.getItems().add(message);
                    Maker.service().writeData(
                                DataTransfer.create(R.Cmd.ACT_SEND, DataTransfer.TYPE_JSON,
                                        message.toJsonString(), 0)
                        );

                    Runnable task = () -> {
                      MessageFriend friend = list_friend.getItems()
                      .stream().filter(x->x.getFriend_id() == current_frid).findFirst()
                              .get();
                      friend.setLast_message(message);
                    };
                    Platform.runLater(task);
                    txt_message.setText(null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();
    }

    public void ItemClick(Event e) {
        User user = ApplicationRun.getCurrentUser();
        MessageFriend msgfr = list_friend.getSelectionModel().getSelectedItem();

        if (msgfr != null && msgfr.getFriend_id() != current_frid) {
            msgfr.setDefault();
            current_frid = msgfr.getFriend_id();

            try {
                flag = false;
                list_message.getItems().clear();
                getMessageOfFriend(current_frid, 0 , 15);
                if (!msgfr.getLast_message().isSeen()) {
                    msgfr.getLast_message().setSeen(true);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void onActionOpenFile() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("C:\\"));
        chooser.setTitle("Select file");
        try {
            File file = chooser.showOpenDialog(new Stage());
            String path = file.getAbsolutePath();
            txt_message.appendText(String.format(" file: %s", path));
        } catch (Exception e) {
            Debug.out("Error: " + e.getMessage());
        }

    }

    @Override
    public void open(String fxml, Parent parent) {
        if (fxml == ApplicationRun.FXML_MESSAGE) {
            User user= ApplicationRun.getCurrentUser();
            Image image = new Image(Maker.fileShare().getInputStream(user.getAvatar()));
            img_avatar.setFill(new ImagePattern(image));
            lb_username.setText(user.getHoten());

            DataTransfer data = DataTransfer.create(
                    R.Cmd.GET_LIST_FRIEND
                    , DataTransfer.TYPE_STRING
                    , ApplicationRun.getCurrentUser().getMasv() + ""
                    , 0);
            try {
                if (current_frid == 0) {
//                    timer.start();
                    Maker.service().writeData(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void receive(int cmd, int type, String response) {
        try {
            JsonObject json = null;
            JsonArray jarray = null;

            switch (cmd) {
                case R.Cmd.ACT_RECV:
                    final Message message = DataTransfer.getObjectFromJson(response, Message.class);
                    /**
                     * Nếu sender là bạn bè hiện tại thì thêm vào danh sách
                     * Ngược lại thì Update friend id
                     */
                    Runnable runnable = () -> {

                        ObservableList<MessageFriend> list = list_friend.getItems();
                        for (int i = 0; i < list_friend.getItems().size(); i++) {
                            MessageFriend e = list_friend.getItems().get(i);
                            if (e.getFriend_id() == message.getSender_id()) {
                                list.remove(e);
                                e.getLast_message().setSeen(false);
                                e.setLast_message(message);
                                list.add(0, e);
                            }
                        }

                        if (message.getSender_id() == current_frid) {
                            list_message.getItems().add(message);
                        }

                        soundManager.playFromResource(SoundManager.Audio.MESSAGE);
                    };
                    Platform.runLater(runnable);
                    break;
                case R.Cmd.GET_LIST_FRIEND:
                    JsonArray jsonArray = (JsonArray) JsonParser.parseString(response);
                    List<MessageFriend> msgfrs = MessageFriend.readJson(jsonArray);
                    list_friend.setItems(FXCollections.observableList(msgfrs));

                    /*nếu danh sách có bạn bè*/
                    if(msgfrs.size() > 0){
                        MessageFriend friend = list_friend.getItems().get(0);
                        friend.setDefault();
                        current_frid = friend.getFriend_id();

                        /**Lấy danh sách tin nhắn của friend id*/
                        getMessageOfFriend(current_frid, 0 , 15);

                        /** Lấy danh sách bạn bè online*/
                        checkFriendOnline();
                    }

                    break;

                case R.Cmd.GET_LIST_MESSAGE:

                    if(flag) return;

                    Runnable run = () -> {
                        Message[] messages = DataTransfer.getObjectFromJson(response, Message[].class);
                        /*list_message.getItems().clear();*/
                        list_message.getItems().addAll(0, Arrays.asList(messages));

                        if (!list_message.getItems().isEmpty()){
                            list_message.scrollTo(messages.length-1);
                            list_message.getSelectionModel().select(messages.length-1);
                        }

                        flag = true;
                    };
                    Platform.runLater(run);
                    break;

                case R.Cmd.FRIEND_ONLINE:
                    jarray = (JsonArray) JsonParser.parseString(response);

                    for (int i = 0; i < jarray.size(); i++) {
                        boolean res = jarray.get(i).getAsJsonObject().get("online").getAsBoolean();
                        list_friend.getItems().get(i).setOnline(res);
                    }
                    list_friend.refresh();

                    break;

                /**
                 * Xu ly cuoc goi
                 * #1. Thuc hien loi moi goi den client muon goi thong qua server
                 * #2. Nhan ket qua loi moi goi
                 * #3. Xu ly ket noi den UDP Server de  dang ky 1 kenh truyen thong tin
                 * #4. Dong cuoc goi
                 */

                //Xu ly loi moi goi ben server
                case R.Cmd.RM_STT_START:
                    Debug.out("Step #1: invite friend to call.");
                    //Mo dialog
                    runnable = () -> {
                        JsonObject js = (JsonObject) JsonParser.parseString(response);
                        boolean result = waitResultFromUser(js);
                    };

                    Platform.runLater(runnable);

                    break;
                //Ket qua ket noi
                case R.Cmd.RM_STT_RESULT:
                    Debug.out("Step #2: verify!");

                    json = (JsonObject) JsonParser.parseString(response);
                    int result = json.get("result").getAsInt();

                    if (result == 1) {
                        dialog.getTimer().start();
                        Debug.out("Chap nhan ket noi...");
                        dialog.getTxt_status().setText("Đường truyền đang được mở.");

                        //Dang ky và kết nối tới phòng chát
                        registerChannel(ApplicationRun.getCurrentUser().getMasv());
                    } else {
                        dialog.getTxt_time().setText(String.format("%s không trực tuyến.", Message.friend_name));
                        dialog.getBtn_chapnhan().setDisable(true);

                        soundManager.playFromResource(SoundManager.Audio.ENDCALL);
                        Debug.out("May ban...");
                    }
                    break;

                //dong cuoc hoi thoai
                case R.Cmd.RM_STT_FINISH:
                    Debug.out("Step #4: finish!");
                    dialog.getTxt_status().setText("Đã kết thúc cuộc gọi.");
                    dialog.getTimer().stop();

                    json = (JsonObject) JsonParser.parseString(response);
                    closeCallPhoneStream(json.get("channel_id").getAsString());
                    break;

                case R.Cmd.PRINT_SCREEN:
                    Debug.out(response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void registerChannel(int channel_id) throws IOException, LineUnavailableException {
        //Dang ky phong chat
        QuickData quickData = QuickData.create(R.Cmd.RM_ACT_REGISTER + "",
                channel_id + "",
                null);
        Maker.UDPService().register(quickData);
        //Mo luong am thanh
        Maker.UDPService().openInputStream();
        //Van tai du lieu
        Maker.UDPService().start();
    }

    public void getMessageOfFriend(int current_frid, int from, int to) throws IOException {
        User user = ApplicationRun.getCurrentUser();
        JsonObject json = DataTransfer.toJsonFromString("myid:" + user.getMasv()
                , "friend_id:"+current_frid, "from_:" + from, "to:" + to);

        DataTransfer data = DataTransfer.create(
                R.Cmd.GET_LIST_MESSAGE, DataTransfer.TYPE_JSON
                , json.toString(), 0
        );
        flag = false;
        Maker.service().writeData(data);
    }

    private boolean waitResultFromUser(final JsonObject json) {
        dialog = new CallPhoneDialog();
        dialog.getTxt_status().setText(String.format("Đang kết nối đến... %s", json.get("hoten").getAsString()));

        EventHandler<MouseEvent> eventHandler = e -> {
            if (e.getSource() == dialog.getBtn_chapnhan()) {
                if (json.has("channel_id")) {
                    try {
                       registerChannel(json.get("channel_id").getAsInt());
                       dialog.getTimer().start();
                    } catch (IOException | LineUnavailableException ioException) {
                        ioException.printStackTrace();
                        dialog.setResult(true);
                        dialog.close();
                        return;
                    }
                }

                JsonObject newjson = DataTransfer.toJsonFromString("myid:"+ApplicationRun.getCurrentUser().getMasv()
                        ,"channel_id:" + json.get("channel_id"),"result:1");

                //Phan hoi loi moi goi
                try {
                    Maker.service().writeData(DataTransfer.create(R.Cmd.RM_STT_RESULT, DataTransfer.TYPE_JSON
                            , newjson.toString() , 0));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                dialog.getBtn_chapnhan().setDisable(true);

            }

            if (e.getSource() == dialog.getBtn_huybo()) {
                try {
                    dialog.setResult(true);
                    dialog.close();

                    Maker.service().writeData(
                            DataTransfer.create(R.Cmd.RM_STT_FINISH, DataTransfer.TYPE_JSON,
                                    String.format("{\"friend_id\": %s, \"channel_id\": %s}"
                                            , json.get("channel_id").getAsString()
                                            , json.get("channel_id").getAsString()), 0)
                    );
                    closeCallPhoneStream(json.get("channel_id").getAsString());
                    soundManager.stop();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        };

        dialog.register(eventHandler);
        dialog.show();
        return true;
    }

    public void checkFriendOnline() throws IOException {
        List<Integer> list = list_friend.getItems().stream()
                .mapToInt(MessageFriend::getFriend_id).boxed().collect(Collectors.toList());

        String json = new Gson().toJson(list);

        DataTransfer data = DataTransfer.create(R.Cmd.FRIEND_ONLINE,
                DataTransfer.TYPE_JSON, json, 0);

        Maker.service().writeData(data);
    }

    private void closeCallPhoneStream(String channel) throws IOException {
        QuickData quickData = QuickData.create(R.Cmd.RM_ACT_DISCONNECT + "",
                channel, null);
        //huy bo kenh dang ky
        Maker.UDPService().sendTo(quickData);
        //Dong luong doc ghi va thread
        Maker.UDPService().closeAndStop();
    }

    @Override
    public void update(User user) {
        lb_username.setText(user.getHoten());
        Image image = new Image(Maker.fileShare().getInputStream(user.getAvatar()));
        img_avatar.setFill(new ImagePattern(image));
    }
}
