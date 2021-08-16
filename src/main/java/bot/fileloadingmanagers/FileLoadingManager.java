package bot.fileloadingmanagers;

public interface FileLoadingManager<K, V> {

    void put(K key, V value);

    void remove(K key);

    boolean contains(K key);

    V get(K key);
}
