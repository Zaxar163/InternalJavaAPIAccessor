package ru.zaxar163.util.dynamicgen;

import java.lang.reflect.Modifier;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.DelegateClassLoader;
import ru.zaxar163.util.dynamicgen.reflect.InvokerConstructor;
import ru.zaxar163.util.dynamicgen.reflect.InvokerMethodF;
import ru.zaxar163.util.dynamicgen.reflect.InvokerMethodR;

final class ProxyData {
	static final AtomicInteger cnter = new AtomicInteger(0);
	static final Method invokeC = Method.getMethod(InvokerConstructor.class.getDeclaredMethods()[0]);
	static final Method invokeF = Method.getMethod(InvokerMethodF.class.getDeclaredMethods()[0]);
	static final Method invokeR = Method.getMethod(InvokerMethodR.class.getDeclaredMethods()[0]);
	static final ClassLoader MAGIC_CLASSLOADER;
	static final String MAGIC_PACKAGE;

	static final String MAGIC_SUPER;

	static final Type OT = Type.getType(Object.class);

	static final Type OTA = Type.getType(Object[].class);

	static final Random r = new Random(System.currentTimeMillis());

	static {
		final Class<?> magic = ClassUtil.nonThrowingFirstClass("jdk.internal.reflect.MagicAccessorImpl",
				"sun.reflect.MagicAccessorImpl");
		MAGIC_SUPER = Type.getInternalName(magic);
		MAGIC_PACKAGE = magic.getName().substring(0, magic.getName().lastIndexOf('.')).replace('.', '/');
		MAGIC_CLASSLOADER = magic.getClassLoader();
		DelegateClassLoader.INSTANCE.append(MAGIC_CLASSLOADER);
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

	static ClassLoader defaultForVer(final Class<?> klass, final boolean sys) {
		DelegateClassLoader.INSTANCE.append(klass);
		return DelegateClassLoader.INSTANCE;
	}

	static String nextName(final boolean magic) {
		if (magic)
			return MAGIC_PACKAGE + "/Proxy" + r.nextInt(Integer.MAX_VALUE) + "ImplGenerated";
		return "ru/zaxar163/unsafe/Proxy" + r.nextInt(Integer.MAX_VALUE) + "N" + cnter.getAndIncrement()
				+ "ImplGenerated";
	}

	public static int switchType(final java.lang.reflect.Method m) {
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

	private ProxyData() {
	}
}
