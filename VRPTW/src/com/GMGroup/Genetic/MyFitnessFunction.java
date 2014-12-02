package com.GMGroup.Genetic;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

public class MyFitnessFunction extends FitnessFunction {

	/**
	 * Simply calling the Objective function in an inverse way
	 */
	@Override
	protected double evaluate(IChromosome arg) {
		
		//MyChromosome chr = (MyChromosome)arg;
		double res = 1/GMObjectiveFunction.evaluate(arg);
		return res;
	}	
	
}
