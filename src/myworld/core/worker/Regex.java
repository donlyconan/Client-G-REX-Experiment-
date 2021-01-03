package myworld.core.worker;

import java.util.regex.Pattern;

public class Regex {


    public static final String REGEX_URL = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    public static final String REGEX_EMAIL = "\\b[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}\\b";

    //la chu so chua it nhat 6 ky tu
    public static final String REGEX_PASSWORD = "^(?=.*[a-zA-Z0-9])(?=\\S+$).{6,}$";

    public static final String REGEX_USERNAME = "^[a-zA-Z0-9\\_\\-\\S]{3,40}";

    public static final String REGEX_PHONE_NUMBER = "(09|08|01[2|6|8|9])+([0-9].{7,12})\\b";

    public static final String REGEX_ADDRESS = ".{3,200}";

    public static final String REGEX_FILE = "file: (\\w:+\\\\)+([\\w\\s]+\\\\)*+[\\w\\s_-]*+\\.\\w*";

    public static final String REGEX_NAME = "^([a-zA-Z]*(\\s))+[a-zA-Z]*$";

    public static final String REGEX_CALL_PHONE = "call-phone: [\\w\\s-:]*";

    /**
     * Kiểm tra sự hợp lệ của ký tự (User-Password - name, email, address, phone number)
     * ^ # bắt đầu của chuỗi
     * (? =. * [0-9]) một chữ số phải xảy ra ít nhất một lần
     * (? =. * [a-z]) một chữ cái viết thường phải xảy ra ít nhất một lần
     * (? =. * [A-Z]) một chữ in hoa phải xảy ra ít nhất một lần
     * (? =. * [@ # $% ^ & + =]) chứa ký tự đăc biệt
     * (? = \ S + $) loại bỏ khoảng trăng
     * .{8,}  ít nhất 8 ký tự
     */

    public static boolean validate(String input, String regex) {
        if (input == null) return false;
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input).matches();
    }
}
