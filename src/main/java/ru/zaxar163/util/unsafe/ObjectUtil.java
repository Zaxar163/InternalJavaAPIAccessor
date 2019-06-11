package ru.zaxar163.util.unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import lombok.experimental.UtilityClass;
import ru.zaxar163.util.LookupUtil;

@UtilityClass
public class ObjectUtil {
	private static final MethodHandle toString;
	static {
		try {
			toString = LookupUtil.ALL_LOOKUP.findSpecial(Object.class, "toString", MethodType.methodType(String.class),
					Object.class);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static long address(final Object obj) {
		if (obj == null)
			return 0;
		try {
			return Long.parseLong(((String) toString.invokeExact(obj)).split("@")[1], 16);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static String objectToString(final Object obj) {
		if (obj == null)
			return null;
		try {
			return (String) toString.invokeExact(obj);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static String objectToStringSafe(final Object obj) {
		if (obj == null)
			return "null";
		try {
			return (String) toString.invokeExact(obj);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}
}
