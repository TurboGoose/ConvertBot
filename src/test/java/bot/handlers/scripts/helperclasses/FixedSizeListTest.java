package bot.handlers.scripts.helperclasses;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

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
    public void whenAddValueThenGetThenClear() {
        int capacity = 5;
        FixedSizeList<Integer> list = new FixedSizeList<>(capacity);

        assertThat(list.isEmpty(), is(true));
        assertThat(list.add(1), is(true));
        assertThat(list.getCapacity(), is(capacity));

        assertThat(list.isEmpty(), is(false));
        assertThat(list.get(), is(List.of(1)));
        assertThat(list.add(2), is(true));
        assertThat(list.getCapacity(), is(capacity));

        assertThat(list.isEmpty(), is(false));
        assertThat(list.get(), is(List.of(1, 2)));
        assertThat(list.getCapacity(), is(capacity));

        list.clear();
        assertThat(list.isEmpty(), is(true));
        assertThat(list.getCapacity(), is(capacity));

    }

    @Test
    public void whenTryingToAddValueOutOfCapacityThenException() {
        FixedSizeList<Integer> list = new FixedSizeList<>(1);
        assertThat(list.add(1), is(true));
        assertThat(list.add(2), is(false));
    }

    @Test
    public void whenIterating() {
        FixedSizeList<Integer> list = new FixedSizeList<>(5);
        assertThat(list.add(1), is(true));
        assertThat(list.add(2), is(true));
        assertThat(list.add(3), is(true));
        for (int i : list) {}
        list.forEach(i -> {});
    }
}