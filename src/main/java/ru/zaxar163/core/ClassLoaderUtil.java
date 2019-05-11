package ru.zaxar163.core;

public class ClassLoaderUtil {
	private ClassLoaderUtil() {
	}

	public static ClassLoader[] allClassLoaders() {
		return new ClassLoader[] {ClassLoader.getSystemClassLoader()};
	}
}
