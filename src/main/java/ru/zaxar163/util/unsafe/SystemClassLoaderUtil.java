package ru.zaxar163.util.unsafe;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.function.Predicate;

import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.dynamicgen.MethodAccGenR;
import ru.zaxar163.util.dynamicgen.reflect.InvokerMethodR;

public final class SystemClassLoaderUtil {
	private static final InvokerMethodR MH_ADD;
	public static final Object SYSTEM_CP;
	public static final ClassLoader SYSYEM_LOADER;
	public static final Class<?> UCP_CLASS;
	private static final UnsafeFieldAcc URLCL_CP_F;

	static {
		UCP_CLASS = ClassUtil.nonThrowingFirstClass("jdk.internal.loader.URLClassPath", "sun.misc.URLClassPath");
		SYSYEM_LOADER = ClassLoader.getSystemClassLoader();
		final Predicate<Field> filter = e -> e.getType().equals(UCP_CLASS) && containsUCP(e);
		URLCL_CP_F = new UnsafeFieldAcc(
				LookupUtil.digFields(URLClassLoader.class).stream().filter(filter).findFirst().get());
		SYSTEM_CP = new UnsafeFieldAcc(
				LookupUtil.digFields(SYSYEM_LOADER.getClass()).stream().filter(filter).findFirst().get())
						.getObject(SYSYEM_LOADER);
		MH_ADD = MethodAccGenR.method(LookupUtil.getMethod(UCP_CLASS, "addURL", URL.class));
	}

	public static void addURL(final Object cp, final URL e) {
		try {
			MH_ADD.invoke(cp, e);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static void addURL(final URL e) {
		try {
			MH_ADD.invoke(SYSTEM_CP, e);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static void addURLToClassLoader(final URLClassLoader cp, final URL e) {
		try {
			MH_ADD.invoke(getURLCP(cp), e);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	private static boolean containsUCP(final Field e) {
		final String name = e.getName().toLowerCase(Locale.US);
		return name.contains("u") || name.contains("c") || name.contains("p");
	}

	public static Object getURLCP(final URLClassLoader loader) {
		return URLCL_CP_F.getObject(loader);
	}

	private SystemClassLoaderUtil() {
	}
}
