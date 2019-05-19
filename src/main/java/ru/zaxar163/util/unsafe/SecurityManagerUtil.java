package ru.zaxar163.util.unsafe;

import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.proxies.ProxyList;

public class SecurityManagerUtil {
	private static final Object secManagerBase;
	private static final long secManagerOffset;
	static {
		try {
			secManagerBase = ProxyList.UNSAFE.staticFieldBase(System.class);
			secManagerOffset = ProxyList.UNSAFE.staticFieldOffset(LookupUtil.getField(System.class, "security"));
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
