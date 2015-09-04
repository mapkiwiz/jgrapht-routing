package com.github.mapkiwiz.util;

import java.util.concurrent.atomic.AtomicInteger;

public class TextProgressTracker {
	
	private final int max;
	private AtomicInteger count;
	private int lastProgress;
	
	public TextProgressTracker(int max) {
		this.max = max;
		this.count = new AtomicInteger(0);
	}
	
	public void increment() {
		int count = this.count.incrementAndGet();
		if (count % 10000 == 0) {
			reportProgressAsNeeded(count);
		}
	}
	
	public synchronized void reportProgressAsNeeded(int count) {
		int progress = (count * 100) / this.max;
		if (progress > this.lastProgress) {
			System.out.print(progress + " %\r");
			lastProgress = progress;
		}
	}

}
