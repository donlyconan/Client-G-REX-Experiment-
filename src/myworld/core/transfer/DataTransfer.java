package myworld.core.transfer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import myworld.debug.Debug;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class DataTransfer {
    public static final String PARTITION = "<!xml>";
    public static final int TYPE_STRING = 1;
    public static final int TYPE_INT = 2;
    public static final int TYPE_FLOAT = 3;
    public static final int TYPE_DOUBLE = 4;
    public static final int TYPE_JSON = 5;
    public static final int TYPE_BINARY_ENCODE64 = 6;

    public int header;
    public int type;
    private String content;
    private int end;

    public DataTransfer() {
        // TODO Auto-generated constructor stub
    }


    private DataTransfer(int header, int type, String content, int end) {
        super();
        this.header = header;
        this.type = type;
        this.content = content;
        this.end = end;
    }

    public static byte[] getBytesFormBuffer(ByteBuffer buffer) {
        if(buffer.position() != 0)
            buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        return data;
    }


    /**
     * Tao nhanh mot data transfer
     * @param header
     * @param type
     * @param content
     * @param end
     * @return
     */
    public static DataTransfer create(int header, int type, String content, int end) {
        return new DataTransfer(header, type, content, end);
    }


    public static JsonObject toJsonFromString(String... parts)  {

        JsonObject json = new JsonObject();

        for(String part: parts){
            String[] lines = part.split(":");
            if(lines.length != 2)
                Debug.out("Lỗi! không phải định dạng json");
//                throw new IOException("Lỗi! không phải định dạng json");
            String key = lines[0];
            String val = lines[1];
            json.addProperty(key, val);
        }
        return json;
    };

    /**
     * Doc mot chuoi string va chuyen no ve dang object
     * @param res
     * @return
     * @throws IOException
     */
    public static DataTransfer readString(String res) throws IOException {
        byte[] bytes = res.getBytes("utf-8");
        return extract(bytes);
    }

	public <T extends Object> T getObjectFromJson(Class<T> cast_class) {
		return getObjectFromJson(content, cast_class);
	}

    /**
     * Modulo lay object tu mot chuoi json cho truoc
     * Class object ve dang class dua tren su anh xa khoa thuoc tinh
     * @param content
     * @param cast_class
     * @param <T>
     * @return
     */
	public static <T extends Object> T getObjectFromJson(String content, Class<T> cast_class) {
		Gson gson = new Gson();
		return gson.fromJson(content, cast_class);
	}

    //chuyen doi du lieu tu nhung mang byte nguyen thuy
    public static DataTransfer extract(byte[] bytes) throws IOException {
        String data = new String(bytes, "utf-8");
        String[] lines = data.split(PARTITION);

        if (lines.length < 4)
            throw new IOException("Lỗi định dạng dữ liệu!");

        int header = Integer.valueOf(lines[0]);
        int type = Integer.valueOf(lines[1]);
        int end = Integer.valueOf(lines[3].trim());

        String content = lines[2];

        return new DataTransfer(header, type, content, end);
    }

    //chuyen du lieu tu bo buffer
    public static DataTransfer extract(ByteBuffer buffer) throws IOException {
        byte[] data = DataTransfer.getBytesFormBuffer(buffer);
        return DataTransfer.extract(data);
    }

    public byte[] toBytes() throws UnsupportedEncodingException {
        return this.toString().getBytes("utf-8");
    }

    @Override
    public String toString() {
        return header + PARTITION + type + PARTITION + content + PARTITION + end;
    }

    public int getHeader() {
        return header;
    }

    public void setHeader(int header) {
        this.header = header;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

}
