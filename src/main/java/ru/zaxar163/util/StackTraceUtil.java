package ru.zaxar163.util;

import ru.zaxar163.util.proxies.ProxyList;

public final class StackTraceUtil {
	private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];

	public static Class<?>[] trace(final Class<?> caller) {
		final StackTraceElement[] trace = uncheckedTrace(Thread.currentThread());
		ClassLoader ldr = caller.getClassLoader();
		if (ldr == null)
			ldr = ClassUtil.SCL;
		final Class<?>[] ret = new Class<?>[trace.length - 2];
		for (int i = 0; i < ret.length; i++)
			ret[i] = ClassUtil.findLoadedClass(ldr, trace[i - 2].getClassName());
		return ret;
	}
	
	public static Class<?>[] trace(final Thread caller) {
		final StackTraceElement[] trace = uncheckedTrace(Thread.currentThread());
		ClassLoader ldr = caller.getContextClassLoader();
		if (ldr == null)
			ldr = ClassUtil.SCL;
		final Class<?>[] ret = new Class<?>[trace.length - 2];
		for (int i = 0; i < ret.length; i++)
			ret[i] = ClassUtil.findLoadedClass(ldr, trace[i - 2].getClassName());
		return ret;
	}

	private StackTraceUtil() {
	}

	public static StackTraceElement[] uncheckedTrace(Thread t) {
        final StackTraceElement[] stackTrace = ProxyList.THREAD.dumpThreads(new Thread[] {t})[0];
        if (stackTrace == null)
            return EMPTY_STACK_TRACE;
        return stackTrace;
	}
}
