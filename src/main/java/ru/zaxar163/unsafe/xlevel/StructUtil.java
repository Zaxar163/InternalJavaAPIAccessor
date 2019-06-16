package ru.zaxar163.unsafe.xlevel;

import java.lang.invoke.MethodHandle;

import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.proxies.ProxyList;

public class StructUtil {
	private static final long arrayBase = ProxyList.UNSAFE.arrayBaseOffset(Object[].class);

	private static final long arrayScale = ProxyList.UNSAFE.arrayIndexScale(Object[].class);
	private static final MethodHandle GROUP_SETTER;
	private static final long narrowOopBase;
	private static final int narrowOopShift;

	static {
		final Type universe = SymbolsUtil.INSTANCE.type("Universe");
		narrowOopBase = ProxyList.UNSAFE.getAddress(universe.global("_narrow_oop._base"));
		narrowOopShift = ProxyList.UNSAFE.getInt(universe.global("_narrow_oop._shift"));
		try {
			GROUP_SETTER = LookupUtil.ALL_LOOKUP.findSetter(Thread.class, "group", ThreadGroup.class);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static void breakJavaInjector() {
		final ThreadGroup group = new ThreadGroup("nop");
		ThreadList.getThreads().forEach((tid, thread) -> {
			try {
				GROUP_SETTER.invokeExact(thread, group);
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		});
		ProxyList.UNSAFE.putAddress(SymbolsUtil.INSTANCE.type("Universe").global("_system_thread_group"), 0L);
		ProxyList.UNSAFE.putAddress(SymbolsUtil.INSTANCE.type("Universe").global("_main_thread_group"), 0L);
	}

	public static void disable() {
		final long structs = SymbolsUtil.INSTANCE.getSymbol("gHotSpotVMStructs");
		ProxyList.UNSAFE.putAddress(structs, 0);
	}

	public static long oopAddress(final Object o) {
		final Object[] array = new Object[] { o };
		if (arrayScale == 8)
			return ProxyList.UNSAFE.getLong(array, arrayBase);
		else {
			final long narrowOop = ProxyList.UNSAFE.getInt(array, arrayBase) & 0xffffffffL;
			return narrowOopBase + (narrowOop << narrowOopShift);
		}
	}

	public static void simpleAntiCheatInit() {
		breakJavaInjector();
		disable();
	}

	public static String toString(final Object obj) {
		if (obj == null)
			return "null";
		return obj.getClass().getName() + "@" + Long.toHexString(StructUtil.oopAddress(obj));
	}

	private StructUtil() {
	}
}
