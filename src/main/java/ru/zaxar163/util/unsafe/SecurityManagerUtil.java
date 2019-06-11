package ru.zaxar163.util.unsafe;

import java.lang.reflect.Field;

import lombok.experimental.UtilityClass;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.proxies.ProxyList;

@UtilityClass
public final class SecurityManagerUtil {
	private static final Object secManagerBase;
	private static final Object secManagerLock;
	private static final long secManagerOffset;
	static {
		try {
			final Field security = LookupUtil.getField(System.class, "security", SecurityManager.class);
			secManagerBase = ProxyList.UNSAFE.staticFieldBase(security);
			secManagerOffset = ProxyList.UNSAFE.staticFieldOffset(security);
			secManagerLock = new Object();
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static SecurityManager nullSMNoCheck() {
		final SecurityManager old = (SecurityManager) ProxyList.UNSAFE.getObject(secManagerBase, secManagerOffset);
		ProxyList.UNSAFE.putObject(secManagerBase, secManagerOffset, null);
		return old;
	}

	public static SecurityManager setSMNoCheck(final SecurityManager sm) {
		final SecurityManager old = (SecurityManager) ProxyList.UNSAFE.getObject(secManagerBase, secManagerOffset);
		ProxyList.UNSAFE.putObject(secManagerBase, secManagerOffset, sm);
		return old;
	}

	public static void suspendRunAndContinue(final Runnable r) {
		synchronized (secManagerLock) {
			final Thread current = Thread.currentThread();
			for (final Thread t : ProxyList.THREAD.getThreads())
				if (t != current)
					ProxyList.THREAD.suspend(t);
			final SecurityManager old = (SecurityManager) ProxyList.UNSAFE.getObject(secManagerBase, secManagerOffset);
			ProxyList.UNSAFE.putObject(secManagerBase, secManagerOffset, null);
			r.run();
			ProxyList.UNSAFE.putObject(secManagerBase, secManagerOffset, old);
			for (final Thread t : ProxyList.THREAD.getThreads())
				if (t != current)
					ProxyList.THREAD.resume(t);
		}
	}
}
