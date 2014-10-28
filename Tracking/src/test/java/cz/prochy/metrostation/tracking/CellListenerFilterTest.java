package cz.prochy.metrostation.tracking;

import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class CellListenerFilterTest {

    private final static int CID = 10000;
    private final static int LAC = 33000;

    private CellListenerFilter filter;
    private CellListener target;

    @Before
    public void setUp() throws Exception {
        target = createStrictMock(CellListener.class);
        filter = new CellListenerFilter(target);
    }

    public void step(Runnable action, Runnable expect) {
        reset(target);
        expect.run();
        replay(target);
        action.run();
        verify(target);
    }

    public Runnable cellInfo(int cid, int lac) {
        return () -> filter.cellInfo(cid, lac);
    }

    public Runnable disconnected() {
        return () -> filter.disconnected();
    }

    public Runnable expectCellInfo(int cid, int lac) {
        return () -> {
            target.cellInfo(eq(cid), eq(lac));
            expectLastCall().once();
        };
    }

    public Runnable expectDisconnect() {
        return () -> {
            target.disconnected();
            expectLastCall().once();
        };
    }

    public Runnable expectNone() {
        return () -> {};
    }

    @Test
    public void testSingleCellInfo() throws Exception {
        step(cellInfo(CID, LAC), expectCellInfo(CID, LAC));
    }

    @Test
    public void testDuplicateCellInfoIsSuppressed() throws Exception {
        step(cellInfo(CID, LAC), expectCellInfo(CID, LAC));
        step(cellInfo(CID, LAC), expectNone());
    }

    @Test
    public void testDisconnectResetsState() throws Exception {
        step(cellInfo(CID, LAC), expectCellInfo(CID, LAC));
        step(disconnected(), expectDisconnect());
        step(cellInfo(CID, LAC), expectCellInfo(CID, LAC));
    }

    @Test
    public void testDifferentCidResetsState() throws Exception {
        step(cellInfo(CID, LAC), expectCellInfo(CID, LAC));
        step(cellInfo(CID + 1, LAC), expectCellInfo(CID + 1, LAC));
        step(cellInfo(CID, LAC), expectCellInfo(CID, LAC));
    }

    @Test
    public void testDifferentLacResetsState() throws Exception {
        step(cellInfo(CID, LAC), expectCellInfo(CID, LAC));
        step(cellInfo(CID, LAC + 1), expectCellInfo(CID, LAC + 1));
        step(cellInfo(CID, LAC), expectCellInfo(CID, LAC));
    }

    @Test
    public void testDuplicateDisconnectIsSuppressed() throws Exception {
        step(disconnected(), expectDisconnect());
        step(disconnected(), expectNone());
    }

    @Test
    public void testInitialValuesAreNotSuppressedForTheFirstTime() throws Exception {
        step(cellInfo(-1, -1), expectCellInfo(-1, -1));
        step(cellInfo(-1, -1), expectNone());
    }

    @Test(expected = NullPointerException.class)
    public void testNullListenerThrows() throws Exception {
        new CellListenerFilter(null);
    }
}