package myworld.core.worker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatHtml {





    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder("toi co mot doan van ban the nay nay khong hieu\n" +
                "\n" +
                "\n" +
                "https://shopee.com.vn\n" +
                "\n" +
                "donlyconan@gmail.com\n" +
                "\n" +
                "09041126060\n" +
                "style=\"tetster\"" +
                "\n" +
                "09192838724");

        Pattern pattern = Pattern.compile(Regex.REGEX_PHONE_NUMBER);
        Matcher matcher = pattern.matcher(builder.toString());

        while(matcher.find()){
            System.out.println(matcher.start());
            System.out.println(matcher.group());

            builder.replace(matcher.start(), matcher.start() + matcher.group().length()
                    , String.format("<h1>%s</h1>", matcher.group()));
        }

        System.out.println(builder);



    }


}
