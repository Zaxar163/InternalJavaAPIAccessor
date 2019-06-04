package ru.zaxar163.util.unsafe;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;

import ru.zaxar163.util.dynamicgen.MethodAccGenR;
import ru.zaxar163.util.dynamicgen.reflect.InvokerMethodR;
import ru.zaxar163.util.proxies.ProxyList;

public final class Cache<T> {
	private static final class Cacher implements Runnable {
		private final Cache<?> c;
		private final WeakReference<Object> obj;

		private Cacher(final Object obj, final Cache<?> c) {
			this.obj = new WeakReference<>(obj);
			this.c = c;
		}

		@Override
		public void run() {
			synchronized (c.lock) {
				if (c.current < c.factLen && obj.get() != null)
					c.cache[++c.current] = obj.get();
				c.lock.notify();
			}
		}
	}

	private final Object[] cache;
	private int current;
	private final int factLen;
	private final InvokerMethodR instancer;
	private final Object lock;

	private final Class<T> type;

	public Cache(final Class<T> type, final Constructor<?> usableConstructor, final int size) {
		this.type = type;
		instancer = MethodAccGenR.method(usableConstructor);
		cache = new Object[size];
		lock = new Object();
		current = -1;
		factLen = size - 1;
	}

	private Object cleanerReg(final Object obj) {
		ProxyList.CLEANER.create(obj, new Cacher(obj, this));
		return obj;
	}

	private Object getOrNew() {
		synchronized (lock) {
			if (current < 0)
				return cleanerReg(ProxyList.UNSAFE.allocateInstance(type));
			final Object ret = cache[current--];
			lock.notify();
			return ret;
		}
	}

	@SuppressWarnings("unchecked")
	public T newInstance(final Object... args) throws Throwable {
		final Object toRet = getOrNew();
		instancer.invoke(toRet, args);
		return (T) toRet;
	}
}
