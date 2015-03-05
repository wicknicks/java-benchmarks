/**
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */
package com.wicknicks.atomics;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicCounterVsMutexWithThreads {

	public static void noLock() {
		long a = 0;
		long start = System.nanoTime();
		for (int i = 0; i < 500000000; i++) {
			a = a + 1;
		}
		long diff = System.nanoTime() - start;
		System.out.println("[no lock] a = " + a + "; time taken = " + diff / (1000 * 1000) + "ms");
	}

	private static final Object mutex = new Object();

	public static void oneThreadWithLock() {
		long a = 0;
		long start = System.nanoTime();
		for (int i = 0; i < 500000000; i++) {
			synchronized (mutex) {
				a = a + 1;
			}
		}
		long diff = System.nanoTime() - start;
		System.out.println("[oneThreadWithLock] a = " + a + "; time taken = " + diff / (1000 * 1000) + "ms");
	}

	public static void singleThreadCAS() {
		AtomicLong a = new AtomicLong(0);
		long start = System.nanoTime();
		for (int i = 0; i < 500000000; i++) {
			a.incrementAndGet();
		}
		long diff = System.nanoTime() - start;
		System.out.println("[singleThreadCAS] a = " + a.get() + "; time taken = " + diff / (1000 * 1000) + "ms");
	}

	public static void multiThreadCAS() {

		final AtomicLong a = new AtomicLong(0);

		class RunImpl implements Runnable {
			@Override
			public void run() {
				for (int i = 0; i < 500000000; i++) {
					a.incrementAndGet();
				}
			}
		}

		long start = System.nanoTime();

		Thread t1 = new Thread(new RunImpl());
		Thread t2 = new Thread(new RunImpl());

		t1.start();
		t2.start();

		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long diff = System.nanoTime() - start;
		System.out.println("[multiThreadCAS] a = " + a.get() + "; time taken = " + diff / (1000 * 1000) + "ms");
	}

	public static void main(String[] args) {
		noLock();
		singleThreadCAS();
		oneThreadWithLock();
		multiThreadCAS();
	}

}