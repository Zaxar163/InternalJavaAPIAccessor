package ru.zaxar163.unsafe.xlevel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import ru.zaxar163.util.proxies.ProxyList;

public class ThreadList {
	private static final long _next = SymbolsUtil.INSTANCE.type("JavaThread").field("_next").offset;
	private static final long _osthread = SymbolsUtil.INSTANCE.type("JavaThread").field("_osthread").offset;
	private static final long _thread_id = SymbolsUtil.INSTANCE.type("OSThread").field("_thread_id").offset;
	private static final long _thread_list = SymbolsUtil.INSTANCE.type("Threads").global("_thread_list");
	private static final long _threadObj = SymbolsUtil.INSTANCE.type("JavaThread").field("_threadObj").offset;

	public static Map<Integer, Thread> getThreads() {
		long curThread = ProxyList.UNSAFE.getAddress(_thread_list);
		final Map<Integer, Thread> threads = new HashMap<>();
		do {
			threads.put(ProxyList.UNSAFE.getInt(ProxyList.UNSAFE.getAddress(curThread + _osthread) + _thread_id),
					(Thread) SymbolsUtil.Ptr2Obj.getFromPtr2Ptr(curThread + _threadObj));
			curThread = ProxyList.UNSAFE.getAddress(curThread + _next);
		} while (curThread != 0);
		return Collections.unmodifiableMap(threads);
	}

	public static void iterateList(final BiConsumer<Integer, Thread> c) {
		long curThread = ProxyList.UNSAFE.getAddress(_thread_list);
		do {
			c.accept(ProxyList.UNSAFE.getInt(ProxyList.UNSAFE.getAddress(curThread + _osthread) + _thread_id),
					(Thread) SymbolsUtil.Ptr2Obj.getFromPtr2Ptr(curThread + _threadObj));
			curThread = ProxyList.UNSAFE.getAddress(curThread + _next);
		} while (curThread != 0);
	}

	private ThreadList() {
	}
}