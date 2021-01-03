package myworld.core.channel;

import myworld.core.event.ReceiveListener;
import myworld.core.transfer.DataTransfer;
import myworld.debug.Debug;
import res.resource.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

public class SocketChannelService extends Thread {
    private static final int KB = 1024;
    private static final int BUFFER_SIZE = 128 * KB;

    private String host;
    private int port;
    private Queue<Object> queue;
    private SocketChannel channel;
    private ByteBuffer bfreader;
    private ByteBuffer bfwriter;
    private boolean running;
    private boolean autoconnect;
    private Set<ReceiveListener> listeners;


    public SocketChannelService(String host, int port) throws IOException {
        bfreader = ByteBuffer.allocate(BUFFER_SIZE);
        bfwriter = ByteBuffer.allocate(BUFFER_SIZE);
        running = false;
        autoconnect = true;
        this.host = host;
        this.port = port;
        channel = createSocketChannel(host, port);
        queue = new LinkedList<Object>();
        listeners = new HashSet<>();
        super.setDaemon(true);
    }

    public static SocketChannel createSocketChannel(String host, int port) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(host, port));
        channel.configureBlocking(true);
        if (channel.isConnected())
            System.out.println("Kết nối thành công!");
        return channel;
    }

    // chay client don luong
    public void clientRunning() {
        Debug.out("Start channel...");
        running = true;

        while (running) {
            try {

                //Nhan toan bo du lieu giu den
                byte[] data = readAll();

                Debug.out(new String(data));

                //Chuyen doi dang du lieu
                DataTransfer transfer = DataTransfer.extract(data);

                //xu ly du lieu duoc chuyen doi
                listeners.forEach(e -> e.receive(transfer.header, transfer.type, transfer.getContent()));

            } catch (IOException e) {
                listenConnectable();
                Debug.out(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

        //Close
        running = false;
    }


    private void listenConnectable() {
        try {
            channel.close();
            Debug.out("Try re-connect...");
            if (autoconnect) {
                channel = createSocketChannel(host, port);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] readAll() throws IOException {
        bfreader.clear();
        int read = channel.read(bfreader);

        //do lien tiep cho den khi gap byte 0x00
        while (!hasEndRead()) {
            read += channel.read(bfreader);
        }

        if (read == -1) {
            listenConnectable();
            Stop();
            Debug.out("Error Read byte: " + read);
        }

        // dao nguoc bo doc
        bfreader.flip();
        byte[] data = new byte[bfreader.remaining()];
        //Truy du lieu sang mang data
        bfreader.get(data);

        return data;
    }

    public void addListener(ReceiveListener listener) {
        listeners.add(listener);
    }


    //Ghi toan bo du lieu tu queue vao stream
    public int writeAll() throws IOException {
        int write = 0;

        while (!queue.isEmpty()) {
            Object data = queue.poll();
            write += writeData(data);
        }

        return write;
    }

    //ghi du lieu tu 1 object vao stream
    public int writeData(Object data) throws IOException {
        bfwriter.clear();
        int write = 0;

        Debug.out(String.format("Send: %s", data));

        // neu o dang byte
        if (data instanceof byte[])
            bfwriter.put((byte[]) data);

            // neu data o dang Datatransfer
        else if (data instanceof DataTransfer)
            bfwriter.put(((DataTransfer) data).toBytes());

            //neu o danh string
        else if (data instanceof String)
            bfwriter.put(((String) data).getBytes("utf-8"));

        else
            throw new IOException("Kiểu dữ liệu không hợp lệ!");

//        day byte ket thuc vao bo dem
        bfwriter.flip();

        synchronized (channel) {
            //ghi chuoi byte vao luong
            while (bfwriter.hasRemaining())
                write += channel.write(bfwriter);
        }

        return write;
    }

    public void Stop() {
        running = false;
        synchronized (this) {
            super.stop();
        }
        try {
            close();
        } catch (IOException e) {
            Debug.out(e.getLocalizedMessage() + "" + e.getMessage());
        }
    }

    public void close() throws IOException {
        try{
            DataTransfer data = DataTransfer.create(R.Cmd.END_CONNECT,
                    DataTransfer.TYPE_STRING, "0", 0);
            this.writeData(data);
            autoconnect = false;
        } finally {
            channel.shutdownInput();
            channel.shutdownOutput();
            channel.finishConnect();
            channel.close();
        }
    }


    @Override
    public void run() {
        clientRunning();
    }

    public boolean hasEndRead() {
        int pos = bfreader.position();
        if(pos == 0) return false;
        byte[] data = Arrays.copyOfRange(bfreader.array(), pos-2, pos);
        return new String(data).contains("0");
    }


    public Queue<Object> getQueue() {
        return queue;
    }

    public void setQueue(Queue<Object> queue) {
        this.queue = queue;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public ByteBuffer getBfreader() {
        return bfreader;
    }

    public void setBfreader(ByteBuffer bfreader) {
        this.bfreader = bfreader;
    }

    public ByteBuffer getBfwriter() {
        return bfwriter;
    }

    public void setBfwriter(ByteBuffer bfwriter) {
        this.bfwriter = bfwriter;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isAutoconnect() {
        return autoconnect;
    }

    public void setAutoconnect(boolean autoconnect) {
        this.autoconnect = autoconnect;
    }
}
