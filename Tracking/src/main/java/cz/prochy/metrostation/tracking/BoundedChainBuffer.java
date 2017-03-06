/*
 *     MetroStation
 *     Copyright (C) 2015, 2016, 2017 Jiri Pokorny
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
