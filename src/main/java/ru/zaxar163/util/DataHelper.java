package ru.zaxar163.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

final class Data1 {
	private static Method a(final Method m) {
		if (!m.isAccessible())
			m.setAccessible(true);
		return m;
	}

	public static Lookup newGet(final Field lookup) throws Throwable {
		final Class<?> unsafe = Class.forName("sun.misc.Unsafe");
		final Field unsafeInst = Arrays.stream(unsafe.getDeclaredFields())
				.filter(e -> e.getType().equals(unsafe) && e.getName().toLowerCase(Locale.US).contains("unsafe"))
				.findFirst().get();
		unsafeInst.setAccessible(true);
		final Object inst = unsafeInst.get(null);
		return (Lookup) a(unsafe.getDeclaredMethod("getObject", Object.class, long.class)).invoke(inst,
				a(unsafe.getDeclaredMethod("staticFieldBase", Field.class)).invoke(inst, lookup),
				(long) a(unsafe.getDeclaredMethod("staticFieldOffset", Field.class)).invoke(inst, lookup));
	}

	public static Lookup oldGet(final Field lookup) throws Throwable {
		AccessibleObject.setAccessible(new AccessibleObject[] { lookup }, true);
		return (Lookup) MethodHandles.publicLookup().unreflectGetter(lookup).invoke();
	}

	private Data1() {
	}
}

final class Data2 {
	private static final MethodHandle DEFINE_CLAZZ;
	static {
		try {
			DEFINE_CLAZZ = LookupUtil.ALL_LOOKUP.findStatic(
					Class.forName("jdk.internal.reflect.ClassDefiner", false, Class.class.getClassLoader()),
					"defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class,
							ClassLoader.class));
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	static Class<?> defineClass(final String name, final byte[] b, final int off, final int len, final ClassLoader cl) {
		try {
			final Class<?> ret = (Class<?>) DEFINE_CLAZZ.invokeExact(name, b, off, len, cl);
			if (cl instanceof DelegatingClassLoader)
				((DelegatingClassLoader) cl).add(ret);
			return ret;
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private Data2() {
	}
}
