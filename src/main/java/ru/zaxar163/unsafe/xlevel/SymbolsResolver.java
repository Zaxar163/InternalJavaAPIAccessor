package ru.zaxar163.unsafe.xlevel;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Locale;

import ru.zaxar163.util.LookupUtil;

public class SymbolsResolver {
	private static final ClassLoader classLoader;
	private static final MethodHandle findNative;

	static {
		if (System.getProperty("os.name").toLowerCase(Locale.US).contains("windows")) {
			final String vmName = System.getProperty("java.vm.name");
			final String dll = vmName.contains("Client VM") ? "/bin/client/jvm.dll" : "/bin/server/jvm.dll";
			try {
				System.load(System.getProperty("java.home") + dll);
			} catch (final UnsatisfiedLinkError e1) {
				try {
					System.load(System.getProperty("java.home") + "/jre" + dll); // Try for JDK.
				} catch (final UnsatisfiedLinkError e2) {
					// Works fine without it!
					// throw new JVMException("Cannot find jvm.dll. Unsupported JVM?");
				}
			}
			classLoader = SymbolsResolver.class.getClassLoader();
		} else
			classLoader = null;

		try {
			findNative = LookupUtil.ALL_LOOKUP.findStatic(ClassLoader.class, "findNative",
					MethodType.methodType(Long.TYPE, ClassLoader.class, String.class));
		} catch (final Throwable e) {
			throw new Error("Method ClassLoader.findNative not found", e);
		}
	}

	public static long lookup(final String name) {
		try {
			return (long) findNative.invokeExact(classLoader, name);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	private SymbolsResolver() {
	}
}
