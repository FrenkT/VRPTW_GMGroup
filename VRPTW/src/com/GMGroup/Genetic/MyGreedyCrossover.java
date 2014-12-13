package com.GMGroup.Genetic;

import java.util.List;

import org.jgap.Configuration;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.RandomGenerator;
import org.jgap.impl.GreedyCrossover;

public class MyGreedyCrossover extends GreedyCrossover {

	private double m_rate = 0.5;
	
	public MyGreedyCrossover(Configuration a_configuration)
			throws InvalidConfigurationException {
		super(a_configuration);
	}

	public MyGreedyCrossover(Configuration a_configuration,double rate) throws InvalidConfigurationException {
		super(a_configuration);
		
		if (rate<0 || rate>1)
			throw new IllegalArgumentException("Invalid rate specified");
		
		m_rate=rate;
	}
	
	public void setRate(double rate)
	{
		if (rate<0 || rate>1)
			throw new IllegalArgumentException("Invalid rate specified");
		this.m_rate=rate;
	}
	
	public double getRate()
	{
		return m_rate;
	}
	
	@Override
	public void operate(final Population a_population, final List a_candidateChromosomes) {
		int size = Math.min(getConfiguration().getPopulationSize(),
				a_population.size());
		int numCrossovers = (int)(size * m_rate);
		RandomGenerator generator = getConfiguration().getRandomGenerator();
		// For each crossover, grab two random chromosomes and do what
		// Grefenstette et al say.
		// --------------------------------------------------------------
		for (int i = 0; i < numCrossovers; i++) {
			IChromosome origChrom1 = a_population.getChromosome(generator
					.nextInt(size));
			IChromosome firstMate = (IChromosome) origChrom1.clone();
			IChromosome origChrom2 = a_population.getChromosome(generator
					.nextInt(size));
			IChromosome secondMate = (IChromosome) origChrom2.clone();
			// In case monitoring is active, support it.
			// -----------------------------------------
			if (m_monitorActive) {
				firstMate.setUniqueIDTemplate(origChrom1.getUniqueID(), 1);
				firstMate.setUniqueIDTemplate(origChrom2.getUniqueID(), 2);
				secondMate.setUniqueIDTemplate(origChrom1.getUniqueID(), 1);
				secondMate.setUniqueIDTemplate(origChrom2.getUniqueID(), 2);
			}
			operate(firstMate, secondMate);
			// Add the modified chromosomes to the candidate pool so that
			// they'll be considered for natural selection during the next
			// phase of evolution.
			// -----------------------------------------------------------
			a_candidateChromosomes.add(firstMate);
			a_candidateChromosomes.add(secondMate);
		}
	}

}
