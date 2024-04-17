package problem1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class pc_dynamic {
	private static AtomicInteger counter = new AtomicInteger(0);
	private static int NUM_END = 200000;
	private static int NUM_THREADS = 32;
	private static ConcurrentHashMap<Integer, Long> taskDurations = new ConcurrentHashMap<>();

	public static void main(String[] args) {
		if(args.length == 2) {
			NUM_THREADS = Integer.parseInt(args[0]);
			NUM_END = Integer.parseInt(args[1]);
		}

		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
		long startTime = System.currentTimeMillis();

		int offset = 10;
		int start = 0;
		int end = offset;

		while (start <= NUM_END) {
			final int s = start;
			final int e = Math.min(end, NUM_END);
			executor.execute(() -> {
				for (int i = s; i < e; i++) {
					if (isPrime(i)) {
						counter.incrementAndGet();
					}
				}
			});

			start += offset;
			end += offset;
		}

		executor.shutdown();
		while (!executor.isTerminated()) {
			// 모든 태스크 종료 후 메인 스레드 종료되도록 대기
		}

		long endTime = System.currentTimeMillis();
		long timeDiff = endTime - startTime;

		// for(int i = 0; i < NUM_THREADS; i++) {
		// 	System.out.println("[thread #" + i + "] completed in " + timeDiff + " ms");
		// }
		System.out.println("Program Execution Time: " + timeDiff + "ms");
		System.out.println("1..." + (NUM_END-1) + " prime# counter=" + counter.get());
	}

	private static boolean isPrime(int x) {
		if(x < 2) {
			return false;
		}
		for(int i = 2; i < x; i++) {
			if (x % i == 0) {
				return false;
			}
		}
		return true;
	}
}
