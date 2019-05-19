package ru.zaxar163.util.generating;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.MethodInfo;

final class UnsafeAccessor {
	public static final Class<?> UNSAFE_CLASS;
	public static final Map<String, Object> UNSAFE_FIELDS;
	public static final Map<MethodInfo, MethodHandle> UNSAFE_METHODS;
	public static final Object UNSAFE_OBJ;

	static {
		try {
			final AtomicInteger integer = new AtomicInteger(0);
			if (integer.incrementAndGet() != 1) // hack to cause classloading of Unsafe.
				throw new Throwable();
			UNSAFE_CLASS = ClassUtil.firstClass("jdk.internal.misc.Unsafe", "sun.misc.Unsafe");
			final Field[] fieldsUnsafe = LookupUtil.getDeclaredFields(UNSAFE_CLASS);
			final Field theUnsafe = Arrays.stream(fieldsUnsafe).filter(
					e -> e.getType().equals(UNSAFE_CLASS) && e.getName().toLowerCase(Locale.US).contains("unsafe"))
					.findFirst().get();
			theUnsafe.setAccessible(true);
			UNSAFE_OBJ = theUnsafe.get(null);
			final Map<String, Object> toFillF = new HashMap<>();
			for (final Field f : fieldsUnsafe) {
				if (f.equals(theUnsafe))
					continue;
				f.setAccessible(true);
				toFillF.put(f.getName(), f.get(UNSAFE_OBJ));
			}
			UNSAFE_FIELDS = Collections.unmodifiableMap(toFillF);
			final Map<MethodInfo, MethodHandle> toFill = new HashMap<>();
			for (final Method m : UNSAFE_CLASS.getDeclaredMethods()) {
				final MethodHandle wrapped = (m.getModifiers() & Modifier.STATIC) != 0
						? LookupUtil.ALL_LOOKUP.unreflect(m)
						: LookupUtil.ALL_LOOKUP.unreflect(m).bindTo(UNSAFE_OBJ);
				toFill.put(new MethodInfo(m.getName(), wrapped.type()), wrapped);
			}
			UNSAFE_METHODS = Collections.unmodifiableMap(toFill);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	private UnsafeAccessor() {
	}
}
