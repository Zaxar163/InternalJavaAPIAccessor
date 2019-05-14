package ru.zaxar163.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public final class DelegateClassLoader extends ClassLoader {
	public static final DelegateClassLoader INSTANCE = new DelegateClassLoader();
	private final Set<ClassLoader> cls;

	static {
		ClassLoader.registerAsParallelCapable();
	}

	private DelegateClassLoader() {
		super(null);
		this.cls = Collections.newSetFromMap(new ConcurrentHashMap<>());
		cls.add(ClassLoader.getSystemClassLoader());
		cls.add(this.getClass().getClassLoader());
	}
	
	public void append(Class<?> c) {
		if (c != null) append(c.getClassLoader());
	}
	public void append(Method c) {
		if (c != null) append(c.getDeclaringClass().getClassLoader());
	}
	public void append(Field c) {
		if (c != null) append(c.getDeclaringClass().getClassLoader());
	}
	public void append(Constructor<?> c) {
		if (c != null) append(c.getDeclaringClass().getClassLoader());
	}
	public void append(ClassLoader c) {
		if (c != null) cls.add(c);
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		for (ClassLoader e : cls) {
			try {
				return e.loadClass(name);
			} catch (Throwable t) { }
		}
		throw new ClassNotFoundException(name);
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		for (ClassLoader e : cls) {
			try {
				return e.loadClass(name);
			} catch (Throwable t) { }
		}
		throw new ClassNotFoundException(name);
	}
	
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		for (ClassLoader e : cls) {
			try {
				Class<?> clazz = e.loadClass(name);
				if (resolve) resolveClass(clazz);
				return clazz;
			} catch (Throwable t) { }
		}
		throw new ClassNotFoundException(name);
	}

    public URL getResource(String name) {
        URL url;
        for (ClassLoader l : cls) {
        	url = l.getResource(name);
        	if (url != null) return url;
        }
        return null;
    }

    public Class<?> findLoadedClassA(String name) {
    	return cls.stream().map(e -> ClassUtil.findLoadedClass(name)).filter(e -> e != null).findFirst().orElse(null);
    }
    
    public Enumeration<URL> getResources(String name) throws IOException {
    	ClassLoader[] current = cls.toArray(new ClassLoader[0]);
        @SuppressWarnings("unchecked")
        Enumeration<URL>[] tmp = (Enumeration<URL>[]) new Enumeration<?>[current.length];
        for (int i = 0; i < current.length; i++)
        	tmp[i] = current[i].getResources(name);

        return new CompoundEnumeration<>(tmp);
    }

    public InputStream getResourceAsStream(String name) {
    	InputStream in;
        for (ClassLoader l : cls) {
        		in = l.getResourceAsStream(name);
        	if (in != null) return in;
        }
        return null;
    }
    
    protected URL findResource(String name) {
        return getResource(name);
    }

    protected Enumeration<URL> findResources(String name) throws IOException {
        return getResources(name);
    }
}

class CompoundEnumeration<E> implements Enumeration<E> {
    private Enumeration<E>[] enums;
    private int index = 0;

    public CompoundEnumeration(Enumeration<E>[] enums) {
        this.enums = enums;
    }

    private boolean next() {
        while (index < enums.length) {
            if (enums[index] != null && enums[index].hasMoreElements()) {
                return true;
            }
            index++;
        }
        return false;
    }

    public boolean hasMoreElements() {
        return next();
    }

    public E nextElement() {
        if (!next()) {
            throw new NoSuchElementException();
        }
        return enums[index].nextElement();
    }
}

