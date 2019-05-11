package ru.zaxar163.unsafe.fast.proxies;

import ru.zaxar163.unsafe.fast.RealName;
import ru.zaxar163.unsafe.fast.Static;

public interface CleanerProxy {
	@Static
	@RealName("create")
	Object create(Object thunk, Runnable r);
	@RealName("clean")
	void clean(Object inst);
}
