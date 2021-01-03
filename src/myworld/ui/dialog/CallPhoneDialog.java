package myworld.ui.dialog;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import myworld.debug.Debug;
import res.resource.Loader;
import res.resource.R;

import javax.swing.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CallPhoneDialog extends Dialog {
    @FXML Button btn_huybo;
    @FXML Button btn_chapnhan;
    @FXML Text txt_time;
    @FXML Text txt_status;

    Timer timer;
    int time = 0;

    public CallPhoneDialog() {
        super();
        setTitle("Cuộc gọi");
        try {
            FXMLLoader loader = new FXMLLoader(Loader.getResource(R.Fxml.CallPhone));
            loader.setController(this);
            Parent root = loader.load();
            getDialogPane().setContent(root);


            getDialogPane().getScene().getWindow().setOnCloseRequest(e -> {
                Debug.out("Close dialog.");
                setResult(true);
                close();
            });
            txt_time.setText("00:00:00");


            timer = new Timer(1000, e-> txt_time.setText(convertTime(time++)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    public static String convertTime(int time){
        Date date = new Date(61200000 + time * 1000);
        return format.format(date);
    }


    public void changestatus(String text){
        txt_status.setText(text);
    }


    public void register(EventHandler<MouseEvent> eventHandler){
        btn_huybo.setOnMouseClicked(eventHandler);
        btn_chapnhan.setOnMouseClicked(eventHandler);
    }

    public Button getBtn_huybo() {
        return btn_huybo;
    }

    public void setBtn_huybo(Button btn_huybo) {
        this.btn_huybo = btn_huybo;
    }

    public Button getBtn_chapnhan() {
        return btn_chapnhan;
    }

    public void setBtn_chapnhan(Button btn_chapnhan) {
        this.btn_chapnhan = btn_chapnhan;
    }

    public Text getTxt_time() {
        return txt_time;
    }

    public void setTxt_time(Text txt_time) {
        this.txt_time = txt_time;
    }

    public Text getTxt_status() {
        return txt_status;
    }

    public void setTxt_status(Text txt_status) {
        this.txt_status = txt_status;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public long getTime() {
        return time;
    }
}
