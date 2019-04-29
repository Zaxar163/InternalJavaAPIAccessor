package ru.zaxar163.unsafe.fast;

import java.util.Random;

import org.objectweb.asm.Type;

import ru.zaxar163.core.ClassUtil;

final class ProxyData {
	static final ClassLoader MAGIC_CLASSLOADER;
	static final String MAGIC_PACKAGE;
	static final String MAGIC_SUPER;
	static final Random r = new Random(System.currentTimeMillis());
	static {
		final Class<?> magic = ClassUtil.nonThrowingFirstClass("jdk.internal.reflect.MagicAccessorImpl",
				"sun.reflect.MagicAccessorImpl");
		MAGIC_SUPER = Type.getInternalName(magic);
		MAGIC_PACKAGE = magic.getName().substring(0, magic.getName().lastIndexOf('.')).replace('.', '/');
		MAGIC_CLASSLOADER = magic.getClassLoader();
	}

	public static ClassLoader defaultForVer(final Class<?> klass, final boolean sys) {
		return klass.getClassLoader();
	}

	public static String nextName(final boolean magic) {
		if (magic)
			return MAGIC_PACKAGE + "/Proxy" + r.nextInt(Integer.MAX_VALUE) + "ImplGenerated";
		return "ru/zaxar163/unsafe/Proxy" + r.nextInt(Integer.MAX_VALUE) + "ImplGenerated";
	}

	private ProxyData() {
	}
}
