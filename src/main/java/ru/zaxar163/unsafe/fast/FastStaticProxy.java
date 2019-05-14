package ru.zaxar163.unsafe.fast;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ru.zaxar163.core.ClassUtil;
import ru.zaxar163.core.LookupUtil;

/**
 * For instance.
 */
public class FastStaticProxy<T> {
	private static Type[] typify(final Class<?>[] clazzs) {
		final Type[] types = new Type[clazzs.length];
		for (int i = 0; i < types.length; i++)
			types[i] = Type.getType(clazzs[i]);
		return types;
	}

	private final Class<?> clazz;

	private final ClassLoader loader;
	private final Class<T> proxy;
	private final MethodHandle proxyC;

	public FastStaticProxy(final ClassLoader loader, final Class<?> clazz, final Class<T> proxy) {
		this.loader = loader;
		this.clazz = clazz;
		this.proxy = proxy;
		this.proxyC = emitProxy();
	}

	private void emit(final Map<java.lang.reflect.Method, Method> methods, final ClassVisitor cw, final Type vt,
			final Type sn) {
		final String handleDescriptor = Type.getDescriptor(clazz);
		final Type handleT = Type.getType(clazz);
		final String internalClassName = vt.getInternalName();
		cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "handle", handleDescriptor, null, null);
		final MethodVisitor init = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", '(' + handleDescriptor + ")V", null,
				null);
		init.visitCode();
		init.visitVarInsn(Opcodes.ALOAD, 0);
		init.visitMethodInsn(Opcodes.INVOKESPECIAL, sn.getInternalName(), "<init>", "()V", false);
		init.visitVarInsn(Opcodes.ALOAD, 0);
		init.visitVarInsn(Opcodes.ALOAD, 1);
		init.visitFieldInsn(Opcodes.PUTFIELD, internalClassName, "handle", handleDescriptor);
		init.visitInsn(Opcodes.RETURN);
		init.visitMaxs(2, 2);
		init.visitEnd();
		for (final Map.Entry<java.lang.reflect.Method, Method> method : methods.entrySet()) {
			final GeneratorAdapter m = new GeneratorAdapter(Opcodes.ACC_PUBLIC, method.getValue(), null,
					typify(method.getKey().getExceptionTypes()), cw);
			final boolean isStatic = Modifier.isStatic(LookupUtil
					.getMethod(clazz, method.getKey().getName(), method.getKey().getParameterTypes()).getModifiers());
			m.visitCode();
			m.loadThis();
			if (!isStatic)
				m.getField(vt, "handle", handleT);
			m.loadArgs();
			if (isStatic)
				m.invokeStatic(handleT, method.getValue());
			else
				m.invokeVirtual(handleT, method.getValue());
			m.returnValue();
			m.visitEnd();
		}
	}

	private MethodHandle emitProxy() {
		final Map<java.lang.reflect.Method, Method> methods = Arrays.stream(proxy.getDeclaredMethods())
				.collect(Collectors.toMap(m -> m,
						m -> m.isAnnotationPresent(RealName.class)
								? new Method(m.getAnnotation(RealName.class).value(), Type.getMethodDescriptor(m))
								: new Method(m.getName(), Type.getMethodDescriptor(m))));
		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final String name = ProxyData.nextName(true);
		cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, ProxyData.MAGIC_SUPER,
				new String[] { Type.getInternalName(proxy) });
		emit(methods, cw, Type.getObjectType(name), Type.getObjectType(ProxyData.MAGIC_SUPER));
		cw.visitEnd();
		final byte[] code = cw.toByteArray();
		try {
			return LookupUtil.ALL_LOOKUP.unreflectConstructor(ClassUtil
					.defineClass1_native(loader, name, code, 0, code.length, null, null).getDeclaredConstructors()[0]);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public T instance(final Object handle) {
		try {
			return (T) proxyC.invoke(handle);
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
