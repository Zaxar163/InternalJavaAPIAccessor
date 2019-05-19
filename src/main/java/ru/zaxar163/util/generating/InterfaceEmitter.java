package ru.zaxar163.util.generating;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
//import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import ru.zaxar163.util.MethodInfo;
import ru.zaxar163.util.dynamicgen.RealName;

public class InterfaceEmitter {
	private static final List<String> objectMethods = Arrays.stream(Object.class.getDeclaredMethods())
			.map(e -> e.getName()).collect(Collectors.toList());

	public static String generate(final Map<MethodInfo, MethodHandle> caller, final String nameClazz,
			final String packagee, final boolean excludeInternalImport, final Set<String> exclusions) {
		final StringBuilder startDecl = new StringBuilder(packagee != null ? "package " + packagee + ";\n\n" : "");
		final StringBuilder code = new StringBuilder(
				"\n@SuppressWarnings(\"rawtypes\") public interface " + nameClazz + " {\n");
		final List<Class<?>> classes = new ArrayList<>();
		if (!excludeInternalImport)
			classes.add(RealName.class);
		caller.forEach((name, handle) -> {
			if (objectMethods.contains(name.name) || exclusions.contains(name.name))
				return;
			final StringBuilder codeBuilder = new StringBuilder();
			codeBuilder.append("	");
			codeBuilder.append("@RealName(\"" + name.name + "\") ").append(handle.type().returnType().getSimpleName());
			if (!classes.contains(name.type.returnType()))
				classes.add(name.type.returnType());
			codeBuilder.append(" ").append(name.name).append("(");
			final Class<?>[] params = handle.type().parameterArray();
			for (int i = 0; i < params.length; i++) {
				if (!classes.contains(params[i]))
					classes.add(params[i]);
				codeBuilder.append(params[i].getSimpleName() + " obj" + i);
				if (i + 1 < params.length)
					codeBuilder.append(", ");
			}
			codeBuilder.append(");");
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
		System.out.println(generate(UnsafeAccessor.UNSAFE_METHODS, "UnsafeProxy", "ru.zaxar163.unsafe.fast.proxies",
				false, Collections.singleton("getUnsafe")/* new HashSet<>(Arrays.asList("throwException")) */));
	}

	private InterfaceEmitter() {
	}
}
