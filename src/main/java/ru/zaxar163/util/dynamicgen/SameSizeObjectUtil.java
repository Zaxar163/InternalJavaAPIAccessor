package ru.zaxar163.util.dynamicgen;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.proxies.ProxyList;

public class SameSizeObjectUtil {
	private SameSizeObjectUtil() {}
	private static final Set<Field> objectFields = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(LookupUtil.getDeclaredFields(Object.class))));

	private static Class<?> sameSizeClass(final ClassLoader loader, final Class<?> klass,
			final Collection<String> excluded) {
		if (klass.equals(Object.class)) return Object.class;
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
}
