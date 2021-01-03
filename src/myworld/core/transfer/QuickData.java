package myworld.core.transfer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class QuickData {
    public String header;
    public String footer;
    private byte[] data;

    private QuickData() {
        // TODO Auto-generated constructor stub
    }

    private QuickData(String header, String footer, byte[] data) {
        super();
        this.header = header;
        this.footer = footer;
        this.data = data;
    }

    public static QuickData create(String header, String footer, byte[] data) {
        if(data == null)
            data = new byte[]{0,0,0,0}; //zero
        return new QuickData(header, footer, data);
    }

    public static QuickData extract(ByteBuffer buffer) throws IOException {
        if(buffer.position() != 0)
            buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return extract(bytes);
    }

    public static QuickData extract(byte[] bytes) throws IOException {
        if (bytes.length < 20)
            throw new IOException("Gói tin không đầy đủ!");

        String header = new String(subBytes(bytes, 0, 10));
        String footer = new String(subBytes(bytes, 10, 20));
        byte[] data = subBytes(bytes, 20, bytes.length);

        return create(header, footer, data);
    }

    public static byte[] subBytes(byte[] bytes, int from, int to) {
        byte[] newbytes = new byte[to - from];
        Arrays.copyOfRange(newbytes, from, to);
        return newbytes;
    }

    //Nen tep tin
    public int compressed(ByteBuffer buffer) throws UnsupportedEncodingException {
        if(buffer.position() != 0)
            buffer.clear();

        byte[] byte_header = createHeader(header);
        byte[] byte_footer = createHeader(footer);
        buffer.put(byte_header);
        buffer.put(byte_footer);
        buffer.put(data);
        return byte_header.length + byte_footer.length + data.length;
    }

    public static byte[] createHeader(String header) throws UnsupportedEncodingException {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(header.getBytes("utf-8"));
        while (buffer.hasRemaining()) buffer.put((byte) 0x0);
        return buffer.array();
    }


    public byte[] data() {
        return data;
    }

    public void data(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "QuickData{" +
                "header='" + header + '\'' +
                ", footer='" + footer + '\'' +
                ", data=" + Arrays.toString(subBytes(data, 0, 50)) + "..."+
                '}';
    }
}
