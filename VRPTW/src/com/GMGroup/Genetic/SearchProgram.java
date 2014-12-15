package com.GMGroup.Genetic;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import sun.java2d.pipe.SpanClipRenderer;

import com.GMGroup.GeneticUI.MainFrame;
import com.TabuSearch.MySearchProgram;
import com.TabuSearch.MySolution;
import com.mdvrp.Instance;
import com.mdvrp.Parameters;

public class SearchProgram extends Thread{

	public static int INITIAL_POPULATION_SIZE=50;
	public static int MAX_EVOLVE_GENERATIONS=30;
	private boolean stopped=false;
	/**
	 * Makes the crossover operator to operate on a limited number of parents.
	 * If INITIAL_SIZE_POPULATION is 90, there will be 90 * CROSS_OVER_RATIO 
	 * crossovers.
	 */
	public static double CROSS_OVER_LIMIT_RATIO = 0.3;
	
	public static double MUTATION_LIMIT_RATIO = 0.005;
	
	public static int MUTATION_NUM_OF_GENES = 4;
	
	private MyGreedyCrossover cop;
	private KChainMutationOperator mop;
	
	public SearchProgram(String fileName) throws Exception
	{
		stopped=false;
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
		Configuration.reset();
		Configuration conf = new DefaultConfiguration();
		BestChromosomesSelector bestChromsSelector = new BestChromosomesSelector(conf, 0.90d);
		bestChromsSelector.setDoubletteChromosomesAllowed(false);
		conf.addNaturalSelector(bestChromsSelector, false);
		conf.setFitnessFunction(new MyFitnessFunction());
		conf.getGeneticOperators().clear();
		cop = new MyGreedyCrossover(conf);
		cop.setStartOffset(0);
		//cop.setRate(CROSS_OVER_LIMIT_RATIO);
		conf.addGeneticOperator(cop);
		
		mop = new KChainMutationOperator(conf);
		mop.setMutationRate(MUTATION_NUM_OF_GENES);
		mop.setParameter(MUTATION_LIMIT_RATIO);
		conf.addGeneticOperator(mop);
		
		conf.addGeneticOperator(new TabuOperator(conf));
		
		// ***** Generating an initial population *****
		MyChromosomeFactory factory = MyChromosomeFactory.getInstance(conf);
		IChromosome[] initialPop = new IChromosome[INITIAL_POPULATION_SIZE];
		
		int feasibleCount = 0;
		int randomCount=0;
		for (int i=0;i<initialPop.length;i++)
		{
			try {
				initialPop[i]=MyChromosomeFactory.getInstance(conf).generateInitialFeasibleChromosome();
				feasibleCount++;
			}
			catch (Exception IncompleteSolutionException)
			{
				initialPop[i]=MyChromosomeFactory.getInstance(conf).generateInitialRandomChromosome();
				randomCount++;
			}
			System.out.println("Age:"+initialPop[i].getAge()+", Fitness: "+initialPop[i].getAge()+","+MyChromosomeFactory.PrintChromosome(initialPop[i]));
		}
		System.out.println("*************** INITIAL POPULATION FUNDED ***************");
		System.out.println("*********  FeasibleCount: "+feasibleCount+"   *********");
		System.out.println("*********   RandomCount: "+randomCount+"    *********");
		System.out.println("*********************************************************");
		conf.setSampleChromosome(initialPop[0]);
		conf.setPopulationSize(INITIAL_POPULATION_SIZE);
		population = new Genotype(conf,initialPop);
		
	}
	Genotype population = null;
	@Override
	public void run()
	{
		if (stopped)
			System.err.println("Cannot start a thread which has been previously stopped.");
		
		IChromosome c = population.getFittestChromosome();
		double res = GMObjectiveFunction.evaluate(c);
		System.out.println("Best of population Before EVOLVE: "+res);
		
		population.evolve(MAX_EVOLVE_GENERATIONS);
		
		PrintStatus();
	}

	public void PrintStatus()
	{

		IChromosome c = population.getFittestChromosome();
		double res = GMObjectiveFunction.evaluate(c);
		System.out.println("\nBest of population: "+res+"\n");
		
		IChromosome best = null;
		for(int i=0;i<population.getPopulation().size();i++)
		{
			c = population.getPopulation().getChromosome(i);
			if (MyChromosomeFactory.getIsChromosomeFeasible(c)){
				if (best == null){
					best = c;
				}
				
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
		
		if (best==null)
			best=population.getFittestChromosome();
		
		System.out.println("\nBest feasible solution: "+MyChromosomeFactory.getIsChromosomeFeasible(best)+";"+GMObjectiveFunction.evaluate(best));
	}

	public void setInitialPopulationSize(int initialPopSize) {
		this.INITIAL_POPULATION_SIZE = initialPopSize;
	}

	public void setCrossOverParam(double crossOverParam) {
	
		this.CROSS_OVER_LIMIT_RATIO = crossOverParam;
		this.cop.setRate(crossOverParam);
	}

	public void setMutationParam(double parameter) {
		this.MUTATION_LIMIT_RATIO=parameter;
		mop.setParameter(parameter);
	}

	
	public void halt() {
		// TODO Auto-generated method stub
		stopped = true;
		this.stop();
	}
	
}
