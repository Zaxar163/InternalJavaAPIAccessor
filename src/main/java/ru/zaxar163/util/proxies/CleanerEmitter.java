package ru.zaxar163.util.proxies;

import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.dynamicgen.FastDynamicProxy;

class CleanerEmitter {
	static final CleanerProxy CL_PROXY;
	static final Class<?> CLEANER;
	static {
		CLEANER = ClassUtil.nonThrowingFirstClass("jdk.internal.ref.Cleaner", "sun.misc.Cleaner");
		CL_PROXY = new FastDynamicProxy<>(CleanerProxy.class.getClassLoader(), CLEANER, CleanerProxy.class).instance();
	}
}
