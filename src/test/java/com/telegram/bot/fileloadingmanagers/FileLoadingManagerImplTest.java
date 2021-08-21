package com.telegram.bot.fileloadingmanagers;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class FileLoadingManagerImplTest {
    @Test
    public void whenContainsThenGetThenRemoveFromEmptyManager() {
        FileLoadingManager<String, String> manager = FileLoadingManagerImpl.getInstance();
        String key = "key";
        assertThat(manager.contains(key), is(false));
        assertThat(manager.get(key), is(nullValue()));
        assertThat(manager.remove(key), is(nullValue()));
    }

    @Test
    public void whenPutThenContainsThenGetThenRemove() {
        FileLoadingManager<String, String> manager = FileLoadingManagerImpl.getInstance();
        String key = "key";
        String value = "value";
        manager.put(key, value);
        assertThat(manager.contains(key), is(true));
        assertThat(manager.get(key), is(value));
        assertThat(manager.remove(key), is(value));
        assertThat(manager.contains(key), is(false));
    }
}
