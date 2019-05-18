package ru.zaxar163.unsafe.fast.reflect;

@FunctionalInterface
public interface MethodAcc {
	Object invoke(Object inst, Object... args) throws Throwable;
}