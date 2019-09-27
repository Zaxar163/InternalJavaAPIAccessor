package ru.zaxar163.demonstration;

import java.util.Arrays;

import ru.zaxar163.unsafe.xlevel.ThreadList;
import ru.zaxar163.util.proxies.ProxyList;

public final class InternalPlayGround {
	public static void main(final String... args) throws Throwable {
		System.out.println("Get list of all threads in JVM excluding JVM management thread:");
		ThreadList.getThreads().forEach((n, t) -> {
			System.out.println("Thread # " + n + " Data: " + t + " Classloader: " + t.getContextClassLoader());
		});
		System.out.println(Arrays.toString(ProxyList.THREAD.getThreads()));
		JVMPlayGround.main(args);
	}

	private InternalPlayGround() {
	}
}
