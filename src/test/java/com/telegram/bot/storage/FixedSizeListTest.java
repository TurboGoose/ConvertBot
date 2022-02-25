package com.telegram.bot.storage;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FixedSizeListTest {
    @Test
    public void whenGetCapacity() {
        FixedSizeList<Integer> list = new FixedSizeList<>(5);
        assertThat(list.getCapacity(), is(5));
    }

    @Test
    public void whenPassingZeroOrNegativeCapacity() {
        new FixedSizeList<>(0);
        assertThrows(IllegalArgumentException.class, () -> new FixedSizeList<>(-1));
    }

    @Test
    public void whenAddValueThenContainsThenGetThenClear() {
        int capacity = 5;
        Storage<Integer> storage = new FixedSizeList<>(capacity);

        assertThat(storage.isEmpty(), is(true));
        assertThat(storage.contains(1), is(false));
        assertThat(storage.add(1), is(true));
        assertThat(storage.contains(1), is(true));
        assertThat(storage.getCapacity(), is(capacity));
        assertThat(storage.size(), is(1));

        assertThat(storage.isEmpty(), is(false));
        assertThat(storage.get(0), is(1));
        assertThat(storage.contains(2), is(false));
        assertThat(storage.add(2), is(true));
        assertThat(storage.contains(2), is(true));
        assertThat(storage.getCapacity(), is(capacity));
        assertThat(storage.size(), is(2));

        assertThat(storage.isEmpty(), is(false));
        assertThat(storage.get(1), is(2));
        assertThat(storage.getCapacity(), is(capacity));

        storage.clear();
        assertThat(storage.isEmpty(), is(true));
        assertThat(storage.getCapacity(), is(capacity));
    }

    @Test
    public void whenTryingToAddValueOutOfCapacityThenException() {
        Storage<Integer> storage = new FixedSizeList<>(1);
        assertThat(storage.isEmpty(), is(true));
        assertThat(storage.isFull(), is(false));
        assertThat(storage.add(1), is(true));
        assertThat(storage.isEmpty(), is(false));
        assertThat(storage.isFull(), is(true));
        assertThat(storage.add(2), is(false));
        assertThat(storage.isEmpty(), is(false));
        assertThat(storage.isFull(), is(true));
    }

    @Test
    public void whenIterating() {
        FixedSizeList<Integer> list = new FixedSizeList<>(5);
        assertThat(list.add(1), is(true));
        assertThat(list.add(2), is(true));
        assertThat(list.add(3), is(true));
        for (int i : list) {
            System.out.println(i);
        }
        list.forEach(i -> {});
    }
}