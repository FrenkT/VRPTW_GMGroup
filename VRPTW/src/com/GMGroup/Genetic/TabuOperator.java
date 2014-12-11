package com.GMGroup.Genetic;

import java.io.FileWriter;
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
import com.mdvrp.Duration;
import com.mdvrp.Instance;
import com.mdvrp.Parameters;

public class TabuOperator extends BaseGeneticOperator{

	private static int NumberOfIterations = 0;
	
	public TabuOperator(Configuration a_configuration)
			throws InvalidConfigurationException {
		super(a_configuration);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void operate(Population a_population, List a_candidateChromosomes) {
//		Duration duration = new Duration();
//		duration.start();

		// TODO
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
		// Create Tabu Search object
        
        for (int k=0;k<a_population.size();k++)
        {
        	System.out.println("Starting tabu iteration # "+NumberOfIterations);
        	IChromosome c = a_population.getChromosome(k);
        	// Create a wrapper
        	MySolutionGMWrapper solWrapper = new MySolutionGMWrapper(c);
	        MySearchProgram search = new MySearchProgram(instance, solWrapper, moveManager,
							            objFunc, tabuList, false,  outPrintSream);
	        // Start solving        
	        search.tabuSearch.setIterationsToGo(parameters.getIterations());
	        search.tabuSearch.startSolving();
	        
	        // wait for the search thread to finish
	        try {
	        	// in order to apply wait on an object synchronization must be done
	        	synchronized(instance){
	        		instance.wait();
	        	}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
	        
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
	        a_candidateChromosomes.add(solWrapper.toChromosome());
	        //FileWriter fw = new FileWriter(parameters.getOutputFileName(),true);
	        //fw.write(outSol);
	        //fw.close();
        }
        
		
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}