package ru.zaxar163.util.dynamicgen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import lombok.experimental.UtilityClass;
import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.DelegatingClassLoader;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.dynamicgen.reflect.InvokerConstructor;
import ru.zaxar163.util.dynamicgen.reflect.InvokerMethodF;
import ru.zaxar163.util.dynamicgen.reflect.InvokerMethodR;
import ru.zaxar163.util.proxies.ProxyList;

@UtilityClass
@Keep
class DataAccessor1 {
	@Keep
	static Object newInstance(final Class<?> clazz) {
		return ProxyList.UNSAFE.allocateInstance(clazz);
	}
}

@UtilityClass
@Keep
class DataAccessor2 {
	@Keep
	static Object newInstance(final Class<?> clazz) {
		return MiscUtil.newInstance(clazz);
	}
}

@UtilityClass
class ProxyData {
	static final AtomicInteger cnter = new AtomicInteger(0);
	static final Method invokeC = Method.getMethod(LookupUtil.getDeclaredMethodsNonCache(InvokerConstructor.class)[0]);
	static final Method invokeF = Method.getMethod(LookupUtil.getDeclaredMethodsNonCache(InvokerMethodF.class)[0]);
	static final Method invokeR = Method.getMethod(LookupUtil.getDeclaredMethodsNonCache(InvokerMethodR.class)[0]);
	static final Class<?> MAGIC_CLASS;
	static final String MAGIC_PACKAGE;
	static final String MAGIC_SUPER;

	static final Type OT = Type.getType(Object.class);

	static final Random r = new Random(System.currentTimeMillis());
	static final Method unsupported = Method.getMethod(LookupUtil.getDeclaredMethodsNonCache(ProxyUtil.class)[0]);

	static {
		MAGIC_CLASS = ClassUtil.nonThrowingFirstClass("jdk.internal.reflect.MagicAccessorImpl",
				"sun.reflect.MagicAccessorImpl");
		MAGIC_SUPER = Type.getInternalName(MAGIC_CLASS);
		MAGIC_PACKAGE = MAGIC_CLASS.getName().substring(0, MAGIC_CLASS.getName().lastIndexOf('.')).replace('.', '/');
	}

	static void caseArg(final GeneratorAdapter m, final Type type) {
		switch (type.getSort()) {
		case Type.BOOLEAN:
			m.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Boolean", "booleanValue", "()Z", false);
			break;
		case Type.BYTE:
			m.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Byte", "byteValue", "()C", false);
			break;
		case Type.CHAR:
			m.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Character", "charValue", "()B", false);
			break;
		case Type.SHORT:
			m.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Short", "shortValue", "()S", false);
			break;
		case Type.INT:
			m.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Integer", "intValue", "()I", false);
			break;
		case Type.FLOAT:
			m.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Float", "floatValue", "()F", false);
			break;
		case Type.LONG:
			m.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Long", "longValue", "()J", false);
			break;
		case Type.DOUBLE:
			m.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Double", "doubleValue", "()D", false);
			break;
		default:
			break;
		}
	}

	static void caseRet(final GeneratorAdapter m, final Type type) {
		switch (type.getSort()) {
		case Type.BOOLEAN:
			visitOf(m, Boolean.class, boolean.class);
			break;
		case Type.BYTE:
			visitOf(m, Byte.class, byte.class);
			break;
		case Type.CHAR:
			visitOf(m, Character.class, char.class);
			break;
		case Type.SHORT:
			visitOf(m, Short.class, short.class);
			break;
		case Type.INT:
			visitOf(m, Integer.class, int.class);
			break;
		case Type.FLOAT:
			visitOf(m, Float.class, float.class);
			break;
		case Type.LONG:
			visitOf(m, Long.class, long.class);
			break;
		case Type.DOUBLE:
			visitOf(m, Double.class, double.class);
			break;
		case Type.VOID:
			m.visitInsn(Opcodes.ACONST_NULL);
			break;
		default:
			break;
		}
	}

	static DelegatingClassLoader forConstructor(final Constructor<?> cons) {
		return DelegatingClassLoader.forClassLoader(LookupUtil.getClassLoader(cons.getDeclaringClass()))
				.add(MAGIC_CLASS).add(InvokerConstructor.class);
	}

	static DelegatingClassLoader forMethodF(final Constructor<?> m) {
		return DelegatingClassLoader.forClassLoader(LookupUtil.getClassLoader(m.getDeclaringClass())).add(MAGIC_CLASS)
				.add(InvokerMethodF.class);
	}

	static DelegatingClassLoader forMethodF(final java.lang.reflect.Method m) {
		return DelegatingClassLoader.forClassLoader(LookupUtil.getClassLoader(m.getDeclaringClass())).add(MAGIC_CLASS)
				.add(InvokerMethodF.class);
	}

	static DelegatingClassLoader forMethodR(final Constructor<?> m) {
		return DelegatingClassLoader.forClassLoader(LookupUtil.getClassLoader(m.getDeclaringClass())).add(MAGIC_CLASS)
				.add(InvokerMethodR.class);
	}

	static DelegatingClassLoader forMethodR(final java.lang.reflect.Method m) {
		return DelegatingClassLoader.forClassLoader(LookupUtil.getClassLoader(m.getDeclaringClass())).add(MAGIC_CLASS)
				.add(InvokerMethodR.class);
	}

	static DelegatingClassLoader forMisc(final Class<?> m) {
		return DelegatingClassLoader.forClassLoader(LookupUtil.getClassLoader(m)).add(MAGIC_CLASS);
	}

	static DelegatingClassLoader forProxy(final ClassLoader loader) {
		return DelegatingClassLoader.forClassLoader(loader).add(MAGIC_CLASS);
	}

	static String nextName() {
		return MAGIC_PACKAGE + "/Proxy" + r.nextInt(Integer.MAX_VALUE) + "N" + cnter.getAndIncrement()
				+ "ImplGenerated";
	}

	static int switchType(final java.lang.reflect.Method m) {
		if (Modifier.isStatic(m.getModifiers()))
			return Opcodes.INVOKESTATIC;
		if (m.getDeclaringClass().isInterface())
			return Opcodes.INVOKEINTERFACE;
		if (Modifier.isFinal(m.getModifiers()) || Modifier.isFinal(m.getDeclaringClass().getModifiers()))
			return Opcodes.INVOKESPECIAL;
		return Opcodes.INVOKEVIRTUAL;
	}

	private static void visitOf(final GeneratorAdapter m, final Class<?> clazz, final Class<?> param) {
		try {
			m.invokeStatic(Type.getType(clazz), Method.getMethod(clazz.getDeclaredMethod("valueOf", param)));
		} catch (final Throwable e) {
			throw new Error(e); // should never happen
		}
	}
}

@UtilityClass
@Keep
class ProxyUtil {
	@Keep
	static void unsupported() {
		throw new IllegalStateException("unsupported on current JVM method");
	}
}