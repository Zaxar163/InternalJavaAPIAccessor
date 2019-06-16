package ru.zaxar163.unsafe.xlevel;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Arrays;

import lombok.experimental.UtilityClass;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.proxies.ProxyList;

@UtilityClass
public class SimpleAntiCheat {
	public static void init() {
		ThreadGroup group;
		try {
			group = (ThreadGroup) LookupUtil.ALL_LOOKUP
					.findConstructor(ThreadGroup.class, MethodType.methodType(void.class)).invokeExact();
			ProxyList.UNSAFE.putObject(group,
					ProxyList.UNSAFE.objectFieldOffset(LookupUtil.getField(ThreadGroup.class, "name", String.class)),
					"nop");
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
		final MethodHandle GROUP_SETTER;
		try {
			GROUP_SETTER = LookupUtil.ALL_LOOKUP.findSetter(Thread.class, "group", ThreadGroup.class);
		} catch (final Throwable e) {
			throw new Error(e);
		}
		if (LookupUtil.JAVA9)
			Arrays.stream(ProxyList.THREAD.getThreads()).forEach(thread -> {
				try {
					GROUP_SETTER.invokeExact(thread, group);
				} catch (final Throwable t) {
					throw new RuntimeException(t);
				}
			});
		else {
			ThreadList.iterateList((tid, thread) -> {
				try {
					GROUP_SETTER.invokeExact(thread, group);
				} catch (final Throwable t) {
					throw new RuntimeException(t);
				}
			});
			StructUtil.disableStructs();
			StructUtil.disableThreads();
		}
	}
}
