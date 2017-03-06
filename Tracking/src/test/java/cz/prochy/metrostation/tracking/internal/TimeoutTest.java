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

package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Timeout;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;

import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.CombinableMatcher.both;
import static org.junit.Assert.fail;

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

    private void expectCancel() throws ExecutionException, InterruptedException {
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

    private static class TSCatcher implements Runnable {
        private long ts;
        private final CountDownLatch latch;

        public TSCatcher(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            ts = System.currentTimeMillis();
            latch.countDown();
        }

        public long getTs() {
            return ts;
        }
    }

    @Test
    public void testRealTest() throws Exception {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Timeout timeout = new Timeout(executor, 1, TimeUnit.SECONDS);
        CountDownLatch latch = new CountDownLatch(1);
        TSCatcher catcher = new TSCatcher(latch);

        long start = System.currentTimeMillis();
        timeout.reset(catcher);
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout while waiting for latch");
        }
        assertThat(catcher.getTs() - start, is(both(greaterThan(500L)).and(lessThan(1500L))));
    }
}