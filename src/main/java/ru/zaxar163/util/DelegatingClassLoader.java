package ru.zaxar163.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class DelegatingClassLoader extends ClassLoader {
	private static final ConcurrentHashMap<ClassLoader, DelegatingClassLoader> classLoaders = new ConcurrentHashMap<>();

	private static final Function<ClassLoader, DelegatingClassLoader> instancer = DelegatingClassLoader::new;
	private static final DelegatingClassLoader NULLCL = new DelegatingClassLoader(null);

	static {
		ClassLoader.registerAsParallelCapable();
	}

	public static DelegatingClassLoader forClassLoader(final ClassLoader cls) {
		if (cls == null)
			return NULLCL;
		return classLoaders.computeIfAbsent(cls, instancer);
	}

	private final Set<Class<?>> cls;

	public DelegatingClassLoader(final ClassLoader parent) {
		super(parent);
		cls = Collections.newSetFromMap(new ConcurrentHashMap<>());
	}

	public DelegatingClassLoader add(final Class<?> clazz) {
		if (!cls.contains(clazz))
			cls.add(clazz);
		return this;
	}

	public DelegatingClassLoader addAll(final Class<?>... classes) {
		Arrays.stream(classes).forEach(this::add);
		return this;
	}

	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		for (final Class<?> e : cls)
			if (e.getName().equals(name))
				return e;
		return super.loadClass(name);
	}

	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		for (final Class<?> e : cls)
			if (e.getName().equals(name))
				return e;
		return super.loadClass(name);
	}

	@Override
	protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		for (final Class<?> e : cls)
			if (e.getName().equals(name))
				return e;
		return super.loadClass(name, resolve);
	}

	public DelegatingClassLoader remove(final Class<?> clazz) {
		cls.remove(clazz);
		return this;
	}
}