package ru.zaxar163.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.Type;

public final class DelegateClassLoader extends ClassLoader {
	public static final DelegateClassLoader INSTANCE = new DelegateClassLoader();
	static {
		ClassLoader.registerAsParallelCapable();
	}

	private final Set<ClassLoader> cls;

	private DelegateClassLoader() {
		super(null);
		cls = Collections.newSetFromMap(new ConcurrentHashMap<>());
		cls.add(ClassLoader.getSystemClassLoader());
		cls.add(this.getClass().getClassLoader());
	}

	public void append(final Class<?> c) {
		if (c != null)
			append(c.getClassLoader());
	}

	public void append(final ClassLoader c) {
		if (c != null)
			cls.add(c);
	}

	public void append(final Constructor<?> c) {
		if (c != null)
			append(c.getDeclaringClass().getClassLoader());
	}

	public void append(final Field c) {
		if (c != null)
			append(c.getDeclaringClass().getClassLoader());
	}

	public void append(final Method c) {
		if (c != null)
			append(c.getDeclaringClass().getClassLoader());
	}

	public byte[] classData(final Class<?> clazz) {
		try {
			append(clazz);
			return getResourceAsArray('/' + Type.getInternalName(clazz) + ".class");
		} catch (final IOException e) {
			throw new RuntimeException(e); // because is strange
		}
	}

	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		for (final ClassLoader e : cls)
			try {
				return e.loadClass(name);
			} catch (final Throwable t) {
			}
		throw new ClassNotFoundException(name);
	}

	/**
	 * Finds loaded class or returns null if not exist.
	 *
	 * @param name
	 *            the name of class.
	 * @return founded class or null.
	 */
	public Class<?> findLoaded(final String name) {
		Class<?> r;
		for (final ClassLoader l : cls) {
			r = ClassUtil.findLoadedClass(l, name);
			if (r != null)
				return r;
		}
		return null;
	}

	public Class<?> findLoadedClassA(final String name) {
		return cls.stream().map(e -> ClassUtil.findLoadedClass(name)).filter(e -> e != null).findFirst().orElse(null);
	}

	@Override
	protected URL findResource(final String name) {
		return getResource(name);
	}

	@Override
	protected Enumeration<URL> findResources(final String name) throws IOException {
		return getResources(name);
	}

	@Override
	public URL getResource(final String name) {
		URL url;
		for (final ClassLoader l : cls) {
			url = l.getResource(name);
			if (url != null)
				return url;
		}
		return null;
	}

	public byte[] getResourceAsArray(final String string) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (InputStream e = getResourceAsStream(string)) {
			final byte[] buffer = new byte[4096];
			for (int length = e.read(buffer); length >= 0; length = e.read(buffer))
				baos.write(buffer, 0, length);
		}
		return baos.toByteArray();
	}

	@Override
	public InputStream getResourceAsStream(final String name) {
		InputStream in;
		for (final ClassLoader l : cls) {
			in = l.getResourceAsStream(name);
			if (in != null)
				return in;
		}
		return null;
	}

	@Override
	public Enumeration<URL> getResources(final String name) throws IOException {
		final ClassLoader[] current = cls.toArray(new ClassLoader[0]);
		@SuppressWarnings("unchecked")
		final Enumeration<URL>[] tmp = (Enumeration<URL>[]) new Enumeration<?>[current.length];
		for (int i = 0; i < current.length; i++)
			tmp[i] = current[i].getResources(name);

		return new CompoundEnumeration<>(tmp);
	}

	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		for (final ClassLoader e : cls)
			try {
				return e.loadClass(name);
			} catch (final Throwable t) {
			}
		throw new ClassNotFoundException(name);
	}

	@Override
	protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		for (final ClassLoader e : cls)
			try {
				final Class<?> clazz = e.loadClass(name);
				if (resolve)
					resolveClass(clazz);
				return clazz;
			} catch (final Throwable t) {
			}
		throw new ClassNotFoundException(name);
	}
}
