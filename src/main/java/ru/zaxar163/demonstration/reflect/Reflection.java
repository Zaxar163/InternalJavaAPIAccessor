package ru.zaxar163.demonstration.reflect;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import ru.zaxar163.demonstration.ReflectionUtil;
import ru.zaxar163.util.LookupUtil;

public final class Reflection {
	public static final byte DEFAULT = 0x20;

	public static final byte UNSAFE = 0x10;

	public static ConstructorAcc constructorAcc(final byte kind, final Class<?> clazz, final MethodType type) {
		try {
			switch (kind) {
			case UNSAFE:
				return ReflectionUtil.wrapConstructor(LookupUtil.getConstructor(clazz, type.parameterArray()));
			case DEFAULT:
				return new ConstructorAcc() {
					private final Constructor<?> m = ReflectionUtil
							.setAccessible(LookupUtil.getConstructor(clazz, type.parameterArray()));

					@Override
					public Object newInstance(final Object... args) throws Throwable {
						return m.newInstance(args);
					}
				};
			default:
				throw new IllegalArgumentException("kind");
			}
		} catch (final Throwable t) {
			if (t instanceof RuntimeException)
				throw (RuntimeException) t;
			throw new RuntimeException(t);
		}
	}

	public static FieldAcc fieldAcc(final byte kind, final String name, final Class<?> clazz, final Class<?> type) {
		try {
			switch (kind) {
			case UNSAFE:
				return ReflectionUtil.wrapField(LookupUtil.getField(clazz, name, type));
			case DEFAULT:
				return new FieldAcc() {
					private final Field m = ReflectionUtil.setAccessible(LookupUtil.getField(clazz, name, type));

					@Override
					public byte getByte(final Object inst) throws IllegalArgumentException {
						try {
							return m.getByte(inst);
						} catch (final IllegalAccessException e) {
							throw new Error(e); // should never happen
						}
					}

					@Override
					public double getDouble(final Object inst) throws IllegalArgumentException {
						try {
							return m.getDouble(inst);
						} catch (final IllegalAccessException e) {
							throw new Error(e); // should never happen
						}
					}

					@Override
					public float getFloat(final Object inst) throws IllegalArgumentException {
						try {
							return m.getFloat(inst);
						} catch (final IllegalAccessException e) {
							throw new Error(e); // should never happen
						}
					}

					@Override
					public int getInt(final Object inst) throws IllegalArgumentException {
						try {
							return m.getInt(inst);
						} catch (final IllegalAccessException e) {
							throw new Error(e); // should never happen
						}
					}

					@Override
					public long getLong(final Object inst) throws IllegalArgumentException {
						try {
							return m.getLong(inst);
						} catch (final IllegalAccessException e) {
							throw new Error(e); // should never happen
						}
					}

					@Override
					public Object getObject(final Object inst) throws IllegalArgumentException {
						try {
							return m.get(inst);
						} catch (final IllegalAccessException e) {
							throw new Error(e); // should never happen
						}
					}

					@Override
					public short getShort(final Object inst) throws IllegalArgumentException {
						try {
							return m.getShort(inst);
						} catch (final IllegalAccessException e) {
							throw new Error(e); // should never happen
						}
					}

					@Override
					public void setByte(final Object inst, final byte to) throws IllegalArgumentException {
						try {
							m.setByte(inst, to);
						} catch (final IllegalAccessException e) {
							throw new Error(e); // should never happen
						}
					}

					@Override
					public void setDouble(final Object inst, final double to) throws IllegalArgumentException {
						try {
							m.setDouble(inst, to);
						} catch (final IllegalAccessException e) {
							throw new Error(e); // should never happen
						}
					}

					@Override
					public void setFloat(final Object inst, final float to) throws IllegalArgumentException {
						try {
							m.setFloat(inst, to);
						} catch (final IllegalAccessException e) {
							throw new Error(e); // should never happen
						}
					}

					@Override
					public void setInt(final Object inst, final int to) throws IllegalArgumentException {
						try {
							m.setInt(inst, to);
						} catch (final IllegalAccessException e) {
							throw new Error(e); // should never happen
						}
					}

					@Override
					public void setLong(final Object inst, final long to) throws IllegalArgumentException {
						try {
							m.setLong(inst, to);
						} catch (final IllegalAccessException e) {
							throw new Error(e); // should never happen
						}
					}

					@Override
					public void setObject(final Object inst, final Object to) throws IllegalArgumentException {
						try {
							m.set(inst, to);
						} catch (final IllegalAccessException e) {
							throw new Error(e); // should never happen
						}
					}

					@Override
					public void setShort(final Object inst, final short to) throws IllegalArgumentException {
						try {
							m.setShort(inst, to);
						} catch (final IllegalAccessException e) {
							throw new Error(e); // should never happen
						}
					}

				};
			default:
				throw new IllegalArgumentException("kind");
			}
		} catch (final Throwable t) {
			if (t instanceof RuntimeException)
				throw (RuntimeException) t;
			throw new RuntimeException(t);
		}
	}

	public static MethodAcc methodAcc(final byte kind, final String name, final Class<?> clazz, final MethodType type) {
		try {
			switch (kind) {
			case UNSAFE:
				return ReflectionUtil.wrapMethod(LookupUtil.getMethod(clazz, name, type.parameterArray()));
			case DEFAULT:
				return new MethodAcc() {
					private final Method m = ReflectionUtil
							.setAccessible(LookupUtil.getMethod(clazz, name, type.parameterArray()));

					@Override
					public Object invoke(final Object inst, final Object... args) throws Throwable {
						return m.invoke(inst, args);
					}
				};
			default:
				throw new IllegalArgumentException("kind");
			}
		} catch (final Throwable t) {
			if (t instanceof RuntimeException)
				throw (RuntimeException) t;
			throw new RuntimeException(t);
		}
	}

	private Reflection() {
	}
}
