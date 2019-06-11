package ru.zaxar163.util.unsafe;

import lombok.experimental.UtilityClass;
import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.dynamicgen.ConstructorAccGenR;
import ru.zaxar163.util.dynamicgen.MethodAccGenF;
import ru.zaxar163.util.dynamicgen.reflect.InvokerConstructor;
import ru.zaxar163.util.dynamicgen.reflect.InvokerMethodF;

@UtilityClass
public class InnocuousThreadUtil {
	private static final Class<?> CLASS_THREAD;
	private static final InvokerConstructor CONSTRUCTOR_FULL;
	private static final InvokerConstructor CONSTRUCTOR_RUNNABLE;
	private static final InvokerMethodF NEWNAME;
	private static final InvokerMethodF NEWTHREAD_FULL;
	private static final InvokerMethodF NEWTHREAD_RUNNABLE;

	static {
		try {
			CLASS_THREAD = ClassUtil.firstClass("jdk.internal.misc.InnocuousThread", "sun.misc.InnocuousThread");
			CONSTRUCTOR_RUNNABLE = ConstructorAccGenR
					.instancer(LookupUtil.getConstructor(CLASS_THREAD, Runnable.class));
			CONSTRUCTOR_FULL = ConstructorAccGenR.instancer(LookupUtil.getConstructor(CLASS_THREAD, ThreadGroup.class,
					Runnable.class, String.class, ClassLoader.class));
			NEWTHREAD_RUNNABLE = MethodAccGenF
					.method(LookupUtil.getMethod(CLASS_THREAD, "newSystemThread", Runnable.class));
			NEWTHREAD_FULL = MethodAccGenF
					.method(LookupUtil.getMethod(CLASS_THREAD, "newSystemThread", String.class, Runnable.class));
			NEWNAME = MethodAccGenF.method(LookupUtil.getMethod(CLASS_THREAD, "newName"));
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static Thread construct(final Runnable e) {
		try {
			return (Thread) CONSTRUCTOR_RUNNABLE.newInstance(e);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static Thread construct(final ThreadGroup group, final Runnable target, final String name,
			final ClassLoader tccl) {
		try {
			return (Thread) CONSTRUCTOR_FULL.newInstance(group, target, name, tccl);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static Thread constructM(final Runnable e) {
		try {
			return (Thread) NEWTHREAD_RUNNABLE.invoke(e);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static Thread constructM(final String name, final Runnable e) {
		try {
			return (Thread) NEWTHREAD_FULL.invoke(name, e);
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public static String newName() {
		try {
			return (String) NEWNAME.invoke();
		} catch (final Throwable t) {
			throw new Error(t);
		}
	}
}
