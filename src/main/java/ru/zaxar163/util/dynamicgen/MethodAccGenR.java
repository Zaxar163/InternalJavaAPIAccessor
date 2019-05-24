package ru.zaxar163.util.dynamicgen;

import java.lang.reflect.Modifier;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ru.zaxar163.util.DelegateClassLoader;
import ru.zaxar163.util.proxies.ProxyList;

public final class MethodAccGenR {
	public static interface InvokerMethod {
		Object invoke(Object obj, Object... args);
	}

	private static final Method invoke = Method.getMethod(InvokerMethod.class.getDeclaredMethods()[0]);
	private static final Type OT = Type.getType(Object.class);
	private static final Type OTA = Type.getType(Object[].class);

	private static void caseArg(final GeneratorAdapter m, final Type type) {
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

	private static void caseRet(final GeneratorAdapter m, final Type type) {
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

	public static void emit(final Type clazz, final Method method, final int type, final ClassVisitor cw,
			final Type ret) {
		final GeneratorAdapter m = new GeneratorAdapter(Opcodes.ACC_PUBLIC, invoke, null,
				new Type[] { Type.getType(Throwable.class) }, cw);
		m.visitCode();
		final int identifier = m.newLocal(OTA);
		m.loadThis();
		m.loadArgs();
		m.storeLocal(identifier);
		for (int i = 0; i < method.getArgumentTypes().length; i++) {
			m.loadLocal(identifier);
			m.push(i);
			m.arrayLoad(OT);
			caseArg(m, method.getArgumentTypes()[i]);
		}
		m.visitMethodInsn(type, clazz.getInternalName(), method.getName(), method.getDescriptor(),
				type == Opcodes.INVOKEINTERFACE);
		caseRet(m, ret);
		m.returnValue();
		m.visitEnd();
	}

	public static InvokerMethod method(final java.lang.reflect.Constructor<?> m) {
		DelegateClassLoader.INSTANCE.append(m);
		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final String name = ProxyData.nextName(true);
		cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, ProxyData.MAGIC_SUPER,
				new String[] { Type.getInternalName(InvokerMethod.class) });
		emit(Type.getType(m.getDeclaringClass()), Method.getMethod(m), Opcodes.INVOKESPECIAL, cw, Type.VOID_TYPE);
		cw.visitEnd();
		final byte[] code = cw.toByteArray();
		try {
			final Class<?> clazz = ProxyList.UNSAFE.defineClass(name, code, 0, code.length,
					DelegateClassLoader.INSTANCE, null);
			return (InvokerMethod) ProxyList.UNSAFE.allocateInstance(clazz);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static InvokerMethod method(final java.lang.reflect.Method m) {
		DelegateClassLoader.INSTANCE.append(m);
		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final String name = ProxyData.nextName(true);
		cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, ProxyData.MAGIC_SUPER,
				new String[] { Type.getInternalName(InvokerMethod.class) });
		emit(Type.getType(m.getDeclaringClass()), Method.getMethod(m), switchType(m), cw,
				Type.getType(m.getReturnType()));
		cw.visitEnd();
		final byte[] code = cw.toByteArray();
		try {
			final Class<?> clazz = ProxyList.UNSAFE.defineClass(name, code, 0, code.length,
					DelegateClassLoader.INSTANCE, null);
			return (InvokerMethod) ProxyList.UNSAFE.allocateInstance(clazz);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private static int switchType(final java.lang.reflect.Method m) {
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

	private MethodAccGenR() {
	}
}