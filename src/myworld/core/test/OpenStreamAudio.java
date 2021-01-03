package myworld.core.test;

import myworld.core.channel.UdpChannelService;
import myworld.core.transfer.QuickData;
import res.resource.R;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class OpenStreamAudio {


    public static void main(String[] args) throws IOException, LineUnavailableException {
        UdpChannelService service = new UdpChannelService();
        service.openInputStream();
        service.register(QuickData.create(R.Cmd.RM_ACT_REGISTER + "","1712101", null));
        service.start();
    }





}
