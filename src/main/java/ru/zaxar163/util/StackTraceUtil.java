package ru.zaxar163.util;

public final class StackTraceUtil {
	private StackTraceUtil() { }
	private static final ThreadLocal<Throwable> LOCAL_EXC = new ThreadLocal<Throwable>() {
		@Override
		protected Throwable initialValue() {
			return new Throwable("tracer");
		}
	};
	
	public static Class<?>[] trace(Class<?> caller) {
		DelegateClassLoader.INSTANCE.append(caller);
		Throwable work = LOCAL_EXC.get();
		work.fillInStackTrace();
		StackTraceElement[] trace = work.getStackTrace();
		Class<?>[] ret = new Class<?>[trace.length-2];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = DelegateClassLoader.INSTANCE.findLoaded(trace[i-2].getClassName());
		}
		return ret;
	}
}
