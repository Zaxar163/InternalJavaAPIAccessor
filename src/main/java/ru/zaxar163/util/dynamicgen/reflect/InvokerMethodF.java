package ru.zaxar163.util.dynamicgen.reflect;

@FunctionalInterface
public interface InvokerMethodF {
	Object invoke(Object... args) throws Throwable;
}
