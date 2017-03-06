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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class LoggingCellListenerTest {

    private CellListener mockListener;
    private LoggingCellListener loggingCellListener;

    @Before
    public void setUp() throws Exception {
        mockListener = createStrictMock(CellListener.class);
        loggingCellListener = new LoggingCellListener(333, 100, mockListener);
    }

    @Test
    public void testCellInfoGetsPropagated() throws Exception {
        mockListener.cellInfo(10, 20, 30);
        expectLastCall().once();
        replay(mockListener);
        loggingCellListener.cellInfo(10, 20, 30);
        verify(mockListener);
    }

    @Test
    public void testCellInfoGetsLogged() throws Exception {
        loggingCellListener.cellInfo(10, 20, 30);
        assertArrayEquals(
                new String[] {"{\"id\": 333, \"ts\": 10, \"cid\": 20, \"lac\": 30}"},
                loggingCellListener.getCellLogger().get().toArray()
        );
    }

    @Test
    public void testDisconnectGetsPropagated() throws Exception {
        mockListener.disconnected(10);
        expectLastCall().once();
        replay(mockListener);
        loggingCellListener.disconnected(10);
        verify(mockListener);
    }

    @Test
    public void testDisconnectGetsLogged() throws Exception {
        loggingCellListener.disconnected(10);
        assertArrayEquals(
                new String[] {"{\"id\": 333, \"ts\": 10}"},
                loggingCellListener.getCellLogger().get().toArray()
        );
    }

}