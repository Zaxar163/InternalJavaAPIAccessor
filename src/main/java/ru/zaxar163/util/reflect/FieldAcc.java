package ru.zaxar163.util.reflect;

public interface FieldAcc {
	default byte getAndSetByte(final Object inst, final byte to) throws IllegalArgumentException {
		final byte ret = getByte(inst);
		setByte(inst, to);
		return ret;
	}

	default double getAndSetDouble(final Object inst, final double to) throws IllegalArgumentException {
		final double ret = getDouble(inst);
		setDouble(inst, to);
		return ret;
	}

	default float getAndSetFloat(final Object inst, final float to) throws IllegalArgumentException {
		final float ret = getFloat(inst);
		setFloat(inst, to);
		return ret;
	}

	default int getAndSetInt(final Object inst, final int to) throws IllegalArgumentException {
		final int ret = getInt(inst);
		setInt(inst, to);
		return ret;
	}

	default long getAndSetLong(final Object inst, final long to) throws IllegalArgumentException {
		final long ret = getLong(inst);
		setLong(inst, to);
		return ret;
	}

	default Object getAndSetObject(final Object inst, final Object to) throws IllegalArgumentException {
		final Object ret = getObject(inst);
		setObject(inst, to);
		return ret;
	}

	default short getAndSetShort(final Object inst, final short to) throws IllegalArgumentException {
		final short ret = getShort(inst);
		setShort(inst, to);
		return ret;
	}

	byte getByte(Object inst) throws IllegalArgumentException;

	double getDouble(Object inst) throws IllegalArgumentException;

	float getFloat(Object inst) throws IllegalArgumentException;

	int getInt(Object inst) throws IllegalArgumentException;

	long getLong(Object inst) throws IllegalArgumentException;

	Object getObject(Object inst) throws IllegalArgumentException;

	short getShort(Object inst) throws IllegalArgumentException;

	void setByte(Object inst, byte to) throws IllegalArgumentException;

	void setDouble(Object inst, double to) throws IllegalArgumentException;

	void setFloat(Object inst, float to) throws IllegalArgumentException;

	void setInt(Object inst, int to) throws IllegalArgumentException;

	void setLong(Object inst, long to) throws IllegalArgumentException;

	void setObject(Object inst, Object to) throws IllegalArgumentException;

	void setShort(Object inst, short to) throws IllegalArgumentException;
}
