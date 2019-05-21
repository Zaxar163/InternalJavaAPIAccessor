package ru.zaxar163.demonstration;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ru.zaxar163.demonstration.reflect.MethodAcc;
import ru.zaxar163.util.DelegateClassLoader;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.dynamicgen.GeneratorAdapter;
import ru.zaxar163.util.dynamicgen.Method;
import ru.zaxar163.util.proxies.ProxyList;

public final class InvokerGenerator {
	private static final Method invoke;
	private static final MethodHandle invokerD;
	static {
		invoke = Method.getMethod(MethodAcc.class.getDeclaredMethods()[0]);
		invokerD = method(ReflectionUtil.ifaceAccessor);
	}

	static void emit(final Type clazz, final Method method, final int type, final ClassVisitor cw, final boolean isVoid,
			final Type thiz) {
		final GeneratorAdapter m = new GeneratorAdapter(Opcodes.ACC_PUBLIC, invoke, null,
				new Type[] { Type.getType(Throwable.class) }, cw);
		m.visitCode();
		m.loadThis();
		m.getField(thiz, "handle", clazz);
		m.loadArgs();
		m.visitMethodInsn(type, clazz.getInternalName(), method.getName(), method.getDescriptor(),
				type == Opcodes.INVOKEINTERFACE);
		if (isVoid)
			m.visitInsn(Opcodes.ACONST_NULL);
		m.returnValue();
		m.visitEnd();
	}

	static MethodAcc invoker(final Object inv) {
		try {
			return (MethodAcc) invokerD.invoke(inv);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	static MethodHandle method(final Class<?> iface) {
		DelegateClassLoader.INSTANCE.append(iface);
		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final String name = ProxyData.nextName(true);
		cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, ProxyData.MAGIC_SUPER,
				new String[] { Type.getInternalName(MethodAcc.class) });
		final String handleDescriptor = Type.getDescriptor(iface);
		cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "handle", handleDescriptor, null, null);
		final MethodVisitor init = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", '(' + handleDescriptor + ")V", null,
				null);
		init.visitCode();
		init.visitVarInsn(Opcodes.ALOAD, 0);
		init.visitMethodInsn(Opcodes.INVOKESPECIAL, ProxyData.MAGIC_SUPER, "<init>", "()V", false);
		init.visitVarInsn(Opcodes.ALOAD, 0);
		init.visitVarInsn(Opcodes.ALOAD, 1);
		init.visitFieldInsn(Opcodes.PUTFIELD, name, "handle", handleDescriptor);
		init.visitInsn(Opcodes.RETURN);
		init.visitMaxs(2, 2);
		init.visitEnd();
		emit(Type.getType(iface), Method.getMethod(LookupUtil.getDeclaredMethods(iface)[0]),
				switchType(LookupUtil.getDeclaredMethods(iface)[0]), cw,
				LookupUtil.getDeclaredMethods(iface)[0].getReturnType().equals(Void.TYPE), Type.getObjectType(name));
		cw.visitEnd();
		final byte[] code = cw.toByteArray();
		try {
			final Class<?> clazz = ProxyList.UNSAFE.defineClass(name, code, 0, code.length,
					DelegateClassLoader.INSTANCE, null);
			return LookupUtil.ALL_LOOKUP.findConstructor(clazz, MethodType.methodType(void.class, iface));
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

	private InvokerGenerator() {
	}
}