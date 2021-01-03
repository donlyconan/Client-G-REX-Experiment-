package myworld.core.test;

import javax.swing.*;

public class Tester  {
    static Timer timer = null;


    public static void main(String[] args) throws InterruptedException {
        timer = new Timer(2000, e-> System.out.println("Tester: " + timer.getDelay()));
        timer.start();

        Thread.sleep(5000);

        timer.setDelay(1000);

        Thread.sleep(10000);

    }


}
