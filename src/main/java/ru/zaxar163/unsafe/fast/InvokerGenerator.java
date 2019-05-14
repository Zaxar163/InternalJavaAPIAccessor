package ru.zaxar163.unsafe.fast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ru.zaxar163.core.DelegateClassLoader;
import ru.zaxar163.unsafe.fast.proxies.ProxyList;

public final class InvokerGenerator {
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

	private static void emit(final Type clazz, final Method method, final int type, final ClassVisitor cw,
			final boolean isVoid) {
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
		if (isVoid)
			m.visitInsn(Opcodes.ACONST_NULL);
		m.returnValue();
		m.visitEnd();
	}

	private static InvokerMethod method(final java.lang.reflect.Constructor<?> m) {
		DelegateClassLoader.INSTANCE.append(m);
		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final String name = ProxyData.nextName(true);
		cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, ProxyData.MAGIC_SUPER,
				new String[] { Type.getInternalName(InvokerMethod.class) });
		emit(Type.getType(m.getDeclaringClass()), Method.getMethod(m), Opcodes.INVOKESPECIAL, cw, true);
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

	private static InvokerMethod method(final java.lang.reflect.Method m) {
		DelegateClassLoader.INSTANCE.append(m);
		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final String name = ProxyData.nextName(true);
		cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, ProxyData.MAGIC_SUPER,
				new String[] { Type.getInternalName(InvokerMethod.class) });
		emit(Type.getType(m.getDeclaringClass()), Method.getMethod(m), switchType(m), cw,
				m.getReturnType().equals(Void.TYPE));
		cw.visitEnd();
		final byte[] code = cw.toByteArray();
		try {
			final Class<?> clazz = ProxyList.UNSAFE.defineClass(name, code, 0,
					code.length, DelegateClassLoader.INSTANCE, null);
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

	public static InvokerConstructor wrapConstructor(final Constructor<?> t) {
		final InvokerMethod cns = wrapConstructorNonInstance(t);
		final Class<?> decl = t.getDeclaringClass();
		return (args) -> {
			final Object[] argsi = new Object[args.length + 1];
			final Object instance = ProxyList.UNSAFE.allocateInstance(decl);
			argsi[0] = instance;
			System.arraycopy(args, 0, argsi, 1, args.length);
			cns.invoke(argsi);
			return instance;
		};
	}

	public static InvokerMethod wrapConstructorNonInstance(final Constructor<?> t) {
		return InvokerGenerator.method(t);
	}

	public static InvokerMethod wrapMethod(final java.lang.reflect.Method t) {
		return InvokerGenerator.method(t);
	}

	private InvokerGenerator() {
	}
}