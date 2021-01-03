package myworld.ui.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import myworld.core.channel.Maker;
import myworld.core.event.ReceiveListener;
import myworld.core.model.Weather;
import myworld.core.transfer.DataTransfer;
import myworld.ui.ApplicationRun;
import res.resource.R;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class HistoryController implements Initializable, ReceiveListener{

    @FXML DatePicker txtdatetime;
    @FXML ImageView btn_search;
    @FXML TableView<Weather.WeatherInfo> tb_now;
    @FXML Label lb_now;
    @FXML ImageView img_now;
    @FXML ImageView close_pane;
    @FXML ComboBox cb_diadiem;

    Map<String, String> mapplace;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Maker.service().addListener(this::receive);
        mappingColumns();
        mapplace = WeatherController.getMapPlace();

        mapplace.keySet().forEach(cb_diadiem.getItems()::add);
        cb_diadiem.getSelectionModel().select(0);

        close_pane.setOnMouseClicked(this::closePane);

        btn_search.setOnMouseClicked(this::onActionSearch);
    }

    private void closePane(MouseEvent mouseEvent) {
        Parent parent = ApplicationRun.MapScene.get(ApplicationRun.FXML_WEATHER);
        parent.toFront();
    }

    private void mappingColumns() {
        List<String> properties = Arrays.asList("thoigian", "dubao", "nhietdo", "mua", "khiap", "gio");
        for (int i = 0; i < properties.size(); i++){
            TableColumn column = tb_now.getColumns().get(i);
            column.setCellValueFactory(new PropertyValueFactory<>(properties.get(i)));
        }

    }

    private void onActionSearch(MouseEvent mouseEvent) {
        if(txtdatetime.getValue() == null)
            return;

        String content = txtdatetime.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String key = cb_diadiem.getSelectionModel().getSelectedItem().toString();
        String place = mapplace.get(key);
        tb_now.getItems().clear();

        if(content.length() > 1){
            JsonObject json = DataTransfer.toJsonFromString("masv:" + ApplicationRun.getCurrentUser().getMasv()
                    ,"diadiem:"+place, "thoigian:"+content);
            DataTransfer data = DataTransfer.create(R.Cmd.SEARCH_HISTORY , DataTransfer.TYPE_JSON,
                    json.toString(), 0);
            try {
                Maker.service().writeData(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void receive(int cmd, int type, String response) {
        if(cmd == R.Cmd.SEARCH_HISTORY){
            Gson gson = new Gson();
            JsonObject json = (JsonObject) JsonParser.parseString(response);
            JsonObject jsonweather = json.getAsJsonObject("ketqua");

            if(json.get("result").getAsInt() == 1) {
                String url_current_weather = jsonweather.get("img-current-weather").getAsString();
                Weather[] list = gson.fromJson(jsonweather.getAsJsonArray("tables"), Weather[].class);

                Runnable runnable = () -> {
                    String title = list[0].getTitle();
                    lb_now.setText(title.substring(title.indexOf(',') + 1, title.length())
                            + "\t\t Thời gian tìm kiếm: " + json.get("thoigian").getAsString());
                    tb_now.setItems(FXCollections.observableList(list[0].getInfo()));

                    if(img_now != null && url_current_weather.startsWith("http")){
                        Image image = new Image(url_current_weather);
                        img_now.setImage(image);
                    }
                };

                Platform.runLater(runnable);
            } else  {

                Platform.runLater(()->lb_now.setText("Không tìm được kết quả nào phù hợp."));

            }
        }
    }

}
