package ru.zaxar163.util.unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.LookupUtil;

public final class InnocuousThreadUtil {
	private static final Class<?> CLASS_THREAD;
	private static final MethodHandle CONSTRUCTOR_FULL;
	private static final MethodHandle CONSTRUCTOR_RUNNABLE;
	private static final MethodHandle NEWNAME;
	private static final MethodHandle NEWTHREAD_FULL;
	private static final MethodHandle NEWTHREAD_RUNNABLE;

	static {
		try {
			CLASS_THREAD = ClassUtil.firstClass("jdk.internal.misc.InnocuousThread", "sun.misc.InnocuousThread");
			CONSTRUCTOR_RUNNABLE = LookupUtil.ALL_LOOKUP.findConstructor(CLASS_THREAD,
					MethodType.methodType(void.class, Runnable.class));
			CONSTRUCTOR_FULL = LookupUtil.ALL_LOOKUP.findConstructor(CLASS_THREAD, MethodType.methodType(void.class,
					ThreadGroup.class, Runnable.class, String.class, ClassLoader.class));
			NEWTHREAD_RUNNABLE = LookupUtil.ALL_LOOKUP.findStatic(CLASS_THREAD, "newSystemThread",
					MethodType.methodType(Thread.class, Runnable.class));
			NEWTHREAD_FULL = LookupUtil.ALL_LOOKUP.findStatic(CLASS_THREAD, "newSystemThread",
					MethodType.methodType(Thread.class, String.class, Runnable.class));
			NEWNAME = LookupUtil.ALL_LOOKUP.findStatic(CLASS_THREAD, "newName", MethodType.methodType(String.class));
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static Thread construct(final Runnable e) {
		try {
			return (Thread) CONSTRUCTOR_RUNNABLE.invokeExact(e);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static Thread construct(final ThreadGroup group, final Runnable target, final String name,
			final ClassLoader tccl) {
		try {
			return (Thread) CONSTRUCTOR_FULL.invokeExact(group, target, name, tccl);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static Thread constructM(final Runnable e) {
		try {
			return (Thread) NEWTHREAD_RUNNABLE.invokeExact(e);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static Thread constructM(final String name, final Runnable e) {
		try {
			return (Thread) NEWTHREAD_FULL.invokeExact(name, e);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static String newName() {
		try {
			return (String) NEWNAME.invokeExact();
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	private InnocuousThreadUtil() {
	}
}
