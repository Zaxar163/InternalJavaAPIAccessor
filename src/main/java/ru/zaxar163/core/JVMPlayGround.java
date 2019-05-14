package ru.zaxar163.core;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.function.Supplier;

import ru.zaxar163.unsafe.fast.FastUtil;
import ru.zaxar163.unsafe.fast.InvokerMethod;
import ru.zaxar163.unsafe.fast.ReflectionUtil;
import ru.zaxar163.unsafe.xlevel.ThreadList;

public final class JVMPlayGround {
	private static InvokerMethod classConstructor = null;
	private static final Supplier<Object> clazzSameSize = ReflectionUtil
			.sameSizeObject(DelegateClassLoader.INSTANCE, Class.class, Collections.emptyList());
	public static void main(final String... args) throws Throwable {
		ThreadList.getThreads().forEach((n, t) -> {
			System.out.println("Thread # " + n + " Data: " + t + " Classloader: " + t.getContextClassLoader());
		});
		init();
		try {
			final Class<?> a = newClazz();
			classConstructor.invoke(a, ClassLoader.getSystemClassLoader());
			System.out.println(a.getClassLoader());
			classConstructor.invoke(a, new URLClassLoader(new URL[0]));
			System.out.println(a.getClassLoader());
		} catch (final Throwable e) {
			e.printStackTrace();
		}
		System.out.println(FastUtil.fastEquals(new byte[] { 2, 5, 5, 6, 7 }, new byte[] { 2, 5, 5, 7, 7 }));
		System.out.println(FastUtil.fastEquals(new byte[] { 2, 5, 5, 6, 7 }, new byte[] { 2, 5, 5, 6, 7 }));
		// Crasher.crashZip();
	}
	
	public static Class<?> newClazz() {
		return ReflectionUtil.changeObjUnsafe(Class.class, clazzSameSize.get());
	}
	
	public static void init() {
		try {
			final InvokerMethod clI = ReflectionUtil
					.wrapMethod(ReflectionUtil.methodify(Class.class.getDeclaredConstructors()[0]));
			final Class<?> a = newClazz();
			if (ClassUtil.JAVA9)
				clI.invoke(a, ClassLoader.getSystemClassLoader(), null);
			else
				clI.invoke(a, ClassLoader.getSystemClassLoader());
			classConstructor = clI;
		} catch (final Throwable e) {
			//e.printStackTrace();
			init();
		}
	}
	
	public static void constructClazz(Class<?> clazz, Object... args) {
		try {
			classConstructor.invoke(clazz, args);
		} catch (Throwable e) {
			throw new Error(e);
		}
	}
	
	public static void constructClazzU(Class<?> clazz, ClassLoader loader) {
		try {
			if (ClassUtil.JAVA9)
				classConstructor.invoke(clazz, loader, null);
			else
				classConstructor.invoke(clazz, loader);
		} catch (Throwable e) {
			throw new Error(e);
		}
	}

	private JVMPlayGround() {
	}
}