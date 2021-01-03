package myworld.ui;

public class ClientTester  {
    public static void main(String[] args) {
        String st = "123";
        Class<String> t = (Class<String>) st.getClass();

        System.out.println(t == String.class);
    }
}
