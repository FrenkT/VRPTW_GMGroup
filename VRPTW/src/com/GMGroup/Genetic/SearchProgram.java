package com.GMGroup.Genetic;

import javax.swing.JTextField;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.DefaultConfiguration;

import com.mdvrp.Instance;
import com.mdvrp.Parameters;

public class SearchProgram extends Thread{

	public static int INITIAL_POPULATION_SIZE=50;
	
	/**
	 * Makes the crossover operator to operate on a limited number of parents.
	 * If INITIAL_SIZE_POPULATION is 90, there will be 90 * CROSS_OVER_RATIO 
	 * crossovers.
	 */
	public static double CROSS_OVER_LIMIT_RATIO = 0.3;
	
	public static double MUTATION_LIMIT_RATIO = 0.005;
	
	public static int MUTATION_NUM_OF_GENES = 4;
	
	public SearchProgram(String fileName) throws Exception
	{
		// ***** Parsing conf input file and populating Instance object *****
		Parameters parameters = new Parameters();
		parameters.updateParameters(new String[]{"-if",fileName});
		if(parameters.getInputFileName() == null){
			System.out.println("You must specify an input file name");
			return;
		}
		Instance.setInstance(parameters);
		Instance instance = Instance.getInstance();
		instance.populateFromHombergFile(parameters.getInputFileName());
		
		// ***** Setting up jgap *****
		Configuration conf = new DefaultConfiguration();
		conf.setFitnessFunction(new MyFitnessFunction());
		conf.getGeneticOperators().clear();
		MyGreedyCrossover cop = new MyGreedyCrossover(conf);
		cop.setStartOffset(0);
		cop.setRate(CROSS_OVER_LIMIT_RATIO);
		conf.addGeneticOperator(cop);
		
		KChainMutationOperator cop2 = new KChainMutationOperator(conf);
		cop2.setMutationRate(MUTATION_NUM_OF_GENES);
		cop2.setParameter(MUTATION_LIMIT_RATIO);
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
		population = new Genotype(conf,initialPop);
		
	}
	Genotype population = null;
	
	@Override
	public void run()
	{
		IChromosome c = population.getFittestChromosome();
		double res = GMObjectiveFunction.evaluate(c);
		System.out.println("Best of population Before EVOLVE: "+res);
		
		// Start alg
		population.evolve(3);
		
		c = population.getFittestChromosome();
		res = GMObjectiveFunction.evaluate(c);
		System.out.println("Best of population: "+res);
		
		for(int i =0;i<population.getPopulation().size();i++)
		{
			c = population.getPopulation().getChromosome(i);
			System.out.print(MyChromosomeFactory.getIsChromosomeFeasible(c)+";");
			System.out.print(GMObjectiveFunction.evaluate(c)+";");
			for (Gene g : c.getGenes())
				System.out.print(g.getAllele()+";");
			
			System.out.println("");
			
		}
	}

	public void setInitialPopulationSize(int initialPopSize) {
		this.INITIAL_POPULATION_SIZE = initialPopSize;
	}

	public void setCrossOverParam(double crossOverParam) {
	
		this.CROSS_OVER_LIMIT_RATIO = crossOverParam;
		
	}

	public void setMutationParam(double mop) {
		this.MUTATION_LIMIT_RATIO=mop;
	}
	
}
