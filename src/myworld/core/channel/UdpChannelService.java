package myworld.core.channel;

import myworld.core.audio.AudioInputStream;
import myworld.core.audio.AudioOutputStream;
import myworld.core.transfer.DataTransfer;
import myworld.core.transfer.QuickData;
import myworld.debug.Debug;
import res.resource.R;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;

public class UdpChannelService extends Thread {
    private static final int KB = 1024;
    private static final int BUFFER_SIZE = 8 * KB;
    public static final String IP_HOST = "localhost";
    public static final int IP_PORT = 4040;

    public static final int MODE_READ = 0x0010;
    public static final int MODE_WRITE = 0x0020;
    public static final int MODE_ALL = MODE_READ | MODE_WRITE;

    private SelectionKey key;
    private Selector selector;
    private DatagramChannel channel;
    private ByteBuffer bfreader;
    private ByteBuffer bfwriter;
    private SocketAddress serverAddress;

    private AudioInputStream inputStream;
    private AudioOutputStream outputStream;

    public String header;
    public String footer;

    private boolean readable = true;
    private boolean writable = true;
    private boolean closed = false;


    public UdpChannelService() throws IOException, LineUnavailableException {
        bfreader = ByteBuffer.allocate(BUFFER_SIZE);
        bfwriter = ByteBuffer.allocate(BUFFER_SIZE);
        serverAddress = new InetSocketAddress(IP_HOST, IP_PORT);

        //tao luong in-out
        inputStream = new AudioInputStream();
        outputStream = new AudioOutputStream();

        selector = Selector.open();
        channel = createUdpChannel(selector);
    }

    public static DatagramChannel createUdpChannel(Selector selector) throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        return channel;
    }

    public void openInputStream() throws LineUnavailableException {
        inputStream.open();
        outputStream.open();
    }

    public void closeInputStream()  {
        inputStream.Close();
        outputStream.Close();
    }

    public void register(QuickData quick) throws IOException {
        //Day du lieu dang ky len server
        quick.compressed(bfreader);
        bfreader.flip();
        channel.send(bfreader, serverAddress);
        this.footer = quick.footer;

        bfwriter.clear();
        channel.receive(bfwriter);
        Debug.out("Kết quả: " + new String(DataTransfer.getBytesFormBuffer(bfwriter), "utf-8"));
    }

    public void sendTo(QuickData data) throws IOException {
        synchronized (channel){
            ByteBuffer bbw = ByteBuffer.allocate(BUFFER_SIZE);
            data.compressed(bbw);
            channel.send(bbw ,serverAddress);
        }
    }

    @Override
    public void run() {
        readable = writable = true;
        Debug.out("UDP channel running...");

        while (readable || writable) {

            try {
                boolean haskey = selector.selectNow() > 0;

                if (haskey) {
                    key = selector.selectedKeys().iterator().next();
                    selector.selectedKeys().clear();

                    //xu lu su kien doc
                    if (key.isValid() && key.isReadable() && readable) {
                        bfreader.clear();
                        channel.receive(bfreader);

                        byte[] data = DataTransfer.getBytesFormBuffer(bfreader);
                        handleReadData(data);
                    }
                }

                if (writable)
                    handleWriteData();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Debug.out("UDP channel close!");

    }

    @Override
    public synchronized void start() {
        if(closed)
            super.resume();
        else
            super.start();
    }

    public void handleReadData(byte[] data) throws IOException {
        if(data.length > 20)
            outputStream.write(Arrays.copyOfRange(data, 20, data.length));
    }

    public void handleWriteData() throws IOException {
        byte[] data = inputStream.read();
        QuickData quickData = QuickData.create(R.Cmd.RM_STT_RUNNING + "", footer, data);

        //lam sach vung nho
        bfwriter.clear();
        //nen du lieu
        quickData.compressed(bfwriter);
        //lat bo dem
        bfwriter.flip();
        channel.send(bfwriter, serverAddress);
    }

    public void closeAndStop() {
        closed = true;
        suspend();
        closeInputStream();
    }

    public boolean disable(int mode) {
        switch (mode) {
            //Mo che do doc
            case MODE_READ:
                readable = false;
                if (inputStream.isOpen())
                    inputStream.Close();
                return true;

            //mo che do ghi
            case MODE_WRITE:
                writable = false;
                if (outputStream.isOpen())
                    outputStream.Close();
                return true;

            //mo ca 2 che do doc va ghi
            case MODE_READ | MODE_WRITE:
                readable = writable = false;
                if (!inputStream.isOpen())
                    inputStream.Close();
                if (!outputStream.isOpen())
                    outputStream.Close();
                return true;
        }

        return false;
    }

    public boolean enable(int mode) throws LineUnavailableException {
        switch (mode) {
            //Mo che do doc
            case MODE_READ:
                readable = true;
                if (!inputStream.isOpen())
                    inputStream.open();
                return true;

            //mo che do ghi
            case MODE_WRITE:
                writable = true;
                if (!outputStream.isOpen())
                    outputStream.open();
                return true;

            //mo ca 2 che do doc va ghi
            case MODE_READ | MODE_WRITE:
                readable = writable = true;
                if (!inputStream.isOpen())
                    inputStream.open();
                if (!outputStream.isOpen())
                    outputStream.open();
                return true;
        }

        return false;
    }

    public static byte[] getbytes(byte[] data) throws UnsupportedEncodingException {
        String tmp = new String(data, "utf-8");
        tmp.substring(1, tmp.length() - 1);
        return tmp.getBytes("utf-16");
    }


    public static byte[] bytestring(byte[] data) throws UnsupportedEncodingException {
        return Arrays.toString(data).getBytes("utf-8");
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public boolean isReadable() {
        return readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }
}
