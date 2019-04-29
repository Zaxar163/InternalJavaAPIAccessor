package ru.zaxar163.unsafe.fast;

import java.lang.reflect.InvocationTargetException;

@FunctionalInterface
public interface InvokerConstructor {
	Object newInstance(Object... args)
			throws InstantiationException, IllegalArgumentException, InvocationTargetException;
}