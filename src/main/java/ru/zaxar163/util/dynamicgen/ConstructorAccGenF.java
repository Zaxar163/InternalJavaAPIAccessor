package ru.zaxar163.util.dynamicgen;

import static ru.zaxar163.util.dynamicgen.ProxyData.OT;
import static ru.zaxar163.util.dynamicgen.ProxyData.caseArg;
import static ru.zaxar163.util.dynamicgen.ProxyData.caseRet;
import static ru.zaxar163.util.dynamicgen.ProxyData.invokeC;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import lombok.experimental.UtilityClass;
import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.dynamicgen.reflect.InvokerConstructor;
import ru.zaxar163.util.proxies.ProxyList;

@UtilityClass
public class ConstructorAccGenF {
	private static final Method newInst = Method
			.getMethod(LookupUtil.getDeclaredMethodsNonCache(DataAccessor1.class)[0]);
	private static final Type newInstC = Type.getType(DataAccessor1.class);

	public static void emit(final Type clazz, final Method method, final int type, final ClassVisitor cw,
			final Type ret) {
		final GeneratorAdapter m = new GeneratorAdapter(Opcodes.ACC_PUBLIC, invokeC, null,
				new Type[] { Type.getType(Throwable.class) }, cw);
		m.visitCode();
		m.visitLdcInsn(clazz);
		m.invokeStatic(newInstC, newInst);
		for (int i = 0; i < method.getArgumentTypes().length; i++) {
			m.loadArgs();
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

	public static InvokerConstructor instancer(final java.lang.reflect.Constructor<?> m) {
		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final String name = ProxyData.nextName();
		cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, ProxyData.MAGIC_SUPER,
				new String[] { Type.getInternalName(InvokerConstructor.class) });
		emit(Type.getType(m.getDeclaringClass()), Method.getMethod(m), Opcodes.INVOKESPECIAL, cw, Type.VOID_TYPE);
		cw.visitEnd();
		final byte[] code = cw.toByteArray();
		try {
			final Class<?> clazz = ClassUtil.defineClass(ProxyData.forConstructor(m), name, code, 0, code.length, null);
			return (InvokerConstructor) ProxyList.UNSAFE.allocateInstance(clazz);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
