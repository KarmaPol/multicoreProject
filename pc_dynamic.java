package problem1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class pc_dynamic {
	private static AtomicInteger counter = new AtomicInteger(0);
	private static int NUM_END = 200000;
	private static int NUM_THREADS = 2;

	public static void main(String[] args) throws InterruptedException {
		if(args.length == 2) {
			NUM_THREADS = Integer.parseInt(args[0]);
			NUM_END = Integer.parseInt(args[1]);
		}

		long startTime = System.currentTimeMillis();
		List<List<pc_static_cyclic.LoadBalancerStaticCyclic.Section>> sections = pc_static_cyclic.LoadBalancerStaticCyclic.getSections();
		Thread[] threads = new Thread[NUM_THREADS];

		for(int i = 0; i < NUM_THREADS; i++) {
			PrimeThread pt = new PrimeThread();
			threads[i] = pt;
		}

		int offset = 10;
		int start = 0; int end = offset;

		int currentThreadNum = 0;
		while(end <= NUM_END) {
			PrimeThread currentThread = (PrimeThread) threads[currentThreadNum];
			currentThread.start = start; currentThread.end = end;
			currentThread.start();

			if(end == NUM_END) {
				break;
			}

			start += offset;
			end += offset;
			if(end > NUM_THREADS - 1) {
				end = NUM_END;
			}

			currentThreadNum = (currentThreadNum+1) % NUM_THREADS;
		}

		for(Thread pt : threads) {
			pt.join();
		}

		long endTime = System.currentTimeMillis();
		long timeDiff = endTime - startTime;
		System.out.println("Program Execution Time: " + timeDiff + "ms");
		System.out.println("1..." + (NUM_END-1) + " prime# counter=" + counter.get());
	}

	static class LoadBalancerStaticCyclic {
		static List<List<pc_static_cyclic.LoadBalancerStaticCyclic.Section>> getSections() {
			List<List<pc_static_cyclic.LoadBalancerStaticCyclic.Section>> sections = new ArrayList<>();

			for(int i = 0; i < NUM_THREADS; i++) {
				sections.add(new ArrayList<>());
			}

			int offset = 10;

			int start = 0; int end = offset;
			int currentThread = 0;
			while(end <= NUM_END) {
				pc_static_cyclic.LoadBalancerStaticCyclic.Section section = new pc_static_cyclic.LoadBalancerStaticCyclic.Section(start, end);
				sections.get(currentThread).add(section);

				if(end == NUM_END) {
					break;
				}

				start += offset;
				end += offset;
				if(end > NUM_THREADS - 1) {
					end = NUM_END;
				}

				currentThread = (currentThread+1) % NUM_THREADS;
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
		int start;
		int end;

		public void run() {
			for(int i = start; i < end; i++) {
				if(isPrime(i)) {
					counter.incrementAndGet();
				}
			}
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
