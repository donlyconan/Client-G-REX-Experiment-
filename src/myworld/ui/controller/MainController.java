package myworld.ui.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import myworld.core.channel.Maker;
import myworld.core.event.ChangeUserInfoListener;
import myworld.core.event.OpenListener;
import myworld.core.model.User;
import myworld.debug.Debug;
import myworld.ui.ApplicationRun;
import res.resource.R;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;


public class MainController implements Initializable, ChangeUserInfoListener,EventHandler<MouseEvent> {

    @FXML Button btn_caidat;
    @FXML Circle img_avatar;
    @FXML Label txt_username;
    @FXML Button btn_thongbao;
    @FXML Button btn_banbe;
    @FXML Button btn_tinnhan;
    @FXML Button btn_file;
    @FXML Button btn_weather;
    @FXML StackPane stackpane;

    Set<OpenListener> listeners;
    static ChangeUserInfoListener changeUserInfoListener;
    static String current_fxml;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listeners = ApplicationRun.listeners;

        changeUserInfoListener = this::update;

        btn_tinnhan.setOnMouseClicked(this::handle);
        btn_caidat.setOnMouseClicked(this::handle);
        btn_weather.setOnMouseClicked(this::handle);
        btn_banbe.setOnMouseClicked(this::handle);
        img_avatar.setOnMouseClicked(this::handle);

        stackpane.getChildren().addAll(ApplicationRun.MapScene.values());
        openController(ApplicationRun.FXML_WEATHER);
        this.update(ApplicationRun.getCurrentUser());
    }


    @Override
    public void handle(MouseEvent event) {

        try {
            if(event.getSource() == img_avatar) {
                openController(ApplicationRun.FXML_PERSONAL_INFO);
            }

            if (event.getSource() == btn_weather){
                openController(ApplicationRun.FXML_WEATHER);
            }
            if (event.getSource() == btn_banbe){
                openController(ApplicationRun.FXML_JSON_VIEWER);
            }

            if (event.getSource() == btn_thongbao){
                openController(ApplicationRun.FXML_NOTIFICATION);
            }
            if (event.getSource() == btn_tinnhan){
                openController(ApplicationRun.FXML_MESSAGE);
            }
            if (event.getSource() == btn_file){
                openController(ApplicationRun.FXML_FILE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Debug.out("66 - MainController: " + e.getMessage());
        }

    }

    private void openController(String fxml) {
        final Parent parent = ApplicationRun.MapScene.get(fxml);
        parent.toFront();
        current_fxml = fxml;
        listeners.forEach(e -> e.open(fxml, parent));
    }

    @Override
    public void update(User user) {
        txt_username.setText(user.getHoten());
        Image image = new Image(Maker.fileShare().getInputStream(user.getAvatar(), R.Icon.me));
        img_avatar.setFill(new ImagePattern(image));
    }
}
