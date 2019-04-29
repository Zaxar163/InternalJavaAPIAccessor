package ru.zaxar163.unsafe.xlevel;

import java.util.NoSuchElementException;
import java.util.Set;

public class Type {
	private static final Field[] NO_FIELDS = new Field[0];

	public final Field[] fields;
	public final boolean isInt;
	public final boolean isOop;
	public final boolean isUnsigned;
	public final String name;
	public final int size;
	public final String superName;

	Type(final String name, final String superName, final int size, final boolean isOop, final boolean isInt,
			final boolean isUnsigned, final Set<Field> fields) {
		this.name = name;
		this.superName = superName;
		this.size = size;
		this.isOop = isOop;
		this.isInt = isInt;
		this.isUnsigned = isUnsigned;
		this.fields = fields == null ? NO_FIELDS : fields.toArray(new Field[0]);
	}

	public Field field(final String name) {
		for (final Field field : fields)
			if (field.name.equals(name))
				return field;
		throw new NoSuchElementException("No such field: " + name);
	}

	public long global(final String name) {
		final Field field = field(name);
		if (field.isStatic)
			return field.offset;
		throw new IllegalArgumentException("Static field expected");
	}

	public long offset(final String name) {
		final Field field = field(name);
		if (!field.isStatic)
			return field.offset;
		throw new IllegalArgumentException("Instance field expected");
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(name);
		if (superName != null)
			sb.append(" extends ").append(superName);
		sb.append(" @ ").append(size).append('\n');
		for (final Field field : fields)
			sb.append("  ").append(field).append('\n');
		return sb.toString();
	}
}
