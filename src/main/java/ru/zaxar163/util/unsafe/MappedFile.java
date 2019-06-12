package ru.zaxar163.util.unsafe;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicReference;

public final class MappedFile implements Closeable {
	private volatile long addr;
	private final long size;

	public MappedFile(final String name, final long size) throws IOException {
		this.size = size;
		final AtomicReference<Throwable> ex = new AtomicReference<>(null);
		SecurityManagerUtil.suspendRunAndContinue(() -> {
			try (RandomAccessFile f = new RandomAccessFile(name, "rw")) {
				f.setLength(size);
				try (FileChannel ch = f.getChannel()) {
					addr = ChannelUtil.map0(ch, ChannelUtil.MAP_RW, 0L, size);
				}
			} catch (final Throwable e) {
				ex.set(e);
			}
		});
		if (ex.get() != null) {
			final Throwable e = ex.get();
			if (e instanceof IOException)
				throw (IOException) e;
			if (e instanceof Error)
				throw (Error) e;
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		if (addr != 0) {
			ChannelUtil.unmap0(addr, size);
			addr = 0;
		}
	}

	public long getAddr() {
		return addr;
	}

	public long getSize() {
		return size;
	}
}