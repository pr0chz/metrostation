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

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ThreadSafe
public class Timeout {

    private final long timeout;
    private final TimeUnit timeUnit;

    private final ScheduledExecutorService executor;
    private final Object lock = new Object();

    private Future<?> taskFuture;

    public Timeout(ScheduledExecutorService executor, long timeout, TimeUnit timeUnit) {
        this.executor = Check.notNull(executor);
        this.timeout = timeout;
        this.timeUnit = Check.notNull(timeUnit);
    }

    public void reset(Runnable task) {
        synchronized (lock) {
            Check.notNull(task);
            cancel();
            taskFuture = executor.schedule(task, timeout, timeUnit);
        }
    }

    public void cancel() {
        if (taskFuture != null) {
            synchronized (lock) {
                taskFuture.cancel(false);
                taskFuture = null;
            }
        }
    }

}
