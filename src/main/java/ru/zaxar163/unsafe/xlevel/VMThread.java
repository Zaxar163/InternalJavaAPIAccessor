package ru.zaxar163.unsafe.xlevel;

import java.lang.invoke.MethodHandle;

import ru.zaxar163.core.LookupUtil;

public class VMThread {
	private static final MethodHandle eetop;

	static {
		try {
			eetop = LookupUtil.ALL_LOOKUP.findGetter(Thread.class, "eetop", Long.TYPE);
		} catch (final Throwable e) {
			throw new NativeAccessError("Thread.eetop field not found");
		}
	}

	public static long current() {
		return of(Thread.currentThread());
	}

	public static long of(final Thread javaThread) {
		try {
			return (long) eetop.invokeExact(javaThread);
		} catch (final Throwable e) {
			throw new NativeAccessError(e);
		}
	}

	private VMThread() {
	}
}
