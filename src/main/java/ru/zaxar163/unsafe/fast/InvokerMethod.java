package ru.zaxar163.unsafe.fast;

import java.lang.reflect.InvocationTargetException;

@FunctionalInterface
public interface InvokerMethod {
	Object invoke(Object instance, Object... args) throws IllegalArgumentException, InvocationTargetException;
}