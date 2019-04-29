package ru.zaxar163.unsafe.xlevel;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import ru.zaxar163.core.LookupUtil;

public class SymbolsResolver {
	private static final ClassLoader classLoader;
	private static final MethodHandle findNative;

	static {
		final String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("windows")) {
			final String vmName = System.getProperty("java.vm.name");
			final String dll = vmName.contains("Client VM") ? "/bin/client/jvm.dll" : "/bin/server/jvm.dll";
			try {
				System.load(System.getProperty("java.home") + dll);
			} catch (final UnsatisfiedLinkError e) {
				// Works fine without it!
				// throw new JVMException("Cannot find jvm.dll. Unsupported JVM?");
			}
			classLoader = SymbolsResolver.class.getClassLoader();
		} else
			classLoader = null;

		try {
			findNative = LookupUtil.ALL_LOOKUP.findStatic(ClassLoader.class, "findNative",
					MethodType.methodType(Long.TYPE, ClassLoader.class, String.class));
		} catch (final Throwable e) {
			throw new NativeAccessError("Method ClassLoader.findNative not found", e);
		}
	}

	public static long lookup(final String name) {
		try {
			return (long) findNative.invokeExact(classLoader, name);
		} catch (final Throwable e) {
			throw new NativeAccessError(e);
		}
	}

	private SymbolsResolver() {
	}
}
