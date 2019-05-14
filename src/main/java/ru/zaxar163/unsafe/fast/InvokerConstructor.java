package ru.zaxar163.unsafe.fast;

@FunctionalInterface
public interface InvokerConstructor {
	Object newInstance(Object... args) throws Throwable;
}