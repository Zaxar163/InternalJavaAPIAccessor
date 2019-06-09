package ru.zaxar163.util.proxies;

import ru.zaxar163.util.dynamicgen.RealName;
import ru.zaxar163.util.dynamicgen.Static;

public interface ThreadProxy {
	@RealName("dumpThreads")
	@Static
	StackTraceElement[][] dumpThreads(Thread[] threads);

	@RealName("getThreads")
	@Static
	Thread[] getThreads();

	@RealName("interrupt0")
	void interrupt(Thread thr);

	@RealName("resume0")
	void resume(Thread thr);

	@RealName("setNativeName")
	void setNativeName(Thread thr, String name);

	@RealName("setPriority0")
	void setPriority(Thread thr, int newPriority);

	@RealName("suspend0")
	void suspend(Thread thr);
}
