package myworld.core.audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AudioOutputStream {
    private SourceDataLine source;
    private AudioFormat format;

    public AudioOutputStream() {
        format = AudioInputStream.getAudioFormat();
    }

    public AudioOutputStream open() throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        if (AudioSystem.isLineSupported(info)) {
            source = (SourceDataLine) AudioSystem.getLine(info);
            source.open();
            source.start();
        } else {
            Logger.getAnonymousLogger().log(Level.WARNING, "System not support audio!");
        }
        return this;
    }

    public boolean isOpen(){return source != null && source.isOpen();}

    public void write(byte[] data) throws IOException {
        if(source == null && !source.isOpen())
            throw new IOException("Luồng ghi dữ liệu chưa được mở.");

        source.write(data, 0, data.length);
    }

    public void Close() {
        if (source != null && (source.isOpen() || source.isRunning())) {
            source.flush();
            source.stop();
            source.close();
        }
    }

//    public static void main(String[] args) throws LineUnavailableException, IOException {
//        AudioInputStream inputStream = new AudioInputStream().open();
//        AudioOutputStream outputStream = new AudioOutputStream().open();
//
//        while (true){
//            byte[] data = inputStream.read();
//            outputStream.write(data);
//        }
//
//    }

    public SourceDataLine getSource() {
        return source;
    }

    public void setSource(SourceDataLine source) {
        this.source = source;
    }

    public AudioFormat getFormat() {
        return format;
    }

    public void setFormat(AudioFormat format) {
        this.format = format;
    }

}
