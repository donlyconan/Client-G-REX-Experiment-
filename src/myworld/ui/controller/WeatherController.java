package myworld.ui.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import myworld.core.channel.Maker;
import myworld.core.event.OpenListener;
import myworld.core.event.ReceiveListener;
import myworld.core.model.Weather;
import myworld.core.transfer.DataTransfer;
import myworld.debug.Debug;
import myworld.ui.ApplicationRun;
import res.resource.R;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class WeatherController implements Initializable, OpenListener, ReceiveListener {
    public static final int HOUR = 3600 * 1000;
    public static final SimpleDateFormat simple = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    static OpenListener openListener;


    @FXML VBox vbox;
    @FXML Label lb_now;
    @FXML ImageView img_now;
    @FXML TableView<Weather.WeatherInfo> tb_now;
    @FXML Label lb_tomorrow;
    @FXML ImageView img_tomorrow;
    @FXML TableView<Weather.WeatherInfo> tb_tomorrow;
    @FXML Label lb_weak;
    @FXML ImageView img_weak;
    @FXML TableView<Weather.WeatherInfo> tb_weak;
    @FXML Text time_now;
    @FXML ComboBox cb_diadiem;
    @FXML ComboBox cb_thoigian;
    @FXML ComboBox cb_update;
    @FXML Label txtdiadiem;
    @FXML ImageView img_current_weather;
    @FXML ImageView img_search;

    Timer timer;
    Timer timerupdate;
    Map<String, String> mapplace;
    Parent history_pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Debug.out("Load WeatherController...");
        mappingColumns();
        mapplace = getMapPlace();
        history_pane = ApplicationRun.MapScene.get(ApplicationRun.FXML_SEARCH_HISTORY);
        openListener = this::open;

        //Thiet lap timer
        timer = new Timer(1000, e->time_now.setText(simple.format(Calendar.getInstance().getTime())));

        //Dang ky su kien nhan du lieu tu server
        Maker.service().addListener(this::receive);
        ApplicationRun.listeners.add(this::open);


        cb_update.setItems(FXCollections.observableList(Arrays.asList("1 Giờ", "3 Giờ", "6 Giờ", "24 Giờ")));
        cb_update.getSelectionModel().select(0);
        cb_update.setOnMouseClicked(this::onClickItemUpdate);
        timerupdate = new Timer(HOUR , this::onRequestWeather);


        mapplace.keySet().forEach(cb_diadiem.getItems()::add);
        cb_diadiem.getSelectionModel().select(0);
        cb_diadiem.setOnAction(e->onRequestWeather(null));

        img_search.setOnMouseClicked(e->history_pane.toFront());

        cb_thoigian.setOnAction(this::onClickItemTime);
        timer.start();
    }

    private void onClickItemTime(Event event) {
        int index = cb_thoigian.getSelectionModel().getSelectedIndex();
        ObservableList children = vbox.getChildren();

        if(index == -1)
            return;

        children.clear();

        switch (index){
            case 0:
                children.addAll(lb_now, tb_now);
                break;
            case 1:
                children.addAll(lb_tomorrow, tb_tomorrow);
                break;
            case 2:
                children.addAll(lb_weak ,tb_weak);
                break;
            default:
                if(children.size() < 3)
                    children.addAll(lb_now, tb_now, lb_tomorrow,tb_tomorrow, lb_weak,tb_weak);
        }
    }

    private void onRequestWeather(ActionEvent actionEvent) {
        String key = cb_diadiem.getSelectionModel().getSelectedItem().toString();
        String place = mapplace.get(key);
        JsonObject json = DataTransfer.toJsonFromString("diadiem:" + place, "masv:" + ApplicationRun.getCurrentUser().getMasv());
        DataTransfer data = DataTransfer.create(R.Cmd.GET_CURRENT_WEATHER, DataTransfer.TYPE_JSON,
                 json.toString() , 0);
        try {
            Maker.service().writeData(data);
            Debug.out(data.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onClickItemUpdate(MouseEvent mouseEvent) {
        int index = cb_update.getSelectionModel().getSelectedIndex();

        if(index == 0) timerupdate.setDelay(HOUR);
        if(index == 1) timerupdate.setDelay(3 * HOUR);
        if(index == 2) timerupdate.setDelay(6 * HOUR);
        if(index == 3) timerupdate.setDelay(24 * HOUR);

        Debug.out(String.format("Update weather after: %s ms", timerupdate.getDelay()));
    }

    private void mappingColumns() {
        List<String> properties = Arrays.asList("thoigian", "dubao", "nhietdo", "mua", "khiap", "gio");
        for (int i = 0; i < properties.size(); i++){
            TableColumn column = tb_now.getColumns().get(i);
            column.setCellValueFactory(new PropertyValueFactory<>(properties.get(i)));
            column = tb_tomorrow.getColumns().get(i);
            column.setCellValueFactory(new PropertyValueFactory<>(properties.get(i)));
            column = tb_weak.getColumns().get(i);
            column.setCellValueFactory(new PropertyValueFactory<>(properties.get(i)));
        }

    }

    @Override
    public void receive(int cmd, int type, String response) {
        if(cmd == R.Cmd.GET_CURRENT_WEATHER){
            Gson gson = new Gson();
            JsonObject json = (JsonObject) JsonParser.parseString(response);
            String title_place = json.get("title-place").getAsString();
            String url_current_weather = json.get("img-current-weather").getAsString();

            Weather[] list = gson.fromJson(json.getAsJsonArray("tables"), Weather[].class);


            Runnable runnable = () -> {
                txtdiadiem.setText(title_place);

                lb_now.setText(list[0].getTitle());
                tb_now.setItems(FXCollections.observableList(list[0].getInfo()));

                lb_tomorrow.setText(list[1].getTitle());
                tb_tomorrow.setItems(FXCollections.observableList(list[1].getInfo()));

                lb_weak.setText(list[2].getTitle());
                tb_weak.setItems(FXCollections.observableList(list[2].getInfo()));

                if(img_current_weather != null && url_current_weather.startsWith("http")){
                    Image image = new Image(url_current_weather);
                    img_current_weather.setImage(image);
                }

                cb_thoigian.getItems().clear();
                cb_thoigian.getItems().addAll(list[0].getTitle(), list[1].getTitle(), list[2].getTitle(), "Trong 3 ngày tới.");
                cb_thoigian.getSelectionModel().select(3);
            };

            Platform.runLater(runnable);
        }
    }

    @Override
    public void open(String fxml, Parent parent)  {
        if(fxml == ApplicationRun.FXML_WEATHER && !timerupdate.isRunning()){
            onRequestWeather(null);
            timerupdate.start();
        }

        if(fxml == null && parent == null){
            history_pane.toBack();
        }
    }

    public static Map<String, String> getMapPlace(){
        Map<String, String> map = new HashMap<>();
        map.put("Nam Định", "NAMDINH");
        map.put("Sơn La", "SONLA");
        map.put("Ninh Bình", "NINHBINH");
        map.put("Tp. Hồ Chí Minh", "HCM");
        map.put("Cà Mau", "CAMAU");
        map.put("Hà Nội", "HANOI");
        return map;
    }


}
