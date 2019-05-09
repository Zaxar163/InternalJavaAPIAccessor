package ru.zaxar163.core;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import ru.zaxar163.unsafe.fast.InvokerConstructor;
import ru.zaxar163.unsafe.fast.ReflectionUtil;

public final class LookupUtil {
	public static final Lookup ALL_LOOKUP;
	public static final int ALL_MODES = Lookup.PUBLIC | Lookup.PRIVATE | Lookup.PROTECTED | Lookup.PACKAGE;
	private static final MethodHandle CONSTRUCTORS_GETTER;
	private static final MethodHandle DECLAREDCLASSES_GETTER;
	private static final MethodHandle FIELDS_GETTER;
	private static final MethodHandle LOOKUP_CONSTRUCTOR;
	private static final InvokerConstructor LOOKUP_SUPERPERM_CONSTRUCTOR;
	private static final InvokerConstructor LOOKUP_UNSAFE_CONSTRUCTOR;
	private static final MethodHandle METHODS_GETTER;
	public static final int TRUSTED;

	static {
		try {
			MethodHandles.publicLookup(); // hack to cause classloading of Lookup
			final Method getFields = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
			getFields.setAccessible(true);
			final Field allPermsLookup = Arrays.stream((Field[]) getFields.invoke(Lookup.class, false))
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
			FIELDS_GETTER = ALL_LOOKUP.unreflect(getFields);
			METHODS_GETTER = ALL_LOOKUP.findSpecial(Class.class, "getDeclaredMethods0",
					MethodType.methodType(Method[].class, boolean.class), Class.class);
			CONSTRUCTORS_GETTER = ALL_LOOKUP.findSpecial(Class.class, "getDeclaredConstructors0",
					MethodType.methodType(Constructor[].class, boolean.class), Class.class);
			DECLAREDCLASSES_GETTER = ALL_LOOKUP.findSpecial(Class.class, "getDeclaredClasses0",
					MethodType.methodType(Class[].class), Class.class);
		} catch (final Throwable e) {
			throw new Error(e);
		}
		InvokerConstructor LOOKUP_UNSAFE_CONSTRUCTORT = null;
		try {
			LOOKUP_UNSAFE_CONSTRUCTORT = ReflectionUtil
					.handleHC(ReflectionUtil.handleD(Arrays.stream(getDeclaredConstructors(Lookup.class))
							.filter(e -> e.getParameterCount() == 1 && e.getParameterTypes()[0].equals(Class.class))
							.findFirst().get()));
		} catch (final Throwable e) {
		}
		LOOKUP_UNSAFE_CONSTRUCTOR = LOOKUP_UNSAFE_CONSTRUCTORT;
		InvokerConstructor SUPER_PERMS_CONSTRUCTORI = null;
		int trusted = 0;
		try {
			SUPER_PERMS_CONSTRUCTORI = ReflectionUtil
					.handleHC(
							ReflectionUtil.handleD(Arrays.stream(getDeclaredConstructors(Lookup.class))
									.filter(e -> e.getParameterCount() == 2
											&& e.getParameterTypes()[0].equals(Class.class)
											&& e.getParameterTypes()[1].equals(int.class))
									.findFirst().get()));
			final Field trustedF = Arrays.stream(getDeclaredFields(Lookup.class))
					.filter(e -> !e.isAccessible() && e.getName().toLowerCase(Locale.US).contains("trust")).findFirst()
					.get();
			trustedF.setAccessible(true);
			trusted = trustedF.getInt(null);
		} catch (final Throwable t) {
		}
		LOOKUP_SUPERPERM_CONSTRUCTOR = SUPER_PERMS_CONSTRUCTORI;
		TRUSTED = trusted;
	}

	public static Lookup constructNormal(final Class<?> clazz) {
		return constructWithoutChecks(clazz, ALL_MODES);
	}

	public static Lookup constructTrusted(final Class<?> clazz) {
		if (TRUSTED == 0)
			return null;
		return constructWithoutChecks(clazz, TRUSTED);
	}

	public static Lookup constructWithoutChecks(final Class<?> clazz, final int mode) {
		if (LOOKUP_SUPERPERM_CONSTRUCTOR == null)
			return null;
		try {
			return (Lookup) LOOKUP_SUPERPERM_CONSTRUCTOR.newInstance(clazz, mode);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static Lookup constuct(final Class<?> clazz) {
		if (LOOKUP_UNSAFE_CONSTRUCTOR == null)
			return null;
		try {
			return (Lookup) LOOKUP_UNSAFE_CONSTRUCTOR.newInstance(clazz);
		} catch (final Throwable e) {
			throw new Error(e);
		}
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
			return (Class<?>[]) DECLAREDCLASSES_GETTER.invoke(clazz);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> Constructor<T>[] getDeclaredConstructors(final Class<T> clazz) {
		try {
			return (Constructor<T>[]) CONSTRUCTORS_GETTER.invoke(clazz, false);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static Field[] getDeclaredFields(final Class<?> clazz) {
		try {
			return (Field[]) FIELDS_GETTER.invoke(clazz, false);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static Method[] getDeclaredMethods(final Class<?> clazz) {
		try {
			return (Method[]) METHODS_GETTER.invoke(clazz, false);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static Field getField(final Class<?> clazz, final String name) {
		Objects.requireNonNull(name, "name");
		return Arrays.stream(getDeclaredFields(clazz)).filter(e -> name.equals(e.getName())).findFirst().get();
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

	private LookupUtil() {
	}
}
