package com.hapiware.util.publisher.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code @NoCacghing} is used to indicate that a substitute interface does not use any
 * caching policy at all. {@code @NoCaching} overrides all other annotations.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NoCaching
{

}
