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