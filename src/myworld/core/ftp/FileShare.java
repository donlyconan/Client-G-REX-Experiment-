package myworld.core.ftp;

import myworld.debug.Debug;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import res.resource.Loader;
import res.resource.R;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class FileShare {
    public static final List<String> Image = Arrays.asList(".png", ".jpeg", ".jpg", ".svg");

    private FTPClient ftpClient;

    public FileShare() throws IOException {
        this.ftpClient = new FTPClient();
    }

    public void login(){
        try {
            ftpClient.connect("localhost", 21);
            ftpClient.login("anonymous", "xxxx");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String upload(String dir, File filename) throws IOException {
        if(!filename.exists())
            throw  new IOException(String.format("File %s don't exists!", filename.getName()));

        login();
        FileInputStream inputStream = new FileInputStream(filename);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.storeFile(String.format("%s/%s", dir, filename.getName()), inputStream);
        inputStream.close();
        ftpClient.logout();
        Debug.out("Upload successfull!");
        return String.format("ftp://localhost/%s/%s", dir, filename.getName());
    }

    public void download(String dir, String url) throws IOException {
        InputStream stream = new URL(url).openStream();
        String filename = url.substring(url.lastIndexOf("/"), url.length());
        FileOutputStream outputStream = new FileOutputStream(String.format("%s/%s",dir, filename));

        while (stream.available() > 0){
            outputStream.write(stream.read());
        }
        stream.close();
        outputStream.flush();
        outputStream.close();
        Debug.out("Download successful!");
    }

    public static boolean filterImage(String path) {
        if (path == null)
            return false;
        int index = path.lastIndexOf('.');
        if (index == -1)
            return false;
        String extend = path.substring(index, path.length());
        return Image.contains(extend);
    }

    public InputStream getInputStream(String url) {
        return getInputStream(url, R.Icon.image);
    }

    public InputStream getInputStream(String url, String resource) {
        InputStream inputStream = null;
        try {
            if(url != null && url.contains("ftp://"))
                inputStream = new URL(url).openStream();
            if(inputStream == null)
                inputStream = Loader.getInputStream(resource);
        } catch (IOException e){
            e.printStackTrace();
        }
        return inputStream;
    }


    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
