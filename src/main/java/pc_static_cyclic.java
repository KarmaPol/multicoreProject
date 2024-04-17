import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class pc_static_cyclic {
	private static AtomicInteger counter = new AtomicInteger(0);
	private static int NUM_END = 200000;
	private static int NUM_THREADS = 32;

	public static void main(String[] args) throws InterruptedException {
		if(args.length == 2) {
			NUM_THREADS = Integer.parseInt(args[0]);
			NUM_END = Integer.parseInt(args[1]);
		}

		long startTime = System.currentTimeMillis();
		List<List<LoadBalancerStaticCyclic.Section>> sections = LoadBalancerStaticCyclic.getSections();
		Thread[] threads = new Thread[NUM_THREADS];

		for(int i = 0; i < NUM_THREADS; i++) {
			PrimeThread pt = new PrimeThread(i, sections.get(i));
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

	static class LoadBalancerStaticCyclic {
		static List<List<Section>> getSections() {
			List<List<Section>> sections = new ArrayList<>();

			for(int i = 0; i < NUM_THREADS; i++) {
				sections.add(new ArrayList<>());
			}

			int offset = 10;

			int start = 0; int end = offset;
			int currentThread = 0;
			while(end <= NUM_END) {
				Section section = new Section(start, end);
				sections.get(currentThread).add(section);

				if(end == NUM_END) {
					break;
				}

				start += offset;
				end += offset;
				if(end > NUM_END - 1) {
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
		int taskNumber;
		List<LoadBalancerStaticCyclic.Section> sections;

		PrimeThread(int taskNumber, List<LoadBalancerStaticCyclic.Section> sections) {
			this.taskNumber = taskNumber;
			this.sections = sections;
		}

		public void run() {
			long startTime = System.currentTimeMillis();
			sections.forEach(section -> {
				int start = section.start;
				int end = section.end;
				for(int i = start; i < end; i++) {
					if(isPrime(i)) {
						counter.incrementAndGet();
					}
				}
			});
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

