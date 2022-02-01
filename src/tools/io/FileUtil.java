package tools.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static String getCurrentPath()
    {
        File directory = new File(".");//设定为当前文件夹
        try{
            return directory.getAbsolutePath();//获取绝对路径
        }catch(Exception e){}
        return "";
    }

    public static byte[] readAllBytes(String filePath) throws IOException {
        Path path= Paths.get(filePath) ;
        byte[] data = Files.readAllBytes(path);
        return data;
    }

    public static String readTextContent(String filePath) throws IOException {
        byte[] data = readAllBytes(filePath);
        String result = new String(data, "utf-8");
        return result;
    }

    public static void saveTextContent(String filePath,String content)  throws IOException
    {
        FileWriter fw = new FileWriter(filePath);
        BufferedWriter bfw = new BufferedWriter(fw);
        bfw.write(content, 0, content.length());
        bfw.flush();
        fw.close();
    }

    public static void saveTextContent(File file,String content)  throws IOException
    {
        FileWriter fw = new FileWriter(file);
        BufferedWriter bfw = new BufferedWriter(fw);
        bfw.write(content, 0, content.length());
        bfw.flush();
        fw.close();
    }
}
