package com.GMGroup.Genetic;

import java.awt.geom.Point2D;
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

import com.TabuSearch.MyObjectiveFunction;
import com.mdvrp.Instance;
import com.mdvrp.Parameters;

public class SearchProgram {

	public static void main(String[] args) throws Exception
	{
		// Parsing conf input file and populating Instance object
		Parameters parameters = new Parameters();
		
		parameters.updateParameters(args);
		if(parameters.getInputFileName() == null){
			System.out.println("You must specify an input file name");
			return;
		}
		Instance.setInstance(parameters);
		Instance instance = Instance.getInstance();
		instance.populateFromHombergFile(parameters.getInputFileName());
		
		// Setting up jgap
		Configuration conf = new DefaultConfiguration();
		conf.setFitnessFunction(new MyFitnessFunction());
		//conf.getGeneticOperators().clear();
		//conf.addGeneticOperator(new GreedyCrossover(conf));
		/*
		Gene[] sampleGenes = new Gene[instance.getCustomersNr()+instance.getVehiclesNr()];
		for (int i=0; i<sampleGenes.length;i++)
			sampleGenes[i]=new IntegerGene(conf,0, instance.getCustomersNr());
		
		conf.setSampleChromosome(new Chromosome(conf,sampleGenes));
		conf.setPopulationSize(100);
		
		// Creating a population
		Genotype population = Genotype.randomInitialGenotype(conf);
		*/
		
		IChromosome[] initialPop = new IChromosome[100];
		for (int i=0;i<initialPop.length;i++)
		{
			initialPop[i]=MyChromosomeFactory.generateInitialChromosome(conf);
		}
		
		conf.setSampleChromosome(initialPop[0]);
		conf.setPopulationSize(50);
		Genotype population = new Genotype(conf,initialPop);
		
		for (int i=0;i<1000;i++)
		{
			// Start alg
			population.evolve();
		}
		
		IChromosome bestSol = population.getFittestChromosome();
		// Testing genetic alg.
		
		System.out.println("Best Solution found:");
		System.out.println(MyChromosomeFactory.PrintChromosome(bestSol));
		System.out.println("Legth: "+GMObjectiveFunction.evaluate(bestSol));
		
		/*
		Parameters parameters = new Parameters();
		
		parameters.updateParameters(args);
		if(parameters.getInputFileName() == null){
			System.out.println("You must specify an input file name");
			return;
		}
		Instance.setInstance(parameters);
		Instance instance = Instance.getInstance();
		instance.populateFromHombergFile(parameters.getInputFileName());
		
		// Genero una popolazione iniziale
		Point2D depPos = new Point2D.Double(instance.getDepot(0).getXCoordinate(),instance.getDepot(0).getYCoordinate());
		Date dti = new Date();
		MyChromosome cw = generateInitialChromosome();
		String res = cw.toString();
		Date dte = new Date();
		System.out.println("\nImpiegato: "+(dte.getTime()-dti.getTime())/1000d + " s");
		System.out.println(res);
		
		//System.out.println(Accelerator.getInstance().getDistanceBetween(100,100));
		System.out.println("Total len: "+GMObjectiveFunction.evaluate(cw));
		*/
	}
	
}
