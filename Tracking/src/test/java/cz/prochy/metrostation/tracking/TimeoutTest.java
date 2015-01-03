package cz.prochy.metrostation.tracking;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.easymock.EasyMock.*;

public class TimeoutTest {

    private final long TIMEOUT = 1;
    private final TimeUnit TIMEUNIT = TimeUnit.HOURS;

    private ScheduledExecutorService executorService;
    private Timeout timeout;
    private Runnable task;
    private ScheduledFuture future;

    @Before
    public void setUp() throws Exception {
        executorService = createStrictMock(ScheduledExecutorService.class);
        timeout = new Timeout(executorService, TIMEOUT, TIMEUNIT);

        task = createStrictMock(Runnable.class);
        future = createStrictMock(ScheduledFuture.class);
    }

    @Test(expected = NullPointerException.class)
    public void testNullExecutorThrows() throws Exception {
        new Timeout(null, 1, TimeUnit.MINUTES);
    }

    @Test(expected = NullPointerException.class)
    public void testNullTimeUnitThrows() throws Exception {
        new Timeout(executorService, 1, null);
    }

    private void expectSchedule() {
        expect(executorService.schedule(same(task), eq(TIMEOUT), eq(TIMEUNIT))).andReturn(future);
    }

    private void expectCancel() {
        expect(future.cancel(eq(false))).andReturn(true);
    }

    @Test
    public void testTaskGetsScheduled() throws Exception {
        expectSchedule();
        replay(task, executorService, future);

        timeout.reset(task);

        verify(task, executorService, future);
    }

    @Test
    public void testTaskGetsCancelled() throws Exception {
        expectSchedule();
        expectCancel();
        replay(task, executorService, future);

        timeout.reset(task);
        timeout.cancel();

        verify(task, future);
    }

    @Test
    public void testTaskIsRescheduled() throws Exception {
        expectSchedule();
        expectCancel();
        expectSchedule();
        replay(task, executorService, future);

        timeout.reset(task);
        timeout.reset(task);

        verify(task, executorService, future);
    }

    @Test
    public void testCancelIsIgnoredWithNoTaskScheduled() throws Exception {
        replay(task, executorService, future);

        timeout.cancel();

        verify(task, executorService, future);
    }

    @Test
    public void testSecondCancelIsIgnored() throws Exception {
        expectSchedule();
        expectCancel();

        replay(task, executorService, future);

        timeout.reset(task);
        timeout.cancel();
        timeout.cancel();

        verify(task, executorService, future);
    }
}