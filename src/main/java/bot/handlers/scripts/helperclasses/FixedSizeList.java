package bot.handlers.scripts.helperclasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FixedSizeList<T> implements Iterable<T> {
    private final int capacity;
    private final List<T> data;

    public FixedSizeList(int capacity) {
        this.capacity = capacity;
        this.data = new ArrayList<>(capacity);
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    public boolean add(T value) {
        if (data.size() < capacity) {
            return data.add(value);
        }
        return false;
    }

    public List<T> get() {
        return Collections.unmodifiableList(data);
    }

    public void clear() {
        data.clear();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int getCapacity() {
        return capacity;
    }
}
