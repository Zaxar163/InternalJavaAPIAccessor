package ru.zaxar163.core;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;

import ru.zaxar163.unsafe.fast.FastUtil;
import ru.zaxar163.unsafe.fast.InvokerMethod;
import ru.zaxar163.unsafe.fast.ReflectionUtil;
import ru.zaxar163.unsafe.xlevel.ThreadList;

public final class JVMPlayGround {
	private static volatile InvokerMethod classConstructor = null;
	private static final Supplier<Object> clazzSameSize = ReflectionUtil.sameSizeObject(DelegateClassLoader.INSTANCE,
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

	public static void init(final Consumer<InstantiationException> acceptor) {
		int i = 0;
		final InstantiationException ret = new InstantiationException();
		boolean flag = false;
		while (classConstructor == null && i < 1024) {
			final Throwable t = init0();
			i++;
			if (t != null) {
				ret.addSuppressed(t);
				flag = true;
			}
		}
		if (flag)
			acceptor.accept(ret);
	}

	public static Throwable init0() {
		try {
			final InvokerMethod clI = ReflectionUtil
					.wrapMethod(ReflectionUtil.methodify(Class.class.getDeclaredConstructors()[0]));
			final Class<?> a = newClazz();
			if (ClassUtil.JAVA9)
				clI.invoke(a, ClassLoader.getSystemClassLoader(), null);
			else
				clI.invoke(a, ClassLoader.getSystemClassLoader());
			classConstructor = clI;
			return null;
		} catch (final Throwable e) {
			return e;
		}
	}

	public static void main(final String... args) throws Throwable {
		final long start = System.currentTimeMillis();
		ThreadList.getThreads().forEach((n, t) -> {
			System.out.println("Thread # " + n + " Data: " + t + " Classloader: " + t.getContextClassLoader());
		});
		init(e -> e.printStackTrace());
		final Class<?> a = newClazz();
		constructClazzU(a, ClassLoader.getSystemClassLoader());
		System.out.println(a.getClassLoader());
		constructClazzU(a, new URLClassLoader(new URL[0]));
		System.out.println(a.getClassLoader());
		System.out.println(FastUtil.fastEquals(new byte[] { 2, 5, 5, 6, 7 }, new byte[] { 2, 5, 5, 7, 7 }));
		System.out.println(FastUtil.fastEquals(new byte[] { 2, 5, 5, 6, 7 }, new byte[] { 2, 5, 5, 6, 7 }));
		final long work = System.currentTimeMillis() - start;
		System.out.println("Worked in: " + work + " millis.");
		// Crasher.crashZip();
	}

	public static Class<?> newClazz() {
		return ReflectionUtil.changeObjUnsafe(Class.class, clazzSameSize.get());
	}

	private JVMPlayGround() {
	}
}