package bot.filemanager;

import java.util.HashMap;
import java.util.Map;

public class TelegramFileLoadingManager implements FileLoadingManager<ConversionInfo, LoadingInfo> {
    private final Map<ConversionInfo, LoadingInfo> files = new HashMap<>();

    @Override
    public void put(ConversionInfo key, LoadingInfo value) {
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
    public LoadingInfo get(ConversionInfo key) {
        return files.get(key);
    }
}
