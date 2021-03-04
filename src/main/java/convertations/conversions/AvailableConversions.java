package convertations.conversions;

import java.util.*;

public class AvailableConversions {


    private static final Set<Conversion> AVAILABLE;

    static {
        AVAILABLE = new HashSet<>();
        AVAILABLE.add(new Conversion(FileType.PDF, FileType.TXT));
        AVAILABLE.add(new Conversion(FileType.TXT, FileType.PDF));
    }

    public static Set<Conversion> getAvailable() {
        return AVAILABLE;
    }

    public static boolean contains(Conversion conversion) {
        return AVAILABLE.contains(conversion);
    }

    public static boolean contains(FileType from, FileType to) {
        return AVAILABLE.contains(new Conversion(from, to));
    }
}
