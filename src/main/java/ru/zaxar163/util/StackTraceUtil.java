package ru.zaxar163.util;

public final class StackTraceUtil {
	private static final ThreadLocal<Throwable> LOCAL_EXC = new ThreadLocal<Throwable>() {
		@Override
		protected Throwable initialValue() {
			return new Throwable("tracer");
		}
	};

	public static Class<?>[] trace(final Class<?> caller) {
		final Throwable work = LOCAL_EXC.get();
		work.fillInStackTrace();
		final StackTraceElement[] trace = work.getStackTrace();
		ClassLoader ldr = caller.getClassLoader();
		if (ldr == null)
			ldr = ClassUtil.SCL;
		final Class<?>[] ret = new Class<?>[trace.length - 2];
		for (int i = 0; i < ret.length; i++)
			ret[i] = ClassUtil.findLoadedClass(ldr, trace[i - 2].getClassName());
		return ret;
	}

	private StackTraceUtil() {
	}
}
