package ru.zaxar163.demonstration;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.function.Supplier;

import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.DelegateClassLoader;
import ru.zaxar163.util.dynamicgen.FastUtil;
import ru.zaxar163.util.dynamicgen.MethodAccGenR;
import ru.zaxar163.util.dynamicgen.MiscUtil;

public final class JVMPlayGround {
	private static final MethodAccGenR.InvokerMethod classConstructor = MethodAccGenR
			.method(Class.class.getDeclaredConstructors()[0]);
	private static final Supplier<Object> clazzSameSize = MiscUtil.sameSizeObject(DelegateClassLoader.INSTANCE,
			Class.class, Collections.emptyList());

	public static void constructClazz(final Class<?> clazz, final Object... args) {
		try {
			classConstructor.invoke(clazz, args);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static void constructClazzU(final Class<?> clazz, final ClassLoader loader) {
		try {
			if (ClassUtil.JAVA9)
				classConstructor.invoke(clazz, loader, null);
			else
				classConstructor.invoke(clazz, loader);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static void main(final String... args) throws Throwable {
		final long start = System.nanoTime();
		final Class<?> a = newClazz();
		constructClazzU(a, ClassLoader.getSystemClassLoader());
		System.out.println(a.getClassLoader());
		constructClazzU(a, new URLClassLoader(new URL[0]));
		System.out.println(a.getClassLoader());
		System.out.println(FastUtil.fastEquals(new byte[] { 2, 5, 5, 6, 7 }, new byte[] { 2, 5, 5, 7, 7 }));
		System.out.println(FastUtil.fastEquals(new byte[] { 2, 5, 5, 6, 7 }, new byte[] { 2, 5, 5, 6, 7 }));
		System.out.println("Worked in: " + (System.nanoTime() - start) + " nanos.");
	}

	public static Class<?> newClazz() {
		return MiscUtil.changeObjUnsafe(Class.class, clazzSameSize.get());
	}

	private JVMPlayGround() {
	}
}