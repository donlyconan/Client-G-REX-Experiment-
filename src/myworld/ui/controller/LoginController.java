package myworld.ui.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import myworld.core.channel.Maker;
import myworld.core.event.ReceiveListener;
import myworld.core.model.User;
import myworld.core.transfer.DataTransfer;
import myworld.debug.Debug;
import myworld.ui.ApplicationRun;
import res.resource.Loader;
import res.resource.R;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class LoginController implements Initializable, ReceiveListener {

    @FXML ImageView image;
    @FXML TextField username;
    @FXML PasswordField password;
    @FXML CheckBox ck_remember;
    @FXML Label lb_forgot;
    @FXML Label txt_notification;
    @FXML Button btn_login;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Debug.out("Load LoginController...");
        Maker.service().addListener(this::receive);

        init();

        txt_notification.setText(null);
        btn_login.setOnMouseClicked(this::onActionLogin);

    }

    private void init() {
        Properties properties = new Properties();
        try {
            properties.load(Loader.getInputStream(R.Mapurl.propertie));
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");

            if(username != null)
                ck_remember.setSelected(true);
            this.username.setText(username);
            this.password.setText(password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onActionLogin(Event e){
        String username = this.username.getText();
        String password = this.password.getText();

        if(true)
        {
            JsonObject json = new JsonObject();
            json.addProperty("masv", username);
            json.addProperty("password", password);
            DataTransfer data = DataTransfer.create(R.Cmd.POST_LOGIN
                    , DataTransfer.TYPE_JSON ,json.toString(), 0);
            try {
                Maker.service().writeData(data);
                Debug.out("Send: " + json.toString());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    @Override
    public void receive(int cmd, int type, String response) {
        if (cmd == R.Cmd.POST_LOGIN) {
            JsonObject json = (JsonObject) new JsonParser().parse(response);
            if (json.get("result").getAsInt() == 1) {
                User user = DataTransfer.getObjectFromJson(json.getAsJsonObject("userinfo").toString(), User.class);
                ApplicationRun.setCurrentUser(user);
                ApplicationRun.showMainController();
            } else
                Platform.runLater(() -> txt_notification.setText(json.get("login").getAsString()));
        }
    }
}
