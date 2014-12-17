package com.GMGroup.Genetic;

import java.io.FileWriter;
import java.io.IOException;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.DefaultConfiguration;
import com.mdvrp.Instance;
import com.mdvrp.Parameters;
import com.opencsv.CSVWriter;

public class SearchProgram extends Thread{

	private boolean stopped=false;
	private MyGreedyCrossover cop;
	private KChainMutationOperator mop;
	private Configuration conf;
	private MySearchParameters params;
	
	public SearchProgram(String fileName,MySearchParameters params) throws Exception
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
		
		// ***** Setting up jgap *****
		Configuration.reset();
		conf = new DefaultConfiguration();
		BestChromosomesSelector bestChromsSelector = new BestChromosomesSelector(conf, 0.90d);
		bestChromsSelector.setDoubletteChromosomesAllowed(false);
		conf.addNaturalSelector(bestChromsSelector, false);
		conf.setFitnessFunction(new MyFitnessFunction());
		conf.getGeneticOperators().clear();
		cop = new MyGreedyCrossover(conf);
		cop.setStartOffset(0);
		cop.setRate(params.getCrossOverLimitRatio());
		conf.addGeneticOperator(cop);
		
		mop = new KChainMutationOperator(conf);
		mop.setAlpha(params.getAlphaParameterKChain());
		mop.setMutationRate(params.getNumOfKChainSwap());
		conf.addGeneticOperator(mop);

		TabuOperator top = new TabuOperator(conf,params.getTabuDeltaRatio(),params.getTabuNonImprovingThresold());
		
		conf.addGeneticOperator(top);
		
		// ***** Generating an initial population *****
		MyChromosomeFactory factory = MyChromosomeFactory.getInstance(conf);
		IChromosome[] initialPop = new IChromosome[params.getInitialPopulationSize()];
		
		int feasibleTarget = (int)params.getInitialPopFeasibleChromosomesRatio()/100*params.getInitialPopulationSize();
		int rndTarget = params.getInitialPopulationSize()-feasibleTarget;
		int generatedChroms = 0;
		
		for (int i=0;i<feasibleTarget;i++)
		{
			try {
				initialPop[generatedChroms]=factory.generateInitialFeasibleChromosome();
				generatedChroms++;
			}
			catch(Exception ex)
			{
				System.out.println("Chromosme not feasible. Generating a random one instead.");
				rndTarget++;
			}
		}
		
		for (int i=0;i<rndTarget;i++)
		{
			initialPop[generatedChroms]=factory.generateInitialRandomChromosome();
			generatedChroms++;
		}
		
		for (IChromosome c : initialPop)
			System.out.println("Feasible: "+MyChromosomeFactory.getIsChromosomeFeasible(c)+", Fitness: "+GMObjectiveFunction.evaluate(c)+","+MyChromosomeFactory.PrintChromosome(c));
		
		System.out.println("*************** INITIAL POPULATION FUNDED ***************");
		System.out.println("*********  Feasible ones: "+(params.getInitialPopulationSize()-rndTarget)+" *********");
		System.out.println("**************  GeneratedRandomCount: "+rndTarget+"  *********");
		System.out.println("*********************************************************");
		conf.setSampleChromosome(initialPop[0]);
		conf.setPopulationSize(params.getInitialPopulationSize());
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
		
		population.evolve(params.getMaxEvolveIterations()==0?Integer.MAX_VALUE:params.getMaxEvolveIterations());
		
		try {
			PrintStatus();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		
		CSVWriter writer = new CSVWriter(new FileWriter("data.csv",true));
		
		String[] rsltStr = new String[]{
				""+params.getInitialPopulationSize()
				,""+params.getMaxEvolveIterations()
				,""+params.getCrossOverLimitRatio()
				,""+params.getAlphaParameterKChain()
				,""+params.getNumOfKChainSwap()
				,""
				,""+params.getTabuNonImprovingThresold()
				,""+params.getTabuDeltaRatio()
				,""// MutationProb
				,"" + MyFitnessFunction.TimeWPenalty
				,"" + MyFitnessFunction.CapacityPenalty
				,""// Empty
				,""+GMObjectiveFunction.evaluate(best)
				,""// Time
		};
		
		writer.writeNext(rsltStr);
		writer.close();
	}

	
	
	public void halt() {
		// TODO Auto-generated method stub
		stopped = true;
		this.stop();
	}
}
