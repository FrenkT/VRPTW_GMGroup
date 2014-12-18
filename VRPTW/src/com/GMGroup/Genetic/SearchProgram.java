package com.GMGroup.Genetic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.DefaultConfiguration;

import com.GMGroup.GeneticUI.MainFrame;
import com.mdvrp.Instance;
import com.mdvrp.Parameters;
import com.opencsv.CSVWriter;

public class SearchProgram extends Thread{

	private boolean stopped=false;
	private MyPMXCrossover cop;
	private KChainMutationOperator mop;
	private Configuration conf;
	private MySearchParameters params;
	
	public SearchProgram(String fileName,int seed,MySearchParameters params) throws Exception
	{
		this.params = params;
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
		bestChromsSelector.setDoubletteChromosomesAllowed(false);
		conf.addNaturalSelector(bestChromsSelector, false);
		conf.setFitnessFunction(new MyFitnessFunction());
		conf.getGeneticOperators().clear();
		cop = new MyPMXCrossover(conf,params.getCrossOverLimitRatio(),40);
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
	@Override
	public void run()
	{
		try {
			if (stopped)
				System.err.println("Cannot start a thread which has been previously stopped.");
			
			IChromosome c = population.getFittestChromosome();
			double res = GMObjectiveFunction.evaluate(c);
			System.out.println("Best of population Before EVOLVE: "+res);
			
			population.evolve(params.getMaxEvolveIterations()==0?Integer.MAX_VALUE:params.getMaxEvolveIterations());
			
			try {
				PrintStatus();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
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
		
		System.out.println("\nBest feasible solution: "+MyChromosomeFactory.getIsChromosomeFeasible(best)+";"+GMObjectiveFunction.evaluate(best));
		
		File outputDir = new File("output");
		if (!outputDir.isDirectory() || !outputDir.exists())
		{
			boolean dirCreated = outputDir.createNewFile();
			if (!dirCreated)
				throw new IOException("Cannot create dir "+outputDir.getAbsolutePath());
		}
		
		File outputFile = new File(outputDir,MainFrame.outputFileName==null ? "solutions.csv" : Instance.getInstance().getParameters().getInputFileName()+".csv");
		
		List<String[]> allRes = new ArrayList<String[]>();
		if (!outputFile.exists())
		{
			// Add the header too
			String[] header = new String[]{
					"INPUT FILE"
					,"TimeLapsed"
					,"INITIAL_POPULATION_SIZE"
					,"MAX_EVOLVE_ITERATIONS"
					,"CROSS_OVER_LIMIT_RATIO"
					,"MUTATION_ALPHA_PARAM"
					,"MUTATION_NUM_SWAPS"
					,"TABU_NON_IMPROVING_ITERATION_TRESHOLD"
					,"TABU_MIN_IMPROVEMENT_DELTA_%"
					,"INITIAL_POP_FASIBILITY_%"
					,"TW_PENALTY"
					,"CAPACITY_PENALTY"
					,"BEST_RESULT"
			};
			allRes.add(header);
		}
		CSVWriter writer = new CSVWriter(new FileWriter(outputFile,true));
		String[] rsltStr = new String[]{
				Instance.getInstance().getParameters().getInputFileName()
				,"300" // We always run for 5 minutes
				,""+params.getInitialPopulationSize()
				,""+params.getMaxEvolveIterations()
				,""+params.getCrossOverLimitRatio()
				,""+params.getAlphaParameterKChain()
				,""+params.getNumOfKChainSwap()
				,""+params.getTabuNonImprovingThresold()
				,""+params.getTabuDeltaRatio()
				,""+params.getInitialPopFeasibleChromosomesRatio()
				,"" + MyFitnessFunction.TimeWPenalty
				,"" + MyFitnessFunction.CapacityPenalty
				,""+GMObjectiveFunction.evaluate(best)
				,""// Time
		};
		allRes.add(rsltStr);
		writer.writeAll(allRes);
		writer.close();
	}

	public void halt() {
		// TODO Auto-generated method stub
		stopped = true;
		this.stop();
		
	}
}
