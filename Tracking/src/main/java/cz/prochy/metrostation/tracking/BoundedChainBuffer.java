package cz.prochy.metrostation.tracking;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class BoundedChainBuffer<T> {

    private final int maxSize;
    private final Deque<T> queue;

    public BoundedChainBuffer(int maxSize) {
        this.maxSize = maxSize;
        this.queue = new ArrayDeque<>();
    }

    public synchronized void append(T item) {
        Check.notNull(item);
        if (queue.size() >= maxSize) {
            queue.removeFirst();
        }
        queue.addLast(item);
    }

    public synchronized List<T> get() {
        List<T> list = new ArrayList<>(queue);
        queue.clear();
        return list;
    }

    public synchronized void putBack(List<T> list) {
        Check.notNull(list);
        List<T> items = new ArrayList<>(list);
        Collections.reverse(items);
        for (T item : items) {
            if (queue.size() < maxSize) {
                queue.addFirst(item);
            }
        }
    }

    public synchronized int size() {
        return queue.size();
    }

}
