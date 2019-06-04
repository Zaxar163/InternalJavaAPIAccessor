package ru.zaxar163.util.dynamicgen;

import static ru.zaxar163.util.dynamicgen.ProxyData.OT;
import static ru.zaxar163.util.dynamicgen.ProxyData.caseArg;
import static ru.zaxar163.util.dynamicgen.ProxyData.caseRet;
import static ru.zaxar163.util.dynamicgen.ProxyData.invokeR;
import static ru.zaxar163.util.dynamicgen.ProxyData.switchType;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ru.zaxar163.util.dynamicgen.reflect.InvokerMethodR;
import ru.zaxar163.util.proxies.ProxyList;

public final class MethodAccGenR {
	static void emit(final Type clazz, final Method method, final int type, final ClassVisitor cw, final Type ret) {
		final GeneratorAdapter m = new GeneratorAdapter(Opcodes.ACC_PUBLIC, invokeR, null,
				new Type[] { Type.getType(Throwable.class) }, cw);
		m.visitCode();
		m.loadArg(0);
		for (int i = 0; i < method.getArgumentTypes().length; i++) {
			m.loadArg(1);
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

	public static InvokerMethodR method(final java.lang.reflect.Constructor<?> m) {
		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final String name = ProxyData.nextName(true);
		cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, ProxyData.MAGIC_SUPER,
				new String[] { Type.getInternalName(InvokerMethodR.class) });
		emit(Type.getType(m.getDeclaringClass()), Method.getMethod(m), Opcodes.INVOKESPECIAL, cw, Type.VOID_TYPE);
		cw.visitEnd();
		final byte[] code = cw.toByteArray();
		try {
			final Class<?> clazz = ProxyList.UNSAFE.defineClass(name, code, 0, code.length, ProxyData.forMethodR(m),
					null);
			return (InvokerMethodR) ProxyList.UNSAFE.allocateInstance(clazz);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static InvokerMethodR method(final java.lang.reflect.Method m) {
		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final String name = ProxyData.nextName(true);
		cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, ProxyData.MAGIC_SUPER,
				new String[] { Type.getInternalName(InvokerMethodR.class) });
		emit(Type.getType(m.getDeclaringClass()), Method.getMethod(m), switchType(m), cw,
				Type.getType(m.getReturnType()));
		cw.visitEnd();
		final byte[] code = cw.toByteArray();
		try {
			final Class<?> clazz = ProxyList.UNSAFE.defineClass(name, code, 0, code.length, ProxyData.forMethodR(m),
					null);
			return (InvokerMethodR) ProxyList.UNSAFE.allocateInstance(clazz);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private MethodAccGenR() {
	}
}