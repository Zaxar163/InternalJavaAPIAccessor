package ru.zaxar163.util.eventbus;

import java.io.Serializable;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import ru.zaxar163.util.LookupUtil;

public class EventCaller {

	public static class Handler<E> implements Comparable<Handler<E>>, Consumer<E>, Serializable {
		private static final long serialVersionUID = 274055930740798807L;
		protected final Consumer<E> c;
		public final byte priority;

		public Handler(final Consumer<E> consumer, final byte priority) {
			this.c = consumer;
			this.priority = priority;
		}

		@Override
		public void accept(final E t) {
			c.accept(t);
		}

		@Override
		public int compareTo(final Handler<E> handler) {
			return priority - handler.priority;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Handler))
				return false;
			final Handler<?> other = (Handler<?>) obj;
			return Objects.equals(c, other.c) && priority == other.priority;
		}

		@Override
		public int hashCode() {
			return Objects.hash(c, priority);
		}

		@Override
		public String toString() {
			return "Handler [c=" + c + ", priority=" + priority + "]";
		}
	}

	protected static <E> Consumer<E> constructConsumer(final Object listener, final Method method) {
		try {
			final Lookup lookup = LookupUtil.in(listener.getClass());
			final MethodHandle c = LambdaMetafactory
					.metafactory(lookup, "accept", MethodType.methodType(Consumer.class, listener.getClass()),
							MethodType.methodType(void.class, Object.class), lookup.unreflect(method),
							MethodType.methodType(void.class, method.getParameterTypes()[0]))
					.getTarget();
			return Modifier.isStatic(method.getModifiers()) ? (Consumer<E>) c.invoke()
					: (Consumer<E>) c.invoke(listener);
		} catch (final Throwable t) {
			throw new RuntimeException("Could not create event listener", t);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<Class<?>, List<Handler>> register(final Object listener) {
		final Class<?> listenerClass = listener.getClass();
		final Map<Class<?>, List<Handler>> handlers = new ConcurrentHashMap<>();
		for (final Method method : LookupUtil.getDeclaredMethods(listenerClass)) {
			if (method.getParameterCount() != 1 || !method.isAnnotationPresent(EventHandler.class))
				continue;
			final Class<?> parameterType = method.getParameterTypes()[0];
			final EventHandler annotation = method.getAnnotation(EventHandler.class);
			handlers.computeIfAbsent(parameterType, e -> new ArrayList<>(1))
					.add(new Handler(constructConsumer(listener, method), annotation.priority()));
		}
		return handlers;
	}
}
