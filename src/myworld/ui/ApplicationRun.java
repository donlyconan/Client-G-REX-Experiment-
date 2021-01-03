package myworld.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import myworld.core.channel.Maker;
import myworld.core.event.OpenListener;
import myworld.core.model.User;
import res.resource.Loader;
import res.resource.R;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ApplicationRun extends Application {
    public static final Map<String, Parent> MapScene = new HashMap<>();
    private static User currentUser = new User();
    public static Set<OpenListener> listeners = new HashSet<>();
    public static final String FXML_MESSAGE = "fxml_message";
    public static final String FXML_WEATHER = "fxml_weather";
    public static final String FXML_FILE = "fxml_file_message";
    public static final String FXML_FRIEND = "fxml_friend_message";
    public static final String FXML_LOGIN = "fxml_login";
    public static final String FXML_PERSONAL_INFO = "fxml_personal_info";
    public static final String FXML_NOTIFICATION = "fxml_notification";
    public static final String FXML_JSON_VIEWER = "fxml_json_viewer";
    public static final String FXML_SEARCH_HISTORY = "fxml_search_history";

    private static Stage stage;
    public static Rectangle2D bounds;

    private boolean loadFxmlResource() throws IOException, LineUnavailableException {
        Maker.makeAll();
        Maker.initStart();

        try {
            MapScene.put(FXML_SEARCH_HISTORY, Loader.getParentFromUrl(R.Fxml.SearchHistory));
            MapScene.put(FXML_JSON_VIEWER, Loader.getParentFromUrl(R.Fxml.JsonViewer));
            MapScene.put(FXML_MESSAGE, Loader.getParentFromUrl(R.Fxml.MessageController));
            MapScene.put(FXML_WEATHER, Loader.getParentFromUrl(R.Fxml.MyWeather));
            MapScene.put(FXML_PERSONAL_INFO, Loader.getParentFromUrl(R.Fxml.PersonalInfo));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadFxmlResource();

        Parent parent = Loader.getParentFromUrl(R.Fxml.LoginController);
        Scene scene = new Scene(parent);

        primaryStage.setScene(scene);
        stage = primaryStage;
        primaryStage.getIcons().add(Loader.loadImage(R.Icon.dashbroad));
        primaryStage.setOnCloseRequest(e-> {
            try {
                Maker.out();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.exit(0);
        });
        primaryStage.setTitle("My World");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void showMainController()  {
        Runnable run = () -> {
            try {
                stage().hide();
                stage.setScene(new Scene(Loader.getParentFromUrl(R.Fxml.MyWorldController)));
                stage().centerOnScreen();
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Platform.runLater(run);
    }

    public static void setCurrentUser(User user){
        currentUser = user;
    }

    public static User getCurrentUser(){return currentUser;}

    public static Stage stage(){return stage;}


    public static void main(String[] args) {  launch(args);  }
}
