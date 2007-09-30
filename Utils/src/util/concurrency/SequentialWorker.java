package util.concurrency;

import java.util.LinkedList;
import java.util.Queue;

/**
 * a SequentialWorker is a thread that run a queue of task in a sequential
 * manner respecting a FIFO
 * 
 * Sequential worker may be started when initialized
 * 
 * @author omotelet
 * 
 */

public class SequentialWorker extends Thread {

	Object emptySemaphore = new Object();

	Queue<Runnable> itsQueue = new LinkedList<Runnable>();

	public SequentialWorker() {
		super();
	}

	public SequentialWorker(boolean shouldStart) {
		super();
		if (shouldStart)
			start();
	}

	public void addTask(Runnable aTask) {
		itsQueue.add(aTask);
		synchronized (emptySemaphore) {
			emptySemaphore.notify();
		}
	}

	protected Runnable pop() {
		try {
			synchronized (emptySemaphore) {
				while (itsQueue.isEmpty())
					emptySemaphore.wait();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itsQueue.poll();
	}

	@Override
	public void run() {
		while (true)
			pop().run();
	}

}
