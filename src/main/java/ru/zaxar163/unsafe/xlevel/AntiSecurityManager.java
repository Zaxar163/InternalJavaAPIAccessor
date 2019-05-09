package ru.zaxar163.unsafe.xlevel;

import ru.zaxar163.core.LookupUtil;
import ru.zaxar163.unsafe.fast.proxies.ProxyList;

public class AntiSecurityManager {
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

	private AntiSecurityManager() {
	}
}
