package cz.prochy.metrostation.tracking;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Timeout {

	private final long timeout;
	private final TimeUnit timeUnit;
	
	private final ScheduledExecutorService executor;
	
	private volatile Future<?> taskFuture;
	
	public Timeout(ScheduledExecutorService executor, long timeout, TimeUnit timeUnit) {
		this.executor = Check.notNull(executor);
		this.timeout = timeout;
		this.timeUnit = Check.notNull(timeUnit);
	}
	
	public void reset(Runnable task) {
		Check.notNull(task);
		cancel();
		taskFuture = executor.schedule(task, timeout, timeUnit);
	}
	
	public void cancel() {
		if (taskFuture != null) {
			taskFuture.cancel(false);
		}		
	}
	
}
