package com.telegram.bot.fileloadingmanagers;

import java.util.HashMap;
import java.util.Map;

public class FileLoadingManagerImpl implements FileLoadingManager<String, String> {
    private final Map<String, String> files = new HashMap<>();
    private static FileLoadingManager<String, String> instance;

    private FileLoadingManagerImpl() {}

    public static FileLoadingManager<String, String> getInstance() {
        if (instance == null) {
            instance = new FileLoadingManagerImpl();
        }
        return instance;
    }

    @Override
    public void put(String key, String value) {
        files.put(key, value);
    }

    @Override
    public String remove(String key) {
        return files.remove(key);
    }

    @Override
    public boolean contains(String key) {
        return files.containsKey(key);
    }

    @Override
    public String get(String key) {
        return files.get(key);
    }
}
