package problem1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class pc_static_block {
	private static AtomicInteger counter = new AtomicInteger(0);
	private static int NUM_END = 200000;
	private static int NUM_THREADS = 32;

	public static void main(String[] args) throws InterruptedException {
		if(args.length == 2) {
			NUM_THREADS = Integer.parseInt(args[0]);
			NUM_END = Integer.parseInt(args[1]);
		}

		long startTime = System.currentTimeMillis();
		List<LoadBalancerStaticBlock.Section> sections = LoadBalancerStaticBlock.getSections();
		Thread[] threads = new Thread[NUM_THREADS];

		for(int i = 0; i < NUM_THREADS; i++) {
			int start = sections.get(i).start;
			int end = sections.get(i).end;
			PrimeThread pt = new PrimeThread(i, start, end);
			threads[i] = pt;
			pt.start();
		}

		for(Thread pt : threads) {
			pt.join();
		}

		long endTime = System.currentTimeMillis();
		long timeDiff = endTime - startTime;
		System.out.println("Program Execution Time: " + timeDiff + "ms");
		System.out.println("1..." + (NUM_END-1) + " prime# counter=" + counter.get());
	}

	static class LoadBalancerStaticBlock {
		static List<Section> getSections() {
			List<Section> sections = new ArrayList<>();
			int offset = NUM_END/NUM_THREADS;
			for(int i = 0; i < NUM_THREADS; i++) {
				int start = i * offset;
				int end = (i+1) * offset;
				if(i == NUM_THREADS - 1) {
					end = NUM_END;
				}
				Section section = new Section(start, end);
				sections.add(section);
			}
			return sections;
		}

		static class Section {
			int start;
			int end;
			public Section(int start, int end) {
				this.start = start;
				this.end = end;
			}
		}
	}

	static class PrimeThread extends Thread {
		int taskNumber;
		int start;
		int end;

		PrimeThread(int taskNumber, int start, int end) {
			this.taskNumber = taskNumber;
			this.start = start;
			this.end = end;
		}

		public void run() {
			long startTime = System.currentTimeMillis();
			for(int i = start; i < end; i++) {
				if(isPrime(i)) {
					counter.incrementAndGet();
				}
			}
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			System.out.println("[thread #" + taskNumber + "] completed in " + duration + " ms");
		}
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

