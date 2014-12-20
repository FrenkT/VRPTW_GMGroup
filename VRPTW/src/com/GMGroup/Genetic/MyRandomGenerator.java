package com.GMGroup.Genetic;

import java.util.Random;
import java.util.UUID;

import org.jgap.RandomGenerator;

@SuppressWarnings("serial")
public class MyRandomGenerator implements RandomGenerator{

	private static MyRandomGenerator instance;
	
	public static MyRandomGenerator getInstance()
	{
		if (instance==null)
			throw new NullPointerException("Error: You must initialize the random generator by invoking setSeed parameter");
		else
			return instance;
	}
	
	public static void setSeed(int seed)
	{
		if (seed<0)
		{
			seed = (int)UUID.randomUUID().getMostSignificantBits();
			System.out.println("* Configuring Random Generator with a random seed: "+seed);
		}
		else
			System.out.println("* Configuring Random Generator with seed: "+seed);
		
		instance=new MyRandomGenerator(seed);
		
	}
	
	private Random rnd;
	
	public MyRandomGenerator(long seed) {
		rnd = new Random(seed);
	}
	
	@Override
	public int nextInt() {
		return rnd.nextInt();
	}

	@Override
	public int nextInt(int a_ceiling) {
		return rnd.nextInt(a_ceiling);
	}

	@Override
	public long nextLong() {
		return rnd.nextLong();
	}

	@Override
	public double nextDouble() {
		return rnd.nextDouble();
	}

	@Override
	public float nextFloat() {
		return rnd.nextFloat();
	}

	@Override
	public boolean nextBoolean() {
		return rnd.nextBoolean();
	}

	
	
}
