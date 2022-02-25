package com.telegram.bot.storage;

public interface Storage<T> extends Iterable<T> {
    boolean add(T value);
    T get(int index);
    boolean contains(T value);
    void clear();
    boolean isEmpty();
    boolean isFull();
    int size();
    int getCapacity();
}
