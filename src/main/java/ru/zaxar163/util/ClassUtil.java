package ru.zaxar163.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.security.ProtectionDomain;
import java.util.Arrays;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassUtil {
	private final static MethodHandle DEFINECLASS_NATIVE1;
	private final static MethodHandle FINDLOADEDCLASS;
	private final static MethodHandle FINDLOADEDCLASS_SCL;
	private final static MethodHandle FORNAME;
	public static final ClassLoader SCL;

	static {
		MethodHandle TDEFINECLASS_NATIVE1 = null;
		try {
			SCL = ClassLoader.getSystemClassLoader();
			FINDLOADEDCLASS = LookupUtil.ALL_LOOKUP.findVirtual(ClassLoader.class, "findLoadedClass0",
					MethodType.methodType(Class.class, String.class));
			FINDLOADEDCLASS_SCL = FINDLOADEDCLASS.bindTo(SCL);
			FORNAME = LookupUtil.ALL_LOOKUP.findStatic(Class.class, "forName0",
					MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class, Class.class));
			if (LookupUtil.JAVA9)
				TDEFINECLASS_NATIVE1 = LookupUtil.ALL_LOOKUP.findStatic(ClassLoader.class, "defineClass1",
						MethodType.methodType(Class.class, ClassLoader.class, String.class, byte[].class, int.class,
								int.class, ProtectionDomain.class, String.class));

			else
				TDEFINECLASS_NATIVE1 = LookupUtil.ALL_LOOKUP.findSpecial(
						ClassLoader.class, "defineClass1", MethodType.methodType(Class.class, String.class,
								byte[].class, int.class, int.class, ProtectionDomain.class, String.class),
						ClassLoader.class);
		} catch (final Throwable e) {
			throw new Error(e);
		}
		DEFINECLASS_NATIVE1 = TDEFINECLASS_NATIVE1;
	}

	public static Class<?> defineClass(final ClassLoader cl, final String name, final byte[] b, final int off,
			final int len, final ProtectionDomain pd) {
		if (name.startsWith("jdk") && LookupUtil.JAVA9)
			return Data2.defineClass(name, b, off, len, cl);
		try {
			return (Class<?>) DEFINECLASS_NATIVE1.invokeExact(cl, name, b, off, len, pd, (String) null);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static Class<?> findLoadedClass(final ClassLoader cl, final String name) {
		try {
			return (Class<?>) FINDLOADEDCLASS.invokeExact(cl, name);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static Class<?> findLoadedClass(final String name) {
		try {
			return (Class<?>) FINDLOADEDCLASS_SCL.invokeExact(name);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static Class<?> firstClass(final ClassLoader loader, final String... names) throws ClassNotFoundException {
		for (final String name : names)
			try {
				return forName(name, false, loader);
			} catch (final ClassNotFoundException ignored) {
				// Expected
			}
		throw new ClassNotFoundException(Arrays.toString(names));
	}

	public static Class<?> firstClass(final String... names) throws ClassNotFoundException {
		return firstClass(SCL, names);
	}

	public static Class<?> forName(final String name, final boolean init, final ClassLoader loader)
			throws ClassNotFoundException {
		try {
			return (Class<?>) FORNAME.invokeExact(name, init, loader, Class.class);
		} catch (final Throwable e) {
			if (e instanceof ClassNotFoundException)
				throw (ClassNotFoundException) e;
			throw new Error(e);
		}
	}

	public static Class<?> nonThrowingFirstClass(final ClassLoader cl, final String... search) {
		for (final String name : search)
			try {
				return forName(name, false, cl);
			} catch (final ClassNotFoundException ignored) {
				// Expected
			}
		throw new RuntimeException(new ClassNotFoundException(Arrays.toString(search)));
	}

	public static Class<?> nonThrowingFirstClass(final String... search) {
		return nonThrowingFirstClass(SCL, search);
	}
}
