package ru.zaxar163.unsafe.fast;

@FunctionalInterface
public interface InvokerMethod {
	Object invoke(Object inst, Object... args) throws Throwable;
}