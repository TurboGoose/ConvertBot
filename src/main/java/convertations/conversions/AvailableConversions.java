package convertations.conversions;

import java.util.*;

public class AvailableConversions {
    private static final List<Conversion> AVAILABLE = List.of(
                new Conversion(FileType.PDF, FileType.TXT),
                new Conversion(FileType.TXT, FileType.PDF),
                new Conversion(FileType.JPG, FileType.PDF));

    public static List<Conversion> getAvailable() {
        return AVAILABLE;
    }

    public static boolean contains(Conversion conversion) {
        return AVAILABLE.contains(conversion);
    }

    public static boolean contains(FileType from, FileType to) {
        return AVAILABLE.contains(new Conversion(from, to));
    }
}
