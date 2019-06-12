package ru.zaxar163.demonstration;

import java.net.URL;
import java.net.URLClassLoader;

import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.dynamicgen.FastUtil;
import ru.zaxar163.util.dynamicgen.MethodAccGenR;
import ru.zaxar163.util.dynamicgen.MiscUtil;
import ru.zaxar163.util.dynamicgen.reflect.InvokerMethodR;
import ru.zaxar163.util.unsafe.ObjectUtil;

public final class JVMPlayGround {
	private static final InvokerMethodR classConstructor = MethodAccGenR
			.method(Class.class.getDeclaredConstructors()[0]);

	public static void constructClazzU(final Class<?> clazz, final ClassLoader loader) {
		try {
			if (LookupUtil.JAVA9)
				classConstructor.invoke(clazz, loader, null);
			else
				classConstructor.invoke(clazz, loader);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static void main(final String... args) throws Throwable {
		MiscUtil.computeSameSize(Class.class);
		final long start = System.nanoTime();
		System.out.println("Change ClassLoader of java.lang.Class via reconstruct of instance:");
		System.out.print("Prev ClassLoader of java.lang.Class: ");
		System.out.println(ObjectUtil.objectToStringSafe(Class.class.getClassLoader()));
		constructClazzU(Class.class, ClassLoader.getSystemClassLoader());
		System.out.print("After change: ");
		System.out.println(ObjectUtil.objectToStringSafe(Class.class.getClassLoader()));
		System.out.println("Make our own class:");
		System.out.print("Instance it: ");
		final Class<?> a = MiscUtil.newInstance(Class.class);
		System.out.println(ObjectUtil.objectToStringSafe(a));
		constructClazzU(a, ClassLoader.getSystemClassLoader());
		System.out.print("Construct it with system ClassLoader: ");
		System.out.println(ObjectUtil.objectToStringSafe(a.getClassLoader()));
		System.out.print("Reconstruct it with same ClassLoader (new instance of URLClassLoader): ");
		constructClazzU(a, new URLClassLoader(new URL[0]));
		System.out.println(ObjectUtil.objectToStringSafe(a.getClassLoader()));
		System.out.print("Fast equals of arrays:\n1. Not match (false).\n2. Match (true).\n1. ");
		System.out.println(FastUtil.fastEquals(new byte[] { 2, 5, 5, 6, 7 }, new byte[] { 2, 5, 5, 7, 7 }));
		System.out.print("2. ");
		System.out.println(FastUtil.fastEquals(new byte[] { 2, 5, 5, 6, 7 }, new byte[] { 2, 5, 5, 6, 7 }));
		System.out.println("Worked in: " + (System.nanoTime() - start) + " nanos.");
	}

	private JVMPlayGround() {
	}
}