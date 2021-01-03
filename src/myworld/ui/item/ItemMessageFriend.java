package myworld.ui.item;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import myworld.core.channel.Maker;
import myworld.core.model.Message;
import myworld.core.model.MessageFriend;
import myworld.core.model.User;
import myworld.ui.ApplicationRun;
import res.resource.Loader;
import res.resource.R;

import java.io.IOException;
import java.io.InputStream;

public class ItemMessageFriend extends ListCell<MessageFriend> {
    private static final String SEEN_FALSE = "-fx-font-weight: bold; -fx-font-size: 12;";
    private static final String SEEN__TRUE = "-fx-font-weight: normal; -fx-font-size: 12;";
    @FXML Circle img_avatar;
    @FXML Label lb_username;
    @FXML Label lb_message;
    @FXML Circle img_status;

    Parent parent;

    public ItemMessageFriend() throws IOException {
        FXMLLoader loader = new FXMLLoader(Loader.getResource(R.Fxml.ItemMessageFriend));
        loader.setController(this);
        parent = loader.load();
        setPrefWidth(0);
        setHeight(40);
    }

    public static ItemMessageFriend createMessageFriend() {
        try {
            return new ItemMessageFriend();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void updateItem(MessageFriend item, boolean empty) {
        super.updateItem(item, empty);
        User user = ApplicationRun.getCurrentUser();

        if (empty) {
            setText(null);
            Node node = new Text();
            node.setStyle("-fx-background-color: transparent;");
            setGraphic(node);
        } else {
            InputStream inputStream = null;
            inputStream = Maker.fileShare().getInputStream(item.getUrl_avatar());
            Image image = new Image(inputStream);
            img_avatar.setFill(new ImagePattern(image));
            lb_username.setText(item.getFriend_name());

            Message message = item.getLast_message();
            String content = (message.getContent() == null || message.getContent().length() < 1 ? message.getUrlfile(): message.getContent());

            if(message.getSender_id() == ApplicationRun.getCurrentUser().getMasv())
                content = String.format("You: %s", content);

            lb_message.setText(content);


            if (item.isOnline())
                img_status.setFill(Color.color(00, 1.0, 00));
            else
                img_status.setFill(Color.RED);

            if (!item.getLast_message().isSeen()) {
                lb_message.setStyle(SEEN_FALSE);
            } else{
                lb_message.setStyle(SEEN__TRUE);
            }

            setGraphic(parent);
        }

    }

}
