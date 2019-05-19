package ru.zaxar163.util.reflect;

@FunctionalInterface
public interface MethodAcc {
	Object invoke(Object inst, Object... args) throws Throwable;
}