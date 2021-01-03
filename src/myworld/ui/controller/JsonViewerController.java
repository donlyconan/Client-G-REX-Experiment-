package myworld.ui.controller;

import com.google.gson.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import myworld.core.channel.Maker;
import myworld.core.event.ReceiveListener;
import myworld.core.transfer.DataTransfer;
import myworld.debug.Debug;
import myworld.ui.ApplicationRun;
import res.resource.Loader;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class JsonViewerController implements Initializable, ReceiveListener {

    @FXML ComboBox cb_cmd;
    @FXML TextArea txt_json;
    @FXML TextField txt_cmd;
    @FXML Button btn_excute;
    @FXML ContextMenu menu_context;

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    static Map<String, Integer> map = new HashMap<>();
    static SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Maker.service().addListener(this::receive);

        try {
            map = Loader.loadConfig();
            Iterator set = map.keySet().iterator();

            while (set.hasNext())
                cb_cmd.getItems().add(set.next());

            cb_cmd.getSelectionModel().select(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        txt_cmd.setOnContextMenuRequested(this::contextRequest);
        menu_context.getItems().forEach(e->e.setOnAction(this::menuItemAction));
        btn_excute.setOnAction(this::onActionExcute);
    }

    private void menuItemAction(ActionEvent actionEvent) {
        String text = ((MenuItem)actionEvent.getSource()).getText().toLowerCase();

        if(text.contains("copy")){
            StringSelection selection = new StringSelection(txt_json.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        }

        if(text.contains("clear"))
            txt_json.setText(null);
    }

    private void contextRequest(ContextMenuEvent e) {
        menu_context.show(txt_cmd, e.getScreenX(), e.getScreenY());
    }

    private void onActionExcute(ActionEvent actionEvent) {
        try {
            String text = txt_cmd.getText() + "";
            String select = cb_cmd.getSelectionModel().getSelectedItem() + "";

            if (select != null) {
                DataTransfer data = null;

                if(text.contains(":")){
                    JsonObject json = DataTransfer.toJsonFromString(text.split(","));

                    data = DataTransfer.create(
                            map.get(select), DataTransfer.TYPE_JSON, json.toString(), 0
                    );
                } else
                    data = DataTransfer.create(
                            map.get(select), DataTransfer.TYPE_STRING, txt_json.getText(), 0
                    );


                try {
                    Maker.service().writeData(data);
                    txt_json.appendText(String.format("\nSend: %s", data.toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            txt_json.setText(null);
        }catch (Exception e){
            Debug.out(e.getMessage());
        }
    }

    @Override
    public void receive(int cmd, int type, String response) {

        if(MainController.current_fxml == ApplicationRun.FXML_JSON_VIEWER)
        {
            synchronized (txt_json) {
                txt_json.appendText("\nTime " + format.format(Calendar.getInstance().getTime()) + String.format(": %s\n",cmd));

                if (type == DataTransfer.TYPE_JSON) {
                    String text = null;
                    try {
                        JsonObject json = (JsonObject) JsonParser.parseString(response);
                        text = gson.toJson(json);
                    }catch (Exception e){
                        JsonArray json = (JsonArray) JsonParser.parseString(response);
                        text = gson.toJson(json);
                    }

                    txt_json.appendText(text);
                } else {
                    txt_json.appendText(response);
                }
            }

        }
    }
}
