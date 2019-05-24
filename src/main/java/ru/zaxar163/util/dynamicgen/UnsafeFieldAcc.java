package ru.zaxar163.util.dynamicgen;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import ru.zaxar163.util.proxies.ProxyList;

public class UnsafeFieldAcc implements FieldAcc {
	private final Object base;
	private final long offset;

	public UnsafeFieldAcc(final Field f) {
		if ((f.getModifiers() & Modifier.STATIC) == 0) {
			offset = ProxyList.UNSAFE.objectFieldOffset(f);
			base = null;
		} else {
			offset = ProxyList.UNSAFE.staticFieldOffset(f);
			base = ProxyList.UNSAFE.staticFieldBase(f.getDeclaringClass());
		}
	}

	@Override
	public Object getAndSetObject(final Object inst, final Object to) {
		return base == null ? ProxyList.UNSAFE.getAndSetObject(inst, offset, to)
				: ProxyList.UNSAFE.getAndSetObject(base, offset, to);
	}

	@Override
	public byte getByte(final Object inst) throws IllegalArgumentException {
		return base == null ? ProxyList.UNSAFE.getByte(inst, offset) : ProxyList.UNSAFE.getByte(base, offset);
	}

	@Override
	public double getDouble(final Object inst) throws IllegalArgumentException {
		return base == null ? ProxyList.UNSAFE.getDouble(inst, offset) : ProxyList.UNSAFE.getDouble(base, offset);
	}

	@Override
	public float getFloat(final Object inst) throws IllegalArgumentException {
		return base == null ? ProxyList.UNSAFE.getFloat(inst, offset) : ProxyList.UNSAFE.getFloat(base, offset);
	}

	@Override
	public int getInt(final Object inst) throws IllegalArgumentException {
		return base == null ? ProxyList.UNSAFE.getInt(inst, offset) : ProxyList.UNSAFE.getInt(base, offset);
	}

	@Override
	public long getLong(final Object inst) throws IllegalArgumentException {
		return base == null ? ProxyList.UNSAFE.getLong(inst, offset) : ProxyList.UNSAFE.getLong(base, offset);
	}

	@Override
	public Object getObject(final Object inst) {
		return base == null ? ProxyList.UNSAFE.getObject(inst, offset) : ProxyList.UNSAFE.getObject(base, offset);
	}

	@Override
	public short getShort(final Object inst) throws IllegalArgumentException {
		return base == null ? ProxyList.UNSAFE.getShort(inst, offset) : ProxyList.UNSAFE.getShort(base, offset);
	}

	@Override
	public void setByte(final Object inst, final byte to) throws IllegalArgumentException {
		if (base == null)
			ProxyList.UNSAFE.putByte(inst, offset, to);
		else
			ProxyList.UNSAFE.putByte(base, offset, to);
	}

	@Override
	public void setDouble(final Object inst, final double to) throws IllegalArgumentException {
		if (base == null)
			ProxyList.UNSAFE.putDouble(inst, offset, to);
		else
			ProxyList.UNSAFE.putDouble(base, offset, to);
	}

	@Override
	public void setFloat(final Object inst, final float to) throws IllegalArgumentException {
		if (base == null)
			ProxyList.UNSAFE.putFloat(inst, offset, to);
		else
			ProxyList.UNSAFE.putFloat(base, offset, to);
	}

	@Override
	public void setInt(final Object inst, final int to) throws IllegalArgumentException {
		if (base == null)
			ProxyList.UNSAFE.putInt(inst, offset, to);
		else
			ProxyList.UNSAFE.putInt(base, offset, to);
	}

	@Override
	public void setLong(final Object inst, final long to) throws IllegalArgumentException {
		if (base == null)
			ProxyList.UNSAFE.putLong(inst, offset, to);
		else
			ProxyList.UNSAFE.putLong(base, offset, to);
	}

	@Override
	public void setObject(final Object inst, final Object to) {
		if (base == null)
			ProxyList.UNSAFE.putObject(inst, offset, to);
		else
			ProxyList.UNSAFE.putObject(base, offset, to);

	}

	@Override
	public void setShort(final Object inst, final short to) throws IllegalArgumentException {
		if (base == null)
			ProxyList.UNSAFE.putShort(inst, offset, to);
		else
			ProxyList.UNSAFE.putShort(base, offset, to);
	}
}
