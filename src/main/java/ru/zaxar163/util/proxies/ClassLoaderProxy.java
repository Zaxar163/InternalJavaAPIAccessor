package ru.zaxar163.util.proxies;

import java.util.Stack;
import java.util.Vector;

import ru.zaxar163.util.ClassUtil;
import ru.zaxar163.util.LookupUtil;
import ru.zaxar163.util.dynamicgen.FastDynamicProxy;
import ru.zaxar163.util.dynamicgen.MethodAccGenF;
import ru.zaxar163.util.dynamicgen.RealName;
import ru.zaxar163.util.dynamicgen.Static;
import ru.zaxar163.util.dynamicgen.reflect.InvokerMethodF;
import ru.zaxar163.util.unsafe.UnsafeFieldAcc;

public final class ClassLoaderProxy {

	private interface NativeLibraryProxy {
		@RealName("find")
		long find(Object inst, String obj0);

		@Static
		@RealName("getFromClass")
		Class<?> getFromClass();

		@RealName("load")
		void load(Object inst, String obj0, boolean obj1);

		@RealName("unload")
		void unload(Object inst, String obj0, boolean obj1);
	}

	private final UnsafeFieldAcc classes = new UnsafeFieldAcc(
			LookupUtil.getField(ClassLoader.class, "classes", Vector.class));

	private final InvokerMethodF findNative;

	private final UnsafeFieldAcc fromClass;

	private final UnsafeFieldAcc handle;

	private final UnsafeFieldAcc isBuiltin;

	private final UnsafeFieldAcc jniVersion;

	private final UnsafeFieldAcc loaded;

	private final UnsafeFieldAcc loadedLibraryNames = new UnsafeFieldAcc(
			LookupUtil.getField(ClassLoader.class, "loadedLibraryNames", Vector.class));

	private final UnsafeFieldAcc name;

	private final UnsafeFieldAcc nativeLibraries = new UnsafeFieldAcc(
			LookupUtil.getField(ClassLoader.class, "nativeLibraries", Vector.class));

	private final UnsafeFieldAcc nativeLibraryContext = new UnsafeFieldAcc(
			LookupUtil.getField(ClassLoader.class, "nativeLibraryContext", Stack.class));

	private final NativeLibraryProxy PROXY;

	private final UnsafeFieldAcc sys_paths = new UnsafeFieldAcc(
			LookupUtil.getField(ClassLoader.class, "sys_paths", String[].class));
	private final UnsafeFieldAcc systemNativeLibraries = new UnsafeFieldAcc(
			LookupUtil.getField(ClassLoader.class, "systemNativeLibraries", Vector.class));
	private final UnsafeFieldAcc usr_paths = new UnsafeFieldAcc(
			LookupUtil.getField(ClassLoader.class, "usr_paths", String[].class));

	ClassLoaderProxy() {
		final Class<?> nativeLib = ClassUtil.nonThrowingFirstClass("java.lang.ClassLoader$NativeLibrary");
		handle = new UnsafeFieldAcc(LookupUtil.getField(nativeLib, "handle", long.class));
		jniVersion = new UnsafeFieldAcc(LookupUtil.getField(nativeLib, "jniVersion", int.class));
		fromClass = new UnsafeFieldAcc(LookupUtil.getField(nativeLib, "fromClass", Class.class));
		name = new UnsafeFieldAcc(LookupUtil.getField(nativeLib, "name", String.class));
		isBuiltin = new UnsafeFieldAcc(LookupUtil.getField(nativeLib, "isBuiltin", boolean.class));
		loaded = new UnsafeFieldAcc(LookupUtil.getField(nativeLib, "loaded", boolean.class));
		PROXY = new FastDynamicProxy<>(null, nativeLib, NativeLibraryProxy.class).instance();
		findNative = MethodAccGenF
				.method(LookupUtil.getMethod(ClassLoader.class, "findNative", ClassLoader.class, String.class));
	}

	public long find(final Object inst, final String obj0) {
		return PROXY.find(inst, obj0);
	}

	@SuppressWarnings("unchecked")
	public Vector<Class<?>> getClasses(final ClassLoader inst) {
		return (Vector<Class<?>>) classes.getObject(inst);
	}

	public Class<?> getFromClass() {
		return PROXY.getFromClass();
	}

	public Class<?> getFromClass(final Object inst) {
		return (Class<?>) fromClass.getObject(inst);
	}

	public long getHandle(final Object inst) {
		return handle.getLong(inst);
	}

	public boolean getIsBuiltin(final Object inst) {
		return isBuiltin.getBoolean(inst);
	}

	public int getJniVersion(final Object inst) {
		return jniVersion.getInt(inst);
	}

	public boolean getLoaded(final Object inst) {
		return loaded.getBoolean(inst);
	}

	@SuppressWarnings("unchecked")
	public Vector<String> getLoadedLibraryNames() {
		return (Vector<String>) loadedLibraryNames.getObject(null);
	}

	public String getName(final Object inst) {
		return (String) name.getObject(inst);
	}

	@SuppressWarnings("unchecked")
	public Vector<Object> getNativeLibraries(final ClassLoader ldr) {
		return (Vector<Object>) nativeLibraries.getObject(ldr);
	}

	@SuppressWarnings("unchecked")
	public Stack<Object> getNativeLibraryContext() {
		return (Stack<Object>) nativeLibraryContext.getObject(null);
	}

	public String[] getSysPaths() {
		return (String[]) sys_paths.getObject(null);
	}

	@SuppressWarnings("unchecked")
	public Vector<Object> getSystemNativeLibraries() {
		return (Vector<Object>) systemNativeLibraries.getObject(null);
	}

	public String[] getUsrPaths() {
		return (String[]) usr_paths.getObject(null);
	}

	public void load(final Object inst, final String obj0, final boolean obj1) {
		PROXY.load(inst, obj0, obj1);
	}

	public long lookup(final ClassLoader cl, final String name) {
		try {
			return (long) findNative.invoke(cl, name);
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public void setClasses(final ClassLoader inst, final Vector<Class<?>> to) {
		classes.setObject(inst, to);
	}

	public void setFromClass(final Object inst, final Class<?> s) {
		fromClass.setObject(inst, s);
	}

	public void setHandle(final Object inst, final long s) {
		handle.setLong(inst, s);
	}

	public void setIsBuiltin(final Object inst, final boolean s) {
		isBuiltin.setBoolean(inst, s);
	}

	public void setJniVersion(final Object inst, final int s) {
		jniVersion.setInt(inst, s);
	}

	public void setLoaded(final Object inst, final boolean s) {
		loaded.setBoolean(inst, s);
	}

	public void setLoadedLibraryNames(final Vector<String> loadedLibraryName) {
		loadedLibraryNames.setObject(null, loadedLibraryName);
	}

	public void setName(final Object inst, final String s) {
		name.setObject(inst, s);
	}

	public void setNativeLibraries(final ClassLoader ldr, final Vector<Object> nativeLibrarie) {
		nativeLibraries.setObject(ldr, nativeLibrarie);
	}

	public void setNativeLibraryContext(final Stack<Object> nativeLibraryContex) {
		nativeLibraryContext.setObject(null, nativeLibraryContex);
	}

	public void setSysPaths(final String[] sys_path) {
		sys_paths.setObject(null, sys_path);
	}

	public void setSystemNativeLibraries(final Vector<Object> systemNativeLibrarie) {
		systemNativeLibraries.setObject(null, systemNativeLibrarie);
	}

	public void setUsrPaths(final String[] usr_path) {
		usr_paths.setObject(null, usr_path);
	}

	public void unload(final Object inst, final String obj0, final boolean obj1) {
		PROXY.unload(inst, obj0, obj1);
	}

}
