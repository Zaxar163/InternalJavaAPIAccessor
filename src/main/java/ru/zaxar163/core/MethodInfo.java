package ru.zaxar163.core;

import java.lang.invoke.MethodType;
import java.util.Objects;

public class MethodInfo {
	public final String name;
	public final MethodType type;

	public MethodInfo(final String name, final Class<?> ret, final Class<?>... params) {
		this.name = name;
		type = MethodType.methodType(ret, params);
	}

	public MethodInfo(final String name, final MethodType type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MethodInfo))
			return false;
		final MethodInfo other = (MethodInfo) obj;
		return Objects.equals(name, other.name) && Objects.equals(type, other.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, type);
	}
}
