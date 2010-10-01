package com.hapiware.util.publisher;

public class PrivateMethods
{
	public static void main(String[] args)
	{
		PrivateMethods m = new PrivateMethods();
		doSomething();
		m.doSomethingElse();
		m.add("Miu", "mau");
		System.out.println("Tulos = " + m.add(1.0, 3.14));
	}
	
	final private static void doSomething()
	{
		System.out.println("(private) Somehting...");
	}
	
	final private void doSomethingElse()
	{
		System.out.println("(private) Something else: ");
	}
	
	private String add(String a, String b)
	{
		return a + b;
	}
	
	private double add(double a, double b)
	{
		return a + b;
	}
	
	private int add(int a, int b)
	{
		return a + b;
	}
	
	
	
	void miumau()
	{
		System.out.println("(private) miu");
	}
	
	final void miumauFinal()
	{
		System.out.println("(private) miu final");
	}
}
