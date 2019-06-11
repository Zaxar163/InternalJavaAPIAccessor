package ru.zaxar163.util.unsafe;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import lombok.experimental.UtilityClass;
import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.dynamicgen.ConstructorAccGenR;
import ru.zaxar163.util.dynamicgen.MethodAccGenF;
import ru.zaxar163.util.dynamicgen.reflect.InvokerConstructor;
import ru.zaxar163.util.dynamicgen.reflect.InvokerMethodF;
import ru.zaxar163.util.proxies.ProxyList;

@UtilityClass
public class ChannelUtil {
	private static final InvokerConstructor CONSTRUCTOR_FILECH;
	public static final int MAP_PV;
	public static final int MAP_RO;
	public static final int MAP_RW;
	private static final InvokerMethodF MAP0;
	private static final InvokerMethodF UNMAP0;
	static {
		try {
			final Class<?> clazz = ClassUtil.nonThrowingFirstClass("jdk.internal.nio.ch.FileChannelImpl",
					"sun.nio.ch.FileChannelImpl");
			CONSTRUCTOR_FILECH = ConstructorAccGenR.instancer(LookupUtil.getConstructor(clazz, FileDescriptor.class,
					String.class, boolean.class, boolean.class, boolean.class, Object.class));
			MAP0 = MethodAccGenF.method(LookupUtil.getMethod(clazz, "map0", int.class, long.class, long.class));
			UNMAP0 = MethodAccGenF.method(LookupUtil.getMethod(clazz, "unmap0", long.class, long.class));
			final Field f0 = LookupUtil.getField(clazz, "MAP_RO", int.class);
			final Object staticBase = ProxyList.UNSAFE.staticFieldBase(f0);
			MAP_RO = ProxyList.UNSAFE.getInt(staticBase, ProxyList.UNSAFE.objectFieldOffset(f0));
			MAP_RW = ProxyList.UNSAFE.getInt(staticBase,
					ProxyList.UNSAFE.objectFieldOffset(LookupUtil.getField(clazz, "MAP_RW", int.class)));
			MAP_PV = ProxyList.UNSAFE.getInt(staticBase,
					ProxyList.UNSAFE.objectFieldOffset(LookupUtil.getField(clazz, "MAP_PV", int.class)));
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static InputStream inputFromChannel(final ReadableByteChannel ch) {
		return Channels.newInputStream(ch);
	}

	public static long map0(final FileChannel ch, final int prot, final long position, final long length)
			throws IOException {
		try {
			return (long) MAP0.invoke(ch, prot, position, length);
		} catch (final IOException ioexc) {
			throw ioexc;
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static FileChannel newFileChannel(final FileDescriptor fd, final String path, final boolean readable,
			final boolean writable, final boolean append, final Object parent) {
		try {
			return (FileChannel) CONSTRUCTOR_FILECH.newInstance(fd, path, readable, writable, append, parent);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static OutputStream outputFromChannel(final WritableByteChannel ch) {
		return Channels.newOutputStream(ch);
	}

	public static int unmap0(final long address, final long length) {
		try {
			return (int) UNMAP0.invoke(address, length);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}
}
