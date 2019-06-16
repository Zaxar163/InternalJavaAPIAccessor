package ru.zaxar163.unsafe.xlevel;

import lombok.experimental.UtilityClass;
import ru.zaxar163.util.proxies.ProxyList;

@UtilityClass
public class StructUtil {
	private static final long arrayBase = ProxyList.UNSAFE.arrayBaseOffset(Object[].class);

	private static final long arrayScale = ProxyList.UNSAFE.arrayIndexScale(Object[].class);
	private static final long narrowOopBase;
	private static final int narrowOopShift;

	static {
		final Type universe = SymbolsUtil.INSTANCE.type("Universe");
		narrowOopBase = ProxyList.UNSAFE.getAddress(universe.global("_narrow_oop._base"));
		narrowOopShift = ProxyList.UNSAFE.getInt(universe.global("_narrow_oop._shift"));
	}

	public static void disableStructs() {
		final long structs = SymbolsUtil.INSTANCE.getSymbol("gHotSpotVMStructs");
		ProxyList.UNSAFE.putAddress(structs, 0);
	}

	public static void disableThreads() {
		ProxyList.UNSAFE.putAddress(SymbolsUtil.INSTANCE.type("Universe").global("_system_thread_group"), 0L);
		ProxyList.UNSAFE.putAddress(SymbolsUtil.INSTANCE.type("Universe").global("_main_thread_group"), 0L);
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

	public static String toString(final Object obj) {
		if (obj == null)
			return "null";
		return obj.getClass().getName() + "@" + Long.toHexString(StructUtil.oopAddress(obj));
	}
}
