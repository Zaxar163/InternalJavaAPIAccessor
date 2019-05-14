package ru.zaxar163.unsafe.fast;

@FunctionalInterface
public interface InvokerMethod {
	Object invoke(Object... args) throws Throwable;
}