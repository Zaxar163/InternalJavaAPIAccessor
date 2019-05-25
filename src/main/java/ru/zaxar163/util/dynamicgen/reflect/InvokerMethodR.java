package ru.zaxar163.util.dynamicgen.reflect;

@FunctionalInterface
public interface InvokerMethodR {
	Object invoke(Object obj, Object... args);
}
