package bot.fileloadingmanagers;

import convertations.conversions.Conversion;
import convertations.conversions.FileType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TelegramFileLoadingManagerTest {
    Conversion conversion = new Conversion(FileType.TXT, FileType.PDF);

    @Test
    public void whenContainsThenGetThenRemoveFromEmptyManager() {
        FileLoadingManager<ConversionInfo, String> manager = TelegramFileLoadingManager.getInstance();
        ConversionInfo info = new ConversionInfo("1", conversion);
        assertThat(manager.contains(info), is(false));
        assertThat(manager.get(info), is(nullValue()));
        assertThat(manager.remove(info), is(nullValue()));
    }

    @Test
    public void whenPutThenContainsThenGetThenRemove() {
        FileLoadingManager<ConversionInfo, String> manager = TelegramFileLoadingManager.getInstance();
        ConversionInfo info = new ConversionInfo("1", conversion);
        String fileId = "fileId 123";
        manager.put(info, fileId);
        assertThat(manager.contains(info), is(true));
        assertThat(manager.get(info), is(fileId));
        assertThat(manager.remove(info), is(fileId));
        assertThat(manager.contains(info), is(false));
    }
}
