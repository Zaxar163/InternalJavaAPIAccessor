package ru.zaxar163.util.dynamicgen;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.DelegateClassLoader;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.proxies.ProxyList;

public final class MiscUtil {
	private static final Set<Field> objectFields;
	private static final Map<Class<?>, Object> objs;
	private static final ConcurrentHashMap<Class<?>, Supplier<Object>> sameSizes;
	static {
		objs = new ConcurrentHashMap<>();
		objs.put(Class.class, Object.class);
		objectFields = Collections
				.unmodifiableSet(new HashSet<>(Arrays.asList(LookupUtil.getDeclaredFields(Object.class))));
		sameSizes = new ConcurrentHashMap<>();
	}

	public static Object changeObjFullUnsafe(final Object required, final Object o) {
		ProxyList.UNSAFE.putInt(o, 8L, ProxyList.UNSAFE.getInt(required, 8L));
		return o;
	}

	@SuppressWarnings("unchecked")
	public static <T> T changeObjUnsafe(final Class<T> required, final Object o) {
		return (T) changeObjFullUnsafe(objs.computeIfAbsent(required, c -> ProxyList.UNSAFE.allocateInstance(c)), o);
	}

	public static void computeSameSize(final Class<?> clazz) {
		sameSizes.computeIfAbsent(clazz, c -> sameSizeObject(DelegateClassLoader.INSTANCE, c, Collections.emptyList()));
	}

	public static <T> T newInstance(final Class<T> t) {
		return changeObjUnsafe(t, sameSizes.computeIfAbsent(t,
				c -> sameSizeObject(DelegateClassLoader.INSTANCE, c, Collections.emptyList())));
	}

	public static Supplier<Object> putSameSize(final Class<?> clazz, final Supplier<Object> instancer) {
		return sameSizes.put(clazz, instancer);
	}

	private static Class<?> sameSizeClass(final ClassLoader loader, final Class<?> klass,
			final Collection<String> excluded) {
		if (klass.equals(Object.class))
			return Object.class;
		DelegateClassLoader.INSTANCE.append(klass);
		final String className = ProxyData.nextName(true);
		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, className, null, ProxyData.MAGIC_SUPER, null);
		int i = 0;
		Class<?> superc = klass;
		while (superc != null && !superc.equals(Object.class)) {
			for (final Field field : LookupUtil.getDeclaredFields(superc)) {
				if (objectFields.contains(field) || excluded.contains(field.getName()))
					continue;
				cw.visitField(Opcodes.ACC_PRIVATE, "field_generated" + ProxyData.r.nextInt(Integer.MAX_VALUE) + "m" + i,
						Type.getDescriptor(field.getType()), null, null);
				i++;
			}
			superc = superc.getSuperclass();
		}
		final byte[] code = cw.toByteArray();
		try {
			return ClassUtil.defineClass1_native(loader, className, code, 0, code.length, null, null);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static Supplier<Object> sameSizeObject(final ClassLoader loader, final Class<?> klass,
			final Collection<String> excluded) {
		final Class<?> proxy = sameSizeClass(loader, klass, excluded);
		return () -> ProxyList.UNSAFE.allocateInstance(proxy);
	}

	private MiscUtil() {
	}
}
