package myworld.core.audio;

import javazoom.jl.player.Player;

import java.io.InputStream;

public class SoundManager {
    public static class Audio {
        public static final String MESSAGE = "audio/Message.mp3";
        public static final String CALLPHONE = "audio/callphone.mp3";
        public static final String ENDCALL = "audio/endcall.mp3";
    }

    private static Player player;


    public static void playFromStream(InputStream stream) {

        try {
            player = new Player(stream);
            player.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void playFromResource(String resource) {
        InputStream inputStream = SoundManager.class.getResourceAsStream(resource);
        playFromStream(inputStream);
    }


    public static void stop() {
        if (player != null)
            player.close();
    }

}
