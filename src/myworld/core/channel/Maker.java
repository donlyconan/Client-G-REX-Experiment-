package myworld.core.channel;

import myworld.core.ftp.FileShare;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class Maker {

    public static final String IP_HOST = "localhost";
    public static final int IP_PORT = 9050;

    private static SocketChannelService service;
    private static FileShare share;
    private static UdpChannelService UDPService;

    public static SocketChannelService makeService() throws IOException {
        if (service == null) {
            service = new SocketChannelService(IP_HOST, IP_PORT);
        }
        return service;
    }

    public static FileShare makeFileShare() throws IOException {
        if (share == null)
            share = new FileShare();
        return share;
    }

    public static UdpChannelService makeUdpService() throws IOException, LineUnavailableException {
        if(UDPService == null)
            UDPService = new UdpChannelService();
        return UDPService;
    }

    public static void out() throws IOException {
        if(service() != null)
            service().Stop();
    }

    public static void makeAll() throws IOException, LineUnavailableException {
        makeService();
        makeUdpService();
        makeFileShare();
    }

    public static void initStart() throws IOException {
        makeService().start();
    }


    public static SocketChannelService service() {
        return service;
    }

    public static FileShare fileShare() {return share;}

    public static UdpChannelService UDPService() {return UDPService;}

}
