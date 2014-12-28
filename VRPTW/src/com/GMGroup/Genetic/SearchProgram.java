package com.GMGroup.Genetic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.coinor.opents.Main;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.GeneticOperator;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.Population;
import org.jgap.audit.IEvolutionMonitor;
import org.jgap.eval.PopulationHistoryIndexed;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.DefaultConfiguration;

import com.GMGroup.GeneticUI.MainFrame;
import com.mdvrp.Instance;
import com.mdvrp.Parameters;


public class SearchProgram extends Thread {

	private boolean stopped=false;
	private MyPMXCrossover cop;
	private KChainMutationOperator mop;
	private Configuration conf;
	private MySearchParameters params;
	private double RELAXING_FACTOR = 0.5;
	private Object[] bestFeasible;
	private long startTime=0;
	
	@SuppressWarnings("deprecation")
	public SearchProgram(String fileName,int seed,MySearchParameters params) throws Exception
	{
		if (fileName==null)
		{
			throw new IllegalArgumentException("No file name specified in Parameters.");
		}
		
		this.params = params;
		stopped=false;
		
		bestFeasible = new Object[2];
		bestFeasible[0] = Double.POSITIVE_INFINITY;
		bestFeasible[1] = null;
		
		// ***** Parsing conf input file and populating Instance object *****
		Parameters parameters = new Parameters();
		parameters.updateParameters(new String[]{"-if",fileName});
		Instance.setInstance(parameters);
		Instance instance = Instance.getInstance();
		instance.populateFromHombergFile(parameters.getInputFileName());
		// ****** Configuring our random generator ********//
		MyRandomGenerator.setSeed(seed);
		
		// ***** Setting up jgap *****
		Configuration.reset();
		conf = new DefaultConfiguration();
		conf.setRandomGenerator(MyRandomGenerator.getInstance());
		// ***** Generating an initial population *****
		MyChromosomeFactory factory = MyChromosomeFactory.getInstance(conf);
		IChromosome[] initialPop = new IChromosome[params.getInitialPopulationSize()];
		
		int feasibleTarget =(int)(params.getInitialPopFeasibleChromosomesRatio()/100*params.getInitialPopulationSize());
		int rndTarget = params.getInitialPopulationSize()-feasibleTarget;
		int generatedChroms = 0;
		int feasibleAlg = 0;
		int feasibleRan = 0;
		
		for (int i=0;i<feasibleTarget;i++)
		{
			try 
			{
				initialPop[generatedChroms]=factory.generateInitialFeasibleChromosome();
				if (MyChromosomeFactory.getIsChromosomeFeasible(initialPop[generatedChroms]))
				{
					feasibleAlg++;
				}
				generatedChroms++;
			}
			catch(Exception ex)
			{
				System.out.println("Chromosome not feasible. Generating a random one instead.");
				rndTarget++;
			}
		}
		
		for (int i=0;i<rndTarget;i++)
		{
			initialPop[generatedChroms]=factory.generateInitialRandomChromosome();
			if (MyChromosomeFactory.getIsChromosomeFeasible(initialPop[generatedChroms]))
			{
				feasibleRan++;
			}
			generatedChroms++;
		}
		
		for (IChromosome c : initialPop)
			System.out.println("Feasible: "+MyChromosomeFactory.getIsChromosomeFeasible(c)+", Route's length: "+GMObjectiveFunction.evaluate(c)+","+MyChromosomeFactory.PrintChromosome(c));
		
		System.out.println("*************** INITIAL POPULATION FUNDED ***************");
		System.out.println("*********  GeneratedWithAlgorithm: "+(params.getInitialPopulationSize()-rndTarget)+" Feasible: "+feasibleAlg+" *********");
		System.out.println("**************  GeneratedRandomCount: "+rndTarget+" Feasible: "+feasibleRan+" *********");
		System.out.println("*********************************************************");
		conf.setSampleChromosome(initialPop[0]);
		conf.setPopulationSize(params.getInitialPopulationSize());
		conf.removeNaturalSelectors(false);
		BestChromosomesSelector bestChromsSelector = new BestChromosomesSelector(conf, 1.0d);
		bestChromsSelector.setDoubletteChromosomesAllowed(true);
		conf.addNaturalSelector(bestChromsSelector, false);
		conf.setFitnessFunction(new MyFitnessFunction());
		conf.getGeneticOperators().clear();
		cop = new MyPMXCrossover(conf,params.getCrossOverLimitRatio(),params.getCrossOverWindowWidth());
		conf.addGeneticOperator(cop);
		mop = new KChainMutationOperator(conf);
		mop.setAlpha(params.getAlphaParameterKChain());
		mop.setMutationRate(params.getNumOfKChainSwap());
		conf.addGeneticOperator(mop);
		TabuOperator top = new TabuOperator(conf,params.getTabuDeltaRatio(),params.getTabuNonImprovingThresold());
		conf.addGeneticOperator(top);
		population = new Genotype(conf,initialPop);
	}
	Genotype population = null;
	private long bestTime;
	@Override
	public void run()
	{
		startTime = (new Date()).getTime();
		bestTime=0;
		try {
			if (stopped)
				System.err.println("Cannot start a thread which has been previously stopped.");
						
			//IChromosome c = population.getFittestChromosome();
			//double res = GMObjectiveFunction.evaluate(c);
			//System.out.println("Best of population Before EVOLVE: "+res);
			
			int maxIt = params.getMaxEvolveIterations()<=0 ? Integer.MAX_VALUE : params.getMaxEvolveIterations();
			boolean tabuRelaxed = false;
			for (int i=0;i<maxIt;i++)
			{
				// Evolve once
				population.evolve();
				IChromosome bestFeasibleChromosome = null;
				double bestFeasibleVal = Double.POSITIVE_INFINITY;
				double bestValue = Double.POSITIVE_INFINITY;
				// Look for best feasible chromosome
				for (IChromosome c : population.getPopulation().getChromosomes())
				{
					double val = GMObjectiveFunction.evaluate(c);
					boolean feasible = MyChromosomeFactory.getIsChromosomeFeasible(c);
					
					if (feasible)
					{
						if (val<bestFeasibleVal)
						{
							bestFeasibleVal = val;
							bestFeasibleChromosome=c;
						}
					}
					else
					{
						if (val<bestValue)
							bestValue = val;
					}
					
				}
				
				if (bestFeasibleVal<(double)bestFeasible[0])
				{
					bestFeasible[0]=bestFeasibleVal;
					bestFeasible[1]=bestFeasibleChromosome.clone();
					bestTime = (new Date()).getTime();
				}
				/*
				String bestFeasibleStr = bestFeasible[1]==null ? "N/A" : MyChromosomeFactory.PrintChromosome((IChromosome)bestFeasible[1]);
				System.out.println("BestFeasible is "+bestFeasibleStr);
				*/
				MainFrame.getInstance().setBestResult(bestValue);
				MainFrame.getInstance().setBestFeasible(bestFeasibleVal);
				MainFrame.getInstance().setFeasibility(bestFeasibleVal<Double.POSITIVE_INFINITY);
				
				
				if (!tabuRelaxed && bestFeasibleVal==Double.POSITIVE_INFINITY)
				{
					RelaxTabu();
					tabuRelaxed=true;
					// Increment penalties
					MyFitnessFunction.TimeWPenalty*=10;
				}
				else if (tabuRelaxed==true && bestFeasibleVal<Double.POSITIVE_INFINITY)
				{
					UnRelaxTabu();
					tabuRelaxed=false;
					MyFitnessFunction.TimeWPenalty/=10;
				}

			}
			
			try {
				PrintStatus();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
	}

	private void RelaxTabu() {
		for (Object o : population.getConfiguration().getGeneticOperators())
		{
			if (o instanceof TabuOperator)
			{
				TabuOperator tabu = (TabuOperator)o;
				int newIt = (int)(tabu.getMaxIterationThreshold()*RELAXING_FACTOR);
				tabu.setMaxIterationThreshold(newIt);
			}
		}
	}
	
	private void UnRelaxTabu() {
		for (Object o : population.getConfiguration().getGeneticOperators())
		{
			if (o instanceof TabuOperator)
			{
				TabuOperator tabu = (TabuOperator)o;
				int newIt = (int)(tabu.getMaxIterationThreshold()/RELAXING_FACTOR);
				tabu.setMaxIterationThreshold(newIt);
			}
		}	
	}

	public void PrintStatus() throws IOException
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
		
		double bestValue = GMObjectiveFunction.evaluate(best);
		boolean feasibility = MyChromosomeFactory.getIsChromosomeFeasible(best);
		if (!feasibility)
		{
			best=(IChromosome)bestFeasible[1];
			bestValue = (double)bestFeasible[0];
		}
		else if (feasibility && bestValue<(double)bestFeasible[0])
		{
			best=(IChromosome)bestFeasible[1];
			bestValue = (double)bestFeasible[0];
		}
		
		System.out.println("\nBest feasible solution: "+feasibility+";"+bestValue);
		//MainFrame.getInstance().setBestResult(bestValue+" ("+feasibility + ")");
		
		MainFrame.getInstance().setFeasibility(feasibility);
		MainFrame.getInstance().setBestResult(bestValue);
		MainFrame.getInstance().setBestFeasible(bestValue);
		File outputFile = new File(MainFrame.outputFileName==null ?  Instance.getInstance().getParameters().getInputFileName()+".csv":MainFrame.outputFileName);
		
		List<String> allRes = new ArrayList<String>();
		if (!outputFile.exists())
		{
			// Add the header too
			String[] header = new String[]{
					"INPUT FILE"
					,"BEST_RESULT"
					,"TIME_LAPSE"
					,"VEHICLE_COUNT"
					,"TIME_TO_TOP_SECONDS"
					,"INITIAL_POPULATION_SIZE"
					,"MAX_WAITING_VEHICLE_NUMBER_RATIO"
					,"MAX_WAITABLE_TIME_RATIO"
					,"MAX_EVOLVE_ITERATIONS"
					,"CROSS_OVER_LIMIT_RATIO"
					,"MUTATION_ALPHA_PARAM"
					,"MUTATION_NUM_SWAPS"
					,"TABU_NON_IMPROVING_ITERATION_TRESHOLD"
					,"TABU_MIN_IMPROVEMENT_DELTA_%"
					,"INITIAL_POP_FASIBILITY_%"
					,"TW_PENALTY"
					,"CAPACITY_PENALTY"
					,"BEST_FEASIBILITY"
					,"BEST_RESULT_CHROM"
			};
			StringBuilder sb = new StringBuilder();
			for(String s : header)
				sb.append(s+";");
			allRes.add(sb.toString());
		}
		
		// Count how many vehichles are we using
		Gene[] gg = best.getGenes();
		int count=0;
		boolean v = false;
		for (int i=0;i<gg.length;i++)
		{
			if (((int)gg[i].getAllele())<=0 && v)
			{
				count++;
				// This is a customer, so start considering a vechichle
				v = false;
			}
			
			if (((int)gg[i].getAllele())>0)
			{
				v=true;
			}
		}
		// Last separator (end of chromosome)
		if (v)
			count++;
		
		// Calculate TimeToTop
		long ttt = (bestTime - startTime)/1000;
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile,true));
		String[] rsltStr = new String[]{
				Instance.getInstance().getParameters().getInputFileName()
				,""+GMObjectiveFunction.evaluate(best)
				,"300" // We always run for 5 minutes
				,"" + count
				,"" + ttt
				,""+params.getInitialPopulationSize()
				,""+MyChromosomeFactory.MAX_WAITING_VEHICLE_NUMBER_RATIO
				,""+MyChromosomeFactory.MAX_WAITABLE_TIME_RATIO
				,""+params.getMaxEvolveIterations()
				,""+params.getCrossOverLimitRatio()
				,""+params.getAlphaParameterKChain()
				,""+params.getNumOfKChainSwap()
				,""+params.getTabuNonImprovingThresold()
				,""+params.getTabuDeltaRatio()
				,""+params.getInitialPopFeasibleChromosomesRatio()
				,"" + MyFitnessFunction.TimeWPenalty
				,"" + MyFitnessFunction.CapacityPenalty
				,""+MyChromosomeFactory.getIsChromosomeFeasible(best)
				,"["+MyChromosomeFactory.PrintChromosome(best)+"]"
		};
		StringBuilder sb = new StringBuilder();
		for(String s : rsltStr)
			sb.append(s+";");
		
		allRes.add(sb.toString());
		for (String ls : allRes)
		{
			writer.write(ls.toString());
			writer.newLine();
		}
		writer.close();
	}

	@SuppressWarnings("deprecation")
	public void halt() {
		// TODO Auto-generated method stub
		stopped = true;
		this.stop();
		
	}
	
	
	public void setEvolutionMonitor(IEvolutionMonitor a_monitor)
	{
		conf.setMonitor(a_monitor);
	}
}
