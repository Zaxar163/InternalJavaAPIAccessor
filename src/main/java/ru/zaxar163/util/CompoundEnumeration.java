package ru.zaxar163.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class CompoundEnumeration<E> implements Enumeration<E> {
	private final Enumeration<E>[] enums;
	private int index = 0;

	public CompoundEnumeration(final Enumeration<E>[] enums) {
		this.enums = enums;
	}

	@Override
	public boolean hasMoreElements() {
		return next();
	}

	private boolean next() {
		while (index < enums.length) {
			if (enums[index] != null && enums[index].hasMoreElements())
				return true;
			index++;
		}
		return false;
	}

	@Override
	public E nextElement() {
		if (!next())
			throw new NoSuchElementException();
		return enums[index].nextElement();
	}
}
