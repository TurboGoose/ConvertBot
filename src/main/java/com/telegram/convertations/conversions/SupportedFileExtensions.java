package com.telegram.convertations.conversions;

import java.util.List;

public class SupportedFileExtensions {
    private static List<String> extensions = null;

    public static List<String> get() {
        if (extensions == null) {
            extensions = List.of("jpg", "jpeg", "png");
        }
        return extensions;
    }
}
