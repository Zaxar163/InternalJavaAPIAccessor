package ru.zaxar163.demonstration.reflect;

@FunctionalInterface
public interface ConstructorAcc {
	Object newInstance(Object... args) throws Throwable;
}