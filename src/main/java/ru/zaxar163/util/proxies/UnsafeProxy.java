package ru.zaxar163.util.proxies;

import java.lang.reflect.Field;
import java.security.ProtectionDomain;

import ru.zaxar163.util.dynamicgen.RealName;

@SuppressWarnings("rawtypes")
public interface UnsafeProxy {
	@RealName("addressSize")
	int addressSize();

	@RealName("allocateInstance")
	Object allocateInstance(Class obj0);

	@RealName("allocateMemory")
	long allocateMemory(long obj0);

	@RealName("arrayBaseOffset")
	int arrayBaseOffset(Class obj0);

	@RealName("arrayIndexScale")
	int arrayIndexScale(Class obj0);

	@RealName("copyMemory")
	void copyMemory(long obj0, long obj1, long obj2);

	@RealName("copyMemory")
	void copyMemory(Object obj0, long obj1, Object obj2, long obj3, long obj4);

	@RealName("defineAnonymousClass")
	Class defineAnonymousClass(Class obj0, byte[] obj1, Object[] obj2);

	@RealName("defineClass")
	Class defineClass(String obj0, byte[] obj1, int obj2, int obj3, ClassLoader obj4, ProtectionDomain obj5);

	@RealName("ensureClassInitialized")
	void ensureClassInitialized(Class obj0);

	@RealName("freeMemory")
	void freeMemory(long obj0);

	@RealName("fullFence")
	void fullFence();

	@RealName("getAddress")
	long getAddress(long obj0);

	@RealName("getAndAddInt")
	int getAndAddInt(Object obj0, long obj1, int obj2);

	@RealName("getAndAddLong")
	long getAndAddLong(Object obj0, long obj1, long obj2);

	@RealName("getAndSetInt")
	int getAndSetInt(Object obj0, long obj1, int obj2);

	@RealName("getAndSetLong")
	long getAndSetLong(Object obj0, long obj1, long obj2);

	@RealName("getAndSetObject")
	Object getAndSetObject(Object obj0, long obj1, Object obj2);

	@RealName("getBoolean")
	boolean getBoolean(Object obj0, long obj1);

	@RealName("getBooleanVolatile")
	boolean getBooleanVolatile(Object obj0, long obj1);

	@RealName("getByte")
	byte getByte(long obj0);

	@RealName("getByte")
	byte getByte(Object obj0, long obj1);

	@RealName("getByteVolatile")
	byte getByteVolatile(Object obj0, long obj1);

	@RealName("getChar")
	char getChar(long obj0);

	@RealName("getChar")
	char getChar(Object obj0, long obj1);

	@RealName("getCharVolatile")
	char getCharVolatile(Object obj0, long obj1);

	@RealName("getDouble")
	double getDouble(long obj0);

	@RealName("getDouble")
	double getDouble(Object obj0, long obj1);

	@RealName("getDoubleVolatile")
	double getDoubleVolatile(Object obj0, long obj1);

	@RealName("getFloat")
	float getFloat(long obj0);

	@RealName("getFloat")
	float getFloat(Object obj0, long obj1);

	@RealName("getFloatVolatile")
	float getFloatVolatile(Object obj0, long obj1);

	@RealName("getInt")
	int getInt(long obj0);

	@RealName("getInt")
	int getInt(Object obj0, long obj1);

	@RealName("getIntVolatile")
	int getIntVolatile(Object obj0, long obj1);

	@RealName("getLoadAverage")
	int getLoadAverage(double[] obj0, int obj1);

	@RealName("getLong")
	long getLong(long obj0);

	@RealName("getLong")
	long getLong(Object obj0, long obj1);

	@RealName("getLongVolatile")
	long getLongVolatile(Object obj0, long obj1);

	@RealName("getObject")
	Object getObject(Object obj0, long obj1);

	@RealName("getObjectVolatile")
	Object getObjectVolatile(Object obj0, long obj1);

	@RealName("getShort")
	short getShort(long obj0);

	@RealName("getShort")
	short getShort(Object obj0, long obj1);

	@RealName("getShortVolatile")
	short getShortVolatile(Object obj0, long obj1);

	@RealName("loadFence")
	void loadFence();

	@RealName("objectFieldOffset")
	long objectFieldOffset(Field obj0);

	@RealName("pageSize")
	int pageSize();

	@RealName("park")
	void park(boolean obj0, long obj1);

	@RealName("putAddress")
	void putAddress(long obj0, long obj1);

	@RealName("putBoolean")
	void putBoolean(Object obj0, long obj1, boolean obj2);

	@RealName("putBooleanVolatile")
	void putBooleanVolatile(Object obj0, long obj1, boolean obj2);

	@RealName("putByte")
	void putByte(long obj0, byte obj1);

	@RealName("putByte")
	void putByte(Object obj0, long obj1, byte obj2);

	@RealName("putByteVolatile")
	void putByteVolatile(Object obj0, long obj1, byte obj2);

	@RealName("putChar")
	void putChar(long obj0, char obj1);

	@RealName("putChar")
	void putChar(Object obj0, long obj1, char obj2);

	@RealName("putCharVolatile")
	void putCharVolatile(Object obj0, long obj1, char obj2);

	@RealName("putDouble")
	void putDouble(long obj0, double obj1);

	@RealName("putDouble")
	void putDouble(Object obj0, long obj1, double obj2);

	@RealName("putDoubleVolatile")
	void putDoubleVolatile(Object obj0, long obj1, double obj2);

	@RealName("putFloat")
	void putFloat(long obj0, float obj1);

	@RealName("putFloat")
	void putFloat(Object obj0, long obj1, float obj2);

	@RealName("putFloatVolatile")
	void putFloatVolatile(Object obj0, long obj1, float obj2);

	@RealName("putInt")
	void putInt(long obj0, int obj1);

	@RealName("putInt")
	void putInt(Object obj0, long obj1, int obj2);

	@RealName("putIntVolatile")
	void putIntVolatile(Object obj0, long obj1, int obj2);

	@RealName("putLong")
	void putLong(long obj0, long obj1);

	@RealName("putLong")
	void putLong(Object obj0, long obj1, long obj2);

	@RealName("putLongVolatile")
	void putLongVolatile(Object obj0, long obj1, long obj2);

	@RealName("putObject")
	void putObject(Object obj0, long obj1, Object obj2);

	@RealName("putObjectVolatile")
	void putObjectVolatile(Object obj0, long obj1, Object obj2);

	@RealName("putShort")
	void putShort(long obj0, short obj1);

	@RealName("putShort")
	void putShort(Object obj0, long obj1, short obj2);

	@RealName("putShortVolatile")
	void putShortVolatile(Object obj0, long obj1, short obj2);

	@RealName("reallocateMemory")
	long reallocateMemory(long obj0, long obj1);

	@RealName("setMemory")
	void setMemory(long obj0, long obj1, byte obj2);

	@RealName("setMemory")
	void setMemory(Object obj0, long obj1, long obj2, byte obj3);

	@RealName("shouldBeInitialized")
	boolean shouldBeInitialized(Class obj0);

	@RealName("staticFieldBase")
	Object staticFieldBase(Field obj0);

	@RealName("staticFieldOffset")
	long staticFieldOffset(Field obj0);

	@RealName("storeFence")
	void storeFence();

	@RealName("unpark")
	void unpark(Object obj0);
}