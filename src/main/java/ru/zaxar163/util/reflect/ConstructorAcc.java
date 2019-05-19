package ru.zaxar163.util.reflect;

@FunctionalInterface
public interface ConstructorAcc {
	Object newInstance(Object... args) throws Throwable;
}