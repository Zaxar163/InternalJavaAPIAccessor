package ru.zaxar163.demonstration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
//import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.dynamicgen.RealName;
import ru.zaxar163.util.dynamicgen.Static;

public final class DynamicInterfaceEmitter {
	private static final List<String> objectMethods = Arrays.stream(Object.class.getDeclaredMethods())
			.map(e -> e.getName()).collect(Collectors.toList());

	public static String generate(final Class<?> caller, final String nameClazz, final String packagee,
			final boolean excludeInternalImport, final Set<String> exclusions) {
		final StringBuilder startDecl = new StringBuilder(packagee != null ? "package " + packagee + ";\n\n" : "");
		final StringBuilder code = new StringBuilder(
				"\n@SuppressWarnings(\"rawtypes\") public interface " + nameClazz + " {\n");
		final List<Class<?>> classes = new ArrayList<>();
		if (!excludeInternalImport)
			classes.add(RealName.class);
		for (final Method m : LookupUtil.getDeclaredMethods(caller)) {
			if (objectMethods.contains(m.getName()) || exclusions.contains(m.getName()))
				continue;
			final StringBuilder codeBuilder = new StringBuilder();
			codeBuilder.append("	");
			if (Modifier.isStatic(m.getModifiers())) {
				if (!classes.contains(Static.class) && !excludeInternalImport)
					classes.add(Static.class);
				codeBuilder.append("@Static ");
			}
			codeBuilder.append("@RealName(\"" + m.getName() + "\") ").append(m.getReturnType().getSimpleName());
			if (!classes.contains(m.getReturnType()))
				classes.add(m.getReturnType());
			codeBuilder.append(" ").append(m.getName()).append("(");
			final Class<?>[] params = m.getParameterTypes();
			if (!Modifier.isStatic(m.getModifiers())) {
				codeBuilder.append("Object inst");
				if (params.length > 0)
					codeBuilder.append(", ");
			}
			for (int i = 0; i < params.length; i++) {
				if (!classes.contains(params[i]))
					classes.add(params[i]);
				codeBuilder.append(params[i].getSimpleName() + " obj" + i);
				if (i + 1 < params.length)
					codeBuilder.append(", ");
			}
			codeBuilder.append(");");
			code.append(codeBuilder).append('\n');
		}
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
		System.out.println(generate(ClassUtil.nonThrowingFirstClass("sun.misc.MessageUtils"), "MessageUtilsProxy",
				"ru.zaxar163.util.proxies", false,
				Collections.emptySet()/* new HashSet<>(Arrays.asList("throwException")) */));
	}

	private DynamicInterfaceEmitter() {
	}
}
