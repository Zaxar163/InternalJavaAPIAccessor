package ru.zaxar163.util.dynamicgen.reflect;

@FunctionalInterface
public interface InvokerConstructor {
	Object newInstance(Object... args) throws Throwable;
}
