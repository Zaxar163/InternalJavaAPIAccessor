package ru.zaxar163.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ShutdownUtil {
	private static final MethodHandle ShutdownExit;
	private static final MethodHandle ShutdownHalt;
	private static final MethodHandle ShutdownHalt0;

	static {
		try {
			ShutdownHalt0 = LookupUtil.ALL_LOOKUP.findStatic(
					Class.forName("java.lang.Shutdown", false, ClassLoader.getSystemClassLoader()), "halt0",
					MethodType.methodType(void.class, int.class));
			ShutdownHalt = LookupUtil.ALL_LOOKUP.findStatic(
					Class.forName("java.lang.Shutdown", false, ClassLoader.getSystemClassLoader()), "halt",
					MethodType.methodType(void.class, int.class));
			ShutdownExit = LookupUtil.ALL_LOOKUP.findStatic(
					Class.forName("java.lang.Shutdown", false, ClassLoader.getSystemClassLoader()), "exit",
					MethodType.methodType(void.class, int.class));
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static void exit(final int status) {
		try {
			ShutdownExit.invokeExact(status);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static void halt(final int status) {
		try {
			ShutdownHalt.invokeExact(status);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static void halt0(final int status) {
		try {
			ShutdownHalt0.invokeExact(status);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}
}
