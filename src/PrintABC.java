
// 1. Создать три потока, каждый из которых выводит определенную букву (A, B и C) 5 раз (порядок – ABСABСABС).
// Используйте wait/notify/notifyAll.

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrintABC {
	private static final int n = 5;
	private static final String SEQUENCE = "ABC";
	private static final int SEQUENCE_LENGTH = SEQUENCE.length();
	private static int index = 0;
	private volatile char currentLetter = SEQUENCE.charAt(index);
	private final Object mon = new Object();

	private static final ExecutorService service = Executors.newFixedThreadPool(SEQUENCE_LENGTH);

	public static void main(String[] args) {
		PrintABC printABC = new PrintABC();

		for (char c : SEQUENCE.toCharArray()) {
			service.execute(() -> {
				printABC.print(c);
			});
		}

		service.shutdown();
	}

	public void print(char letter) {
		synchronized (mon) {
			try {
				for (int i = 0; i < n; i++) {
					while (currentLetter != letter) {
						mon.wait();
					}
					System.out.print(letter);
					if (index == SEQUENCE_LENGTH - 1) {
						index = 0;
					} else {
						index++;
					}
					currentLetter = SEQUENCE.charAt(index);
					mon.notifyAll();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
