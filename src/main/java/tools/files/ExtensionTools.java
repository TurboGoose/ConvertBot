package tools.files;

import java.io.File;

public class ExtensionTools {
    public static String extractExtension(File file) {
        String filename = file.getName();
        int index = filename.lastIndexOf(".");
        return index == -1 ? "" : filename.substring(index + 1);
    }

    public static String extractFilenameWithoutExtension(File file) {
        String filename = file.getName();
        int index = filename.lastIndexOf(".");
        return index == -1 ? filename : filename.substring(0, index);
    }
}
