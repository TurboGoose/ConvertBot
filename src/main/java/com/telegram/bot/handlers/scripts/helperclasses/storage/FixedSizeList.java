package com.telegram.bot.handlers.scripts.helperclasses.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FixedSizeList<T> implements Storage<T> {
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

    @Override
    public boolean add(T value) {
        if (data.size() < capacity) {
            return data.add(value);
        }
        return false;
    }

    @Override
    public T get(int index) {
        return data.get(index);
    }

    @Override
    public boolean contains(T value) {
        return data.contains(value);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean isFull() {
        return data.size() == capacity;
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
