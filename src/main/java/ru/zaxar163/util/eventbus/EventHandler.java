package ru.zaxar163.util.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for a method to be included as an event listener by
 * {@link SimpleEventManager <Event>#register(Object)}.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface EventHandler {

	/**
	 * Method's priority of execution when handled by {@link SimpleEventManager
	 * <Event>}. The higher the priority is the later the annotated method is
	 * called.
	 *
	 * @return execution priority of annotated method.
	 */
	byte priority() default 0;
}