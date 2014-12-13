package com.GMGroup.Genetic;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.DefaultConfiguration;

import com.mdvrp.Instance;
import com.mdvrp.Parameters;

public class SearchProgram {

	public static final int INITIAL_POPULATION_SIZE=50;
	
	/**
	 * Makes the crossover operator to operate on a limited number of parents.
	 * If INITIAL_SIZE_POPULATION is 90, there will be 90 * CROSS_OVER_RATIO 
	 * crossovers.
	 */
	public static final double CROSS_OVER_LIMIT_RATIO = 0.3;
	
	public static void main(String[] args) throws Exception
	{
		// ***** Parsing conf input file and populating Instance object *****
		Parameters parameters = new Parameters();
		
		parameters.updateParameters(args);
		if(parameters.getInputFileName() == null){
			System.out.println("You must specify an input file name");
			return;
		}
		Instance.setInstance(parameters);
		Instance instance = Instance.getInstance();
		instance.populateFromHombergFile(parameters.getInputFileName());
		
		// ***** Setting up jgap *****
		Configuration conf = new DefaultConfiguration();
		BestChromosomesSelector bestChromsSelector = new BestChromosomesSelector(conf, 0.90d);
		bestChromsSelector.setDoubletteChromosomesAllowed(false);
		conf.addNaturalSelector(bestChromsSelector, false);
		conf.setFitnessFunction(new MyFitnessFunction());
		conf.getGeneticOperators().clear();
		MyGreedyCrossover cop = new MyGreedyCrossover(conf);
		cop.setStartOffset(0);
		cop.setRate(CROSS_OVER_LIMIT_RATIO);
		conf.addGeneticOperator(cop);
		
		KChainMutationOperator cop2 = new KChainMutationOperator(conf);
		cop2.setMutationRate(4);
		conf.addGeneticOperator(cop2);
		
		conf.addGeneticOperator(new TabuOperator(conf));
		
		// ***** Generating an initial population *****
		IChromosome[] initialPop = new IChromosome[INITIAL_POPULATION_SIZE];
		for (int i=0;i<initialPop.length;i++)
		{
			initialPop[i]=MyChromosomeFactory.getInstance().generateInitialFeasibleChromosome(conf);
			System.out.println("Age:"+initialPop[i].getAge()+", Fitness: "+initialPop[i].getAge()+","+MyChromosomeFactory.PrintChromosome(initialPop[i]));
		}
		conf.setSampleChromosome(initialPop[0]);
		conf.setPopulationSize(INITIAL_POPULATION_SIZE);
		Genotype population = new Genotype(conf,initialPop);

		IChromosome c = population.getFittestChromosome();
		double res = GMObjectiveFunction.evaluate(c);
		System.out.println("Best of population Before EVOLVE: "+res);
		
		population.evolve(3);
		
		c = population.getFittestChromosome();
		res = GMObjectiveFunction.evaluate(c);
		System.out.println("\nBest of population: "+res+"\n");
		
		IChromosome best = population.getPopulation().getChromosome(0);
		for(int i=0;i<population.getPopulation().size();i++)
		{
			c = population.getPopulation().getChromosome(i);
			if (MyChromosomeFactory.getIsChromosomeFeasible(c)){
				if (GMObjectiveFunction.evaluate(c) < GMObjectiveFunction.evaluate(best)){
					best = c;
				}
			}
			System.out.print(MyChromosomeFactory.getIsChromosomeFeasible(c)+";");
			System.out.print(GMObjectiveFunction.evaluate(c)+";");
			for (Gene g : c.getGenes())
				System.out.print(g.getAllele()+";");
			
			System.out.println("");	
		}
		System.out.println("\nBest feasible solution: "+GMObjectiveFunction.evaluate(best));
		
	}
	
}
