package ru.zaxar163.unsafe.xlevel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ThreadList {
	private static final long _next = SymbolsUtil.getInstance().type("JavaThread").field("_next").offset;
	private static final long _osthread = SymbolsUtil.getInstance().type("JavaThread").field("_osthread").offset;
	private static final long _thread_id = SymbolsUtil.getInstance().type("OSThread").field("_thread_id").offset;
	private static final long _thread_list = SymbolsUtil.getInstance().type("Threads").global("_thread_list");
	private static final long _threadObj = SymbolsUtil.getInstance().type("JavaThread").field("_threadObj").offset;

	public static Map<Integer, Thread> getThreads() {
		long curThread = SymbolsUtil.getInstance().getAddress(_thread_list);
		final Map<Integer, Thread> threads = new HashMap<>();
		do {
			threads.put(
					SymbolsUtil.getInstance()
							.getInt(SymbolsUtil.getInstance().getAddress(curThread + _osthread) + _thread_id),
					(Thread) SymbolsUtil.Ptr2Obj.getFromPtr2Ptr(curThread + _threadObj));
			curThread = SymbolsUtil.getInstance().getAddress(curThread + _next);
		} while (curThread != 0);
		return Collections.unmodifiableMap(threads);
	}

	private ThreadList() {
	}
}