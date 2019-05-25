package ru.zaxar163.util.dynamicgen;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Do not remove anything, that is annotated with this type.
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD, ANNOTATION_TYPE })
public @interface Keep {
}
