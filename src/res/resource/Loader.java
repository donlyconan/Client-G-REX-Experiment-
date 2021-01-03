package res.resource;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.util.*;

public class Loader {
	public static final String FILE = "data/";
	public static final String FILE_ICON = "icon/";
	public static final String FILE_FXML = "fxml/";
	public static final String FILE_AUDIO = "audio/";


	public static URL getResource(String pathname) {
		return Loader.class.getResource(pathname);
	}

	public static Map<String, Integer> loadConfig() throws IOException {
		Map<String, Integer> map = new HashMap<>();
		InputStream inputStream = Loader.getInputStream(R.Mapurl.config);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		String line = null;

		while ((line=reader.readLine()) != null){
			String[] lines = line.split(":");

			if(lines.length == 2)
				map.put(lines[0], Integer.valueOf(lines[1]));
		}

		reader.close();
		return map;
	}


	public static Parent getParentFromUrl(String pathname) throws IOException {
		return FXMLLoader.load(Loader.class.getResource(pathname));
	}

	public static Parent loadAudio(String pathname) throws IOException {
		return FXMLLoader.load(Loader.class.getResource(pathname));
	}

	public static Image loadImage(String resource) {
		return new Image(Loader.class.getResourceAsStream(resource));
	}

	public static Image loadImageFormUrl(String url){
		return new Image(url);	}

	public static List<Image> loadImages(String[] pathname) {
		List<Image> images = new ArrayList<Image>();
		for (String item : pathname)
			images.add(loadImage(item));
		return images;
	}


	public static InputStream getInputStream(String path) {
		return Loader.class.getResourceAsStream(path);
	}
}
