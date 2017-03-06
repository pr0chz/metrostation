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
        for (int i=1; i<100; ++i) {
            buffer.append(i);
        }
        assertArrayEquals(new Integer[] {95, 96, 97, 98, 99}, buffer.get().toArray());
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