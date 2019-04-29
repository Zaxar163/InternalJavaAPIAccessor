package ru.zaxar163.unsafe;

import java.lang.invoke.MethodHandle;
import java.security.ProtectionDomain;

import ru.zaxar163.core.MethodInfo;

@SuppressWarnings("rawtypes")
public class UnsafeUtil {
	private static final MethodHandle defineAnonymousClass3 = UnsafeAccessor.UNSAFE_METHODS
			.get(new MethodInfo("defineAnonymousClass", Class.class, Class.class, byte[].class, Object[].class));
	private static final MethodHandle defineClass1 = UnsafeAccessor.UNSAFE_METHODS.get(new MethodInfo("defineClass",
			Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class));
	private static final MethodHandle ensureClassInitialized2 = UnsafeAccessor.UNSAFE_METHODS
			.get(new MethodInfo("ensureClassInitialized", void.class, Class.class));
	private static final MethodHandle shouldBeInitialized0 = UnsafeAccessor.UNSAFE_METHODS
			.get(new MethodInfo("shouldBeInitialized", boolean.class, Class.class));

	public static final Class defineAnonymousClass(final Class obj0, final byte[] obj1, final Object[] obj2) {
		try {
			return (Class) defineAnonymousClass3.invokeExact(obj0, obj1, obj2);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static final Class defineClass(final String obj0, final byte[] obj1, final int obj2, final int obj3,
			final ClassLoader obj4, final ProtectionDomain obj5) {
		try {
			return (Class) defineClass1.invokeExact(obj0, obj1, obj2, obj3, obj4, obj5);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static final void ensureClassInitialized(final Class obj0) {
		try {
			ensureClassInitialized2.invokeExact(obj0);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static final boolean shouldBeInitialized(final Class obj0) {
		try {
			return (boolean) shouldBeInitialized0.invokeExact(obj0);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	private UnsafeUtil() {
	}
}