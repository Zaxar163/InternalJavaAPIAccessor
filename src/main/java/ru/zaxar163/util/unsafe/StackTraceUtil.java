package ru.zaxar163.util.unsafe;

import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.proxies.ProxyList;

public final class StackTraceUtil {
	private static class ContextGetterSM extends SecurityManager {
		private static final long INITIALIZED_OFFSET = ProxyList.UNSAFE
				.objectFieldOffset(LookupUtil.getField(SecurityManager.class, "initialized", boolean.class));

		private ContextGetterSM() {
		}

		@Override
		public Class<?>[] getClassContext() {
			return super.getClassContext();
		}

		private ContextGetterSM selfInit() {
			ProxyList.UNSAFE.putBoolean(this, INITIALIZED_OFFSET, true);
			return this;
		}
	}

	private static final ContextGetterSM CALLER_GETTER = ((ContextGetterSM) ProxyList.UNSAFE
			.allocateInstance(ContextGetterSM.class)).selfInit();

	private static final long CONTECT_LOADER_OFFSET = ProxyList.UNSAFE
			.objectFieldOffset(LookupUtil.getField(Thread.class, "contextClassLoader", ClassLoader.class));

	private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];

	public static Class<?>[] trace() {
		final Class<?>[] preRet = CALLER_GETTER.getClassContext();
		final Class<?>[] ret = new Class<?>[preRet.length - 3];
		System.arraycopy(preRet, 3, ret, 0, ret.length);
		return ret;
	}

	public static Class<?>[] trace(final Thread caller) {
		final StackTraceElement[] trace = uncheckedTrace(Thread.currentThread());
		ClassLoader ldr = (ClassLoader) ProxyList.UNSAFE.getObject(caller, CONTECT_LOADER_OFFSET);
		if (ldr == null)
			ldr = ClassUtil.SCL;
		final Class<?>[] ret = new Class<?>[trace.length - 2];
		for (int i = 0; i < ret.length; i++)
			ret[i] = ClassUtil.findLoadedClass(ldr, trace[i - 2].getClassName());
		return ret;
	}

	public static StackTraceElement[] uncheckedTrace(final Thread t) {
		final StackTraceElement[] stackTrace = ProxyList.THREAD.dumpThreads(new Thread[] { t })[0];
		if (stackTrace == null)
			return EMPTY_STACK_TRACE;
		return stackTrace;
	}

	private StackTraceUtil() {
	}
}
