package ru.zaxar163.unsafe.fast.proxies;

import ru.zaxar163.unsafe.fast.RealName;
import ru.zaxar163.unsafe.fast.Static;

public interface CleanerProxy {
	@RealName("clean")
	void clean(Object inst);

	@Static
	@RealName("create")
	Object create(Object thunk, Runnable r);
}
