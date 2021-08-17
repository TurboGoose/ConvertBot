package com.telegram.bot.fileloadingmanagers;

import java.util.HashMap;
import java.util.Map;

public class TelegramFileLoadingManager implements FileLoadingManager<ConversionInfo, String> {
    private final Map<ConversionInfo, String> files = new HashMap<>();
    private static FileLoadingManager<ConversionInfo, String> instance;

    private TelegramFileLoadingManager() {}

    public static FileLoadingManager<ConversionInfo, String> getInstance() {
        if (instance == null) {
            instance = new TelegramFileLoadingManager();
        }
        return instance;
    }

    @Override
    public void put(ConversionInfo key, String value) {
        files.put(key, value);
    }

    @Override
    public String remove(ConversionInfo key) {
        return files.remove(key);
    }

    @Override
    public boolean contains(ConversionInfo key) {
        return files.containsKey(key);
    }

    @Override
    public String get(ConversionInfo key) {
        return files.get(key);
    }
}
