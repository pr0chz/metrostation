package cz.prochy.metrostation.tracking;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class BoundedChainBufferTest {

    private BoundedChainBuffer<Integer> buffer;

    @Before
    public void setUp() throws Exception {
        buffer = new BoundedChainBuffer<>(5);
    }

    @Test
    public void testAppendAndGet() throws Exception {
        buffer.append(1);
        buffer.append(2);
        buffer.append(3);
        assertArrayEquals(new Integer[] {1, 2, 3}, buffer.get().toArray());
    }

    @Test
    public void testAppendOverSizeBoundary() throws Exception {
        buffer.append(1);
        buffer.append(2);
        buffer.append(3);
        buffer.append(4);
        buffer.append(5);
        buffer.append(6);
        assertArrayEquals(new Integer[] {2, 3, 4, 5, 6}, buffer.get().toArray());
    }

    @Test
    public void testGetCleansBuffer() throws Exception {
        buffer.append(1);
        buffer.append(2);
        buffer.append(3);
        assertEquals(3, buffer.size());
        buffer.get();
        assertEquals(0, buffer.size());
        assertEquals(0, buffer.get().size());
    }

    @Test
    public void testPutBack() throws Exception {
        buffer.append(1);
        buffer.append(2);
        buffer.append(3);
        List<Integer> buf = buffer.get();
        buffer.append(4);
        buffer.putBack(buf);
        assertArrayEquals(new Integer[] {1, 2, 3, 4}, buffer.get().toArray());
    }

    @Test
    public void testPutBackOverSizeBoundary() throws Exception {
        buffer.append(1);
        buffer.append(2);
        buffer.append(3);
        List<Integer> buf = buffer.get();
        buffer.append(4);
        buffer.append(5);
        buffer.append(6);
        buffer.putBack(buf);
        assertArrayEquals(new Integer[] {2, 3, 4, 5, 6}, buffer.get().toArray());
    }

    @Test
    public void testPutBackOnFullBuffer() throws Exception {
        buffer.append(1);
        buffer.append(2);
        buffer.append(3);
        List<Integer> buf = buffer.get();
        buffer.append(4);
        buffer.append(5);
        buffer.append(6);
        buffer.append(7);
        buffer.append(8);
        buffer.putBack(buf);
        assertArrayEquals(new Integer[] {4, 5, 6, 7, 8}, buffer.get().toArray());
    }

    @Test
    public void testGetOnEmptyBuffer() throws Exception {
        assertTrue(buffer.get().isEmpty());
    }

    @Test
    public void testPutBackEmptyBuffer() throws Exception {
        List<Integer> buf = buffer.get();
        buffer.append(1);
        buffer.putBack(buf);
        assertArrayEquals(new Integer[] {1}, buffer.get().toArray());
    }
}