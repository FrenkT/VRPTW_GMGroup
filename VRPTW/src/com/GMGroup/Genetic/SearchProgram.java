package com.GMGroup.Genetic;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.DefaultConfiguration;

import com.TabuSearch.MySearchProgram;
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
		/*
		 * currentParams.setAlphaParameterKChain(Double.parseDouble(alphaParamInput.getText()));
					currentParams.setCrossOverLimitRatio(Double.parseDouble(crossOverLimitRatioInput.getText()));
					currentParams.setCrossOverLimitRatio(Double.parseDouble(initialPopulationInput.getText()));
					currentParams.setMaxEvolveIterations(Integer.parseInt(maxEvolveIterationsInput.getText()));
					currentParams.setNumOfKChainSwap(Integer.parseInt(numOfKChainSwapsInput.getText()));
					currentParams.setTabuNonImprovingThresold(Integer.parseInt(tabuNonImprovingThresholdInput.getText()));
					currentParams.setTabuDeltaRatio(Integer.parseInt(tabuDeltaThresholdInput.getText()));
					
		 */
		TabuOperator top = new TabuOperator(conf,params.getTabuDeltaRatio(),params.getTabuNonImprovingThresold());
		
		conf.addGeneticOperator(top);
		
		// ***** Generating an initial population *****
		MyChromosomeFactory factory = MyChromosomeFactory.getInstance(conf);
		IChromosome[] initialPop = new IChromosome[params.getInitialPopulationSize()];
		
		int algorithmCount = 0;
		int randomCount=0;
		int feasibleAlg=0;
		int feasibleRan=0;
		for (int i=0;i<initialPop.length;i++)
		{
			try {
				initialPop[i]=factory.generateInitialFeasibleChromosome();
				if (MyChromosomeFactory.getIsChromosomeFeasible(initialPop[i]))
				{
					feasibleAlg++;
				}
				algorithmCount++;
			}
			catch (Exception IncompleteSolutionException)
			{
				initialPop[i]=factory.generateInitialRandomChromosome();
				if (MyChromosomeFactory.getIsChromosomeFeasible(initialPop[i]))
				{
					feasibleRan++;
				}
				randomCount++;
			}
			System.out.println("Age:"+initialPop[i].getAge()+", Fitness: "+initialPop[i].getAge()+","+MyChromosomeFactory.PrintChromosome(initialPop[i]));
		}
		System.out.println("*************** INITIAL POPULATION FUNDED ***************");
		System.out.println("*********  GeneretadWithTheAlgorithmCount: "+algorithmCount+" Feasible: "+feasibleAlg+"  *********");
		System.out.println("**************  GeneratedRandomCount: "+randomCount+" Feasible: "+feasibleRan+"  *********");
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
