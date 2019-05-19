package ru.zaxar163.unsafe.xlevel;

import ru.zaxar163.util.proxies.ProxyList;

public class StructUtil {
	private static final long arrayBase = ProxyList.UNSAFE.arrayBaseOffset(Object[].class);

	private static final long arrayScale = ProxyList.UNSAFE.arrayIndexScale(Object[].class);
	private static final long narrowOopBase;
	private static final int narrowOopShift;
	static {
		final Type universe = SymbolsUtil.getInstance().type("Universe");
		narrowOopBase = SymbolsUtil.getInstance().getAddress(universe.global("_narrow_oop._base"));
		narrowOopShift = SymbolsUtil.getInstance().getInt(universe.global("_narrow_oop._shift"));
	}

	public static void disable() {
		final long structs = SymbolsUtil.getInstance().getSymbol("gHotSpotVMStructs");
		SymbolsUtil.getInstance().putAddress(structs, 0);
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

	private StructUtil() {
	}
}
