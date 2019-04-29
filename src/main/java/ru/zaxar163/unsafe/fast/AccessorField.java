package ru.zaxar163.unsafe.fast;

public interface AccessorField {
	Object get(Object inst) throws IllegalArgumentException;

	default Object getAndSet(final Object inst, final Object to) throws IllegalArgumentException {
		final Object ret = get(inst);
		set(inst, to);
		return ret;
	}

	void set(Object inst, Object to) throws IllegalArgumentException;
}
