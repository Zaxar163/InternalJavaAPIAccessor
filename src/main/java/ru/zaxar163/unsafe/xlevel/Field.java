package ru.zaxar163.unsafe.xlevel;

public class Field implements Comparable<Field> {
	public final boolean isStatic;
	public final String name;
	public final long offset;
	public final String typeName;

	Field(final String name, final String typeName, final long offset, final boolean isStatic) {
		this.name = name;
		this.typeName = typeName;
		this.offset = offset;
		this.isStatic = isStatic;
	}

	@Override
	public int compareTo(final Field o) {
		if (isStatic != o.isStatic)
			return isStatic ? -1 : 1;
		return Long.compare(offset, o.offset);
	}

	@Override
	public String toString() {
		if (isStatic)
			return "static " + typeName + ' ' + name + " @ 0x" + Long.toHexString(offset);
		else
			return typeName + ' ' + name + " @ " + offset;
	}
}
