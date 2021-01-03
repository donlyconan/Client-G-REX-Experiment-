package res.resource;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Build {

    public static void renameFiles() {
        File root = new File("src\\res\\resource\\icon");
        for (File file : root.listFiles()) {
            String name = file.getName().replace(".png", "");
            name = name.replace(".", "_")
                    .replace(" ", "_").toLowerCase();
            file.renameTo(new File("src\\res\\resource\\icon" + name + ".png"));

        }
    }

    public static boolean contains(String key, List<String> list) {
        for (String line : list) {
            if (line.contains(key))
                return true;
        }
        return false;
    }

    public static void overrideWriteResource(String path) {
        List<String> lines = new ArrayList<>(100);

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(path)))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null)
                lines.add(line);

            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int index_icon = 0;
        for (; index_icon < lines.size(); index_icon++)
            if (lines.get(index_icon).contains("class Icon")) break;
        index_icon++;


        //lay ten file
        File root = new File("src\\res\\resource\\icon");
        for (File file : root.listFiles()) {
            String key = file.getName().split("\\.")[0];

            if(contains(key, lines))
                continue;

            String line = String.format("\t\tpublic static final String %s = \"%s\";", key, "icon\\\\" + file.getName());
            lines.add(index_icon, line);
        }


        //write resource image
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)))) {
            writer.write("package res.resource;\n");

            for (String line : lines)
                writer.write(line + "\n");

            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void build() {
        System.out.println("Start building....");

        overrideWriteResource("src\\res\\resource\\R.java");
        System.out.println("Building successful!");
    }

    public static void main(String[] args) {
        build();
    }

}
