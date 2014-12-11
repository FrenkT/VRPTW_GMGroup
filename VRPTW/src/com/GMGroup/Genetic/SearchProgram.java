package com.GMGroup.Genetic;

import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.Date;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.Population;
import org.jgap.impl.CrossoverOperator;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.GreedyCrossover;
import org.jgap.impl.IntegerGene;

import com.TabuSearch.MyMoveManager;
import com.TabuSearch.MyObjectiveFunction;
import com.TabuSearch.MySearchProgram;
import com.TabuSearch.MySolution;
import com.TabuSearch.MyTabuList;
import com.mdvrp.Duration;
import com.mdvrp.Instance;
import com.mdvrp.Parameters;

public class SearchProgram {

	private static final int INITIAL_POPULATION_SIZE=50;
	
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
		conf.setFitnessFunction(new MyFitnessFunction());
		conf.getGeneticOperators().clear();
		//GreedyCrossover cop = new GreedyCrossover(conf);
		//cop.setStartOffset(0);
		//conf.addGeneticOperator(cop);
		
		//KChainMutationOperator cop2 = new KChainMutationOperator(conf);
		//cop2.setMutationRate(4);
		//conf.addGeneticOperator(cop2);
		
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
//		
//		for(int i =0;i<population.getPopulation().size();i++)
//		{
//			IChromosome c = population.getPopulation().getChromosome(i);
//			
//			Gene[] g = c.getGenes();
//			for(int j=0;j<g.length;j++)
//			{
//				Gene g1=g[j];
//				for (int w=0;w<g.length;w++)
//				{
//					Gene g2=g[w];
//					if ((int)g1.getAllele()==(int)g2.getAllele() && g1!=g2) // second is a reference equals
//					{
//						throw new Exception("Error!!!");
//					}
//				}
//				
//			}
//			
//		}
//		
		// Start alg
		population.evolve(1);
		
	}
	
}
