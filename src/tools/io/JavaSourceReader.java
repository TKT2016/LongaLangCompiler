package tools.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaSourceReader {
    public final String filePath;

    public JavaSourceReader(String filePath)
    {
        this.filePath =filePath;
    }

    public String readFileContent() throws IOException {
        final Path path = Paths.get(filePath) ;
        byte[] data = Files.readAllBytes(path);
        String result = new String(data, "utf-8");
        return result;
    }

}
