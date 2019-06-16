package ru.zaxar163.unsafe.xlevel;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import ru.zaxar163.util.proxies.ProxyList;

public class SymbolsUtil {
	public static class Ptr2Obj {
		private static final long _narrow_oop_base = ProxyList.UNSAFE
				.getAddress(INSTANCE.type("Universe").global("_narrow_oop._base"));
		private static final int _narrow_oop_shift = ProxyList.UNSAFE
				.getInt(INSTANCE.type("Universe").global("_narrow_oop._shift"));
		private final static long objFieldOffset;
		static {
			try {
				final java.lang.reflect.Field objField = Arrays.stream(Ptr2Obj.class.getDeclaredFields())
						.filter(e -> !Modifier.isStatic(e.getModifiers()) && Modifier.isVolatile(e.getModifiers()))
						.findFirst().get();
				objFieldOffset = ProxyList.UNSAFE.objectFieldOffset(objField);
			} catch (final Throwable e) {
				throw new Error("Couldn't obtain obj field of own class");
			}
		}

		public static Object getFromPtr2Ptr(final long address) {
			if (address == 0)
				return null;
			final Ptr2Obj ptr2Obj = new Ptr2Obj();
			ProxyList.UNSAFE.putIntVolatile(ptr2Obj, objFieldOffset,
					(int) (ProxyList.UNSAFE.getAddress(address) - _narrow_oop_base >> _narrow_oop_shift));
			return ptr2Obj.obj;
		}

		private volatile Object obj;
	}

	public static final SymbolsUtil INSTANCE = new SymbolsUtil();

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

	public String getString(final long addr) {
		if (addr == 0)
			return null;

		char[] chars = new char[40];
		int offset = 0;
		for (byte b; (b = ProxyList.UNSAFE.getByte(addr + offset)) != 0;) {
			if (offset >= chars.length)
				chars = Arrays.copyOf(chars, offset * 2);
			chars[offset++] = (char) b;
		}
		return new String(chars, 0, offset);
	}

	public String getStringRef(final long addr) {
		return getString(ProxyList.UNSAFE.getAddress(addr));
	}

	public long getSymbol(final String name) {
		final long address = SymbolsResolver.lookup(name);
		if (address == 0)
			throw new NoSuchElementException("No such symbol: " + name);
		return ProxyList.UNSAFE.getLong(address);
	}

	public int intConstant(final String name) {
		return constant(name).intValue();
	}

	public long longConstant(final String name) {
		return constant(name).longValue();
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

			final int value = ProxyList.UNSAFE.getInt(entry + valueOffset);
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

			final long value = ProxyList.UNSAFE.getLong(entry + valueOffset);
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
			final boolean isStatic = ProxyList.UNSAFE.getInt(entry + isStaticOffset) != 0;
			final long offset = ProxyList.UNSAFE.getLong(entry + (isStatic ? addressOffset : offsetOffset));

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
			final boolean isOop = ProxyList.UNSAFE.getInt(entry + isOopTypeOffset) != 0;
			final boolean isInt = ProxyList.UNSAFE.getInt(entry + isIntegerTypeOffset) != 0;
			final boolean isUnsigned = ProxyList.UNSAFE.getInt(entry + isUnsignedOffset) != 0;
			final int size = ProxyList.UNSAFE.getInt(entry + sizeOffset);

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
