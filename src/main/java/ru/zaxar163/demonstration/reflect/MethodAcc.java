package ru.zaxar163.demonstration.reflect;

@FunctionalInterface
public interface MethodAcc {
	Object invoke(Object inst, Object... args) throws Throwable;
}