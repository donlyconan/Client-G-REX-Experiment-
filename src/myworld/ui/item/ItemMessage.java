package myworld.ui.item;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import myworld.core.channel.Maker;
import myworld.core.ftp.FileShare;
import myworld.core.model.Message;
import myworld.core.model.User;
import myworld.debug.Debug;
import myworld.ui.ApplicationRun;
import res.resource.Loader;
import res.resource.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ItemMessage extends ListCell<Message> implements EventHandler<MouseEvent> {
    static SimpleDateFormat format = new SimpleDateFormat("HH:mm");

    @FXML Circle img_avatar;
    @FXML VBox box;
    @FXML Label lbfullname;
    @FXML Label lbmessage;
    @FXML ImageView img_image;
    @FXML Button btnfile;
    @FXML ImageView imgdownload;
    @FXML Label txtTime;
    Parent parent = null;

    public ItemMessage() {
        setPrefWidth(0);
    }

    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        User user = ApplicationRun.getCurrentUser();

        if (!empty) {
            Image image = null;
            String current_name = null;

            try {
                FXMLLoader loader = null;
                if (item.getSender_id() == user.getMasv()){
                    loader = new FXMLLoader(Loader.getResource(R.Fxml.ItemMessageRight));
                    image = new Image(Maker.fileShare().getInputStream(user.getAvatar()));//new Image(item.getUrlprofile());
                    current_name = user.getHoten();
                }
                else{
                    loader = new FXMLLoader(Loader.getResource(R.Fxml.ItemMessageLeft));
                    current_name = Message.friend_name;
                    image = new Image(Maker.fileShare().getInputStream(Message.urlavatar));
                }
                loader.setController(this);
                parent = loader.load();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }

            lbfullname.setText(current_name);
            img_avatar.setFill(new ImagePattern(image));

            if (FileShare.filterImage(item.getUrlfile())) {
                InputStream inputStream = Maker.fileShare().getInputStream(item.getUrlfile());
                if (inputStream != null)
                    img_image.setImage(new Image(inputStream));
            } else {
                box.getChildren().remove(img_image);
            }

            if (!FileShare.filterImage(item.getUrlfile()) && item.getUrlfile() != null
                    && item.getUrlfile().length() > 1) {
                imgdownload.setImage(Loader.loadImage(R.Icon.download));
                btnfile.setText(item.getUrlfile());
                btnfile.setOnMouseClicked(this::handle);
            } else
                box.getChildren().remove(btnfile);

            if (item.getContent() != null && item.getContent().length() > 0)
                lbmessage.setText(item.getContent());
            else
                box.getChildren().remove(lbmessage);

            try {
                txtTime.setText(format.format(item.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            setGraphic(parent);
        }
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getSource() == btnfile) {
            try {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("Save file.");
                File dir = chooser.showDialog(new Stage());

                if(dir == null)
                    throw new IOException("Lỗi: thư mục Null.");

                Runnable runnable = () -> {
                        try {
                            Maker.fileShare().download(dir.getAbsolutePath(), btnfile.getText());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                };

                new Thread(runnable).start();
            } catch (IOException e) {
                Debug.out(e.getMessage());
            };

        }
    }
}
