package ru.zaxar163.demonstration.utility;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class ClassEmitter {
	private static final List<String> objectMethods = Arrays.stream(Object.class.getDeclaredMethods())
			.map(e -> e.getName()).collect(Collectors.toList());

	private static String emitArgs(final MethodInfo name) {
		if (name.type.parameterCount() == 0)
			return "new MethodInfo(\"" + name.name + "\", " + name.type.returnType().getSimpleName() + ".class)";
		final StringBuilder sb = new StringBuilder(
				"new MethodInfo(\"" + name.name + "\", " + name.type.returnType().getSimpleName() + ".class, ");
		for (int i = 0; i < name.type.parameterCount(); i++) {
			sb.append(name.type.parameterArray()[i].getSimpleName() + ".class");
			if (i + 1 < name.type.parameterCount())
				sb.append(", ");
		}
		return sb.append(')').toString();
	}

	public static String generate(final Map<MethodInfo, MethodHandle> caller, final String nameClazz,
			final String accessor, final String packagee, final Set<String> includes) {
		final StringBuilder startDecl = new StringBuilder(packagee != null ? "package " + packagee + ";\n\n" : "");
		final StringBuilder code = new StringBuilder("\n@SuppressWarnings(\"rawtypes\") public class " + nameClazz
				+ " {\n	private " + nameClazz + "() {}\n");
		final List<Class<?>> classes = new ArrayList<>();
		classes.add(MethodHandle.class);
		classes.add(MethodInfo.class);
		final AtomicInteger a = new AtomicInteger(0);
		caller.forEach((name, handle) -> {
			if (objectMethods.contains(name.name) || includes.stream().noneMatch(e -> name.name.startsWith(e)))
				return;
			put(classes, handle);
			final String fn = name.name + a.getAndIncrement();
			final String field = "	private static final MethodHandle " + fn + " = " + accessor + "(" + emitArgs(name)
					+ ");\n";
			code.append(field);
			final StringBuilder codeBuilder = new StringBuilder();
			codeBuilder.append("	public static final ").append(handle.type().returnType().getSimpleName());
			codeBuilder.append(" ").append(name.name).append("(");
			final Class<?>[] params = handle.type().parameterArray();
			for (int i = 0; i < params.length; i++) {
				codeBuilder.append(params[i].getSimpleName() + " obj" + i);
				if (i + 1 < params.length)
					codeBuilder.append(", ");
			}
			codeBuilder.append(") { try { ");
			if (!handle.type().returnType().equals(void.class))
				codeBuilder.append("return (" + handle.type().returnType().getSimpleName() + ")");
			codeBuilder.append(fn + ".invokeExact(");
			for (int i = 0; i < params.length; i++) {
				codeBuilder.append("obj" + i);
				if (i + 1 < params.length)
					codeBuilder.append(", ");
			}
			codeBuilder.append("); } catch (Throwable t) { throw new Error(t); } }");
			code.append(codeBuilder).append('\n');
		});
		code.append('}');
		classes.forEach(e -> {
			if (!e.isArray() && !e.isPrimitive() && !isJavaLang(e))
				startDecl.append("import " + e.getName() + ";\n");
		});
		return startDecl.toString() + code.toString();
	}

	private static boolean isJavaLang(final Class<?> e) {
		if (!e.getName().startsWith("java.lang."))
			return false;
		return Character.isUpperCase(e.getName().charAt(10)); // small hack to get is it class or not.
	}

	public static void main(final String[] args) {
		System.out.println(generate(UnsafeAccessor.UNSAFE_METHODS, "UnsafeUtil", "UnsafeAccessor.UNSAFE_METHODS.get",
				"ru.zaxar163.unsafe", new HashSet<>(Arrays.asList("define", "ensureClass", "shouldBeInitialized"))));
	}

	private static void put(final List<Class<?>> classes, final MethodHandle handle) {
		final MethodType type = handle.type();
		if (!classes.contains(type.returnType()))
			classes.add(type.returnType());
		for (final Class<?> arg : type.parameterArray())
			if (!classes.contains(arg))
				classes.add(arg);
	}

	private ClassEmitter() {
	}
}
