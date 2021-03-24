package bot.fileloadingmanagers;

import java.util.HashMap;
import java.util.Map;

public class TelegramFileLoadingManager implements FileLoadingManager<ConversionInfo, String> {
    private final Map<ConversionInfo, String> files = new HashMap<>();

    @Override
    public void put(ConversionInfo key, String value) {
        files.put(key, value);
    }

    @Override
    public void remove(ConversionInfo key) {
        files.remove(key);
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
