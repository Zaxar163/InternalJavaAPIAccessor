package ru.zaxar163.util.unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;

import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.proxies.ProxyList;

public class InstanceLookupUtil {
	private static final MethodHandle LOOKUP_SUPERPERM_CONSTRUCTOR;

	private static final MethodHandle LOOKUP_UNSAFE_CONSTRUCTOR;
	public static final int TRUSTED;
	static {
		MethodHandle LOOKUP_UNSAFE_CONSTRUCTORT = null;
		try {
			LOOKUP_UNSAFE_CONSTRUCTORT = LookupUtil.ALL_LOOKUP
					.unreflectConstructor(Arrays.stream(LookupUtil.getDeclaredConstructors(Lookup.class))
							.filter(e -> e.getParameterCount() == 1 && e.getParameterTypes()[0].equals(Class.class))
							.findFirst().get());
		} catch (final Throwable e) {
		}
		LOOKUP_UNSAFE_CONSTRUCTOR = LOOKUP_UNSAFE_CONSTRUCTORT;
		MethodHandle SUPER_PERMS_CONSTRUCTORI = null;
		int trusted = 0;
		try {
			SUPER_PERMS_CONSTRUCTORI = LookupUtil.ALL_LOOKUP
					.unreflectConstructor(
							Arrays.stream(LookupUtil.getDeclaredConstructors(Lookup.class))
									.filter(e -> e.getParameterCount() == 2
											&& e.getParameterTypes()[0].equals(Class.class)
											&& e.getParameterTypes()[1].equals(int.class))
									.findFirst().get());
			final Field trustedF = Arrays.stream(LookupUtil.getDeclaredFields(Lookup.class))
					.filter(e -> !e.isAccessible() && e.getName().toLowerCase(Locale.US).contains("trust")).findFirst()
					.get();
			trusted = ProxyList.UNSAFE.getInt(ProxyList.UNSAFE.staticFieldBase(Lookup.class),
					ProxyList.UNSAFE.staticFieldOffset(trustedF));
		} catch (final Throwable t) {
		}
		LOOKUP_SUPERPERM_CONSTRUCTOR = SUPER_PERMS_CONSTRUCTORI;
		TRUSTED = trusted;
	}

	public static Lookup constructNormal(final Class<?> clazz) {
		return constructWithoutChecks(clazz, LookupUtil.ALL_MODES);
	}

	public static Lookup constructTrusted(final Class<?> clazz) {
		if (TRUSTED == 0)
			return null;
		return constructWithoutChecks(clazz, TRUSTED);
	}

	public static Lookup constructWithoutChecks(final Class<?> clazz, final int mode) {
		if (LOOKUP_SUPERPERM_CONSTRUCTOR == null)
			return null;
		try {
			return (Lookup) LOOKUP_SUPERPERM_CONSTRUCTOR.invoke(clazz, mode);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static Lookup constuct(final Class<?> clazz) {
		if (LOOKUP_UNSAFE_CONSTRUCTOR == null)
			return null;
		try {
			return (Lookup) LOOKUP_UNSAFE_CONSTRUCTOR.invoke(clazz);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	private InstanceLookupUtil() {

	}
}
