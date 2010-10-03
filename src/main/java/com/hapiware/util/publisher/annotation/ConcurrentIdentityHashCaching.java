package com.hapiware.util.publisher.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code @ConcurrentIdentityHashCaching} is used to introduce a concurrent identity hash caching
 * policy. <u>This is not a recommended caching policy.</u>
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConcurrentIdentityHashCaching
{
}
