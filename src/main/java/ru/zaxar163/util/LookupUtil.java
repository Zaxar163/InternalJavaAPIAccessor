package ru.zaxar163.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class LookupUtil {
	public static final Lookup ALL_LOOKUP;
	public static final int ALL_MODES = Lookup.PUBLIC | Lookup.PRIVATE | Lookup.PROTECTED | Lookup.PACKAGE;
	private static final MethodHandle CONSTRUCTORS_GETTER;
	private static final MethodHandle DECLAREDCLASSES_GETTER;
	private static final MethodHandle FIELDS_GETTER;
	private static final MethodHandle LOOKUP_CONSTRUCTOR;
	private static final MethodHandle METHODS_GETTER;
	private static final MethodHandle CLASSLOADER_GETTER;

	static {
		try {
			MethodHandles.publicLookup(); // hack to cause classloading of Lookup
			final Field allPermsLookup = Arrays.stream(Lookup.class.getDeclaredFields())
					.filter(e -> e.getType().equals(Lookup.class)
							&& e.getName().toLowerCase(Locale.US).contains("lookup")
							&& e.getName().toLowerCase(Locale.US).contains("impl")
							&& !e.getName().toLowerCase(Locale.US).contains("public"))
					.findFirst().get();
			allPermsLookup.setAccessible(true);
			ALL_LOOKUP = (Lookup) allPermsLookup.get(null);
			LOOKUP_CONSTRUCTOR = ALL_LOOKUP
					.findVirtual(Lookup.class, "in", MethodType.methodType(Lookup.class, Class.class))
					.bindTo(ALL_LOOKUP);
			FIELDS_GETTER = ALL_LOOKUP.findSpecial(Class.class, "getDeclaredFields0",
					MethodType.methodType(Field[].class, boolean.class), Class.class);
			METHODS_GETTER = ALL_LOOKUP.findSpecial(Class.class, "getDeclaredMethods0",
					MethodType.methodType(Method[].class, boolean.class), Class.class);
			CONSTRUCTORS_GETTER = ALL_LOOKUP.findSpecial(Class.class, "getDeclaredConstructors0",
					MethodType.methodType(Constructor[].class, boolean.class), Class.class);
			DECLAREDCLASSES_GETTER = ALL_LOOKUP.findSpecial(Class.class, "getDeclaredClasses0",
					MethodType.methodType(Class[].class), Class.class);
			CLASSLOADER_GETTER = ALL_LOOKUP.findSpecial(Class.class, "getClassLoader0",
					MethodType.methodType(ClassLoader.class), Class.class);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static List<Field> digFields(final Class<?> top) {
		final List<Field> ret = new ArrayList<>();
		Class<?> superc = top;
		while (superc != null && !superc.equals(Object.class)) {
			for (final Field field : getDeclaredFields(superc))
				ret.add(field);
			superc = superc.getSuperclass();
		}
		return ret;
	}

	public static List<Method> digMethods(final Class<?> top) {
		final List<Method> ret = new ArrayList<>();
		Class<?> superc = top;
		while (superc != null && !superc.equals(Object.class)) {
			for (final Method field : getDeclaredMethods(superc))
				ret.add(field);
			superc = superc.getSuperclass();
		}
		return ret;
	}

	public static MethodHandle fromWrapped(final Object handle) {
		return MethodHandleProxies.wrapperInstanceTarget(handle);
	}

	public static Constructor<?> getConstructor(final Class<?> cls, final Class<?>... types) {
		return Arrays.stream(getDeclaredConstructors(cls)).filter(e -> Arrays.equals(e.getParameterTypes(), types))
				.findFirst().get();
	}

	public static Class<?>[] getDeclaredClasses(final Class<?> clazz) {
		try {
			return (Class<?>[]) DECLAREDCLASSES_GETTER.invokeExact(clazz);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> Constructor<T>[] getDeclaredConstructors(final Class<T> clazz) {
		try {
			return (Constructor<T>[]) CONSTRUCTORS_GETTER.invokeExact(clazz, false);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static Field[] getDeclaredFields(final Class<?> clazz) {
		try {
			return (Field[]) FIELDS_GETTER.invokeExact(clazz, false);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static Method[] getDeclaredMethods(final Class<?> clazz) {
		try {
			return (Method[]) METHODS_GETTER.invokeExact(clazz, false);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static Field getField(final Class<?> clazz, final String name) {
		Objects.requireNonNull(name, "name");
		return Arrays.stream(getDeclaredFields(clazz)).filter(e -> name.equals(e.getName())).findFirst().get();
	}

	public static Field getField(final Class<?> clazz, final String name, final Class<?> type) {
		Objects.requireNonNull(name, "name");
		return Arrays.stream(getDeclaredFields(clazz)).filter(e -> name.equals(e.getName()) && e.getType().equals(type))
				.findFirst().get();
	}

	public static Method getMethod(final Class<?> cls, final String name, final Class<?>... types) {
		return Arrays.stream(getDeclaredMethods(cls)).filter(e -> name.equals(e.getName()))
				.filter(e -> Arrays.equals(e.getParameterTypes(), types)).findFirst().get();
	}

	public static Class<?> ifaceFromWrapped(final Object handle) {
		return MethodHandleProxies.wrapperInstanceType(handle);
	}

	public static Lookup in(final Class<?> clazz) {
		try {
			return (Lookup) LOOKUP_CONSTRUCTOR.invokeExact(clazz);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static boolean isWrapped(final Object handle) {
		return MethodHandleProxies.isWrapperInstance(handle);
	}

	public static <T> T wrap(final Class<T> iFace, final MethodHandle handle) {
		return MethodHandleProxies.asInterfaceInstance(iFace, handle);
	}
	
	public static ClassLoader getClassLoader(Class<?> clazz) {
		try {
			return (ClassLoader) CLASSLOADER_GETTER.invokeExact(clazz);
		} catch (Throwable e) {
			throw new Error(e);
		}
	}

	private LookupUtil() {
	}
}
