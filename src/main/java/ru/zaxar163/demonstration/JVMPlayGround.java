package ru.zaxar163.demonstration;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;

import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.DelegateClassLoader;
import ru.zaxar163.util.dynamicgen.FastUtil;
import ru.zaxar163.util.dynamicgen.MethodAccGenR;
import ru.zaxar163.util.dynamicgen.MiscUtil;

public final class JVMPlayGround {
	private static volatile MethodAccGenR.InvokerMethod classConstructor = null;
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

	public static void init(final Consumer<Throwable> acceptor) {
		for (int i = 0; classConstructor == null && i < 1024; i++)
			init0().ifPresent(acceptor);
	}

	public static Optional<Throwable> init0() {
		try {
			final MethodAccGenR.InvokerMethod clI = MethodAccGenR.method(Class.class.getDeclaredConstructors()[0]);
			final Class<?> a = newClazz();
			if (ClassUtil.JAVA9)
				clI.invoke(a, ClassLoader.getSystemClassLoader(), null);
			else
				clI.invoke(a, ClassLoader.getSystemClassLoader());
			classConstructor = clI;
			return Optional.empty();
		} catch (final Throwable t) {
			return Optional.of(t);
		}
	}

	public static void main(final String... args) throws Throwable {
		final long start = System.currentTimeMillis();
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
		try (Scanner scanner = new Scanner(System.in)) {
			if (scanner.nextLine().equals("crash"))
				Crasher.fullCrashZip();
		}
	}

	public static Class<?> newClazz() {
		return MiscUtil.changeObjUnsafe(Class.class, clazzSameSize.get());
	}

	private JVMPlayGround() {
	}
}