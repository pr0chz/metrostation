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
