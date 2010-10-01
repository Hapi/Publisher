package com.hapiware.util.publisher;

import com.hapiware.util.publisher.annotation.Id;
import com.hapiware.util.publisher.annotation.IdentityHashed;


public class Tester
{
	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		Publisher<IPrivateMethods> p = Publisher.create(IPrivateMethods.class);
		for(int i = 0; i < 1000000; i++) {
			PrivateMethods pm = new PrivateMethods();
			IPrivateMethods ipm = p.publish(pm);
			//System.out.println(ip.add(2.3, 4.3));
			ipm.add(2.3, 4.3);
			ipm.add(616, 666);
			ipm.add("Moro ", "poro");
		}
		long end = System.currentTimeMillis(); 
		System.out.println("Time = " + (end - start));
	}
	
	@IdentityHashed
	interface IPrivateMethods
	{
		@Id(0)
		public void doSomething();
		@Id(1)
		public void doSomethingElse();
		@Id(2)
		public String add(String a, String b);
		@Id(3)
		public double add(double a, double b);
		@Id(4)
		public int add(int a, int b);
		@Id(5)
		public void miumau();
		@Id(6)
		public void miumauFinal();
	}
}
