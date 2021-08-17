package convertations.conversions;

import java.util.*;

public class AvailableConversions {
    private static final List<Conversion> DOC_CONVERSIONS = List.of(
            new Conversion(FileType.PDF, FileType.TXT),
            new Conversion(FileType.TXT, FileType.PDF));

    private static final List<Conversion> IMG_CONVERSIONS = List.of(
            new Conversion(FileType.JPG, FileType.PDF));

    public static List<Conversion> getDocConversions() {
        return DOC_CONVERSIONS;
    }

    public static List<Conversion> getImgConversions() {
        return IMG_CONVERSIONS;
    }
}
