package ru.zaxar163.util.unsafe;

import java.lang.reflect.Field;

import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.proxies.ProxyList;

public final class SecurityManagerUtil {
	private static final Object secManagerBase;
	private static final long secManagerOffset;
	static {
		try {
			final Field security = LookupUtil.getField(System.class, "security", SecurityManager.class);
			secManagerBase = ProxyList.UNSAFE.staticFieldBase(security);
			secManagerOffset = ProxyList.UNSAFE.staticFieldOffset(security);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static void nullSMNoCheck() {
		ProxyList.UNSAFE.putObject(secManagerBase, secManagerOffset, null);
	}

	public static void setSMNoCheck(final SecurityManager sm) {
		ProxyList.UNSAFE.putObject(secManagerBase, secManagerOffset, sm);
	}

	private SecurityManagerUtil() {
	}
}
