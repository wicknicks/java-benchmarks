/**
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */
package com.wicknicks.util;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeHolder {

	private static Unsafe unsafe;

	static {
		try {
			Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			unsafe = (sun.misc.Unsafe) theUnsafe.get(null);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public static Unsafe getUnsafe() {
		return unsafe;
	}

}
