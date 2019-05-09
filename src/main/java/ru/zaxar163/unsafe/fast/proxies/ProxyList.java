package ru.zaxar163.unsafe.fast.proxies;

import ru.zaxar163.unsafe.UnsafeAccessor;
import ru.zaxar163.unsafe.fast.FastProxy;

public class ProxyList {
	public static final UnsafeProxy UNSAFE;

	static {
		UNSAFE = new FastProxy<>(UnsafeProxy.class.getClassLoader(), UnsafeAccessor.UNSAFE_CLASS, UnsafeProxy.class)
				.instance(UnsafeAccessor.UNSAFE_OBJ);
	}

	private ProxyList() {
	}
}
