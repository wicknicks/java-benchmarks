/**
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */
package com.wicknicks;

import java.nio.ByteBuffer;
import java.util.Random;

import sun.misc.Unsafe;

public class UnsafeVsByteBufferIntReadWrite {

// for bytes.length = 12

//	-- absolute times ---
//	bufferWrite=18213910446, bufferRead=17444069358, rewindTime=16378200608,
//	unsafeWrite=11967058315, unsafeRead=11756869780
//
//			-- ratios ---
//	Write=1.522004, Read=1.4837341
//
//			-- per record times ---
//	bufferWrite=36, bufferRead=34, rewindTime=32,
//	unsafeWrite=23, unsafeRead=23


// for bytes.length = 64 * 1024

//	-- absolute times ---
//	bufferWrite=49189446680, bufferRead=20490030, rewindTime=12945271,
//	unsafeWrite=14051943, unsafeRead=17851097241
//
//			-- ratios ---
//	Write=3500.544, Read=0.0011478303
//
//			-- per record times ---
//	bufferWrite=98378, bufferRead=40, rewindTime=25,
//	unsafeWrite=28, unsafeRead=35702


	public static void main(String[] args) throws Exception {

		Unsafe unsafe = UnsafeHolder.getUnsafe();

		int offset = unsafe.arrayBaseOffset(byte[].class);
		int limit = 1000 * 500;
		Random gen = new Random();

		byte[] bytes = new byte[64 * 1024];
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		long bufferWrite = 0, bufferRead = 0,
				rewindTime = 0,
				unsafeWrite = 0, unsafeRead = 0, tmp;

		// read write with byte buffer
		for (int i = 0; i < limit; i++) {
			int value = gen.nextInt();

			tmp = System.nanoTime();
			for (int jj = 0; jj < bytes.length / 4; jj++) {
				buffer.putInt(value + jj);
			}
			bufferWrite += (System.nanoTime() - tmp);

			tmp = System.nanoTime();
			int read = buffer.getInt(0);
			bufferRead += (System.nanoTime() - tmp);

			if (read != value) {
				System.out.println("ByteBuffer WTF! " + read + " " + value);
			}

			tmp = System.nanoTime();
			buffer.rewind();
			rewindTime += (System.nanoTime() - tmp);
		}

		// read write with unsafe
		for (int i = 0; i < limit; i++) {
			int value = gen.nextInt();

			tmp = System.nanoTime();
			for (int jj = 0; jj < bytes.length / 4; jj++) {
				unsafe.putInt(bytes, offset + jj * 4, value);
			}
			unsafeRead += (System.nanoTime() - tmp);

//			System.out.println("val=" + value + " = "
//					+ makeInt(bytes[3], bytes[2], bytes[1], bytes[0])
//					+ " "
//					+ unsafe.getInt(bytes, offset));

			tmp = System.nanoTime();
			int read = unsafe.getInt(bytes, offset);
			unsafeWrite += (System.nanoTime() - tmp);

			if (read != value) {
				System.out.println("Unsafe WTF! " + read + " " + value);
			}
		}

		System.out.println("\n\n-- absolute times ---");
		System.out.println("bufferWrite=" + bufferWrite
						+ ", bufferRead=" + bufferRead
						+ ", rewindTime=" + rewindTime
						+ ",\nunsafeWrite=" + unsafeWrite
						+ ", unsafeRead=" + unsafeRead
		);


		System.out.println();
		System.out.println("-- ratios ---");

		System.out.println("Write=" + (float) bufferWrite / unsafeWrite
				+ ", Read=" + (float) bufferRead / unsafeRead);

		System.out.println();
		System.out.println("-- per record times ---");

		System.out.println("bufferWrite=" + bufferWrite / limit
				+ ", bufferRead=" + bufferRead / limit
				+ ", rewindTime=" + rewindTime / limit
				+ ",\nunsafeWrite=" + unsafeWrite / limit
				+ ", unsafeRead=" + unsafeRead / limit);
	}

	static private int makeInt(byte b3, byte b2, byte b1, byte b0) {
		return (((b3 & 0xff) << 24) |
				((b2 & 0xff) << 16) |
				((b1 & 0xff) << 8) |
				((b0 & 0xff)));
	}

}
