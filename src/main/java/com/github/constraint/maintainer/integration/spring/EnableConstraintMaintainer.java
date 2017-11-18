package com.github.constraint.maintainer.integration.spring;

import com.github.constraint.maintainer.DisablingMode;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Stanislav Dobrovolschi
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ConstraintMaintainerConfiguration.class)
public @interface EnableConstraintMaintainer {

    DisablingMode value() default DisablingMode.ALL;
}
