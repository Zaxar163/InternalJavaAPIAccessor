package ru.zaxar163.unsafe.xlevel;

import ru.zaxar163.core.LookupUtil;
import ru.zaxar163.unsafe.fast.proxies.ProxyList;

public class VMThread {
	private static final long eetop;

	static {
		eetop = ProxyList.UNSAFE.objectFieldOffset(LookupUtil.getField(Thread.class, "eetop"));
	}

	public static long current() {
		return of(Thread.currentThread());
	}

	public static long of(final Thread javaThread) {
		return ProxyList.UNSAFE.getLong(javaThread, eetop);
	}

	private VMThread() {
	}
}
