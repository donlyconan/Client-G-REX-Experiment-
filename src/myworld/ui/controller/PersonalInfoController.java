package myworld.ui.controller;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import myworld.core.channel.Maker;
import myworld.core.event.OpenListener;
import myworld.core.event.ReceiveListener;
import myworld.core.model.User;
import myworld.core.transfer.DataTransfer;
import myworld.debug.Debug;
import myworld.ui.ApplicationRun;
import res.resource.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class PersonalInfoController implements Initializable, ReceiveListener, OpenListener {
    @FXML TextField txt_masv;
    @FXML TextField txt_email;
    @FXML TextField txt_hoten;
    @FXML TextArea txt_diachi;
    @FXML ComboBox cb_gioitinh;
    @FXML TextField txt_malop;
    @FXML ImageView img_profile;
    @FXML Button btn_select;
    @FXML Button btn_update;
    @FXML TextField txt_profile;
    @FXML TextField txt_sdt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Maker.service().addListener(this::receive);
        ApplicationRun.listeners.add(this::open);

        //Su kien chuot
        btn_select.setOnMouseClicked(this::onMouseClick);
        btn_update.setOnMouseClicked(this::onMouseClick);

        cb_gioitinh.setItems(FXCollections.observableList(Arrays.asList("Nam", "Nữ")));
    }

    public void onMouseClick(Event e)  {
        if (e.getSource() == btn_select) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select file");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("IMAGE files",
                            "*.png", "*.jpg", "*.jpeg")
            );
            try {
                File file = chooser.showOpenDialog(new Stage());
                txt_profile.setText(file.getAbsolutePath());
                InputStream inputStream = new FileInputStream(file);
                img_profile.setImage(new Image(inputStream));
                inputStream.close();
            } catch (Exception x){
                Debug.out(x.getMessage());
            }
        }

        if (e.getSource() == btn_update) {
            String avatar = null;

            if (txt_profile.getText() != null) {
                File file = new File(txt_profile.getText());
                if(file.exists()) {
                    try {
                        avatar = Maker.fileShare().upload("/profile", file);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }

            User user = ApplicationRun.getCurrentUser();
            user.setHoten(txt_hoten.getText());

            if(avatar != null)
                 user.setAvatar(avatar);
            user.setDiachi(txt_diachi.getText());
            user.setEmail(txt_email.getText());
            user.setSodt(txt_sdt.getText());
            user.setGioitinh(cb_gioitinh.getSelectionModel().getSelectedIndex());

            DataTransfer data = DataTransfer.create(
                    R.Cmd.UPDATE_USER,
                    DataTransfer.TYPE_JSON,
                    user.toJsonString(), 0
            );

            try {
                Maker.service().writeData(data);
                MainController.changeUserInfoListener
                        .update(user);
                MessageController.changeinfouser.update(user);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public void open(String fxml, Parent parent) {
        if (fxml == ApplicationRun.FXML_PERSONAL_INFO) {
            User user = ApplicationRun.getCurrentUser();
            txt_hoten.setText(user.getHoten());
            txt_masv.setText(user.getMasv() + "");
            txt_diachi.setText(user.getDiachi());
            txt_email.setText(user.getEmail());
            txt_profile.setText(user.getAvatar());
            txt_sdt.setText(user.getSodt());
            cb_gioitinh.getSelectionModel().select(user.getGioitinh());
            txt_malop.setText(user.getMalop());

            txt_profile.setText(user.getAvatar());
            Image image = new Image(Maker.fileShare().getInputStream(user.getAvatar()));
            img_profile.setImage(image);
        }

    }

    @Override
    public void receive(int cmd, int type, String response) {
        switch (cmd){
            case R.Cmd.PRINT_SCREEN:
                Debug.out(response);
                break;
        }
    }
}
