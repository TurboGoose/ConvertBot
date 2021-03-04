package tools.files;

import java.io.File;

public class FileNameTools {

    public static String extractExtension(String filename) {
        int index = filename.lastIndexOf(".");
        return index == -1 ? "" : filename.substring(index + 1);
    }

    public static String extractExtension(File file) {
        return extractExtension(file.getName());
    }

    public static String extractFilenameWithoutExtension(String filename) {
        int index = filename.lastIndexOf(".");
        return index == -1 ? filename : filename.substring(0, index);
    }

    public static String extractFilenameWithoutExtension(File file) {
        return extractFilenameWithoutExtension(file.getName());
    }
}
