package bot.handlers.scripts;

import org.telegram.telegrambots.meta.api.objects.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImageBuffer implements Iterable<Document> {
    private final List<Document> images;
    private final int capacity;

    public ImageBuffer(int capacity) {
        this.capacity = capacity;
        images = new ArrayList<>(capacity);
    }

    public ImageBuffer() {
        this(16);
    }

    @Override
    public Iterator<Document> iterator() {
        return images.iterator();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean add(Document document) {
        if (images.size() < capacity) {
            return images.add(document);
        }
        return false;
    }

    public int size() {
        return images.size();
    }

    public void clear() {
        images.clear();
    }

}
