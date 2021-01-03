package myworld.core.validate;

import java.util.regex.Pattern;

public class Validate {
    public static final String REGEX_USERNAME = "{6,20}$";
    public static final String REGEX_PASSWORD = "{6,50}$";


    public static boolean valid(String text, String Regex) {
        if (text == null)
            return false;
        Pattern pattern = Pattern.compile(Regex);
        return pattern.matcher(text).matches();
    }

}
