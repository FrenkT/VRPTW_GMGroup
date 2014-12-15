package com.GMGroup.Genetic;

import java.io.PrintStream;
import java.util.List;

import org.jgap.BaseGeneticOperator;
import org.jgap.Configuration;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;

import com.TabuSearch.MyMoveManager;
import com.TabuSearch.MyObjectiveFunction;
import com.TabuSearch.MySearchProgram;
import com.TabuSearch.MyTabuList;
import com.mdvrp.Instance;
import com.mdvrp.Parameters;

public class TabuOperator extends BaseGeneticOperator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int NumberOfIterations = 0;
	public TabuOperator(Configuration a_configuration)
			throws InvalidConfigurationException {
		super(a_configuration);		
		// TODO Auto-generated constructor stub
	}

	
	@SuppressWarnings("rawtypes")
	@Override
	public void operate(final Population a_population, List a_candidateChromosomes) {

		// Should we apply the tabu to all the chromosomes of the population?
		Instance instance = Instance.getInstance();		
		Parameters parameters = instance.getParameters();
		// Init memory for Tabu Search
		MyObjectiveFunction objFunc = new MyObjectiveFunction(instance);
        MyMoveManager moveManager = new MyMoveManager(instance);
        moveManager.setMovesType(parameters.getMovesType());
        
        // Tabu list
        int dimension[] = {instance.getDepotsNr(), instance.getVehiclesNr(), instance.getCustomersNr(), 1, 1};
        MyTabuList tabuList = new MyTabuList(parameters.getTabuTenure(), dimension);
        
        PrintStream outPrintSream = null ;
		
        // Apply the Tabu Search only to offsprings, do not touch parents
        int startIndex = a_population.getConfiguration().getPopulationSize();
        if (startIndex==0)
        	return;
        IChromosome[] offsprings = new IChromosome[a_candidateChromosomes.size()-startIndex];
        for (int k=startIndex;k<a_candidateChromosomes.size();k++)
        {
        	System.out.println("Starting tabu iteration # "+NumberOfIterations);
        	IChromosome c = (IChromosome)a_candidateChromosomes.get(k);
        	// Create a wrapper
        	MySolutionGMWrapper solWrapper = new MySolutionGMWrapper(c);
	        MySearchProgram search = new MySearchProgram(instance, solWrapper, moveManager,
							            objFunc, tabuList, false,  outPrintSream);
	        
	        // Start solving        
        	search.tabuSearch.setIterationsToGo(parameters.getIterations());
    		search.tabuSearch.startSolving();

	        // Count routes
	        int routesNr = 0;
	        for(int i =0; i < search.feasibleRoutes.length; ++i)
	        	for(int j=0; j < search.feasibleRoutes[i].length; ++j)
	        		if(search.feasibleRoutes[i][j].getCustomersLength() > 0)
	        			routesNr++;
	        // Print results
	        String outSol = String.format("%s; %5.2f; %4d\r\n" ,
	        		instance.getParameters().getInputFileName(), search.feasibleCost.total,
	            	routesNr);
	        System.out.println(outSol);
	        System.out.println("Ended tabu iteration # "+NumberOfIterations);
	        NumberOfIterations++;
	        try {
	        	offsprings[k-startIndex]=((MySolutionGMWrapper)search.tabuSearch.getBestSolution()).toChromosome();
	        	a_candidateChromosomes.remove(k);
	        	a_candidateChromosomes.add(k,offsprings[k-startIndex]);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        // ----- SUBSISTUTE offsprings with tabu-searhed ones ------
        // Swap old with new
        //for (int j=a_candidateChromosomes.size()-1;j>=startIndex;j--)
        //{
        //	a_candidateChromosomes.remove(j);
        //}
        //for (IChromosome c : offsprings)
        //	a_candidateChromosomes.add(c);        
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
