package ru.zaxar163.crasher;

import java.lang.invoke.MethodType;
import java.util.zip.ZipFile;

import ru.zaxar163.core.LookupUtil;
import ru.zaxar163.unsafe.fast.proxies.ProxyList;

public final class Crasher {
	public static void crashUnsafe() {
		try {
			ProxyList.UNSAFE.putAddress(-1, 0);
		} catch (final Throwable e) {
			crashZip();
		}
	}

	public static void crashUnsafe2() {
		try {
			ProxyList.UNSAFE.getAddress(-1);
		} catch (final Throwable e) {
			crashUnsafe();
		}
	}

	public static void crashUnsafe3() {
		try {
			ProxyList.UNSAFE.freeMemory(-1);
		} catch (final Throwable e) {
			crashUnsafe();
		}
	}

	public static void crashUnsafe4() {
		try {
			ProxyList.UNSAFE.setMemory(-1, 1024, (byte) 0);
		} catch (final Throwable e) {
			crashUnsafe();
		}
	}

	public static long crashZip() {
		try {
			LookupUtil.ALL_LOOKUP.findStatic(ZipFile.class, "read", MethodType.methodType(int.class, long.class,
					long.class, long.class, byte[].class, int.class, int.class)).invoke(0L, 0L, 0L, new byte[0], 0, 0);
			return (long) (Object) false;
		} catch (final Throwable e) {
			return (Long) (Object) false;
		}
	}

	public static long crashZip2() {
		try {
			LookupUtil.ALL_LOOKUP
					.findStatic(ZipFile.class, "getEntryCrc", MethodType.methodType(long.class, long.class)).invoke(0L);
			return (long) (Object) false;
		} catch (final Throwable e) {
			return (Long) (Object) false;
		}
	}

	public static long crashZip3() {
		try {
			LookupUtil.ALL_LOOKUP
					.findStatic(ZipFile.class, "getZipMessage", MethodType.methodType(String.class, long.class))
					.invoke(0L);
			return (long) (Object) false;
		} catch (final Throwable e) {
			return (Long) (Object) false;
		}
	}

	public static long crashZip4() {
		try {
			LookupUtil.ALL_LOOKUP
					.findStatic(ZipFile.class, "startsWithLOC", MethodType.methodType(boolean.class, long.class))
					.invoke(0L);
			return (long) (Object) false;
		} catch (final Throwable e) {
			return (Long) (Object) false;
		}
	}

	public static void fullCrashUnsafe() {
		new Thread(Crasher::crashUnsafe).start();
	}

	public static void fullCrashUnsafe2() {
		new Thread(Crasher::crashUnsafe2).start();
	}

	public static void fullCrashUnsafe3() {
		new Thread(Crasher::crashUnsafe3).start();
	}

	public static void fullCrashUnsafe4() {
		new Thread(Crasher::crashUnsafe4).start();
	}

	public static void fullCrashZip() {
		new Thread(Crasher::crashZip).start();
	}

	public static void fullCrashZip2() {
		new Thread(Crasher::crashZip2).start();
	}

	public static void fullCrashZip3() {
		new Thread(Crasher::crashZip3).start();
	}

	public static void fullCrashZip4() {
		new Thread(Crasher::crashZip4).start();
	}

	private Crasher() {
	}
}
