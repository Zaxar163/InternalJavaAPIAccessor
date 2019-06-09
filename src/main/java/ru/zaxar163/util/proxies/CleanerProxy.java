package ru.zaxar163.util.proxies;

import java.lang.ref.PhantomReference;

import ru.zaxar163.util.dynamicgen.RealName;
import ru.zaxar163.util.dynamicgen.Static;

public interface CleanerProxy {
	@Static
	@RealName("add")
	Object add(Object cleaner);

	@RealName("clean")
	void clean(Object inst);

	@SuppressWarnings("rawtypes")
	@Static
	@RealName("create")
	PhantomReference create(Object thunk, Runnable r);
}
