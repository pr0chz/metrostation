package cz.prochy.metrostation.tracking;

import java.util.LinkedList;

public class BoundedStringBuffer {

    private final int maxSize;
    private final LinkedList<String> buffer = new LinkedList<>();

    private int size;

    public BoundedStringBuffer(int maxLines) {
        this.maxSize = maxLines;
    }

    public synchronized void addLine(String string) {
        if (size == maxSize) {
            buffer.removeFirst();
            --size;
        }
        buffer.addLast(string);
        ++size;
    }

    public synchronized String getContent() {
        StringBuffer result = new StringBuffer();
        for (String s : buffer) {
            result.append(s).append('\n');
        }
        return result.toString();
    }

    public synchronized void clear() {
        size = 0;
        buffer.clear();
    }


    public int getSize() {
        return size;
    }
}
