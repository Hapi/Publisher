package com.hapiware.util.publisher.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code @Id} is used to introduce a (normal) caching policy for handling substitute interfaces.
 * There are several constraints for {@code @Id}:
 * 	<ul>
 * 		<li>
 * 			<u>Every method in the substitute interface must have an {@code @Id} annotation</u>
 * 			to introduce a normal caching policy.
 * 		</li>
 * 		<li>Same value is not allowed for multiple methods.</li>
 * 		<li>Value cannot be negative.</li>
 * 		<li>Values must be sequential.</li>
 * 		<li>The first value must be zero (0).</li>
 * 	</ul>
 *  
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Id
{
	int value();
}
