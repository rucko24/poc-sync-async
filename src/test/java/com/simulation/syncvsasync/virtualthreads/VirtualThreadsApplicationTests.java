package com.simulation.syncvsasync.virtualthreads;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rubn
 */
@Log4j2
class VirtualThreadsApplicationTests {

	private static final int COUNT = 10_000_000;
	private static final long RESULT = 50000005000000L;

	@Test
	@DisplayName("Executing normal threads")
	void normalThread() throws InterruptedException {
		var result = new AtomicLong();
		var latch = new CountDownLatch(COUNT);
		for (int i = 1; i <= COUNT; i++) {
			final int index = i;
			new Thread(() -> {
				result.addAndGet(index);
				latch.countDown();
			}).start();
		}
		latch.await();
		log.info("Result: {}", result.get());

		assertThat(result.get()).isEqualTo(RESULT);
	}

	@Test
	@DisplayName("Using subscribeOn operator with Schedulers.boundedElastic")
	void boundedElastic() throws InterruptedException {
		var result = new AtomicLong();
		var latch = new CountDownLatch(COUNT);
		for (int i = 1; i <= COUNT; i++) {
			final int index = i;
			Mono.fromSupplier(() -> result.addAndGet(index))
					.subscribeOn(Schedulers.boundedElastic())
					.doOnTerminate(latch::countDown)
					.subscribe();
		}
		latch.await();
		log.info("Result: {}", result.get());

		assertThat(result.get()).isEqualTo(RESULT);
	}

	@Test
	@SneakyThrows
	@DisplayName("Executing virtual thread using subscribeOn operator " +
			" with Schedulers.fromExecutorService and newVirtualThreadPerTaskExecutor")
	void virtualThread()  {
		var result = new AtomicLong();
		var latch = new CountDownLatch(COUNT);
		for (int i = 1; i <= COUNT; i++) {
			final int index = i;
			Mono.fromSupplier(() -> result.addAndGet(index))
					.subscribeOn(Schedulers.fromExecutorService(
							Executors.newVirtualThreadPerTaskExecutor()))
					.doOnTerminate(latch::countDown)
					.subscribe();
		}
		latch.await();
		log.info("Result: {}", result.get());

		assertThat(result.get()).isEqualTo(RESULT);
	}

	@Test
	@SneakyThrows
	@DisplayName("Executing virtual thread using subscribeOn operator " +
			" with Schedulers.fromExecutorService and newThreadPerTaskExecutor- and factory")
	void virtualThread2() {
		var result = new AtomicLong();
		var latch = new CountDownLatch(COUNT);
		for (int i = 1; i <= COUNT; i++) {
			final int index = i;
			Mono.fromSupplier(() -> result.addAndGet(index))
					.subscribeOn(Schedulers.fromExecutorService(Executors.newThreadPerTaskExecutor(
							Thread.ofVirtual()
									.name("newThreadPerTaskExecutor-")
									.factory())))
					.doOnTerminate(latch::countDown)
					.subscribe();
		}
		latch.await();
		log.info("Result: {}", result.get());

		assertThat(result.get()).isEqualTo(RESULT);
	}

}
