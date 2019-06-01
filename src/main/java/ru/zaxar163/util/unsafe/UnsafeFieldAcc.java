package ru.zaxar163.util.unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import ru.zaxar163.util.proxies.ProxyList;

public final class UnsafeFieldAcc {
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

	public byte getAndSetByte(final Object inst, final byte to) {
		final byte ret = getByte(inst);
		setByte(inst, to);
		return ret;
	}

	public double getAndSetDouble(final Object inst, final double to) {
		return Double.longBitsToDouble(getAndSetLong(inst, Double.doubleToLongBits(to)));
	}

	public float getAndSetFloat(final Object inst, final float to) {
		return Float.intBitsToFloat(getAndSetInt(inst, Float.floatToIntBits(to)));
	}

	public int getAndSetInt(final Object inst, final int to) {
		return base == null ? ProxyList.UNSAFE.getAndSetInt(inst, offset, to)
				: ProxyList.UNSAFE.getAndSetInt(base, offset, to);
	}

	public long getAndSetLong(final Object inst, final long to) {
		return base == null ? ProxyList.UNSAFE.getAndSetLong(inst, offset, to)
				: ProxyList.UNSAFE.getAndSetLong(base, offset, to);
	}

	public Object getAndSetObject(final Object inst, final Object to) {
		return base == null ? ProxyList.UNSAFE.getAndSetObject(inst, offset, to)
				: ProxyList.UNSAFE.getAndSetObject(base, offset, to);
	}

	public short getAndSetShort(final Object inst, final short to) {
		final short ret = getShort(inst);
		setShort(inst, to);
		return ret;
	}

	public byte getByte(final Object inst) {
		return base == null ? ProxyList.UNSAFE.getByte(inst, offset) : ProxyList.UNSAFE.getByte(base, offset);
	}

	public double getDouble(final Object inst) {
		return base == null ? ProxyList.UNSAFE.getDouble(inst, offset) : ProxyList.UNSAFE.getDouble(base, offset);
	}

	public float getFloat(final Object inst) {
		return base == null ? ProxyList.UNSAFE.getFloat(inst, offset) : ProxyList.UNSAFE.getFloat(base, offset);
	}

	public int getInt(final Object inst) {
		return base == null ? ProxyList.UNSAFE.getInt(inst, offset) : ProxyList.UNSAFE.getInt(base, offset);
	}

	public long getLong(final Object inst) {
		return base == null ? ProxyList.UNSAFE.getLong(inst, offset) : ProxyList.UNSAFE.getLong(base, offset);
	}

	public Object getObject(final Object inst) {
		return base == null ? ProxyList.UNSAFE.getObject(inst, offset) : ProxyList.UNSAFE.getObject(base, offset);
	}

	public short getShort(final Object inst) {
		return base == null ? ProxyList.UNSAFE.getShort(inst, offset) : ProxyList.UNSAFE.getShort(base, offset);
	}

	public void setByte(final Object inst, final byte to) {
		if (base == null)
			ProxyList.UNSAFE.putByte(inst, offset, to);
		else
			ProxyList.UNSAFE.putByte(base, offset, to);
	}

	public void setDouble(final Object inst, final double to) {
		if (base == null)
			ProxyList.UNSAFE.putDouble(inst, offset, to);
		else
			ProxyList.UNSAFE.putDouble(base, offset, to);
	}

	public void setFloat(final Object inst, final float to) {
		if (base == null)
			ProxyList.UNSAFE.putFloat(inst, offset, to);
		else
			ProxyList.UNSAFE.putFloat(base, offset, to);
	}

	public void setInt(final Object inst, final int to) {
		if (base == null)
			ProxyList.UNSAFE.putInt(inst, offset, to);
		else
			ProxyList.UNSAFE.putInt(base, offset, to);
	}

	public void setLong(final Object inst, final long to) {
		if (base == null)
			ProxyList.UNSAFE.putLong(inst, offset, to);
		else
			ProxyList.UNSAFE.putLong(base, offset, to);
	}

	public void setObject(final Object inst, final Object to) {
		if (base == null)
			ProxyList.UNSAFE.putObject(inst, offset, to);
		else
			ProxyList.UNSAFE.putObject(base, offset, to);

	}

	public void setShort(final Object inst, final short to) {
		if (base == null)
			ProxyList.UNSAFE.putShort(inst, offset, to);
		else
			ProxyList.UNSAFE.putShort(base, offset, to);
	}
}
