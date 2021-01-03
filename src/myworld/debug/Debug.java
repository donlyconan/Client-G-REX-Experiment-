package myworld.debug;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Debug {
    public static final SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");


    public static void out(Level level, String content) {
        Logger.getAnonymousLogger().log(level, content);
    }

    public static void out(String content) {
        System.out.println(simple.format(Calendar.getInstance().getTime())+": " + content);
    }


}
