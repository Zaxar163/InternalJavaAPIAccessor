package ru.zaxar163.util.proxies;

import ru.zaxar163.util.dynamicgen.RealName;
import ru.zaxar163.util.dynamicgen.Static;

public interface CleanerProxy {
	@RealName("clean")
	void clean(Object inst);

	@Static
	@RealName("create")
	Object create(Object thunk, Runnable r);
}
