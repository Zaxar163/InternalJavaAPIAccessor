package ru.zaxar163.util.unsafe;

import java.io.FileDescriptor;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import lombok.experimental.UtilityClass;
import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.dynamicgen.ConstructorAccGenR;
import ru.zaxar163.util.dynamicgen.reflect.InvokerConstructor;

@UtilityClass
public class ChannelUtil {
	private static final InvokerConstructor CONSTRUCTOR_FILECH;
	static {
		try {
			CONSTRUCTOR_FILECH = ConstructorAccGenR.instancer(LookupUtil.getConstructor(
					ClassUtil.nonThrowingFirstClass("jdk.internal.nio.ch.FileChannelImpl",
							"sun.nio.ch.FileChannelImpl"),
					FileDescriptor.class, String.class, boolean.class, boolean.class, boolean.class, Object.class));
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static InputStream inputFromChannel(final ReadableByteChannel ch) {
		return Channels.newInputStream(ch);
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
}
