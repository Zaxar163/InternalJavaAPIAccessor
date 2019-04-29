package ru.zaxar163.unsafe.xlevel;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import ru.zaxar163.unsafe.fast.proxies.ProxyList;

public class SymbolsUtil {
	public static class Ptr2Obj {
		private static final long _narrow_oop_base = INSTANCE
				.getAddress(INSTANCE.type("Universe").global("_narrow_oop._base"));
		private static final int _narrow_oop_shift = INSTANCE
				.getInt(INSTANCE.type("Universe").global("_narrow_oop._shift"));
		private static long objFieldOffset;
		static {
			try {
				final java.lang.reflect.Field objField = Ptr2Obj.class.getDeclaredField("obj");
				objFieldOffset = INSTANCE.fieldOffset(objField);
			} catch (final NoSuchFieldException e) {
				throw new NativeAccessError("Couldn't obtain obj field of own class");
			}
		}

		public static Object getFromNarrowPtr(final long address) {
			if (address == 0)
				return null;
			final Ptr2Obj ptr2Obj = new Ptr2Obj();
			ProxyList.UNSAFE.compareAndSwapInt(ptr2Obj, objFieldOffset, 0, (int) address);
			return ptr2Obj.obj;
		}

		public static Object getFromPtr(final long address) {
			if (address == 0)
				return null;
			final Ptr2Obj ptr2Obj = new Ptr2Obj();
			ProxyList.UNSAFE.compareAndSwapInt(ptr2Obj, objFieldOffset, 0,
					(int) (address - _narrow_oop_base >> _narrow_oop_shift));
			return ptr2Obj.obj;
		}

		public static Object getFromPtr2NarrowPtr(final long address) {
			if (address == 0)
				return null;
			final Ptr2Obj ptr2Obj = new Ptr2Obj();
			ProxyList.UNSAFE.compareAndSwapInt(ptr2Obj, objFieldOffset, 0, (int) INSTANCE.getAddress(address));
			return ptr2Obj.obj;
		}

		public static Object getFromPtr2Ptr(final long address) {
			if (address == 0)
				return null;
			final Ptr2Obj ptr2Obj = new Ptr2Obj();
			ProxyList.UNSAFE.compareAndSwapInt(ptr2Obj, objFieldOffset, 0,
					(int) (INSTANCE.getAddress(address) - _narrow_oop_base >> _narrow_oop_shift));
			return ptr2Obj.obj;
		}

		private volatile Object obj;
	}

	private static final SymbolsUtil INSTANCE = new SymbolsUtil();

	public static SymbolsUtil getInstance() {
		return INSTANCE;
	}

	private final Map<String, Number> constants = new LinkedHashMap<>();

	private final Map<String, Type> types = new LinkedHashMap<>();

	private SymbolsUtil() {
		readVmTypes(readVmStructs());
		readVmIntConstants();
		readVmLongConstants();
	}

	public Number constant(final String name) {
		final Number constant = constants.get(name);
		if (constant == null)
			throw new NoSuchElementException("No such constant: " + name);
		return constant;
	}

	public void dump(final PrintStream out) {
		out.println("Constants:");
		for (final Map.Entry<String, Number> entry : constants.entrySet()) {
			final String type = entry.getValue() instanceof Long ? "long" : "int";
			out.println("const " + type + ' ' + entry.getKey() + " = " + entry.getValue());
		}
		out.println();

		out.println("Types:");
		for (final Type type : types.values())
			out.println(type);
	}

	public long fieldOffset(final java.lang.reflect.Field field) {
		return ProxyList.UNSAFE.objectFieldOffset(field);
	}

	public long getAddress(final long addr) {
		return ProxyList.UNSAFE.getAddress(addr);
	}

	public byte getByte(final long addr) {
		return ProxyList.UNSAFE.getByte(addr);
	}

	public int getInt(final long addr) {
		return ProxyList.UNSAFE.getInt(addr);
	}

	public long getLong(final long addr) {
		return ProxyList.UNSAFE.getLong(addr);
	}

	public short getShort(final long addr) {
		return ProxyList.UNSAFE.getShort(addr);
	}

	public String getString(final long addr) {
		if (addr == 0)
			return null;

		char[] chars = new char[40];
		int offset = 0;
		for (byte b; (b = getByte(addr + offset)) != 0;) {
			if (offset >= chars.length)
				chars = Arrays.copyOf(chars, offset * 2);
			chars[offset++] = (char) b;
		}
		return new String(chars, 0, offset);
	}

	public String getStringRef(final long addr) {
		return getString(getAddress(addr));
	}

	public long getSymbol(final String name) {
		final long address = SymbolsResolver.lookup(name);
		if (address == 0)
			throw new NoSuchElementException("No such symbol: " + name);
		return getLong(address);
	}

	public int intConstant(final String name) {
		return constant(name).intValue();
	}

	public long longConstant(final String name) {
		return constant(name).longValue();
	}

	public void putAddress(final long addr, final long val) {
		ProxyList.UNSAFE.putAddress(addr, val);
	}

	public void putByte(final long addr, final byte val) {
		ProxyList.UNSAFE.putByte(addr, val);
	}

	public void putInt(final long addr, final int val) {
		ProxyList.UNSAFE.putInt(addr, val);
	}

	public void putLong(final long addr, final long val) {
		ProxyList.UNSAFE.putLong(addr, val);
	}

	public void putShort(final long addr, final short val) {
		ProxyList.UNSAFE.putShort(addr, val);
	}

	private void readVmIntConstants() {
		long entry = getSymbol("gHotSpotVMIntConstants");
		final long nameOffset = getSymbol("gHotSpotVMIntConstantEntryNameOffset");
		final long valueOffset = getSymbol("gHotSpotVMIntConstantEntryValueOffset");
		final long arrayStride = getSymbol("gHotSpotVMIntConstantEntryArrayStride");

		for (;; entry += arrayStride) {
			final String name = getStringRef(entry + nameOffset);
			if (name == null)
				break;

			final int value = getInt(entry + valueOffset);
			constants.put(name, value);
		}
	}

	private void readVmLongConstants() {
		long entry = getSymbol("gHotSpotVMLongConstants");
		final long nameOffset = getSymbol("gHotSpotVMLongConstantEntryNameOffset");
		final long valueOffset = getSymbol("gHotSpotVMLongConstantEntryValueOffset");
		final long arrayStride = getSymbol("gHotSpotVMLongConstantEntryArrayStride");

		for (;; entry += arrayStride) {
			final String name = getStringRef(entry + nameOffset);
			if (name == null)
				break;

			final long value = getLong(entry + valueOffset);
			constants.put(name, value);
		}
	}

	private Map<String, Set<Field>> readVmStructs() {
		long entry = getSymbol("gHotSpotVMStructs");
		final long typeNameOffset = getSymbol("gHotSpotVMStructEntryTypeNameOffset");
		final long fieldNameOffset = getSymbol("gHotSpotVMStructEntryFieldNameOffset");
		final long typeStringOffset = getSymbol("gHotSpotVMStructEntryTypeStringOffset");
		final long isStaticOffset = getSymbol("gHotSpotVMStructEntryIsStaticOffset");
		final long offsetOffset = getSymbol("gHotSpotVMStructEntryOffsetOffset");
		final long addressOffset = getSymbol("gHotSpotVMStructEntryAddressOffset");
		final long arrayStride = getSymbol("gHotSpotVMStructEntryArrayStride");

		final Map<String, Set<Field>> structs = new HashMap<>();

		for (;; entry += arrayStride) {
			final String typeName = getStringRef(entry + typeNameOffset);
			final String fieldName = getStringRef(entry + fieldNameOffset);
			if (fieldName == null)
				break;

			final String typeString = getStringRef(entry + typeStringOffset);
			final boolean isStatic = getInt(entry + isStaticOffset) != 0;
			final long offset = getLong(entry + (isStatic ? addressOffset : offsetOffset));

			Set<Field> fields = structs.get(typeName);
			if (fields == null)
				structs.put(typeName, fields = new TreeSet<>());
			fields.add(new Field(fieldName, typeString, offset, isStatic));
		}

		return structs;
	}

	private void readVmTypes(final Map<String, Set<Field>> structs) {
		long entry = getSymbol("gHotSpotVMTypes");
		final long typeNameOffset = getSymbol("gHotSpotVMTypeEntryTypeNameOffset");
		final long superclassNameOffset = getSymbol("gHotSpotVMTypeEntrySuperclassNameOffset");
		final long isOopTypeOffset = getSymbol("gHotSpotVMTypeEntryIsOopTypeOffset");
		final long isIntegerTypeOffset = getSymbol("gHotSpotVMTypeEntryIsIntegerTypeOffset");
		final long isUnsignedOffset = getSymbol("gHotSpotVMTypeEntryIsUnsignedOffset");
		final long sizeOffset = getSymbol("gHotSpotVMTypeEntrySizeOffset");
		final long arrayStride = getSymbol("gHotSpotVMTypeEntryArrayStride");

		for (;; entry += arrayStride) {
			final String typeName = getStringRef(entry + typeNameOffset);
			if (typeName == null)
				break;

			final String superclassName = getStringRef(entry + superclassNameOffset);
			final boolean isOop = getInt(entry + isOopTypeOffset) != 0;
			final boolean isInt = getInt(entry + isIntegerTypeOffset) != 0;
			final boolean isUnsigned = getInt(entry + isUnsignedOffset) != 0;
			final int size = getInt(entry + sizeOffset);

			final Set<Field> fields = structs.get(typeName);
			types.put(typeName, new Type(typeName, superclassName, size, isOop, isInt, isUnsigned, fields));
		}
	}

	public Type type(final String name) {
		final Type type = types.get(name);
		if (type == null)
			throw new NoSuchElementException("No such type: " + name);
		return type;
	}
}
