package myworld.core.audio;


import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat.Encoding;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AudioInputStream {
    public static final int STANDARD_SIZE = 512; //512 byte

    private TargetDataLine target;
    private AudioFormat format;

    public AudioInputStream()  {
        format = getAudioFormat();
    }

    public AudioInputStream open() throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (AudioSystem.isLineSupported(info)) {
            target = (TargetDataLine) AudioSystem.getLine(info);
            target.open();
            target.start();
        } else {
            Logger.getAnonymousLogger().log(Level.WARNING, "System not support!");
        }
        return this;
    }

    public boolean isOpen(){return target != null && target.isOpen(); }

    public void read(byte[] data) {
        target.read(data, 0, data.length);
    }

    public byte[] read() throws IOException {
        if (target == null || !target.isOpen())
            throw new IOException("Luồng dữ liệu chưa được mở.");

        byte[] data = new byte[STANDARD_SIZE];
        target.read(data, 0 , data.length);
        return data;
    }


    public void Close() {
        if (target != null && (target.isOpen() || target.isRunning())) {
            target.flush();
            target.stop();
            target.close();
        }
    }

    public static AudioFormat getAudioFormat() {
        AudioFormat audio = new AudioFormat(Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
        return audio;
    }

//    public static void main(String[] args) throws LineUnavailableException, IOException, InterruptedException {
//        AudioInputStream inputStream = new AudioInputStream().open();
//        AudioOutputStream outputStream = new AudioOutputStream().open();
//
//
//
//        while (true){
//            byte[] data = inputStream.read();
//            data = new String(data).getBytes("utf-16");
//            System.out.println("Send: " + Arrays.toString(data));
//
//            data = new String(data, "utf-16").getBytes();
//            System.out.println("Recv: " + Arrays.toString(data));
//
//            Thread.sleep(1000);
////            outputStream.write(data);
//        }
//    }

    public TargetDataLine getTarget() {
        return target;
    }

    public void setTarget(TargetDataLine target) {
        this.target = target;
    }

    public AudioFormat getFomat() {
        return format;
    }

    public void setFomat(AudioFormat fomat) {
        this.format = fomat;
    }
}
