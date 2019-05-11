package ru.zaxar163.unsafe.fast.proxies;

import ru.zaxar163.unsafe.UnsafeAccessor;
import ru.zaxar163.unsafe.fast.FastStaticProxy;

public class ProxyList {
	public static final UnsafeProxy UNSAFE;
	public static final CleanerProxy CLEANER;

	static {
		UNSAFE = new FastStaticProxy<>(UnsafeProxy.class.getClassLoader(), UnsafeAccessor.UNSAFE_CLASS, UnsafeProxy.class)
				.instance(UnsafeAccessor.UNSAFE_OBJ);
		CLEANER = CleanerEmitter.CL_PROXY;
	}

	private ProxyList() {
	}
}
