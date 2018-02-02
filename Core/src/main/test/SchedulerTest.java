import org.junit.jupiter.api.Test;

import static com.penzzly.engine.core.base.Scheduler.every;
import static com.penzzly.engine.core.base.Scheduler.in;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchedulerTest {
	private int times = 0;
	private long lastRun;
	private long timeTaken;
	
	@Test
	public void timesRepeatingTask() throws InterruptedException {
		lastRun = currentTimeMillis();
		every(1).seconds().forTheNext(4).times().run(() -> {
			times++;
			long timeTaken = currentTimeMillis() - lastRun;
			lastRun = currentTimeMillis();
			assertTrue(timeTaken > 900 && timeTaken < 1100);
		}).enable();
		sleep(5000);
		assertEquals(4, times);
	}
	
	@Test
	public void delayedTask() throws InterruptedException {
		lastRun = currentTimeMillis();
		in(1).seconds().run(() -> {
			timeTaken = currentTimeMillis() - lastRun;
		}).enable();
		sleep(2000);
		assertTrue(timeTaken > 900 && timeTaken < 1100);
	}
}
