package com.hapiware.util.publisher;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.hapiware.util.publisher.annotation.ConcurrentIdentityHashCaching;
import com.hapiware.util.publisher.annotation.Id;
import com.hapiware.util.publisher.annotation.IdAnnotationError;
import com.hapiware.util.publisher.annotation.IdentityHashCaching;
import com.hapiware.util.publisher.annotation.NoCaching;

/**
 * {@code Publisher} is a Java utility library to make private methods public in a type safe
 * manner very easily. Type safety is created by using <i>public substitute interfaces</i> (or
 * <i>substitute interface</i> for short). Substitute interface is a Java {@code interface} where
 * all the wanted private methods from the class to be substituted are duplicated. Publishing
 * a substitute interface creates a <i>public substitute object</i> (or <i>substitute object</i>
 * for short) which can be used to write unit tests for private methods of the substituted class.
 * <p>
 * Using {@code Publisher} is a very convenient way to write unit tests for private methods and
 * still maintain type safety.
 * 
 * 
 * 
 * <h3>Using {@code Publisher}</h3>
 * <h4>Normal usage</h4>
 * To use private methods of the instantiated class type safely a substitute interface must be
 * introduced. The substitute interface will have all the needed private methods with <u>exactly
 * the same signature</u> but with <u>different access modifiers</u>. The access modifier in the
 * substitute interface must be always {@code public}. The substitute interface must be published
 * with {@link #publish(Class, Object)} along respective instantiated class to create a substitute
 * object. For example, if the class is like this:
 * <pre>
 * 	class HashGenerator {
 * 		...
 * 		private String createDigest(String algorithm, String input)
 * 		{
 * 			// Some implementation.
 * 		}
 * 		...
 * 		private static byte[] concat(byte[] left, byte[] right)
 * 		{
 * 			byte[] retVal = new byte[left.length + right.length];
 * 			System.arraycopy(left, 0, retVal, 0, left.length);
 *			System.arraycopy(right, 0, retVal, left.length, right.length);
 *			return retVal;
 *		}
 * 		...
 * 	}
 * </pre> 
 * 
 * A substitute interface for the class would be:
 * <pre>
 * 	interface SHashGenerator
 * 	{
 * 		public String createDigest(String algorithm, String input);
 * 		public byte[] concat(byte[] left, byte[] right);
 * 	}
 * </pre>
 * 
 * And the unit test code could be:
 * <pre>
 * 	{@code @Test}
 *	public void concatTest()
 *	{
 *		HashGenerator hashGenerator = new HashGenerator();
 *		SHashGenerator substitute = Publisher.publish(SHashGenerator.class, hashGenerator);
 *		byte[] left = { 1, 2, 3, 4 };
 *		byte[] right = { 10, 20, 30, 40, 50 };
 *		byte[] result = substitute.concat(left, right);
 *		assertEquals(9, result.length);
 *		int total = 0;
 *		for(byte b : result)
 *			total += b;
 *		assertEquals(160, total);
 *	}
 * </pre>
 * 
 * 
 * <h4><a name="publisher-usage-with-loops">Usage with loops</a></h4>
 * If there is a need to create multiple instances of the substituted class (i.e. the one having
 * private methods) repetitively then {@link #create(Class)} and {@link #publish(Object)} methods
 * can be used together and achieve better performance than using {@link #publish(Class, Object)}
 * alone. Here is a continuation to the example above:
 * <pre>
 * 	{@code @Test}
 *	public void digestTest()
 *	{
 *		Publisher<SHashGenerator> publisher = Publisher.create(SHashGenerator.class);
 *		for(int i = 0; i < _table.length; i++) {
 *			HashGenerator hashGenerator = new HashGenerator(i);
 *			SHashGenerator substitute = publisher.publish(hashGenerator);
 *			String result = substitute.createDigest("sha-1", _table[i]);
 *			// Make assertions.
 *		}
 *	}
 * </pre>
 *
 * 
 * <h4><a name="publisher-static-methods">Static methods</a></h4>
 * If there is a need to test only private static methods of the substituted class
 * {@link #publish(Class, Class)} method can be used. The benefit is that there is no need to
 * create an instance of the substituted class. Let's change the {@code concatTest()} example test
 * method above to use {@link #publish(Class, Class)} method. A substitute interface would be:
 * <pre>
 * 	interface SHashGenerator
 * 	{
 * 		public byte[] concat(byte[] left, byte[] right);
 * 	}
 * </pre>
 * 
 * And the unit test:
 * <pre>
 * 	{@code @Test}
 *	public void concatTest()
 *	{
 *		SHashGenerator substitute = Publisher.publish(SHashGenerator.class, HashGenerator.class);
 *		byte[] left = { 1, 2, 3, 4 };
 *		byte[] right = { 10, 20, 30, 40, 50 };
 *		byte[] result = substitute.concat(left, right);
 *		assertEquals(9, result.length);
 *		int total = 0;
 *		for(byte b : result)
 *			total += b;
 *		assertEquals(160, total);
 *	}
 * </pre>
 * 
 * 
 * 
 * <h4>Exceptions</h4>
 * If a substitute interface has a method which does not exist in the substituted class then
 * {@link SubstituteMethodNameConflictError} is thrown. Most frequently this error is thrown
 * because there is a typo in the substitute interface. So, if {@link SubstituteMethodNameConflictError}
 * is thrown check the error message carefully and start comparing your substitute interface
 * and substituted class.
 * 
 * 
 * 
 * <h3>Recommended conventions</h3>
 * <h4>Where to put substitute interfaces?</h4>
 * The recommendation is that substitute interfaces are created as a nested interface inside
 * the test class. For example:
 * <pre>
 * 	public class HashGeneratorTest
 * 	{
 * 		interface SHashGenerator
 * 		{
 * 			public String createDigest(String algorithm, String input);
 * 			public byte[] concat(byte[] left, byte[] right);
 * 		}
 * 
 * 		{@code @Test}
 * 		public void digestTest()
 * 		{
 * 			...
 * 		}
 * 
 * 		...
 * 	}
 * </pre>
 * 
 * 
 * <h4>Naming</h4>
 * A programmer is absolutely free to select whatever name he/she sees fit. There are no
 * limitations other than the Java specification states. However, it is recommended to use one
 * of the following prefixes for the substitute interface:
 * 	<ul>
 * 		<li><b>S</b>, which stands for <i>substitute</i></li>
 * 		<li><b>SI</b>, which stands for <i>substitute interface</i></li>
 * 		<li><b>PS</b>, which stands for <i>public substitute</i></li>
 * 		<li><b>PSI</b>, which stands for <i>public substitute interface</i></li>
 * 	</ul>
 * 
 * This way the unit test writing is simple and the substitute interface and the original class
 * can be easily identified.
 * 
 * 
 * 
 * <h3>Performance</h3>
 * Because the implementation relies on {@link java.lang.reflect.Proxy} the performance is not
 * as good as with direct calls (which, of course, cannot be made to {@code private} methods).
 * To improve performance {@code Publisher} can be directed to use different caching policies.
 * A caching policy is selected with annotations. There are several different publishing policies
 * implemented but only one of them is really recommended and it is called normal caching
 * (<a href="#publisher-normal-caching">the one created with {@link Id} annotation</a>).
 * The others can be used but at the user's own risk.
 * <p>
 * There is also a simple trick to improve performance with a {@link NoCaching} annotation.
 * For more information see <a href="#publisher-disabling-caching">Disabling caching</a>.
 * 
 * 
 * <h4><a name="publisher-normal-caching">Normal caching</a></h4>
 * If it seems that there is a performance issue with a normal usage the performance can be
 * improved by introducing a caching policy for published methods. This is done by introducing
 * an index id for each of the methods in the substitute interface. {@link Id} annotation is
 * used for this purpouse. For example:
 * <pre>
 * 	interface SHashGenerator
 * 	{
 * 		{@code @Id}(0) public String createDigest(String algorithm, String input);
 * 		{@code @Id}(1) public byte[] concat(byte[] left, byte[] right);
 * 	}
 * </pre>
 * 
 * Constraints for {@link Id} values are:
 * 	<ul>
 * 		<li>
 * 			<u>Every method in the substitute interface must have an {@link Id} annotation</u>
 * 			to introduce a normal caching policy.
 * 		</li>
 * 		<li>Same value is not allowed for multiple methods.</li>
 * 		<li>Value cannot be negative.</li>
 * 		<li>Values must be sequential.</li>
 * 		<li>The first value must be zero (0).</li>
 * 	</ul>
 * 
 * If any constraint is broken then {@link IdAnnotationError} is thrown. 
 * <p>
 * <u>Normal caching is the recommended caching policy</u>, if caching is needed.
 * 
 * 
 * <h4><a name="publisher-disabling-caching">Disabling caching</a></h4>
 * To disable caching there are two options; removing all the {@link Id} annotations or using
 * {@link NoCaching} annotation. If the latter is used the other annotations are completely
 * ignored and no caching is used. For example:
 * <pre>
 * 	{@code @NoCaching}
 * 	interface SHashGenerator
 * 	{
 * 		{@code @Id}(0) public String createDigest(String algorithm, String input);
 * 		{@code @Id}(1) public byte[] concat(byte[] left, byte[] right);
 * 	}
 * </pre>
 * 
 * <b>Notice</b> also that <u>using a {@link NoCaching} annotation can improve performance</u>
 * because other annotations are not checked and thus the time used for finding a proper publishing
 * policy is greatly diminished. This is true even if no {@link Id} annotations are used. 
 * 
 * 
 * <h4><a name="publisher-identity-hash-caching">Identity hash caching</a></h4>
 * <u>*** Identity hash caching policy IS NOT RECOMMENDED. Use at your own risk!!! ***</u> See
 * the explanation below.
 * <p>
 * Identity hash caching is turned on by marking the substitute interface with
 * {@link IdentityHashCaching} annotation. {@link Id} annotations are completely ignored and
 * there is no need to remove possibly existing {@link Id} annotations. {@link IdentityHashCaching}
 * overrides {@link ConcurrentIdentityHashCaching} and is overridden by {@link NoCaching}.
 * <p>
 * Here is an example:
 * <pre>
 * 	{@code @IdentityHashCaching}
 * 	interface SHashGenerator
 * 	{
 * 		public String createDigest(String algorithm, String input);
 * 		public byte[] concat(byte[] left, byte[] right);
 * 	}
 * </pre>
 * 
 * Comparing to the <a href="#publisher-normal-caching">normal caching</a> the identity hash
 * caching has roughly twice as good performance and it is much simpler to use because the only
 * annotation needed is {@link IdentityHashCaching} annotation. However, the problem with the
 * identity hash caching is that Java does not have any public identity for objects. The closest
 * alternative to the object identity is {@link System#identityHashCode(Object)}. Usually this works
 * but there is no guarantee that the {@link System#identityHashCode(Object)} returns a different
 * value for different objects (i.e. substitute interface {@link Method}s in this case) and thus
 * the caching policy may fail.
 * <p>
 * When you start seeing {@link ClassCastException}s, {@link NullPointerException}s,
 * {@link UndeclaredThrowableException}s or {@link AmbiguousMethodNameError}s then it means that
 * there is a hash key conflict and you <u>must</u> change your caching policy. There are three
 * options available:
 * 	<ul>
 * 		<li>Use <a href="#publisher-normal-caching">normal caching</a></li>
 * 		<li><a href="#publisher-disabling-caching">Disable caching</a></li>
 * 		<li>Do not use {@code Publisher} at all</li>
 * 	</ul>
 * 
 * 
 * 
 * <h4>Concurrent identity hash caching</h4>
 * <u>*** Concurrent hash caching policy IS NOT RECOMMENDED. Use at your own risk!!! ***</u> See
 * the explanation from <a href="#publisher-identity-hash-caching">identity hash caching</a>.
 * <p>
 * Concurrent identity hash caching is turned on by marking the substitute interface with
 * {@link ConcurrentIdentityHashCaching} annotation. {@link Id} annotations are completely
 * ignored and there is no need to remove possibly existing {@link Id} annotations.
 * {@link ConcurrentIdentityHashCaching} is overridden by {@link IdentityHashCaching} and
 * {@link NoCaching}.
 * <p>
 * Here is an example. 
 * <pre>
 * 	{@code @ConcurrentIdentityHashCaching}
 * 	interface SHashGenerator
 * 	{
 * 		public String createDigest(String algorithm, String input);
 * 		public byte[] concat(byte[] left, byte[] right);
 * 	}
 * </pre>
 * 
 * This and <a href="#publisher-identity-hash-caching">identity hash caching</a> have the same
 * caching policy except concurrent identity hash caching policy uses {@link ConcurrentMap}
 * for caching. Concurrent caching policy is a little bit slower but otherwise all which is true
 * for <a href="#publisher-identity-hash-caching">identity hash caching</a> is true for concurrent
 * hash caching policy, also.
 * 
 * 
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 * @param <PSI>
 * 		A public substitute interface.
 * 
 * @see Id
 * @see NoCaching
 * @see IdentityHashCaching
 * @see ConcurrentIdentityHashCaching
 */
final public class Publisher<PSI>
{
	private PublishingPolicy<PSI> _publishingPolicy = null; 
	
	
	private Publisher()
	{
		// Prevents a construction of Publisher.
	}
	
	private Publisher(PublishingPolicy<PSI> publishingPolicy)
	{
		_publishingPolicy = publishingPolicy;
	}
	
	/**
	 * Creates a substitute object for an object which have private methods to be published.
	 * 
	 * @param <PSI>
	 * 		A public substitute interface.
	 * 
	 * @param substituteInterface
	 * 		A substitute interface class.
	 * 
	 * @param substitutedObject
	 * 		An object to be substituted (i.e. the object which does the real work).
	 * 
	 * @return
	 * 		A substitute object.
	 * 
	 * @throws SubstituteMethodNameConflictError
	 * 		If substitute interface has a method which does not exist in the substituted class. 
	 * 
	 * @throws IdAnnotationError
	 * 		If any of the {@link Id} constraints are broken.
	 */
	public static <PSI> PSI publish(
		final Class<PSI> substituteInterface,
		final Object substitutedObject
	)
	{
		return (PSI)findPublishingPolicy(substituteInterface).publish(substitutedObject);
	}
	
	/**
	 * Creates a substitute object for a class which have private static methods to be published.
	 * 
	 * @param <PSI>
	 * 		A public substitute interface.
	 * 
	 * @param substituteInterface
	 * 		A substitute interface class.
	 * 
	 * @param substitutedClass
	 * 		A class to be substituted (i.e. the class which static methods do the real work).
	 * 
	 * @return
	 * 		A substitute object.
	 * 
	 * @throws SubstituteMethodNameConflictError
	 * 		If substitute interface has a method which does not exist in the substituted class. 
	 * 
	 * @throws IdAnnotationError
	 * 		If any of the {@link Id} constraints are broken.
	 */
	public static <PSI> PSI publish(
		final Class<PSI> substituteInterface,
		final Class<?> substitutedClass
	)
	{
		return (PSI)findPublishingPolicy(substituteInterface).publish(substitutedClass);
	}
	
	/**
	 * Creates a substitute object for an object which have private methods to be published.
	 * This is supposed to be used together with {@link #create(Class)} method in loops or
	 * other places where the substitute objects are created in a frequent manner. See
	 * <a href="#publisher-usage-with-loops">Usage with loops</a>.
	 * 
	 * @param substitutedObject
	 * 		An object to be substituted (i.e. the object which does the real work).
	 * 
	 * @return
	 * 		A substitute object.
	 * 
	 * @throws SubstituteMethodNameConflictError
	 * 		If substitute interface has a method which does not exist in the substituted class.
	 * 
	 *  @see #create(Class)
	 */
	public PSI publish(final Object substitutedObject)
	{
		return (PSI)_publishingPolicy.publish(substitutedObject);
	}

	/**
	 * Creates a {@link Publisher} object to used later. This is supposed to be used together
	 * with {@link #publish(Object)} method in loops or other places where the substitute
	 * objects are created in a frequent manner.
	 * See <a href="#publisher-usage-with-loops">Usage with loops</a>.
	 * 
	 * @param <PSI>
	 * 		A public substitute interface.
	 * 
	 * @param substituteInterface
	 * 		A substitute interface class.
	 * 
	 * @return
	 * 		A publisher to be used later.
	 * 
	 * @throws IdAnnotationError
	 * 		If any of the {@link Id} constraints are broken.
	 * 
	 * @see #publish(Object)
	 */
	public static <PSI> Publisher<PSI> create(
		final Class<PSI> substituteInterface
	)
	{
		Publisher<PSI> p = new Publisher<PSI>();
		p._publishingPolicy = findPublishingPolicy(substituteInterface);
		return p;
	}

	
	/**
	 * Finds a proper publishing policy based on selected annotations.
	 * 
	 * @param <PSI>
	 * 		A public substitute interface.
	 * 
	 * @param substituteInterface
	 * 		A substitute interface class.
	 * 
	 * @return
	 * 		A publishing policy.
	 * 
	 * @throws IllegalArgumentException
	 * 		If {@code substituteInterface} argument is not an interface.
	 */
	private static <PSI> PublishingPolicy<PSI> findPublishingPolicy(
		final Class<PSI> substituteInterface
	)
	{
		if(!substituteInterface.isInterface())
			throw
				new IllegalArgumentException(
					"'substituteInterface' class must be an interface. Was: "
						+ substituteInterface.getName() + "."
				);
		if(substituteInterface.isAnnotationPresent(NoCaching.class))
			return new NonCachingPublishingPolicy<PSI>(substituteInterface);
		if(substituteInterface.isAnnotationPresent(IdentityHashCaching.class))
			return new IdentityHashedCachingPublishingPolicy<PSI>(substituteInterface);
		if(substituteInterface.isAnnotationPresent(ConcurrentIdentityHashCaching.class))
			return new ConcurrentIdentityHashedCachingPublishingPolicy<PSI>(substituteInterface);

		Method[] methods = substituteInterface.getDeclaredMethods();
		int numberOfAnnotations = 0;
		int expectedTotalIdSum = 0;
		int i = 0;
		int totalIdSum = 0;
		boolean idZeroFound = false;
		Set<Integer> set = new HashSet<Integer>();
		for(Method m : methods) {
			expectedTotalIdSum += i++;
			Id id = m.getAnnotation(Id.class);
			if(id == null)
				continue;
			
			if(id.value() < 0)
				throw new IdAnnotationError("@Id values cannot be negative numbers.");
			numberOfAnnotations++;
			if(id.value() == 0)
				idZeroFound = true;
			
			if(!set.add(id.value()))
				throw new IdAnnotationError("Same @Id values are not allowed.");
			totalIdSum += id.value();
		}
		if(numberOfAnnotations == 0)
			return new NonCachingPublishingPolicy<PSI>(substituteInterface);
		if(methods.length != numberOfAnnotations)
			throw new IdAnnotationError("Every substitute method needs @Id.");
		if(!idZeroFound)
			throw new IdAnnotationError("@Id values must start from zero (0).");
		if(expectedTotalIdSum != totalIdSum)
			throw new IdAnnotationError("@Id values must be sequential.");
		
		return new CachingPublishingPolicy<PSI>(substituteInterface);
	}
}
