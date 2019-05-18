package ru.zaxar163.unsafe.fast.reflect;

@FunctionalInterface
public interface ConstructorAcc {
	Object newInstance(Object... args) throws Throwable;
}